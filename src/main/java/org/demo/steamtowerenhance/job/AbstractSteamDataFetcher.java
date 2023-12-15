package org.demo.steamtowerenhance.job;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSteamDataFetcher {
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
        final List<List<T>> groups;
        if (list.size() > groupSize) {
            final int groupCount = list.size() / groupSize + 1;
            groups = new ArrayList<>(getCapacity(groupCount));
            for (int i = 0, fromIndex, toIndex; i < groupCount; i++) {
                fromIndex = i * groupSize;
                toIndex = Math.min(list.size(), (i + 1) * groupSize);
                groups.add(list.subList(fromIndex, toIndex));
            }
        } else {
            groups = List.of(list);
        }
        return groups;
    }

    protected int getCapacity(int actual) {
        return (int) (actual / 0.75) + 1;
    }
}
