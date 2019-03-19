package global.redefine.watchdog;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;

@SpringCloudApplication
public class WatchdogStratup {


	public static void main(String[] args) {
		SpringApplication.run(WatchdogStratup.class, args);
	}
}
