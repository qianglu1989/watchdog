package global.redefine.watchdog.service.impl;

import com.alibaba.fastjson.JSON;
import com.redefine.redis.utils.RedefineClusterUtils;
import global.redefine.watchdog.po.ChartModel;
import global.redefine.watchdog.service.RedisMonitorService;
import global.redefine.watchdog.utils.ChartUtils;
import global.redefine.watchdog.utils.GitUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 获取redis 集群监控信息
 */
@Service("redisMonitorService")
public class RedisMonitorServiceImpl implements RedisMonitorService {


    private Logger LOG = LoggerFactory.getLogger(RedisMonitorServiceImpl.class);

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 获取集群基础信息
     *
     * @return
     */
    @Override
    public Map<String, String> getClusterInfo(String host) {

        Map<String, String> config = GitUtils.CONFIG_DATA.get();
        String pwd = config.get("redefine.watchdog.redis.pwd");

        if (StringUtils.isEmpty(host)) {
            String rh = config.get("redefine.watchdog.redis.hosts");
            String[] hosts = rh.split(",");
            host = hosts[0];
        }

        LOG.info("hosts:{},pwd:{}", host, pwd);
        return getInfo(host, pwd);
    }

    /**
     * 获取集群hosts
     *
     * @return
     */
    @Override
    public String[] getHosts() {


        Map<String, String> config = GitUtils.CONFIG_DATA.get();
        String rh = config.get("redefine.watchdog.redis.hosts");

        String[] hosts = rh.split(",");
        return hosts;

    }

    /**
     * 获取集群信息
     *
     * @return
     */
    public Map<String, String> getInfo(String host, String pwd) {


        Map<String, String> result = new HashMap<>();


        Jedis jedis = null;
        try {
            String[] hp = host.split(":");
            jedis = new Jedis(hp[0], Integer.valueOf(hp[1]));
            if (StringUtils.isNotEmpty(pwd)) {
                jedis.auth(pwd);
            }
            String[] server = jedis.info("Server").split("\r\n");
            server[0] = "server:0";
            Map<String, String> serverData = splitData(server);
            result.put("uptime", serverData.get("uptime_in_days"));


            String[] memory = jedis.info("Memory").split("\r\n");
            memory[0] = "memory:0";
            Map<String, String> memoryData = splitData(memory);
            result.put("used_memory", memoryData.get("used_memory_human"));

            //﻿其中，第一个XXX表示数据库的编号，第二个XXX表示键的数量，第三个XXX表示具有过期时间的键的数量，第四个XXX表示键的平均生存时间。
            String[] Keyspace = jedis.info("Keyspace").split("\r\n");
            String[] data = StringUtils.split(Keyspace[1], ",");
            String[] keys = StringUtils.split(data[0], "=");
            String[] expires = StringUtils.split(data[1], "=");
            result.put("keys", keys[1]);
            result.put("expires", expires[1]);

            String[] clients = jedis.info("Clients").split("\r\n");
            clients[0] = "Clients:0";
            Map<String, String> clientsData = splitData(clients);
            result.put("connected_clients", clientsData.get("connected_clients"));

            String[] stats = jedis.info("Stats").split("\r\n");
            stats[0] = "Stats:0";
            Map<String, String> statsData = splitData(stats);
            result.put("ops", statsData.get("instantaneous_ops_per_sec"));
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
        return result;
    }

    /**
     * 获取redis memory监控图表数据
     *
     * @param host
     * @return
     */
    @Override
    public ChartModel getMemoryChartData(String host) {
        Map<String, String> config = GitUtils.CONFIG_DATA.get();
        String pwd = config.get("redefine.watchdog.redis.pwd");
        String profile = config.get("profile");
        String project = config.get("project");

        if (StringUtils.isEmpty(host)) {
            String rh = config.get("redefine.watchdog.redis.hosts");
            String[] hosts = rh.split(",");
            host = hosts[0];
        }
        ZonedDateTime indDate = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"));
        LOG.info("hosts:{},pwd:{}", host, pwd);
        return ChartUtils.dispose(host, "ultron:redischartmemory:" + project + ":" + profile + ":" + host, indDate);

    }

    /**
     * 获取redis ops
     *
     * @param host
     * @return
     */
    @Override
    public ChartModel getOpsChartData(String host) {
        Map<String, String> config = GitUtils.CONFIG_DATA.get();
        String pwd = config.get("redefine.watchdog.redis.pwd");
        String profile = config.get("profile");
        String project = config.get("project");

        if (StringUtils.isEmpty(host)) {
            String rh = config.get("redefine.watchdog.redis.hosts");
            String[] hosts = rh.split(",");
            host = hosts[0];
        }
        ZonedDateTime indDate = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"));
        LOG.info("hosts:{},pwd:{}", host, pwd);
        return ChartUtils.dispose(host, "ultron:redischartops:" + project + ":" + profile + ":" + host, indDate);

    }

    private Map<String, String> splitData(String[] datas) {
        Map<String, String> result = new HashMap<>();
        if (datas == null | datas.length == 0) {
            return result;
        }

        for (String data : datas) {
            try {
                String[] rd = data.split(":");
                result.put(rd[0], rd[1]);
            } catch (Exception e) {
                LOG.error("splitData error:{}", e.getMessage());
            }

        }
        return result;
    }

    /**
     * 统计redis图表数据
     */
    @Override
    public void chartData() {
        String mini = ChartUtils.getCurrHour();

        //获取环境信息
        Set<byte[]> profiles = RedefineClusterUtils.sMembers("watchdog:profile:all");
        for (byte[] bytes : profiles) {
            try {
                String project = new String(bytes, "UTF-8");
                Map<Object, Object> profile = RedefineClusterUtils.hgetAll("watchdog:profile:" + project);
                //获取每个环境信息
                profile.forEach((k, v) -> {

                    try {
                        String data = String.valueOf(v);
                        Map<Object, Object> pf = JSON.parseObject(data, Map.class);
                        Object hosts = pf.get("redefine.watchdog.redis.hosts");
                        Object pwd = pf.get("redefine.watchdog.redis.pwd");
                        if (hosts != null) {
                            String[] ips = String.valueOf(hosts).split(",");
                            for (String ip : ips) {
                                Jedis jedis = null;
                                try {
                                    String[] hp = ip.split(":");
                                    jedis = new Jedis(hp[0], Integer.valueOf(hp[1]));
                                    if (pwd != null) {
                                        jedis.auth(String.valueOf(pwd));
                                    }
                                    String[] server = jedis.info("Server").split("\r\n");
                                    server[0] = "server:0";
                                    Map<String, String> serverData = splitData(server);


                                    String[] memory = jedis.info("Memory").split("\r\n");
                                    memory[0] = "memory:0";
                                    Map<String, String> memoryData = splitData(memory);
                                    String human = memoryData.get("used_memory_human");
                                    String memo = human.substring(0, human.length() - 1);
                                    RedefineClusterUtils.hashSet("ultron:redischartmemory:" + project + ":" + k + ":" + ip, mini, memo);


                                    String[] stats = jedis.info("Stats").split("\r\n");
                                    stats[0] = "Stats:0";
                                    Map<String, String> statsData = splitData(stats);
                                    RedefineClusterUtils.hashSet("ultron:redischartops:" + project + ":" + k + ":" + ip, mini, statsData.get("instantaneous_ops_per_sec"));

                                } finally {
                                    if (null != jedis) {
                                        jedis.close();
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        LOG.error("forEach profile data error:{}", e.getMessage());
                    }

                });
            } catch (Exception e) {
                LOG.error("chartData error:{}", e.getMessage());
            }
        }

    }


}
