package global.redefine.watchdog.controller;

import com.alibaba.fastjson.JSON;
import com.redefine.redis.utils.RedefineClusterUtils;
import global.redefine.watchdog.utils.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by luqiang on 2018/5/18.
 *
 * @author QIANGLU
 */
@RestController
@RequestMapping("/watch")
@CrossOrigin
public class LoginController {

    Logger LOG = LoggerFactory.getLogger(LoginController.class);

    /**
     * 登陆返回token
     *
     * @param username
     * @param password
     * @return
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public Map<String, String> login(@RequestParam String username, @RequestParam String password, HttpServletResponse response) {

        Map<String, String> result = new HashMap<>();

        result.put("status", "0");

        Object pass = RedefineClusterUtils.hashGet("watchdog:login", username);
        String[] auth = String.valueOf(pass).split(":");
        if (password.equals(auth[0])) {
            result.put("auth", auth.length == 1 ? "0" : auth[1]);
            result.put("status", "1");
            String token = UUID.randomUUID().toString();
            result.put("token", token);
            result.put("username", username);
            RedefineClusterUtils.saveString("watchdog:login:token:" + token, JSON.toJSONString(result));
            response.addHeader("token", token);
            LOG.info("用户:{},已登陆", username);
        }


        return result;
    }

    /**
     * 初始化系统配置
     * @param username
     * @param password
     * @return
     */
    @RequestMapping(value = "/init", method = RequestMethod.GET)
    public String init(@RequestParam(defaultValue = "admin",required = false) String username, @RequestParam(defaultValue = "qianglu:1",required = false) String password) {


        //初始化系统用户名密码
        RedefineClusterUtils.hashSet("watchdog:login", username, password);

        //初始化系统环境配置
        String key = Constant.WATCHDOG_PROFILE + "Prod";

        Map<String,String> data = new HashMap<>();
        data.put("redefine.git.localPath","/data/redefine/wlconfig/prod");
        data.put("redefine.git.branch","master");
        //git 配置文件如果有下级目录则添加否则不需要
        data.put("redefine.git.realPath","/config");
        data.put("redefine.git.username","luqiang");
        data.put("redefine.git.password","598505651");
        data.put("redefine.git.uri","http://code.rdfamily.cn:8086/luqiang/remote-config.git");
        //动态刷新地址，一般是config服务器
        data.put("redefine.git.refreshIp","http://172.23.2.156:8091/bus/refresh");
        data.put("redefine.eureka.defaultZone","http://eureka01.welike.in:8090/eureka/,http://eureka02.welike.in:8090/eureka/");
        data.put("redefine.monitor.app","http://apps.rdfamily.cn");
        data.put("redefine.monitor.register","http://eureka.welike.in/");
        data.put("redefine.monitor.flow","http://flow.rdfamily.cn/hystrix/monitor?stream=http://flow.rdfamily.cn/turbine.stream");
        //用于首页图表数据项目标示
        data.put("redefine.ultron.profile","welike");
        RedefineClusterUtils.hashSet(key, "dev", data);
        RedefineClusterUtils.hashSet(key, "pre", data);
        RedefineClusterUtils.hashSet(key, "prod", data);

        RedefineClusterUtils.sAdd(Constant.WATCHDOG_PROFILE_ALL, "Prod");
        return "SUCC";
    }
}
