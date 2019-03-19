package global.redefine.watchdog.service;

import java.util.Map;
import java.util.Set;

public interface ProfileService {

    /**
     * 创建项目条目
     * @param pjName
     * @param prName
     * @param data
     * @return
     */
    public boolean createProfile(String pjName,String prName,String data);

    /**
     * 获取项目配置
     *
     * @return
     */
    public Map<String, Set<Object>> getProfile();


    /**
     * 获取环境详细数据
     *
     * @param pjName
     * @param prName
     * @return
     */
    public Object getDetaile(String pjName, String prName);

    /**
     * 获取环境详细数据
     *
     * @param pjName
     * @param prName
     * @return
     */
    public boolean deleteProfile(String pjName, String prName);


}
