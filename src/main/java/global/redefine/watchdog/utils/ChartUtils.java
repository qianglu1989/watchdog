package global.redefine.watchdog.utils;

import com.redefine.redis.utils.RedefineClusterUtils;
import global.redefine.watchdog.po.ChartModel;
import org.apache.commons.collections4.MapUtils;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;

public class ChartUtils {

    /**
     * 处理图表数据
     *
     * @param chartName 图表名称
     * @param key       查询数据key
     * @param indDate   当前相对时间
     * @return
     */
    public static ChartModel dispose(String chartName, String key, ZonedDateTime indDate) {


        ZonedDateTime preTime = indDate.minusHours(1);
        int minute = preTime.getMinute();
        int hour = preTime.getHour();

        double base = 0;
        Map<Object, Object> datas = RedefineClusterUtils.hgetAll(key);

        LinkedList<Object> xaxis = new LinkedList();
        LinkedList<Object> yaxis = new LinkedList();

        ChartModel chartModel = new ChartModel();

        int max = 60;
        for (int i = 0; i < max; i++) {

            if (minute >= 60) {

                minute = minute - 60;
                hour = hour + 1;
            }

            //进行60min处理

            String finalHour = String.valueOf(hour);
            String finalMin = String.valueOf(minute).length() == 1 ? ("0" + minute) : String.valueOf(minute);
            String name = finalHour + ":" + finalMin;
            Object obj = datas.get(name);
            Object count = obj == null ? "0" : obj;
            Double c = Double.valueOf((String) count);
            xaxis.add(name);
            yaxis.add(c);
            base += c;
            minute++;
        }

        if (base == 0) {
            return null;
        }
        chartModel.setName(chartName);
        chartModel.setXaxis(xaxis);
        chartModel.setYaxis(yaxis);
        return chartModel;
    }


    public static String getCurrHour() {
        ZonedDateTime indDate = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"));

        int minute = indDate.getMinute();
        int hour = indDate.getHour();

        if (minute >= 60) {

            minute = minute - 60;
            hour = hour + 1;
        }

        String mini = hour + ":" + (minute >= 10 ? minute : "0" + minute);
        return mini;
    }

    public static ChartModel getOneDay(String chartName, String key) {

        Map<Object, Object> datas = RedefineClusterUtils.hgetAll(key);

        LinkedList<Object> xaxis = new LinkedList();
        LinkedList<Object> yaxis = new LinkedList();
        double base = 0;
        ChartModel chartModel = new ChartModel();
        chartModel.setName(chartName);

        int minute = 0;
        int hour = 0;
        boolean flag = false;

        while (!flag) {

            if (minute >= 60) {

                minute = minute - 60;
                hour = hour + 1;
            }
            minute++;
            String finalHour = String.valueOf(hour);
            String finalMin = String.valueOf(minute).length() == 1 ? ("0" + minute) : String.valueOf(minute);
            String name = finalHour + ":" + finalMin;
            Object obj = datas.get(name);
            Object count = obj == null ? "0" : obj;
            Double c = Double.valueOf((String) count);
            xaxis.add(name);
            yaxis.add(c);
            base += c;

            if (hour == 23 && minute == 59) {
                flag = true;
            }

        }
        if (base == 0) {
            return null;
        }
        chartModel.setName(chartName);
        chartModel.setXaxis(xaxis);
        chartModel.setYaxis(yaxis);
        return chartModel;

    }


    public static ChartModel getWeekDay(String chartName, String key) {


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        ZonedDateTime indDate = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"));
        LinkedList<Object> xaxis = new LinkedList();
        LinkedList<Object> yaxis = new LinkedList();
        ChartModel chartModel = new ChartModel();
        Integer base = 0;
        for (int i = 1; i < 14; i++) {

            ZonedDateTime time = indDate.minusDays(i);
            String day = time.format(formatter);

            Map<Object, Object> data = RedefineClusterUtils.hgetAll(key + ":" + day);

            Object count = 0;

            if (MapUtils.isNotEmpty(data)) {
                count = data.values().stream().reduce(0, (sum, item) -> {
                    Integer s = (Integer) sum;
                    Integer it = Integer.valueOf((String) item);
                    return s + it;
                });
            }
            Integer c = (Integer) count;
            xaxis.add(day);
            yaxis.add(c);
            base += c;
        }


        if (base == 0) {
            return null;
        }
        Collections.reverse(xaxis);
        Collections.reverse(yaxis);
        chartModel.setName(chartName);
        chartModel.setXaxis(xaxis);
        chartModel.setYaxis(yaxis);
        return chartModel;

    }
}
