package global.redefine.watchdog.listener;

import global.redefine.watchdog.event.UpdateAbTestWatchEvent;
import org.springframework.context.ApplicationListener;

import java.util.Map;

/**
 * @author luqiang on 28/12/2018.
 */
public class AbTestWatchListener implements ApplicationListener<UpdateAbTestWatchEvent> {


    public AbTestWatchListener() {
    }


    @Override
    public void onApplicationEvent(UpdateAbTestWatchEvent event) {

        Map<String, String> newData = event.getNewData();
        Map<String, String> oldData = event.getOldData();
    }
}
