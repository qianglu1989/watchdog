package global.redefine.watchdog.config;

import global.redefine.watchdog.interceptor.AccesProfileInterceptor;
import global.redefine.watchdog.interceptor.AccessTokenInterceptor;
import global.redefine.watchdog.listener.AbTestWatchListener;
import global.redefine.watchdog.listener.WatchListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by luqiang on 2018/5/18.
 *
 * @author luqiang
 */
@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AccessTokenInterceptor()).addPathPatterns("/config/**", "/warn/**", "/manager/**").excludePathPatterns("/watch/login", "/watch/add", "error", "/config/getABConfigs", "/config/updateABConfigs");
        registry.addInterceptor(new AccesProfileInterceptor()).addPathPatterns("/profile/**", "/version/**").excludePathPatterns("/profile/getProfile", "/version/getVersionList");
    }


//    @Override
//    public void addViewControllers(ViewControllerRegistry registry) {
//        registry.addViewController("/login").setViewName("index");
//    }

    @Bean
    public WatchListener watchListener(KafkaTemplate kafkaTemplate) {


        return new WatchListener(kafkaTemplate);
    }

    @Bean
    public AbTestWatchListener abTestWatchListener() {
        return new AbTestWatchListener();
    }
}
