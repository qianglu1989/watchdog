package global.redefine.watchdog.po;

import java.io.Serializable;
import java.util.List;

public class TraceGroup implements Serializable {

    private String id;

    private String content;

    private Long value;


    private List<String> nestedGroups;


    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public List<String> getNestedGroups() {
        return nestedGroups;
    }

    public void setNestedGroups(List<String> nestedGroups) {
        this.nestedGroups = nestedGroups;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
