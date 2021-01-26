package xyz.zerotoone.demo.monitoringcoin.domain;

import lombok.Data;
import xyz.zerotoone.demo.monitoringcoin.constant.CoinStatusEnum;

import java.math.BigDecimal;

@Data
public class CoinDomain {
    private int id;
    private String name;
    private BigDecimal price;
    private boolean lessThanOrEqual;
    private CoinStatusEnum status;
    private String receiver;
    private long createdAt;
}
