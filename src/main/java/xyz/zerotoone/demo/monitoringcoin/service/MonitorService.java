package xyz.zerotoone.demo.monitoringcoin.service;

import org.springframework.stereotype.Service;
import xyz.zerotoone.demo.monitoringcoin.constant.CoinStatusEnum;
import xyz.zerotoone.demo.monitoringcoin.domain.CoinDomain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class MonitorService {
    private final AtomicInteger number = new AtomicInteger(1);
    private final Map<Integer, CoinDomain> activeList = new ConcurrentHashMap<>();
    private final Map<Integer, CoinDomain> archiveList = new ConcurrentHashMap<>();

    public Map<Integer, CoinDomain> getActiveList() {
        return activeList;
    }

    public Map<Integer, CoinDomain> getArchiveList() {
        return archiveList;
    }

    public int addMonitor(CoinDomain coinDomain) {
        int id = number.getAndIncrement();
        coinDomain.setId(id);
        coinDomain.setStatus(CoinStatusEnum.Operating);
        coinDomain.setCreatedAt(System.currentTimeMillis());
        activeList.put(id, coinDomain);
        return id;
    }

    public void deleteMonitor(int id) {
        hasKeyInActive(id);

        activeList.remove(id);
    }

    public void stopMonitor(int id) {
        hasKeyInActive(id);

        CoinDomain coin = activeList.get(id);
        coin.setStatus(CoinStatusEnum.SUSPENDED);
    }

    public void finishMonitor(int id) {
        hasKeyInActive(id);

        CoinDomain coin = activeList.get(id);
        activeList.remove(id);
        coin.setStatus(CoinStatusEnum.FINISHED);
        archiveList.put(id, coin);
    }

    public List<CoinDomain> getAllMonitor() {
        List<CoinDomain> result = new ArrayList<>();
        result.addAll(activeList.values());
        result.addAll(archiveList.values());
        return result.parallelStream().sorted(Comparator.comparingLong(CoinDomain::getCreatedAt)).collect(Collectors.toList());
    }

    private void hasKeyInActive(int id) {
        hasKey(activeList.containsKey(id));
    }

    private void hasKeyInArchive(int id) {
        hasKey(archiveList.containsKey(id));
    }

    private void hasKey(boolean exist) {
        if (!exist) {
            throw new IllegalArgumentException();
        }
    }
}
