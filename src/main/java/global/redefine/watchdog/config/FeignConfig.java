package global.redefine.watchdog.config;

import feign.Logger;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Created by luqiang on 2018/3/13.
 */
@Configuration
@EnableFeignClients(basePackages = "global.redefine.watchdog.service")
public class FeignConfig {


    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}
