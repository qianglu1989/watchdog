package global.redefine.watchdog.controller;

import com.redefine.redis.utils.RedefineClusterUtils;
import global.redefine.watchdog.po.ChartModel;
import global.redefine.watchdog.service.KafkaCheckService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * kafka管理
 * Created by luqiang on 2018/5/14.
 *
 * @author QIANG
 */
@RestController
@RequestMapping("/kafka")
@CrossOrigin
public class KafkaCheckController {

    Logger LOG = LoggerFactory.getLogger(KafkaCheckController.class);


    @Resource
    private KafkaCheckService kafkaCheckService;


    /**
     * 添加集群信息
     *
     * @param bootStrap 添加一个ip即可
     * @return
     */
    @RequestMapping(value = "/addClustrInfo", method = RequestMethod.GET)
    public boolean addClustrInfo(@RequestParam String name, @RequestParam String bootStrap) {
        return kafkaCheckService.addClustrInfo(name, bootStrap);
    }

    @RequestMapping(value = "/deleteCluster", method = RequestMethod.GET)
    public boolean deleteCluster(@RequestParam String name) {
        return kafkaCheckService.deleteCluster(name);
    }

    /**
     * @param clusterName 集群代号
     * @return
     */
    @RequestMapping(value = "/getKafkaClusterInfo", method = RequestMethod.GET)
    public Map<String, Object> getKafkaClusterInfo(@RequestParam String clusterName) {

        return kafkaCheckService.getKafkaClusterInfo(clusterName);
    }

    @RequestMapping(value = "/addmonitor", method = RequestMethod.GET)
    public boolean addmonitor(@RequestParam String clusterName, @RequestParam String groupId, @RequestParam String topics, @RequestParam(required = false, defaultValue = "500") String limit) {

        return kafkaCheckService.addmonitor(clusterName, groupId, topics, limit);
    }

    /**
     * 获取集群信息
     *
     * @return
     */
    @RequestMapping(value = "/getClustrInfo", method = RequestMethod.GET)
    public List<String> getClustrInfo() {

        Map<Object, Object> cluster = RedefineClusterUtils.hgetAll("wathcdog:kafkacheck");

        List<String> result = new LinkedList<>();
        cluster.forEach((k, v) -> {
            result.add(String.valueOf(k));
        });
        return result;
    }


    @RequestMapping(value = "/getMonitorDatas", method = RequestMethod.GET)
    public List<Map<Object, Object>> getMonitorDatas() {

        return kafkaCheckService.getMonitorDatas();
    }

    @RequestMapping(value = "/delMonitor", method = RequestMethod.GET)
    public boolean delMonitor(@RequestParam String clusterName, @RequestParam String groupId) {

        return kafkaCheckService.delMonitor(clusterName, groupId);
    }

    @RequestMapping(value = "/getChartByTopic", method = RequestMethod.GET)
    public ChartModel getChartByTopic(@RequestParam String clusterName, @RequestParam String topic) {

        return kafkaCheckService.getChartByTopic(clusterName, topic);
    }

}
