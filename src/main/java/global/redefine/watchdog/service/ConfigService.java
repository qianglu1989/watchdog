package global.redefine.watchdog.service;

import java.util.List;
import java.util.Map;

/**
 * Created by luqiang on 2018/5/14.
 */
public interface ConfigService {

    /**
     * 获取应用服务列表
     *
     * @return
     */
    public List<String> getServices(boolean type);

    /**
     * 获取应用配置详情
     *
     * @param name 应用配置文件名称
     * @return
     */
    public Map<String, String> getServiceConfig(String name);

    /**
     * 更新配置文件
     *
     * @param name
     * @param datas
     * @return
     */
    public boolean updateConfig(String name, Map<String, String> datas, String token);


    /**
     * 刷新服务配置
     *
     * @return
     */
    public boolean refreshConfig();
}
