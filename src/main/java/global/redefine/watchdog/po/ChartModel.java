package global.redefine.watchdog.po;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * 图表模型
 */
public class ChartModel implements Serializable {

    private String name;

    private LinkedList<Object> xaxis;

    private LinkedList<Object> yaxis;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LinkedList<Object> getXaxis() {
        return xaxis;
    }

    public void setXaxis(LinkedList<Object> xaxis) {
        this.xaxis = xaxis;
    }

    public LinkedList<Object> getYaxis() {
        return yaxis;
    }

    public void setYaxis(LinkedList<Object> yaxis) {
        this.yaxis = yaxis;
    }
}
