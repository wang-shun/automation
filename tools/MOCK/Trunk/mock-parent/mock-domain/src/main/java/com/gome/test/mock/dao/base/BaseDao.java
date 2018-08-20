/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gome.test.mock.dao.base;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.springframework.stereotype.Component;

/**
 * @param <T>
 * @author Zonglin.Li
 */
@Component
public class BaseDao<T> {

    private Class<T> entityClass;

    public BaseDao() {
        try {
            Type genType = this.getClass().getGenericSuperclass();
            Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
            this.entityClass = (Class) params[0];
        } catch (Exception ex) {}
    }

    //    @Autowired
    //    private LoadConfigureDictionaryService lcdService;

    @PersistenceContext
    private EntityManager entityManager;

    public EntityTransaction getTransaction() {
        return this.entityManager.getTransaction();
    }

    @Transactional
    public void save(T entity) {
        this.entityManager.persist(entity);
        this.entityManager.flush();
    }

    @Transactional
    public int delete(Integer id) {
        this.entityManager.flush();
        T entity = this.entityManager.find(this.entityClass, id);
        if (entity == null) {
            return -1;
        } else {
            this.entityManager.remove(entity);
            return 1;
        }
    }

    public Object[][] getAll(String... column) {
        List<T> list = this.getAll();

        if (column != null && column.length > 0 && list.size() > 0) {
            List<Field> fields = new ArrayList<Field>();
            for (String col : column) {
                for (Field f : list.get(0).getClass().getDeclaredFields()) {
                    if (col.equals(f.getName())) {
                        f.setAccessible(true);
                        fields.add(f);
                    }
                }
            }

            Object[][] result = new Object[list.size()][fields.size()];
            for (int i = 0; i < list.size(); i++) {
                for (int j = 0; j < fields.size(); j++) {
                    try {
                        result[i][j] = fields.get(j).get(list.get(i));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
            return result;
        }

        return new Object[0][0];
    }

    public List<T> getAll() {
        String name = this.entityClass.getName();
        int index = name.lastIndexOf("[.]");
        String tablename = name.substring(index + 1, name.length());

        List list = this.entityManager.createQuery("from " + tablename).getResultList();

        //        try {
        //            return convertDicKeyToValue(list);//hql 语句中模板类型怎么写？？？？
        //        } catch (IllegalAccessException e) {
        //            e.printStackTrace();
        //        }

        return list;
    }

    @Transactional
    public T get(String id) {
        return this.entityManager.find(this.entityClass, id);
    }

    @Transactional
    public T get(Integer id) {
        return this.entityManager.find(this.entityClass, id);
    }

    //
    private T setValue(T entity1, T entity2) {
        Field[] fields = entity1.getClass().getDeclaredFields();
        for (Field field : fields) {
            PropertyDescriptor pd = null;
            try {
                String fieldname = field.getName();
                pd = new PropertyDescriptor(fieldname, entity1.getClass());
                Method method = pd.getReadMethod();//获得get方法
                Object obj = method.invoke(entity1);
                if (obj == null) {
                    Object v = this.getValue(fieldname, entity2);
                    Method setmethod = pd.getWriteMethod();
                    setmethod.invoke(entity1, v);
                }
            } catch (IntrospectionException ex) {
                Logger.getLogger(BaseDao.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(BaseDao.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(BaseDao.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(BaseDao.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        return entity1;
    }

    private Object getValue(String fieldname, T entity2) {
        Object obj = null;
        Field[] fields = entity2.getClass().getDeclaredFields();
        for (Field field : fields) {
            PropertyDescriptor pd = null;
            try {
                String fname = field.getName();
                if (fieldname.endsWith(fname)) {
                    pd = new PropertyDescriptor(fieldname, entity2.getClass());
                    Method method = pd.getReadMethod();//获得get方法
                    obj = method.invoke(entity2);
                }

            } catch (IntrospectionException ex) {
                Logger.getLogger(BaseDao.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(BaseDao.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(BaseDao.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(BaseDao.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        return obj;
    }

    private Object getAnnotationId(T entity) {
        Field[] fields = entity.getClass().getDeclaredFields();
        Method getMethod = null;
        for (Field field : fields) {
            PropertyDescriptor pd = null;
            try {
                pd = new PropertyDescriptor(field.getName(), entity.getClass());
            } catch (IntrospectionException ex) {
                Logger.getLogger(BaseDao.class.getName()).log(Level.SEVERE, null, ex);
            }
            Method method = pd.getReadMethod();//获得get方法
            if (method.getAnnotation(javax.persistence.Id.class) == null) {
                continue;
            } else {
                getMethod = method;
                break;
            }
        }
        Object id = null;
        if (getMethod != null) {
            try {
                id = getMethod.invoke(entity);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(BaseDao.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(BaseDao.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(BaseDao.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return id;
    }

    @Transactional
    public T update(T entity) {
        Object id = this.getAnnotationId(entity);
        this.entityManager.flush();
        T newentity = this.entityManager.find(this.entityClass, id);
        if (newentity == null) {
            return null;
        }
        return this.entityManager.merge(entity);//setValue(entity, newentity)
    }

    public List sqlQuery(String sql) { //根据数据库具体表进行查找
        List list = this.entityManager.createNativeQuery(sql).getResultList();
        if (0 == list.size()) {
            return null;
        } else {
            return list;
        }
    }

    public List sqlQuery(String sql, Class resultClass) { //根据数据库具体表进行查找
        return this.entityManager.createNativeQuery(sql, resultClass).getResultList();
    }

    @Transactional
    public int executeSql(String sql) {
        Query query = this.entityManager.createNativeQuery(sql);
        return query.executeUpdate();
    }

    public List find(String hql) { //根据定义的实体类进行查找操作
        List list = this.entityManager.createQuery(hql).getResultList();
        if (list.size() == 0) {
            return null;
        }
        return this.entityManager.createQuery(hql).getResultList();
    }

    @Transactional
    public List find(String hql, Object... values) {
        Object[] item = this.hqlAndParams(hql, values);
        String newhql = (String) item[0];
        Object[] params = (Object[]) item[1];
        if (params == null) {
            return this.find(newhql);
        }
        Query query = this.entityManager.createQuery(newhql);

        for (int i = 0; i < params.length; i++) {

            query.setParameter(i + 1, params[i]);
        }
        return query.getResultList();
    }

    @Transactional
    public int betchDelete(List<Integer> ids) {

        int i = 0;
        for (Integer id : ids) {
            if (i % 100 == 0) {
                this.entityManager.flush();
            }
            T entity = this.entityManager.find(this.entityClass, id);
            if (entity == null) {
                continue;
            }
            this.entityManager.remove(entity);
            i++;
        }
        return i;
    }

    //    @Transactional
    //    public Page pagedQuery(String hql, int pageNo, int pageSize, Object... values) {
    //        Object[] item = hqlAndParams(hql, values);
    //        String newhql = (String) item[0];
    //        Object[] params = (Object[]) item[1];
    //        if (params == null) {
    //            return page(newhql, pageNo, pageSize);
    //        } else {
    //            return page(newhql, pageNo, pageSize, params);
    //        }
    //    }
    //
    //    @Transactional
    //    private Page page(String hql, int pageNo, int pageSize, Object... values) {
    //        String countQueryString = "select count(*) " + removeSelect(hql);
    //        Query query = entityManager.createQuery(countQueryString);
    //        for (int i = 0; i < values.length; i++) {
    //            query.setParameter(i + 1, values[i]);
    //        }
    //        List countQueryList = query.getResultList();
    //        long totalCount = (Long) countQueryList.get(0);
    //        if (totalCount < 1) {
    //            return new Page();
    //        }
    //
    //        int startIndex = Page.getStartOfPage(pageNo, pageSize);
    //        Query _query = entityManager.createQuery(hql);
    //        for (int i = 0; i < values.length; i++) {
    //            _query.setParameter(i + 1, values[i]);
    //        }
    //        List list = _query.setFirstResult(startIndex).setMaxResults(pageSize).getResultList();
    //        return new Page(startIndex, totalCount, pageSize, list);
    //    }

    @Transactional
    private String removeSelect(String hql) {
        int begin = hql.toLowerCase().indexOf("from");
        return hql.substring(begin);
    }

    private Object[] paramFilter(Object... values) {
        int size = values.length;
        List v = new ArrayList();//存放参数值
        List index = new ArrayList();
        for (int i = 0; i < size; i++) {
            Object obj = values[i];
            if (obj instanceof Integer) {
                if (((Integer) obj) == -100) {
                    index.add(i);
                    continue;
                }
                v.add(Integer.valueOf(obj.toString()));
            }
            if (obj instanceof String) {
                if (((String) obj).equalsIgnoreCase("none")) {
                    index.add(i);
                    continue;
                }
                v.add(obj.toString());
            }
        }
        Object[] item = new Object[] { v, index };
        return item;
    }

    //List index 为Hql需要去掉的位置坐标
    private String newHql(String hql, List<Integer> index) {
        if (index.size() == 0) {
            //where后面的条件都保留
            return hql;
        }
        String sqlhead = "";
        String sqlmid = " where ";
        String sqlfoot = "";
        List<String> param_part = new ArrayList<String>();

        sqlhead = hql.split("where")[0];

        sqlfoot = hql.split("where")[1];

        if (sqlfoot.indexOf("and") != -1) {
            String[] allparams = sqlfoot.split("and");
            for (String st : allparams) {
                param_part.add(st);
            }
        } else {
            param_part.add(sqlfoot);
        }

        //对Hql进行替换 将需要被删除的部分替换为“-1”
        for (int i = 0; i < index.size(); i++) {
            int j = index.get(i);
            param_part.set(j, "-1");
        }

        if (param_part.size() == index.size()) {
            return "none";//在外层把where 及后面的条件 去掉
        }

        String Hql = sqlhead + sqlmid;
        int size = param_part.size();

        for (int i = 0; i < size; i++) {

            if (param_part.get(i).equalsIgnoreCase("-1")) {
                continue;
            }

            if (i == (size - 1)) {
                Hql += " ";
                Hql += param_part.get(i);
                continue;
            }

            Hql += " ";
            Hql += (param_part.get(i) + " and");

        }
        if (Hql.endsWith("and")) {
            int lastend = Hql.lastIndexOf("and");
            Hql = Hql.substring(0, lastend);
        }
        return Hql;
    }

    // hql + 参数
    public Object[] hqlAndParams(String hql, Object... values) {

        if (hql.indexOf("where") != -1) {
            Object[] item = this.paramFilter(values);

            List params = (List) item[0];
            List removeIndex = (List) item[1];
            String newhql = this.newHql(hql, removeIndex);

            if (!newhql.equalsIgnoreCase("none")) {
                hql = newhql;
            } else {
                hql = hql.split("where")[0];
                return new Object[] { hql, null };
            }
            return new Object[] { hql, this.toarray(params) };
        } else {
            return new Object[] { hql, null };
        }

    }

    private Object[] toarray(List list) {
        int length = list.size();
        Object[] objs = new Object[length];
        for (int i = 0; i < length; i++) {
            objs[i] = list.get(i);
        }
        return objs;
    }

    public void clear() {
        this.entityManager.flush();
        this.entityManager.clear();
    }

    //    private List<T> convertDicKeyToValue(List<T> list) throws IllegalAccessException {
    //        if (list != null && list.isEmpty() == false) {
    //            Class cls = list.get(0).getClass();
    //            if (cls.getAnnotation(DictionaryMap.class) != null) {
    //                Field[] fields = cls.getDeclaredFields();
    //                HashMap<String, HashMap<Integer, String>> ssiMap = lcdService.loadSISMap();
    //
    //                for (T obj : list) {
    //                    for (Field f : fields) {
    //                        f.setAccessible(true);
    //                        for (Annotation annotation : f.getAnnotations()) {
    //                            if (annotation.annotationType() == DictionaryMap.class) {
    //                                Object value = null;
    //                                for (Field fv : fields) {
    //                                    if (fv.getName().equals(((DictionaryMap) annotation).keyColName()))
    //                                        value = fv.get(obj);
    //                                }
    //
    //                                if (value != null) {
    //                                    f.set(obj, ssiMap.get(((DictionaryMap) annotation).parent()).get(value));
    //                                }
    //                            }
    //                        }
    //                    }
    //                }
    //            }
    //        }
    //
    //        return list;
    //    }

}
