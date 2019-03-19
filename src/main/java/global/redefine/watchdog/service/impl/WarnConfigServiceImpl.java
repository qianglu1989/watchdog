package global.redefine.watchdog.service.impl;

import com.alibaba.fastjson.JSON;
import global.redefine.watchdog.service.ConfigService;
import global.redefine.watchdog.service.WarnConfigService;
import global.redefine.watchdog.utils.GitUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by luqiang on 2018/5/16.
 */
@Service("warnConfigService")
public class WarnConfigServiceImpl implements WarnConfigService {

    Logger LOG = LoggerFactory.getLogger(WarnConfigServiceImpl.class);

    private static String FILE_PRE = "redefine-exwarn-";
    private static String FILE_SUFFIX = ".properties";

    @Resource
    private ConfigService configService;

    /**
     * 获取报警服务配置列表
     *
     * @return
     */
    @Override
    public List<String> getWarnServices() {

        Map<String,String> config = GitUtils.CONFIG_DATA.get();
        String profile = config.get("profile");

        List<String> result = new LinkedList<>();

        Map<String, String> services = configService.getServiceConfig(FILE_PRE + profile + FILE_SUFFIX);

        String pre = "redefine.weixin.group.";
        for (Map.Entry<String, String> entry : services.entrySet()) {
            String key = entry.getKey();
            if (key.contains(pre)) {
                String val = key.substring(pre.length(), key.length());
                result.add(val);
            }

        }

        return result;
    }

    /**
     * 获取报警配置详情
     */
    @Override
    public Map<String, String> getWarnConfigs(String name) {

        Map<String,String> config = GitUtils.CONFIG_DATA.get();
        String profile = config.get("profile");


        Map<String, String> services = configService.getServiceConfig(FILE_PRE + profile + FILE_SUFFIX);

        Map<String, String> result = new LinkedHashMap<>();
        String filterPath = "redefine.weixin.filterPath." + name;
        String interval = "redefine.weixin.interval." + name;
        result.put("path", services.get(filterPath));
        result.put("interval", services.get(interval));
        return result;
    }

    /**
     * 更新应用报警配置
     */
    @Override
    public boolean updateWarnConfig(String data,String token) {

        boolean result = false;

        Map<String,String> config = GitUtils.CONFIG_DATA.get();
        String profile = config.get("profile");


        if (StringUtils.isEmpty(data)) {
            return result;
        }

        String fileName = FILE_PRE + profile + FILE_SUFFIX;
        Map<String, String> configs = configService.getServiceConfig(fileName);
        Map<String, String> maps = JSON.parseObject(data, Map.class);
        String interval = maps.get("interval");
        String path = maps.get("path");
        String name = maps.get("name");
        String key = "redefine.weixin.filterPath." + name;
        String ikey = "redefine.weixin.interval." + name;

        if (StringUtils.isEmpty(name)) {
            return false;
        }

        if(StringUtils.isEmpty(path) && StringUtils.isNotEmpty(configs.get(key))){
            maps.remove(key);
        }

        if(StringUtils.isEmpty(interval) && StringUtils.isNotEmpty(configs.get(ikey))){
            maps.remove(ikey);
        }

        if(StringUtils.isNotEmpty(path)){
            configs.put(key, path);

        }
        if(StringUtils.isNotEmpty(interval)){
            configs.put(ikey, interval);
        }
        result = configService.updateConfig(fileName,configs,token);
//        if(result){
//            result = configService.refreshConfig();
//        }
        return result;


    }
}
