package global.redefine.watchdog.po;

import global.redefine.watchdog.utils.SpanTimeSerializer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 节点信息
 */
public class Node implements Serializable {

    private String praentId;
    private String spanId;
    private String tarceId;


    private String name;
    private String kind;
    private String duration;

    private String time;
    private String path;

    private Map<String, String> localEndpoint = new HashMap<>();
    private Map<String, String> tags = new HashMap<>();

    public String getPraentId() {
        return praentId;
    }

    public void setPraentId(String praentId) {
        this.praentId = praentId;
    }

    public String getSpanId() {
        return spanId;
    }

    public void setSpanId(String spanId) {
        this.spanId = spanId;
    }

    public String getTarceId() {
        return tarceId;
    }

    public void setTarceId(String tarceId) {
        this.tarceId = tarceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Map<String, String> getLocalEndpoint() {
        return localEndpoint;
    }

    public void setLocalEndpoint(Map<String, String> localEndpoint) {
        this.localEndpoint = localEndpoint;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }

    public static NodeBuilder builder() {
        return new NodeBuilder();
    }

    public static class NodeBuilder {
        private Node node = new Node();

        public NodeBuilder praentId(String praentId) {
            this.node.setPraentId(praentId);
            return this;
        }

        public NodeBuilder spanId(String spanId) {
            this.node.setSpanId(spanId);
            return this;
        }

        public NodeBuilder tarceId(String tarceId) {
            this.node.setTarceId(tarceId);
            return this;
        }

        public NodeBuilder name(String name) {
            this.node.setName(name);
            return this;
        }

        public NodeBuilder kind(String kind) {
            this.node.setKind(kind);
            return this;
        }

        public NodeBuilder duration(String duration) {
            this.node.setDuration(duration);
            return this;
        }

        public NodeBuilder time(String time) {
            this.node.setTime(SpanTimeSerializer.seconds(time));
            return this;
        }

        public NodeBuilder path(String path) {
            this.node.setPath(path);
            return this;
        }


        public NodeBuilder localEndpoint(Map localEndpoint) {
            this.node.setLocalEndpoint(localEndpoint);
            return this;
        }

        public NodeBuilder tags(Map tags) {
            this.node.setTags(tags);
            return this;
        }

        public Node build() {
            return this.node;
        }
    }
}
