package global.redefine.watchdog.po;

import java.io.Serializable;
import java.util.List;

/**
 * Created by luqiang on 2018/5/28.
 */
public class PageModel<T> implements Serializable {
    private static final long serialVersionUID = 8776788184840922942L;
    private List<T> datas;
    private int rowCount;
    private int pageSize = 100;
    private int pageNo = 1;
    private int skip = 0;

    public PageModel() {
    }

    public int getTotalPages() {
        return (this.rowCount + this.pageSize - 1) / this.pageSize;
    }

    public List<T> getDatas() {
        return this.datas;
    }

    public void setDatas(List<T> datas) {
        this.datas = datas;
    }

    public int getRowCount() {
        return this.rowCount;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getSkip() {
        this.skip = (this.pageNo - 1) * this.pageSize;
        return this.skip;
    }

    public void setSkip(int skip) {
        this.skip = skip;
    }

    public int getPageNo() {
        return this.pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }
}
