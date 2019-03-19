package global.redefine.watchdog.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface LogHubService {

    public List<Map<Object, Object>> serach(String query);

    /**
     * 根据sql 查询当天数据
     *
     * @param query
     * @return
     */
    public List<Map<Object, Object>> serach(String query, LocalDateTime start, LocalDateTime end);
}
