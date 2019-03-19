package global.redefine.watchdog.service.impl;

import com.alibaba.fastjson.JSON;
import com.redefine.redis.utils.RedefineClusterUtils;
import global.redefine.watchdog.po.ChartModel;
import global.redefine.watchdog.service.AppChartService;
import global.redefine.watchdog.utils.ChartUtils;
import global.redefine.watchdog.utils.GitUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
@Deprecated
@Service("appChartService")
public class AppChartServiceImpl implements AppChartService {

    private Logger LOG = LoggerFactory.getLogger(AppChartServiceImpl.class);


    /**
     * 获取图表基础参数
     *
     * @param serviceName 如果不传，并且t
     * @param clazz       类名称只有在查询方法名称时候需要
     * @return
     */
    @Override
    public Set<String> getBaseData(String serviceName, String clazz) {
        Map<String, String> config = GitUtils.CONFIG_DATA.get();
        String profile = config.get("redefine.ultron.profile");
        Set<byte[]> services = RedefineClusterUtils.sMembers("ultron:charts:" + profile + ":all");

        Set<String> result = new HashSet<>();
        for (byte[] bytes : services) {
            try {
                result.add(new String(bytes, "UTF-8"));
            } catch (UnsupportedEncodingException e) {

            }
        }

        return result;
    }


    /**
     * 添加数据图表
     *
     * @param chartName ultron:testController:test
     * @return
     */
    @Override
    public boolean addChartData(String chartName) {
        Map<String, String> config = GitUtils.CONFIG_DATA.get();
        String profile = config.get("redefine.ultron.profile");
        RedefineClusterUtils.sAdd("ultron:apps:" + profile + ":current", chartName);
        return true;
    }

    /**
     * 删除数据图表
     *
     * @param chartName ultron:testController:test
     * @return
     */
    @Override
    public boolean deleteChartData(String chartName) {

        boolean result = false;
        try {
            Map<String, String> config = GitUtils.CONFIG_DATA.get();
            String profile = config.get("redefine.ultron.profile");
            RedefineClusterUtils.sRem("ultron:apps:" + profile + ":current", chartName);
            result = true;

        } catch (Exception e) {
            LOG.error("deleteChartData error:{}", e);
        }
        return result;
    }

    /**
     * 查询图表数据
     *
     * @param chartName
     * @param type      代表获取图表数据的时间区间，0 一小时内，1 24小时，2一周
     * @return
     */
    @Override
    public List<ChartModel> getChartData(String chartName, String type, String date) {


        List<ChartModel> result = new LinkedList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        ZonedDateTime indDate = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"));

        if (StringUtils.isEmpty(date)) {
            date = indDate.format(formatter);
        }

        Map<String, String> config = GitUtils.CONFIG_DATA.get();
        String profile = config.get("redefine.ultron.profile");

        Set<byte[]> sets = new HashSet<>();
        sets.add(chartName.getBytes());
        if (StringUtils.isEmpty(chartName)) {
            sets.addAll(RedefineClusterUtils.sMembers("ultron:apps:" + profile + ":current"));
        }

        for (byte[] bytes : sets) {

            try {
                String dc = new String(bytes, "UTF-8");
                if (StringUtils.isEmpty(dc)) {
                    continue;
                }
                String key = "smchart:" + profile + ":" + dc + ":" + date;

                ChartModel data = null;

                switch (type) {
                    case "0":
                        data = ChartUtils.dispose(dc, key, indDate);
                        break;
                    case "1":
                        data = ChartUtils.getOneDay(dc, key);
                        break;
                    case "2":
                        String cache = RedefineClusterUtils.getString("watchdog:flow:week:" + dc);
                        if (StringUtils.isNotEmpty(cache)) {
                            data = JSON.parseObject(cache, ChartModel.class);
                            break;
                        }
                        data = ChartUtils.getWeekDay(dc, "smchart:" + profile + ":" + dc);

                        RedefineClusterUtils.saveString("watchdog:flow:week:" + dc, JSON.toJSONString(data), 1, TimeUnit.HOURS);

                        break;
                    default:
                        break;

                }
                if (data != null) {
                    result.add(data);
                }


            } catch (UnsupportedEncodingException e) {
                LOG.error("getChartData ERROR:{}", e);

            }
        }

        Collections.sort(result, Comparator.comparing(ChartModel::getName));
        return result;
    }









}
