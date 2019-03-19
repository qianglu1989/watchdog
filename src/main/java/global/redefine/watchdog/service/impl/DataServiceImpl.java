package global.redefine.watchdog.service.impl;

import com.redefine.redis.utils.RedefineClusterUtils;
import global.redefine.watchdog.po.ChartModel;
import global.redefine.watchdog.service.DataService;
import global.redefine.watchdog.utils.GitUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service("dataService")
public class DataServiceImpl implements DataService {

    /**
     * 异常排行榜
     *
     * @return
     */
    @Override
    public Map<Object, Object> errorList() {

        Map<Object, Object> rt = new LinkedHashMap();


        try {
            String curr = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);

            Map<Object, Object> result = RedefineClusterUtils.hgetAll("exwarn:error:list:" + curr);

            if (result == null || result.size() == 0) {
                return rt;
            }
            Comparator<Map.Entry<Object, Object>> valueComparatordesc = (o1, o2) -> Integer.valueOf((String) o2.getValue()) - Integer.valueOf((String) o1.getValue());

            List<Map.Entry<Object, Object>> list = new ArrayList<>(result.entrySet());
            Collections.sort(list, valueComparatordesc);

            for (Map.Entry<Object, Object> entry : list) {
                rt.put(entry.getKey(), entry.getValue());
            }
        } catch (Exception e) {
        }

        return rt;
    }

    /**
     * 服务响应排行榜
     *
     * @return
     */
    @Override
    public Map<Object, Object> serviceList() {

        Map<Object, Object> rt = new LinkedHashMap();
        Map<String, String> config = GitUtils.CONFIG_DATA.get();
        String profile = config.get("redefine.ultron.profile");

        String curr = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);

        Map<Object, Object> data = RedefineClusterUtils.hgetAll("ultron:service:" + profile + ":" + curr);
        Map<Object, Object> result = new HashMap<>();
        //目前因为 zipkin sr ss问题所以数据需要折半
        data.forEach((k, v) -> {
            result.put(k, Integer.valueOf((String) v));
        });

        if (result == null || result.size() == 0) {
            return rt;
        }
        Comparator<Map.Entry<Object, Object>> valueComparatordesc = (o1, o2) -> (Integer) o2.getValue() - (Integer) o1.getValue();

        List<Map.Entry<Object, Object>> list = new ArrayList<>(result.entrySet());
        Collections.sort(list, valueComparatordesc);

        for (Map.Entry<Object, Object> entry : list) {
            rt.put(entry.getKey(), entry.getValue());
        }

        return rt;
    }


    /**
     * 接口调用排行榜
     *
     * @return
     */
    @Override
    public Map<Object, Object> interfaceTop(String name, String date) {

        Map<Object, Object> rt = new LinkedHashMap();
        Map<String, String> config = GitUtils.CONFIG_DATA.get();
        String profile = config.get("redefine.ultron.profile");


        Map<Object, Object> data = RedefineClusterUtils.hgetAll("ultron:interface:" + profile + ":" + date + ":" + name);
        Map<Object, Object> result = new HashMap<>();

        data.forEach((k, v) -> {
            result.put(k, Integer.valueOf((String) v));
        });

        if (result == null || result.size() == 0) {
            return rt;
        }
        Comparator<Map.Entry<Object, Object>> valueComparatordesc = (o1, o2) -> (Integer) o2.getValue() - (Integer) o1.getValue();

        List<Map.Entry<Object, Object>> list = new ArrayList<>(result.entrySet());
        Collections.sort(list, valueComparatordesc);

        for (Map.Entry<Object, Object> entry : list) {
            rt.put(entry.getKey(), entry.getValue());
        }

        return rt;
    }

    /**
     * 接口调用周图，根据应用显示本周接口图
     * 根据不同项目 不同时间段获取数据信息
     *
     * @param name 应用
     * @return
     */
    @Override
    public ChartModel interfaceChart(String name, String face) {

        Map<String, String> config = GitUtils.CONFIG_DATA.get();
        String profile = config.get("redefine.ultron.profile");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        ZonedDateTime indDate = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"));
        LinkedList<Object> x = new LinkedList<>();
        LinkedList<Object> y = new LinkedList<>();
        ChartModel model = new ChartModel();
        for (int i = 1; i < 14; i++) {
            ZonedDateTime time = indDate.minusDays(i);
            String day = time.format(formatter);

            Map<Object, Object> data = RedefineClusterUtils.hgetAll("ultron:interface:" + profile + ":" + day + ":" + name);

            if (MapUtils.isEmpty(data)) {
                x.add(0);
                y.add(day);
                continue;
            }

            data.forEach((k, v) -> {
                if (face.equals(k)) {
                    y.add(day);
                    x.add(v);
                }
            });

        }
        Collections.reverse(x);
        Collections.reverse(y);
        model.setName(face);
        model.setXaxis(x);
        model.setYaxis(y);

        return model;
    }
}
