package global.redefine.watchdog.service;

import java.util.List;
import java.util.Map;

public interface VersionService {


    /**
     * 添加应用配置信息
     */
    public boolean addVersionConfig(String appName, String gitUrl);

    /**
     * 获取应用版本列表
     */
    public List<Map<Object, Object>> getVersionList();

    /**
     * 检测版本信息
     */
    public boolean checkVersion(String name);


    /**
     * 检测版本信息
     */
    public boolean checkVersion();
}
