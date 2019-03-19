package global.redefine.watchdog.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redefine.nove.http.HttpClientUtils;
import com.redefine.redis.utils.RedefineClusterUtils;
import global.redefine.watchdog.po.ChartModel;
import global.redefine.watchdog.service.HomeService;
import global.redefine.watchdog.service.LogHubService;
import global.redefine.watchdog.utils.ChartUtils;
import global.redefine.watchdog.utils.GitUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service("homeService")
public class HomeServiceImpl implements HomeService {

    Logger LOG = LoggerFactory.getLogger(HomeServiceImpl.class);

    @Resource
    private LogHubService logHubService;


    /**
     * 获取首页基础数据
     *
     * @return
     */
    @Override
    public Map<String, Object> getHomeData() {
        Map<String, String> config = GitUtils.CONFIG_DATA.get();
        String defaultZone = config.get("redefine.eureka.defaultZone");
        String project = config.get("project");

        Map<String, Object> result = new LinkedHashMap<>();
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        long count = 0;
        try {

            String datas = HttpClientUtils.doGet(defaultZone + "/apps", new HashMap<>(), "UTF-8", headers);
            Map<String, JSONObject> maps = JSON.parseObject(datas, Map.class);
            JSONObject jsonObject = maps.get("applications");
            JSONArray jsonArray = (JSONArray) jsonObject.get("application");
            result.put("appcount", jsonArray.size());

            Set<String> instanceids = new HashSet<>();
            Set<String> ips = new HashSet<>();
            Set<String> apps = new HashSet<>();
            jsonArray.forEach(obj -> {

                JSONObject o = (JSONObject) obj;
                String name = (String) o.get("name");
                apps.add(name);
                JSONArray instances = (JSONArray) o.get("instance");
                instances.forEach(ins -> {
                    JSONObject instance = (JSONObject) ins;
                    String addr = (String) instance.get("ipAddr");
                    String instanceId = (String) instance.get("instanceId");
                    ips.add(addr);
                    instanceids.add(instanceId);
                });
            });

            result.put("ips", ips.size());
            result.put("instances", instanceids.size());

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            ZonedDateTime indDate = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"));
            String day = indDate.format(formatter);
            Set<byte[]> serviceNames = RedefineClusterUtils.sMembers("ultron:charts:" + project + ":all");
            for (byte[] bytes : serviceNames) {
                try {
                    String name = new String(bytes, "UTF-8");
                    Long c = RedefineClusterUtils.hLen("ultron:interface:" + project + ":" + day + ":" + name);
                    count += c;

                } catch (Exception e) {
                }
            }

            result.put("ifcount", count);
        } catch (Exception e) {
            LOG.error("getHomeData error:{}", e);
        }

        return result;
    }

    /**
     * 获取首页饼图
     *
     * @return
     */
    @Override
    public List<Map<Object, Object>> getPipData() {
        Map<String, String> config = GitUtils.CONFIG_DATA.get();
        String project = config.get("project");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        ZonedDateTime indDate = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"));
        String day = indDate.format(formatter);

        List<Map<Object, Object>> result = new LinkedList<>();
        Map<Object, Object> datas = RedefineClusterUtils.hgetAll("ultron:service:" + project + ":" + day);
        datas.forEach((k, v) -> {
            try {
                Map<Object, Object> pip = new LinkedHashMap<>();
                pip.put("value", v);
                pip.put("name", k);
                result.add(pip);
            } catch (Exception e) {
                LOG.error("getPipData error:{}", e);
            }

        });
        return result;
    }

    /**
     * 获取方法耗时排行
     *
     * @return
     */
    @Override
    public List<Map<Object, Object>> methodDuration() {

        List<Map<Object, Object>> data = logHubService.serach("* | select concat(name, '_',  uri) name , max(duration) duration group by name order by duration desc limit 20 ");

        List<Map<Object, Object>> result = new LinkedList<>();


        return data;


    }


    /**
     * 获取并发
     *
     * @return
     */
    @Override
    public List<Map<Object, Object>> concurrent() {
        List<Map<Object, Object>> data = logHubService.serach("* | select concat(name, '_',  uri) name , max(concurrent) concurrent group by name order by concurrent desc limit 20");

        List<Map<Object, Object>> result = new LinkedList<>();


        return data;

    }

