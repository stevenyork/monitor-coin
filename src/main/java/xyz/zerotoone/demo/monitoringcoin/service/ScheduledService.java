package xyz.zerotoone.demo.monitoringcoin.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import xyz.zerotoone.demo.monitoringcoin.constant.CoinStatusEnum;
import xyz.zerotoone.demo.monitoringcoin.domain.CoinDomain;
import xyz.zerotoone.demo.monitoringcoin.domain.CoinPriceDomain;
import xyz.zerotoone.demo.monitoringcoin.exception.RequestException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ScheduledService {
    private final static String BASE_NAME_KEY = "USDT";

    private MonitorService monitorService;
    private AgencyService agencyService;

    @Autowired
    public void setMonitorService(MonitorService monitorService) {
        this.monitorService = monitorService;
    }

    @Autowired
    public void setAgencyService(AgencyService agencyService) {
        this.agencyService = agencyService;
    }

    @Scheduled(cron = "0 */1 * * * ?")
    public void monitor() {
        log.debug("begin scheduled task");
        priceTask();
        log.debug("end scheduled task");
    }

    public void priceTask() {
        Map<Integer, CoinDomain> activeList = monitorService.getActiveList();
        List<CoinDomain> activeTask = activeList.values().parallelStream()
                .filter((e) -> e.getStatus().equals(CoinStatusEnum.Operating)).collect(Collectors.toList());

        Map<String, BigDecimal> coinPrice = activeTask.parallelStream()
                .map(CoinDomain::getName)
                .distinct()
                .map((e) -> CompletableFuture.supplyAsync(() -> getPriceByBinance(e)))
                .map(CompletableFuture::join)
                .collect(Collectors.toMap((e) -> e.getSymbol().replace(BASE_NAME_KEY, ""), CoinPriceDomain::getPrice));

        activeTask.parallelStream().forEach((e) -> {
            BigDecimal currentPrice = coinPrice.get(e.getName().toUpperCase());
            int priceCompareResult = currentPrice.compareTo(e.getPrice());
            if ((priceCompareResult > 0 && !e.isLessThanOrEqual())
                    || (priceCompareResult <= 0 && e.isLessThanOrEqual())) {
                agencyService.setNews(e);
                monitorService.finishMonitor(e.getId());
            }
        });
    }

    /**
     * TODO: refactor the code
     * add connection pool
     */
    public CoinPriceDomain getPriceByBinance(String name) {
        String baseUrl = String.format("https://api.binance.com/api/v3/ticker/price?symbol=%s%s", name.toUpperCase(), BASE_NAME_KEY);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<CoinPriceDomain> response = restTemplate.getForEntity(baseUrl, CoinPriceDomain.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RequestException(response.getStatusCode().toString());
        }

        return response.getBody();
    }
}
