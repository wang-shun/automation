package com.gome.test.mq.dao.base;


import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by zhangjiadi on 15/12/22.
 */
@Component
public class BaseDao<T> {

    private Class<T> entityClass;

    public BaseDao() {
        try {
            Type genType = getClass().getGenericSuperclass();
            Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
            entityClass = (Class) params[0];
        } catch (Exception ex) {
        }
    }



    @PersistenceContext
    private EntityManager entityManager;

    public EntityTransaction getTransaction() {
        return entityManager.getTransaction();
    }


    @Transactional
    public void save(T entity) {
        entityManager.persist(entity);
        entityManager.flush();
    }

    @Transactional
    public int delete(Integer id) {
        entityManager.flush();
        T entity = entityManager.find(entityClass, id);
        if (entity == null) {
            return -1;
        } else {
            entityManager.remove(entity);
            return 1;
        }
    }

    public Object[][] getAll(String... column) {
        List<T> list = getAll();

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
        String name = entityClass.getName();
        int index = name.lastIndexOf("[.]");
        String tablename = name.substring(index + 1, name.length());

        List list = entityManager.createQuery("from " + tablename).getResultList();

//        try {
//            return convertDicKeyToValue(list);//hql 语句中模板类型怎么写？？？？
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }

        return list;
    }

    @Transactional
    public T get(String id) {
        return entityManager.find(entityClass, id);
    }

    @Transactional
    public T get(Integer id) {
        return entityManager.find(entityClass, id);
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
                    Object v = getValue(fieldname, entity2);
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
        Object id = getAnnotationId(entity);
        entityManager.flush();
        T newentity = entityManager.find(entityClass, id);
        if (newentity == null) {
            return null;
        }
        return entityManager.merge(entity);//setValue(entity, newentity)
    }

    public List sqlQuery(String sql) {                      //根据数据库具体表进行查找
        List list = entityManager.createNativeQuery(sql).getResultList();
        if (0 == list.size()) {
            return null;
        } else {
            return list;
        }
    }

    public List sqlQuery(String sql, Class resultClass) {                      //根据数据库具体表进行查找
        return entityManager.createNativeQuery(sql, resultClass).getResultList();
    }


    @Transactional
    public int executeSql(String sql) {
        Query query = entityManager.createNativeQuery(sql);
        return query.executeUpdate();
    }


    public List find(String hql) {                            //根据定义的实体类进行查找操作
        List list = entityManager.createQuery(hql).getResultList();
        if (list.size() == 0) {
            return null;
        }
        return entityManager.createQuery(hql).getResultList();
    }


    @Transactional
    public List find(String hql, Object... values) {
        Object[] item = hqlAndParams(hql, values);
        String newhql = (String) item[0];
        Object[] params = (Object[]) item[1];
        if (params == null) {
            return find(newhql);
        }
        Query query = entityManager.createQuery(newhql);

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
                entityManager.flush();
            }
            T entity = entityManager.find(entityClass, id);
            if (entity == null) {
                continue;
            }
            entityManager.remove(entity);
            i++;
        }
        return i;
    }



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
            }
            if (obj instanceof String) {
                if (((String) obj).equalsIgnoreCase("none")) {
                    index.add(i);
                    continue;
                }
            }
            v.add(obj);
        }
        Object[] item = new Object[]{v, index};
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
            Object[] item = paramFilter(values);

            List params = (List) item[0];
            List removeIndex = (List) item[1];
            String newhql = newHql(hql, removeIndex);

            if (!newhql.equalsIgnoreCase("none")) {
                hql = newhql;
            } else {
                hql = hql.split("where")[0];
                return new Object[]{hql, null};
            }
            return new Object[]{hql, toarray(params)};
        } else {
            return new Object[]{hql, null};
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
        entityManager.flush();
        entityManager.clear();
    }



}
