package xyz.zerotoone.demo.monitoringcoin.domain;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CoinPriceDomain {
    private String symbol;
    private BigDecimal price;
}
