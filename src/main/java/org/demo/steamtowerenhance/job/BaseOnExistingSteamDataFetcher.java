package org.demo.steamtowerenhance.job;

import okhttp3.Request;
import okhttp3.Response;
import org.demo.steamtowerenhance.domain.DatabaseEntity;
import org.demo.steamtowerenhance.dto.steamresponse.SteamResponse;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

/**
 * @param <T> DB 查出的参数类型(通常是String)
 * @param <R> 用于 HTTP 请求的参数类型
 * @param <S> 数据库实体类型
 * @param <U> SteamWebApi 返回类型
 */
public interface BaseOnExistingSteamDataFetcher<T, R, S extends DatabaseEntity, U extends SteamResponse> {
    int getDatabaseQueryPageSize();

    int getMaxCountInRound();

    String getHttpRequestCaption();

    int countBasicDataRecords();

    List<T> findBasicDataByPage(int offset, int pageSize);

    CompletableFuture<Void> generateDataFetcherFuture(R parameter, BiFunction<List<S>, R, List<S>> dataDecorator, Collection<S> concurrentBucket);

    void insertData(Collection<S> data);

    Logger getLogger();

    String getSteamWebApiUrl(Object ... params);

    Class<U> getResponseClass();

    void requestErrorHandler(Request request, Throwable throwable);
    U requestFailedHandler(Response response);
}