package global.redefine.watchdog.service.impl;

import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.LogContent;
import com.aliyun.openservices.log.common.LogItem;
import com.aliyun.openservices.log.common.QueriedLog;
import com.aliyun.openservices.log.request.GetLogsRequest;
import com.aliyun.openservices.log.response.GetLogsResponse;
import global.redefine.watchdog.config.LogHubConfig;
import global.redefine.watchdog.service.LogHubService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service("LogHubService")
public class LogHubServiceImpl implements LogHubService {

    Logger LOG = LoggerFactory.getLogger(LogHubServiceImpl.class);

    @Resource
    private Client client;

    @Resource
    private LogHubConfig logHubConfig;

    /**
     * 根据sql 查询当天数据
     *
     * @param query
     * @return
     */
    @Override
    public List<Map<Object, Object>> serach(String query) {
        List<Map<Object, Object>> result = serach(query, null, null);

        return result;
    }

    /**
     * 根据sql 查询当天数据
     *
     * @param query
     * @return
     */
    @Override
    public List<Map<Object, Object>> serach(String query, LocalDateTime start, LocalDateTime end) {
        List<Map<Object, Object>> result = new LinkedList<>();
        try {

            if (start == null || end == null) {
                start = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
                end = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

            }
            int from = (int) (start.toInstant(ZoneOffset.UTC).toEpochMilli() / 1000);
            int to = (int) (end.toInstant(ZoneOffset.UTC).toEpochMilli() / 1000);

            GetLogsResponse response = null;
            GetLogsRequest request = new GetLogsRequest("welike-business", "welike-service", from, to, "", query);
            response = client.GetLogs(request);
            if (response != null && response.IsCompleted()) {
                for (QueriedLog log : response.GetLogs()) {
                    Map<Object, Object> data = new HashMap<>();
                    LogItem item = log.GetLogItem();
                    for (LogContent content : item.GetLogContents()) {
                        data.put(content.mKey, content.mValue);
                    }
                    result.add(data);
                }
            }
        } catch (Exception e) {
        }
        return result;
    }
}
