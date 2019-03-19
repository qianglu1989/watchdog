package global.redefine.watchdog.event;

import org.springframework.context.ApplicationEvent;

import java.util.Map;

/**
 * @author luqiang on 28/12/2018.
 */
public class UpdateAbTestWatchEvent extends ApplicationEvent {

    private Map<String, String> newData;
    private Map<String, String> oldData;

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public UpdateAbTestWatchEvent(Object source, Map<String, String> newData, Map<String, String> oldData) {
        super(source);
        this.newData = newData;
        this.oldData = oldData;
    }

    public Map<String, String> getNewData() {
        return newData;
    }

    public void setNewData(Map<String, String> newData) {
        this.newData = newData;
    }

    public Map<String, String> getOldData() {
        return oldData;
    }

    public void setOldData(Map<String, String> oldData) {
        this.oldData = oldData;
    }
}
