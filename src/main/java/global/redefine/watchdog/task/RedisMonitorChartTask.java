package global.redefine.watchdog.task;


import global.redefine.watchdog.service.RedisMonitorService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class RedisMonitorChartTask {


    @Resource
    private RedisMonitorService redisMonitorService;


    /**
     * 用于计算redis每分钟数据
     */
    @Scheduled(fixedDelayString = "10000")
    public void redishartData(){

        redisMonitorService.chartData();

    }
}
