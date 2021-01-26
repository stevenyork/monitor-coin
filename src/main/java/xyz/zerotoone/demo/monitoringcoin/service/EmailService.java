package xyz.zerotoone.demo.monitoringcoin.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.zerotoone.demo.monitoringcoin.domain.CoinDomain;
import xyz.zerotoone.demo.monitoringcoin.utils.EmailUtil;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

@Slf4j
@Service
public class EmailService implements PropertyChangeListener {
    private EmailUtil emailUtil;

    @Autowired
    public void setEmailUtil(EmailUtil emailUtil) {
        this.emailUtil = emailUtil;
    }

    public void notification(CoinDomain coin) {
        emailUtil.sendSimpleMessage(coin.getReceiver(),
                "达到预定价格",
                String.format("价格已经%s%s", coin.isLessThanOrEqual() ? "小于" : "大于", coin.getPrice()));
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        log.info("property change begin action");
        notification((CoinDomain) evt.getNewValue());
        log.info("property change end");
    }
}
