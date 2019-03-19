package global.redefine.watchdog.service;

import java.util.Map;

/**
 * Created by luqiang on 2018/5/31.
 */
public interface ApplicationConfigService {

    /**
     * 获取应用数据列表
     */
    public Map<String, Object> getApps();


    /**
     * 获取服务配置
     */
    public String getAppConfigs(String instanceId);

    /**
     * DB 监控列表
     */
    public Map<String, Object> getDBList();

    /**
     * 获取应用监控地址数据信息
     */
    public String getMonitorHosts(String name);

    /**
     * 刷新服务配置
     *
     * @return
     */
    public boolean refreshConfig(String name, String ip);
}
