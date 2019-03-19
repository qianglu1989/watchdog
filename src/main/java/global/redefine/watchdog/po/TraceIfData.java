package global.redefine.watchdog.po;

import java.io.Serializable;

/**
 * Created by luqiang on 2018/5/28.
 *
 * @author luqiang
 */
public class TraceIfData implements Serializable {

    private String id;

    /**
     * 服务名称
     */
    private String serviceName;


    /**
     * 接口名称
     */
    private String interfaceName;

    /**
     * 总数
     */
    private String count;

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }
}
