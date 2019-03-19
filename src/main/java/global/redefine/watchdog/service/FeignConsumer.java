package global.redefine.watchdog.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Map;

/**
 * Created by luqiang on 2018/3/12.
 */
@FeignClient(value = "redefine-statistic")
@Component("feignConsumer")
public interface FeignConsumer {

    @RequestMapping(value = "/abtest/update", method = RequestMethod.POST)
    void updateAbtestInfo(@RequestBody List<Map<String, Object>> body);


}
