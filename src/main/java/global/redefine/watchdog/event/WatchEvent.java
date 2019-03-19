package global.redefine.watchdog.event;

import org.springframework.context.ApplicationEvent;

import java.util.Map;

/**
 * @author luqiang on 28/12/2018.
 */
public class WatchEvent extends ApplicationEvent {

    private Map<String, String> msg;
    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public WatchEvent(Object source, Map<String, String> msg) {
        super(source);
        this.msg = msg;
    }

    public Map<String, String> getMsg() {
        return msg;
    }

    public void setMsg(Map<String, String> msg) {
        this.msg = msg;
    }
}
