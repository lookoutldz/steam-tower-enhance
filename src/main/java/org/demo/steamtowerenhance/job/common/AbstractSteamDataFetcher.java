package org.demo.steamtowerenhance.job.common;

import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public abstract class AbstractSteamDataFetcher implements SteamDataFetcher {
    public void fetchApps() {
        throw new RuntimeException("Not implemented yet.");
    }

    public void fetchPlayers() {
        throw new RuntimeException("Not implemented yet.");
    }

    public void fetchFriends() {
        throw new RuntimeException("Not implemented yet.");
    }

    public void fetchOwnedGames() {
        throw new RuntimeException("Not implemented yet.");
    }

    public void fetchGameSchemas() {
        throw new RuntimeException("Not implemented yet.");
    }

    public void fetchPlayerAchievements() {
        throw new RuntimeException("Not implemented yet.");
    }

    // 将 list 按给定 size 分组
    protected <T> List<List<T>> separateList(List<T> list, int groupSize) {
        int size = list.size();
        int partitionCount = (size + groupSize - 1) / groupSize;
        return IntStream.range(0, partitionCount)
                .mapToObj(i -> list.subList(i * groupSize, Math.min((i + 1) * groupSize, size)))
                .collect(toList());
    }

    // 计算集合容器的初始容量
    protected int getCapacity(int actual) {
        return (int) (actual / 0.75) + 1;
    }
}