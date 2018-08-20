package com.gome.test.mock.db.base;

import com.gome.test.mock.db.uitls.PageInfo;
import com.gome.test.mock.db.uitls.QueryRule;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
/**
 * <h3>Dao基类接口</h3>
 * <p> ■ 获取单个实例</p>
 * <p> ■ 获取所有实例</p>
 * <p> ■ 判断实例是否存在</p>
 * <p> ■ 保存单个实例</p>
 * <p> ■ 修改实例</p>
 * <p> ■ 删除实例（按主键删除）</p>
 * <p> ■ 删除实例（按对象删除）</p>
 * <p> ■ 根据queryRule的条件查询实例列表</p>
 * <p> ■ 根据queryRule的条件以及当前页码、每页显示条数实现分页查询实例列表</p>
 * <p> ■ 根据queryRule的条件以及开始下标、查询条数查询多个实例</p>
 * <p> ■ 根据实体属性名、属性值查询单个实例，当查询到多条时抛出非受查异常</p>
 * <p> ■ 根据map里包含的多个属性名、属性值查询单个实例，当查询到多条时抛出非受查异常</p>
 * <p> ■ 根据queryRule查询单个实例，当查询到多条时抛出非受查异常</p>
 * 
 * @author liangzh
 *
 * @param <T> 实体类型
 * @param <PK> 主键类型
 */
public abstract interface BaseDao<T extends Serializable, PK extends Serializable> {

    /**
     * <b>获取单个实例</b>
     * （根据主键PK获取类型为T的实例）
     * 
     * @param paramPK 实例主键
     * @return T
     */
    public abstract T get(PK paramPK);

    /**
     * <b>获取所有实例</b>
     * （获取所有类型为T的实例）
     * 
     * @return List<T>
     */
    public abstract List<T> getAll();

    /**
     * <b>判断实例是否存在</b>
     * （根据主键PK判断类型为T的实例是否存在）
     * 
     * @param paramPK 实例主键
     * @return boolean
     */
    public abstract boolean exists(PK paramPK);

    /**
     * <b>保存单个实例</b>
     * 
     * @param object 实例
     */
    public abstract void save(T object);

    /**
     * <b>保存单个实例（仅仅insert）</b>
     * @author wei
     * @param object 实例
     */
    public abstract void insert(T object);

    /**
     * <b>修改实例</b>
     * 
     * @param object 实例
     */
    public abstract void update(T object);

    /**
     * <b>删除实例</b>
     * 
     * @param paramPK 实例主键
     */
    public abstract void deleteByPK(PK paramPK);

    /**
     * <b>删除实例</b>
     * 
     * @param object 实例
     */
    public abstract void delete(T object);

    /**
     * <b>合并实例</b>
     * 
     * @param object 实例
     */
    public abstract void merge(T object);

    /**
     * <b>查询列表</b>
     * （根据queryRule的条件查询实例列表）
     * 
     * @param queryRule 查询规则对象
     * 
     * @return List<T>
     */
    public abstract List<T> find(QueryRule queryRule);

    /**
     * <b>分页查询列表</b>
     * （根据queryRule的条件以及当前页码、每页显示条数实现分页查询实例列表）
     * 
     * @param queryRule 查询规则对象
     * @param pageNo 当前页码
     * @param pageSize 每页显示条数
     * @return PageInfo<T>
     */
    public abstract PageInfo<T> find(QueryRule queryRule, int pageNo, int pageSize);

    /**
     * <b>查询多条实例</b>
     * 
     * @param queryRule 查询条件
     * @param start 开始下标
     * @param count 查询条数
     * 
     * @return List<T>
     */
    public abstract List<T> findList(QueryRule queryRule, int start, int count);

    /**
     * <b>查询单个实例</b>
     * （根据实体属性名、属性值查询单个实例，当查询到多条时抛出非受查异常）
     * 
     * @param propertyName 属性名
     * @param value 属性值
     * @return T
     */
    public abstract T findUnique(String propertyName, Object value);

    /**
     * <b>查询单个实例</b>
     * （根据map里包含的多个属性名、属性值查询单个实例，当查询到多条时抛出非受查异常）
     * 
     * @param map 属性map
     * @return T
     */
    public abstract T findUnique(Map<String, Object> map);

    /**
     * <b>查询单个实例</b>
     * （根据queryRule查询单个实例，当查询到多条时抛出非受查异常）
     * 
     * @param queryRule 规则对象
     * 
     * @return T
     */
    public abstract T findUnique(QueryRule queryRule);
}