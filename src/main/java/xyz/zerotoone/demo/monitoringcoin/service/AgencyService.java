package xyz.zerotoone.demo.monitoringcoin.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.zerotoone.demo.monitoringcoin.domain.CoinDomain;

import javax.annotation.PostConstruct;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

@Slf4j
@Service
public class AgencyService {
    private CoinDomain coinDomain;
    private PropertyChangeSupport support;
    private EmailService emailService;

    public AgencyService() {
        support = new PropertyChangeSupport(this);
    }

    @Autowired
    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        support.removePropertyChangeListener(pcl);
    }

    public void setNews(CoinDomain value) {
        support.firePropertyChange("coin", this.coinDomain, value);
        this.coinDomain = value;
    }

    @PostConstruct
    public void init() {
        log.info("初始化代理者的监听器");
        support.addPropertyChangeListener(emailService);
        log.info("完成初始化代理者的监听器");
    }
}
