package global.redefine.watchdog.config;

import com.aliyun.openservices.log.Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Created by haoyl on 2018/10/18.
 */
@Configuration
@ConfigurationProperties(prefix = "aliyun.loghub")
public class LogHubConfig {

    @Value("${aliyun.loghub.endpoint}")
    private String endpoint;

    @Value("${aliyun.loghub.accesskey.id}")
    private String accessKeyId;

    @Value("${aliyun.loghub.accesskey.secret}")
    private String accessKeySecret;

    @Value("${aliyun.loghub.project}")
    private String project;

    @Value("${aliyun.loghub.logstore}")
    private String logStore;


    @Bean(name = "logHubClient")
    public Client logHubClient() {
        return new Client(endpoint, accessKeyId, accessKeySecret);
    }


}
