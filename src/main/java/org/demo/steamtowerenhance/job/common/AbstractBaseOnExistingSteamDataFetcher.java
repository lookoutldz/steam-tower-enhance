package org.demo.steamtowerenhance.job.common;

import okhttp3.Request;
import okhttp3.Response;
import org.demo.steamtowerenhance.config.CommonThreadPool;
import org.demo.steamtowerenhance.domain.DatabaseEntity;
import org.demo.steamtowerenhance.dto.steamresponse.*;
import org.demo.steamtowerenhance.thirdparty.SteamWebApi;
import org.demo.steamtowerenhance.util.HttpUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

/**
 * 基于数据库的一些数据去API查询更多数据的通用逻辑
 * @param <D> 数据库实体类型
 * @param <S> SteamWebApi 返回类型
 */
public abstract class AbstractBaseOnExistingSteamDataFetcher<D extends DatabaseEntity, S extends SteamResponse>
        extends AbstractSteamDataFetcher implements BaseOnExistingSteamDataFetcher<D, S> {

    // 记录从 DB 里查询的 offset, 方便下一轮查询
    protected Integer offset;
    // 记录请求失败的记录, 方便重试, Map<url, errorCode>, 有并发
    protected final ConcurrentHashMap<String, Integer> failedMap;
    // 请求获得的实体在插入 DB 前的汇总
    protected final CopyOnWriteArrayList<D> preparedDataConcurrentList;

    protected final SteamWebApi steamWebApi;
    protected final HttpUtils httpUtils;
    protected final CommonThreadPool commonThreadPool;

    protected AbstractBaseOnExistingSteamDataFetcher(SteamWebApi steamWebApi, HttpUtils httpUtils, CommonThreadPool commonThreadPool) {
        this.steamWebApi = steamWebApi;
        this.httpUtils = httpUtils;
        this.commonThreadPool = commonThreadPool;

        offset = 0;
        failedMap = new ConcurrentHashMap<>();
        preparedDataConcurrentList = new CopyOnWriteArrayList<>();
    }



    public void fetch() {
        List<CompletableFuture<Void>> futures = pageableQueryDataAndGenerateUrl(offset, getSelectionPageSize())
                .parallelStream()
                .filter(Objects::nonNull)
                .map(this::getFuture)
                .toList();
        int rows = fetchRecordsToDatabase(futures, null);
        afterFetchRecordsToDatabase(rows);
    }

    public void reFetch() {
        if (failedMap.size() < 1) {
            return;
        }
        Iterator<String> iterator = failedMap.keySet().iterator();
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int i = 0; i < getSelectionPageSize() && iterator.hasNext(); i++) {
            String url = iterator.next();
            if (url != null) {
                futures.add(getFuture(url));
            }
        }
        int rows = fetchRecordsToDatabase(futures, null);
        afterFetchRecordsToDatabase(rows);
    }

    @Override
    public List<D> dataPostProcessor(String url, List<D> rawData) {
        return rawData;
    }

    private void afterFetchRecordsToDatabase(int rows) {
        getLogger().info("本轮 fetch 信息: offset {}, pageSize {}, 有效数据 {}", offset, getSelectionPageSize(), rows);
        if (rows > 0) {
            offset += getSelectionPageSize();
        }
    }

    // 任务 Future 的工厂
    protected CompletableFuture<Void> getFuture(String url) {
        return CompletableFuture
                .supplyAsync(()->
                        httpUtils.getAsObject(url,
                                null,
                                getSteamResponseType(),
                                this::httpRequestFailedHandler,
                                this::httpRequestErrorHandler
                        ), commonThreadPool)
                .thenApplyAsync(
                        this::dataTransformer
                )
                .thenApplyAsync(dList ->
                        dataPostProcessor(url, dList)
                )
                .thenAcceptAsync(
                        preparedDataConcurrentList::addAll
                );
    }

    protected int fetchRecordsToDatabase(Collection<CompletableFuture<Void>> futures, Consumer<Throwable> errorHandler) {
        try {
            CompletableFuture<Void> allOfFuture = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
            allOfFuture.get();
            final int availableRecords = preparedDataConcurrentList.size();
            if (availableRecords > 0) {
                insertData(preparedDataConcurrentList);
            }
            return availableRecords;
        } catch (InterruptedException | ExecutionException e) {
            if (errorHandler != null) {
                errorHandler.accept(e);
            } else {
                getLogger().error("Error occurred.", e);
            }
        } finally {
            preparedDataConcurrentList.clear();
        }
        return 0;
    }

    protected S httpRequestFailedHandler(Response response) {
        final String urlString = response.request().url().toString();
        final int code = response.code();
        failedMap.put(urlString, code);
        getLogger().warn("HTTP {} for {} ", code, urlString);
        return null;
    }

    protected void httpRequestErrorHandler(Request request, Throwable throwable) {
        final String urlString = request.url().toString();
        failedMap.put(urlString, 999);
        if (throwable != null) {
            getLogger().error("ERROR for {} ", urlString, throwable);
        } else {
            getLogger().error("ERROR for {} ", urlString);
        }
    }

    @SuppressWarnings("unchecked")
    protected List<D> dataTransformer(S response) {
        if (response == null) {
            return List.of();
        }

        if (response instanceof GetAppListResponse r) {
            if (r.applist() != null && r.applist().apps() != null) {
                return (List<D>) r.applist().apps();
            }
        } else if (response instanceof GetPlayerSummariesResponse r) {
            if (r.response() != null && r.response().players() != null) {
                return (List<D>) r.response().players();
            }
        } else if (response instanceof GetFriendListResponse r) {
            if (r.friendslist() != null && r.friendslist().friends() != null) {
                return (List<D>) r.friendslist().friends();
            }
        } else if (response instanceof GetOwnedGamesResponse r) {
            if (r.response().game_count() != null && r.response().games() != null) {
                return (List<D>) r.response().games();
            }
        } else {
            return List.of();
        }
        return List.of();
    }
}