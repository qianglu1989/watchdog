package global.redefine.watchdog.service;

import java.util.Map;

/**
 * Created by luqiang on 2018/7/6.
 * @author QIANG
 */
public interface AppManagerService {

    /**
     * 获取应用数据列表
     */
    public Map<String, Object> getMetaData();

    /**
     * 更新mete配置信息
     * @param app
     * @param instanceId
     * @return
     */
    public boolean updateMetaData(String app,String instanceId,String weight);
}
