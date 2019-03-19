package global.redefine.watchdog.service.impl;

import com.redefine.redis.utils.RedefineClusterUtils;
import global.redefine.watchdog.service.ProfileService;
import global.redefine.watchdog.utils.Constant;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


@Service("profileService")
public class ProfileServiceImpl implements ProfileService {

    private static Logger LOG = LoggerFactory.getLogger(ProfileServiceImpl.class);

    private static String LOCAL_PATH = "/data/redefine/code";

    /**
     * 创建项目条目
     *
     * @param pjName 项目标识
     * @param prName 环境标识
     * @param data
     * @return
     */
    @Override
    public boolean createProfile(String pjName, String prName, String data) {
        boolean result = true;
        try {
            RedefineClusterUtils.hashSet(Constant.WATCHDOG_PROFILE + pjName, prName, data);

            RedefineClusterUtils.sAdd(Constant.WATCHDOG_PROFILE_ALL, pjName);
        } catch (Exception e) {
            result = false;
            LOG.error("创建项目条目出现异常，{}", e);
        }

        return result;
    }

    /**
     * 获取项目配置
     *
     * @return
     */
    @Override
    public Map<String, Set<Object>> getProfile() {

        Set<byte[]> pjNames = RedefineClusterUtils.sMembers(Constant.WATCHDOG_PROFILE_ALL);

        Map<String, Set<Object>> result = new HashMap<>();
        for (byte[] data : pjNames) {
            try {
                String name = new String(data, "UTF-8");

                Set<Object> keys = RedefineClusterUtils.hkeys(Constant.WATCHDOG_PROFILE + name);
                result.put(name, keys);
            } catch (UnsupportedEncodingException e) {
                LOG.error("获取项目配置异常,{}", e);
            }
        }
        return result;

    }

    /**
     * 获取环境详细数据
     *
     * @param pjName
     * @param prName
     * @return
     */
    @Override
    public Object getDetaile(String pjName, String prName) {

        Object result = RedefineClusterUtils.hashGet(Constant.WATCHDOG_PROFILE + pjName, prName);

        return result;
    }


    /**
     * 获取环境详细数据
     *
     * @param pjName
     * @param prName
     * @return
     */
    @Override
    public boolean deleteProfile(String pjName, String prName) {

        boolean result = true;
        if (StringUtils.isNotEmpty(pjName) && StringUtils.isNotEmpty(prName)) {
            RedefineClusterUtils.hDel(Constant.WATCHDOG_PROFILE + pjName, prName);
        } else if (StringUtils.isNotEmpty(pjName)) {
            RedefineClusterUtils.delKey(Constant.WATCHDOG_PROFILE + pjName);
            RedefineClusterUtils.sRem(Constant.WATCHDOG_PROFILE_ALL, pjName);
        }
        return result;
    }


}
