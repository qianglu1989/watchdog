package global.redefine.watchdog.controller;

import global.redefine.watchdog.service.VersionService;
import global.redefine.watchdog.utils.GitUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 用于版本监控操作
 *
 * @author QIANGLU
 */
@RestController
@RequestMapping("/version")
@CrossOrigin
public class VersionController {


    @Resource
    private VersionService versionService;



    /**
     * 获取版本配置信息
     * @return
     */
    @RequestMapping(value = "/getVersionList", method = RequestMethod.GET)
    public List<Map<Object, Object>> getVersionList() {

        return versionService.getVersionList();

    }

    /**
     * 检测版本配置信息
     * @return
     */
    @RequestMapping(value = "/checkVersion", method = RequestMethod.GET)
    public boolean checkVersion(@RequestParam(defaultValue = "",required = false) String name, HttpServletRequest request) {
        GitUtils.initConfig(request);
        return versionService.checkVersion();

    }
}
