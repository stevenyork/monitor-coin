package xyz.zerotoone.demo.monitoringcoin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.zerotoone.demo.monitoringcoin.domain.CoinDomain;
import xyz.zerotoone.demo.monitoringcoin.service.MonitorService;

import java.util.List;

@RequestMapping("/monitor")
@RestController
public class MonitorController {
    private MonitorService monitorService;

    @Autowired
    public void setMonitorService(MonitorService monitorService) {
        this.monitorService = monitorService;
    }

    @GetMapping("/coins")
    public List<CoinDomain> getAll() {
        return monitorService.getAllMonitor();
    }

    @PostMapping("/add")
    public int add(@RequestBody CoinDomain coinDomain) {
        return monitorService.addMonitor(coinDomain);
    }

    @PutMapping("/stop/{id}")
    public void stop(@PathVariable int id) {
        monitorService.stopMonitor(id);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable int id) {
        monitorService.deleteMonitor(id);
    }
}
