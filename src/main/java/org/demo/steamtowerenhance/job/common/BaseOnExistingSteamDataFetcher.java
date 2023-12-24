package org.demo.steamtowerenhance.job.common;

import org.demo.steamtowerenhance.domain.DatabaseEntity;
import org.demo.steamtowerenhance.dto.steamresponse.SteamResponse;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.List;

/**
 * @param <D> 数据库 domain 类型
 * @param <S> steamWebApi 返回体类型
 */
public interface BaseOnExistingSteamDataFetcher<D extends DatabaseEntity, S extends SteamResponse> {
    /**
     * 从 DB 查出必要信息组成本轮 fetch 的参数, 再组装成可直接访问的 steam web api url
     * @param offset 本轮查询 offset
     * @param pageSize 每次查询的最大条数
     * @return url string
     */
    List<String> pageableQueryDataAndGenerateUrl(Integer offset, Integer pageSize);

    /**
     * 用于数据加工的处理器
     * @param url 请求的 url
     * @param rawData 从 API 返回的数据, 已 map 到 DatabaseEntity 但尚未对数据改动
     * @return 可直接插入 DB 的数据
     */
    List<D> dataPostProcessor(String url, List<D> rawData);

    /**
     * 插入数据的逻辑
     * @param data 经过 dataPostProcessor 的数据
     */
    void insertData(Collection<D> data);

    /**
     * 每次查询的最大条数, 用于 pageableQueryDataAndGenerateUrl
     * @return int
     */
    Integer getSelectionPageSize();

    /**
     * S.class
     * @return Class
     */
    Class<S> getSteamResponseType();

    /**
     * 日志记录器
     * @return LOGGER
     */
    Logger getLogger();
}