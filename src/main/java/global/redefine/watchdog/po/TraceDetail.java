package global.redefine.watchdog.po;

import java.io.Serializable;

/**
 * Created by luqiang on 2018/5/30.
 */
public class TraceDetail implements Serializable {

    private long date;

    private String duration;

    private String kind;

    private String address;

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
