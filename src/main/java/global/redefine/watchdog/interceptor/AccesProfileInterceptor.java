package global.redefine.watchdog.interceptor;

import com.alibaba.fastjson.JSON;
import com.redefine.redis.utils.RedefineClusterUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by luqiang on 2018/5/18.
 *
 * @author QIANG
 */
public class AccesProfileInterceptor extends HandlerInterceptorAdapter {


    private final static Logger LOG = LoggerFactory.getLogger(AccesProfileInterceptor.class);


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        LOG.info("AccessToken executing ...");
        boolean flag = false;
        // token
        String accessToken = request.getParameter("token");

        if (StringUtils.isNotBlank(accessToken)) {
            // 用户验证
            Object data = RedefineClusterUtils.getString("watchdog:login:token:" + accessToken);
            Map<String, String> result = JSON.parseObject(String.valueOf(data), Map.class);

            if (result != null && accessToken.equals(result.get("token")) &&  "1".equals(result.get("auth"))) {
                LOG.info("AccessToken SUCCESS ...  user:" + accessToken);
                flag = true;
                LOG.info("用户:{}，token:{},正在访问：{}", result.get("username"), result.get("token"), request.getRequestURI());

            }
        }

        if (!flag) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            LOG.info("AccessToken FAIL ...  user:" + accessToken);
        }

        return flag;
    }


}
