package com.gome.test.mock.db.uitls;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <h3>分页对象</h3>
 * <p>分页对象包含信息：</p>
 * <p> ■ 当前页码（pageNo）</p>
 * <p> ■ 每页显示条数(pageSize)</p>
 * <p> ■ 总条数(totalCount)</p>
 * <p> ■ 总页数(totalPageCount)</p>
 * <p> ■ 数据列表(data)</p>
 * @param <T> 分页数据对象类型
 */
public class PageInfo<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    //当前页码
    private int pageNo;
    //每页显示条数
    private int pageSize;
    //数据列表
    private List<T> data;
    //总条数
    private int totalCount;
    //总页数
    private int totalPageCount;

    /**
     * <b>构造函数</b>
     * <p>（默认pageNo=1，pageSize=20,totalCount=0）</p>
     * 
     */
    public PageInfo() {
        this(1, 20, 0, new ArrayList<T>());
    }

    /**
     * <b>构造函数</b>
     * 
     * @param pageNo 当前页码
     * @param pageSize 每页显示条数
     */
    public PageInfo(int pageNo, int pageSize) {
        super();
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }

    /**
     * <b>构造函数</b>
     * <p>（总页数根据总条数计算获得）</p>
     * 
     * @param pageNo 当前页码
     * @param pageSize 每页显示条数
     * @param totalCount 总条数
     * @param data 数据
     */
    public PageInfo(int pageNo, int pageSize, int totalCount, List<T> data) {
        this.pageSize = pageSize;
        this.pageNo = pageNo;
        this.totalCount = totalCount;
        this.data = data;

        if (this.totalCount % this.pageSize == 0L) {
            this.totalPageCount = (this.totalCount / this.pageSize);
        } else {
            this.totalPageCount = (this.totalCount / this.pageSize + 1);
        }
    }

    /**
     * <b>设置当前页码</b>
     * 
     * @param pageNo
     */
    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    /**
     * <b>设置每页显示条数</b>
     * 
     * @param pageSize
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * <b>设置数据</b>
     * 
     * @param data
     */
    public void setData(List<T> data) {
        this.data = data;
    }

    /**
     * <b>设置总条数</b>
     * <p>（设置总条数，同时通过计算设置总页数）</p>
     * 
     * @param totalCount
     */
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;

        if (this.totalCount % this.pageSize == 0L) {
            this.totalPageCount = (this.totalCount / this.pageSize);
        } else {
            this.totalPageCount = (this.totalCount / this.pageSize + 1);
        }
    }

    /**
     * <b>获取当前页码</b>
     * 
     * @return 当前页码
     */
    public int getPageNo() {
        return this.pageNo;
    }

    /**
     * <b>获取每页显示条数</b>
     * 
     * @return 每页显示条数
     */
    public int getPageSize() {
        return this.pageSize;
    }

    /**
     * <b>获取数据</b>
     * 
     * @return List<T> 分页数据
     */
    public List<T> getData() {
        return this.data;
    }

    /**
     * <b>获取总条数</b>
     * 
     * @return 总条数
     */
    public int getTotalCount() {
        return this.totalCount;
    }

    /**
     * <b>获取总页数</b>
     * 
     * @return 总页数
     */
    public int getTotalPageCount() {
        return this.totalPageCount;
    }

    /**
     * <b>获取开始下标</b>
     * 
     * @param pageNo 当前页码
     * @param pageSize 每页显示条数
     * 
     * @return 下一页开始下标
     */
    public static int getStartIndex(int pageNo, int pageSize) {
        return ((pageNo - 1) * pageSize);
    }
}
