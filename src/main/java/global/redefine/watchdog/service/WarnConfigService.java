package global.redefine.watchdog.service;

import java.util.List;
import java.util.Map;

/**
 * Created by luqiang on 2018/5/16.
 */
public interface WarnConfigService {

    /**
     * 获取报警服务配置列表
     *
     * @return
     */
    public List<String> getWarnServices();

    /**
     * 更新应用报警配置
     */
    public boolean updateWarnConfig(String data,String token);

    /**
     * 获取报警配置详情
     */
    public Map<String, String> getWarnConfigs(String name);
}
