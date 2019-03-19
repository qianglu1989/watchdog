package global.redefine.watchdog.controller;

import com.redefine.redis.utils.RedefineClusterUtils;
import global.redefine.watchdog.service.ProfileService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Set;

/**
 * 提供环境配置功能 配置存储在redis 其他服务从header中获取配置信息
 *
 * @author QIANGLU
 */
@RestController
@RequestMapping("/profile")
@CrossOrigin
public class ProfileController {


    @Resource
    private ProfileService profileService;

    /**
     * 获取环境配置
     *
     * @return
     */
    @RequestMapping(value = "/getProfile", method = RequestMethod.GET)
    public Map<String, Set<Object>> getProfile() {


        return profileService.getProfile();
    }

    /**
     * 创建项目条目
     *
     * @param pjName
     * @param prName
     * @param data
     * @return
     */
    @RequestMapping(value = "/createProfile", method = RequestMethod.GET)
    public boolean createProfile(@RequestParam String pjName, @RequestParam String prName, @RequestParam String data) {

        if(StringUtils.isEmpty(pjName) || StringUtils.isEmpty(prName)){
            return false;
        }
        return profileService.createProfile(pjName, prName, data);
    }


    /**
     * 获取环境详细数据
     *
     * @param pjName
     * @param prName
     * @return
     */
    @RequestMapping(value = "/getDetaile",method = RequestMethod.GET)
    public Object getDetaile(@RequestParam String pjName,@RequestParam String prName){

        return profileService.getDetaile(pjName,prName);
    }

    /**
     * 删除环境配置
     *
     * @param pjName
     * @param prName
     * @return
     */
    @RequestMapping(value = "/deleteProfile",method = RequestMethod.GET)
    public Object deleteProfile(@RequestParam String pjName,@RequestParam(defaultValue = "") String prName){

        return profileService.deleteProfile(pjName,prName);
    }

    /**
     * 添加用户
     * @param username
     * @param password
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String add(@RequestParam String username, @RequestParam String password) {


        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            return "FAIL";
        }
        RedefineClusterUtils.hashSet("watchdog:login", username, password);

        return "SUCC";
    }

    /**
     * 获取用户列表
     * @return
     */
    @RequestMapping(value = "/getUserList", method = RequestMethod.GET)
    public Map<Object, Object> getUserList() {

        return RedefineClusterUtils.hgetAll("watchdog:login");

    }


}