    /**
     * 首页图表数据
     *
     * @return
     */
    @Override
    public List<ChartModel> getHomeChartData(String type, String date) {

        Map<String, String> config = GitUtils.CONFIG_DATA.get();
        String profile = config.get("redefine.ultron.profile");

        List<ChartModel> result = new LinkedList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        ZonedDateTime indDate = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"));

        if (StringUtils.isEmpty(date)) {
            date = indDate.format(formatter);
        }
        Set<byte[]> services = RedefineClusterUtils.sMembers("ultron:charts:" + profile + ":all");
        LOG.info("get ultron:charts:welike data size:{}", services.size());
        for (byte[] bytes : services) {

            try {
                String dc = new String(bytes, "UTF-8");
                if (StringUtils.isEmpty(dc)) {
                    continue;
                }
                String key = "ultron:charts:" + profile + ":" + dc + ":" + date;

                LOG.info("get ultron:charts:welike key :{}", key);
                ChartModel data = null;
                switch (type) {
                    case "0":
                        data = ChartUtils.dispose(dc, key, indDate);
                        break;
                    case "1":
                        data = ChartUtils.getOneDay(dc, key);
                        break;
                    case "2":
                        String cache = RedefineClusterUtils.getString("watchdog:homechart:week:" + dc);
                        if (StringUtils.isNotEmpty(cache)) {
                            data = JSON.parseObject(cache, ChartModel.class);
                            break;
                        }
                        data = ChartUtils.getWeekDay(dc, "ultron:charts:" + profile + ":" + dc);

                        RedefineClusterUtils.saveString("watchdog:homechart:week:" + dc, JSON.toJSONString(data), 1, TimeUnit.HOURS);

                        break;
                    default:
                        break;

                }

                if (data != null) {
                    result.add(data);
                }


            } catch (UnsupportedEncodingException e) {
                LOG.error("getHomeChartData ERROR:{}", e);

            }
        }


        Collections.sort(result, Comparator.comparing(ChartModel::getName));
        return result;
    }


    /**
     * 获取首页响应时长数据图表
     *
     * @param type
     * @param date
     * @return
     */
    @Override
    public List<ChartModel> getDurationChartData(String type, String date) {

        Map<String, String> config = GitUtils.CONFIG_DATA.get();
        String profile = config.get("redefine.ultron.profile");

        List<ChartModel> result = new LinkedList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        ZonedDateTime indDate = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"));

        if (StringUtils.isEmpty(date)) {
            date = indDate.format(formatter);
        }
        Set<byte[]> services = RedefineClusterUtils.sMembers("ultron:charts:" + profile + ":all");
        LOG.info("get ultron:charts:welike data size:{}", services.size());
        for (byte[] bytes : services) {

            try {
                String serive = new String(bytes, "UTF-8");
                if (StringUtils.isEmpty(serive)) {
                    continue;
                }
                String key = "jarvis:avgduration:" + profile + ":" + serive + ":" + date;

                LOG.info("get ultron:charts:welike key :{}", key);
                ChartModel data = null;
                switch (type) {
                    case "0":
                        data = ChartUtils.dispose(serive, key, indDate);
                        break;
                    case "1":
                        data = ChartUtils.getOneDay(serive, key);
                        break;
                    default:
                        break;

                }
                disposeData(data, serive, date);
                if (data != null) {
                    result.add(data);
                }


            } catch (UnsupportedEncodingException e) {
                LOG.error("getHomeChartData ERROR:{}", e);

            }
        }


        Collections.sort(result, Comparator.comparing(ChartModel::getName));
        return result;
    }

    private void disposeData(ChartModel chartModel, String service, String day) {

        LinkedList<Object> xaxis = chartModel.getXaxis();
        LinkedList<Object> yaxis = chartModel.getYaxis();
        LinkedList<Object> avgs = new LinkedList<>();
        for (int i = 0; i <= xaxis.size(); i++) {
            Object duration = yaxis.get(i);
            Object mini = xaxis.get(i);
            Object count = RedefineClusterUtils.hashGet("jarvis:avgcount:" + ":" + service + ':' + day, String.valueOf(mini));
            BigDecimal avg = new BigDecimal(Long.valueOf((String) duration)).divide(new BigDecimal(Long.valueOf((String) count)), 0, BigDecimal.ROUND_HALF_UP);
            avgs.add(avg.longValue());
        }
        chartModel.setYaxis(avgs);
    }
}
