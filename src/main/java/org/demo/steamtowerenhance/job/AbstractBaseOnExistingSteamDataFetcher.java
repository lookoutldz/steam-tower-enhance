package org.demo.steamtowerenhance.job;

import org.demo.steamtowerenhance.config.CommonThreadPool;
import org.demo.steamtowerenhance.domain.DatabaseEntity;
import org.demo.steamtowerenhance.dto.steamresponse.GetAppListResponse;
import org.demo.steamtowerenhance.dto.steamresponse.GetFriendListResponse;
import org.demo.steamtowerenhance.dto.steamresponse.GetPlayerSummariesResponse;
import org.demo.steamtowerenhance.dto.steamresponse.SteamResponse;
import org.demo.steamtowerenhance.thirdparty.SteamWebApi;
import org.demo.steamtowerenhance.util.HttpUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * 基于数据库的一些数据去API查询更多数据的通用逻辑
 * @param <T> DB 查出的参数类型(通常是String)
 * @param <R> 用于 HTTP 请求的参数类型
 * @param <S> 数据库实体类型
 * @param <U> SteamWebApi 返回类型
 */
public abstract class AbstractBaseOnExistingSteamDataFetcher<T, R, S extends DatabaseEntity, U extends SteamResponse>
        extends AbstractSteamDataFetcher
        implements BaseOnExistingSteamDataFetcher<T, R, S, U> {

    final SteamWebApi steamWebApi;
    final HttpUtils httpUtils;
    final CommonThreadPool commonThreadPool;

    protected AbstractBaseOnExistingSteamDataFetcher(SteamWebApi steamWebApi, HttpUtils httpUtils, CommonThreadPool commonThreadPool) {
        this.steamWebApi = steamWebApi;
        this.httpUtils = httpUtils;
        this.commonThreadPool = commonThreadPool;
    }

    /**
     * 主逻辑
     * @param dataToParams T -> R
     * @param errorHandler Just a Handler
     */
    protected void process(Function<List<T>, List<R>> dataToParams,
                           BiFunction<List<S>, R, List<S>> dataDecoratorBeforeInsert,
                           BiConsumer<Throwable, List<R>> errorHandler) {
        // 总计数器
        int total = 0;

        // 获取库内数据总数量
        final int allRecordCount = countBasicDataRecords();

        // 数据超过 getDatabaseQueryPageSize 时分批查询避免 OOM
        final int pageSize = getDatabaseQueryPageSize();
        final int pageCount = allRecordCount > pageSize ? allRecordCount / pageSize + 1 : 1;

        for (int i = 0; i < pageCount; i++) {
            // 每页数据计数器
            int rows = 0;

            // 分页
            final int offset = i * pageSize;
            final int actualPageSize = i == pageCount - 1 ? allRecordCount % pageSize : pageSize;
            List<T> basicDataList = findBasicDataByPage(offset, actualPageSize);

            // 单次查得的数据按 getMaxCountInRound 大小拆分成多组, 避免后续 HTTP 请求量过大、造成 OOM 或插入失败
            List<List<T>> basicDataRecordsList = separateList(basicDataList, getMaxCountInRound());
            for (int j = 0; j < basicDataRecordsList.size(); j++) {
                // 插入数据库的最小数据集
                final List<T> basicDataRecords = basicDataRecordsList.get(j);

                final List<CompletableFuture<Void>> futures = new ArrayList<>();
                final CopyOnWriteArrayList<S> concurrentBucket = new CopyOnWriteArrayList<>();

                // 转化为合适的参数形式
                final List<R> parameters = dataToParams.apply(basicDataRecords);
                // 启动多线程异步请求任务
                for (R parameter : parameters) {
                    futures.add(generateDataFetcherFuture(parameter, dataDecoratorBeforeInsert, concurrentBucket));
                }
                // 合并 future
                CompletableFuture<Void> allOfFuture = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

                try {
                    // 同步
                    allOfFuture.get();
                    final int availableRecords = concurrentBucket.size();
                    // 插入数据库
                    if (availableRecords > 0) {
                        insertData(concurrentBucket);
                    }
                    getLogger().info("第 {} 页第 {} 轮, 数据库: {}, 请求返回有效数据: {} ", i, j, basicDataRecordsList.size(), availableRecords);
                    rows += availableRecords;
                } catch (InterruptedException | ExecutionException e) {
                    if (errorHandler != null) {
                        errorHandler.accept(e, parameters);
                    }
                }
            }
            getLogger().info("第 {} 页, 数据库: {}, 请求返回有效数据: {} ", i, actualPageSize, rows);
            total += rows;
        }
        getLogger().info("本次 Fetch 任务完成, 数据库: {}, 请求返回有效数据: {} ", allRecordCount, total);
    }

    /**
     * 创建通用的请求 SteamWebApi 并处理返回数据的 futureTask
     * @param parameter 入参
     * @param concurrentBucket 线程安全的容器
     * @return 异步任务
     */
    @Override
    public CompletableFuture<Void> generateDataFetcherFuture(
            R parameter,
            BiFunction<List<S>, R, List<S>> dataDecorator,
            Collection<S> concurrentBucket) {
        return CompletableFuture
                .supplyAsync(() ->
                        httpUtils.getAsObject(
                                getSteamWebApiUrl(parameter),
                                getHttpRequestCaption(),
                                getResponseClass(),
                                this::requestFailedHandler,
                                this::requestErrorHandler
                        ), commonThreadPool)
                .thenApplyAsync(
                        this::getEntityFromResponse
                )
                .thenApplyAsync(result -> {
                    if (dataDecorator != null) {
                        return dataDecorator.apply(result, parameter);
                    }
                    return result;
                })
                .thenAcceptAsync(result -> {
                    if (!result.isEmpty()) {
                        concurrentBucket.addAll(result);
                    }
                })
                .handleAsync((unused, throwable) -> {
                    if (throwable != null) {
                        getLogger().error("Something went wrong: " + throwable.getMessage(), throwable);
                    }
                    return unused;
                });
    }

    @SuppressWarnings("unchecked")
    public List<S> getEntityFromResponse(SteamResponse response) {
        final List<S> emptyList = List.of();
        if (response == null) {
            return emptyList;
        }

        if (response instanceof GetAppListResponse r) {
            if (r.applist() != null && r.applist().apps() != null) {
                return (List<S>) r.applist().apps();
            }
        } else if (response instanceof GetPlayerSummariesResponse r) {
            if (r.response() != null && r.response().players() != null) {
                return (List<S>) r.response().players();
            }
        } else if (response instanceof GetFriendListResponse r) {
            if (r.friendslist() != null && r.friendslist().friends() != null) {
                return (List<S>) r.friendslist().friends();
            }
        } else {
            return emptyList;
        }
        return emptyList;
    }
}