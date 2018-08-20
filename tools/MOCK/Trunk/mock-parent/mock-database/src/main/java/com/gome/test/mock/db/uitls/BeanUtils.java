package com.gome.test.mock.db.uitls;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * <h3>bean工具类</h3>
 * 
 * @author liangzh
 *
 * @since  common-0.0.1-SNAPSHOT
 * 
 * 2013年8月29日 下午3:13:40
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class BeanUtils extends org.apache.commons.beanutils.BeanUtils {
    private static final Log logger = LogFactory.getLog(BeanUtils.class);

    /**
     * <b>获取Field</b>
     * 
     * @param object 对象
     * @param propertyName 字段名
     * 
     * @return Field
     * @throws NoSuchFieldException
     */
    public static Field getDeclaredField(Object object, String propertyName) throws NoSuchFieldException {
        Assert.notNull(object);
        Assert.hasText(propertyName);
        return getDeclaredField(object.getClass(), propertyName);
    }

    /**
     * <b>获取Field</b>
     * 
     * @param clazz Class
     * @param propertyName 字段名
     * 
     * @return Field
     * @throws NoSuchFieldException
     */
    public static Field getDeclaredField(Class clazz, String propertyName) throws NoSuchFieldException {
        Assert.notNull(clazz);
        Assert.hasText(propertyName);
        for (Class superClass = clazz; superClass != Object.class; superClass = superClass.getSuperclass()) {
            try {
                return superClass.getDeclaredField(propertyName);
            } catch (NoSuchFieldException e) {}
        }
        throw new NoSuchFieldException("No such field: " + clazz.getName() + '.' + propertyName);
    }

    /**
     * <b>获取对象字段名</b>
     * 
     * @param object 对象
     * @param propertyName 字段名
     * 
     * @return Object
     * @throws NoSuchFieldException
     */
    public static Object forceGetProperty(Object object, String propertyName) throws NoSuchFieldException {
        Assert.notNull(object);
        Assert.hasText(propertyName);
        Field field = getDeclaredField(object, propertyName);
        boolean accessible = field.isAccessible();
        field.setAccessible(true);
        Object result = null;
        try {
            result = field.get(object);
        } catch (IllegalAccessException e) {
            logger.info("error wont' happen");
        }
        field.setAccessible(accessible);
        return result;
    }

    /**
     * <b>设置对象字段名</b>
     * 
     * @param object  对象
     * @param propertyName 字段名
     * @param newValue 值
     * 
     * @throws NoSuchFieldException
     */
    public static void forceSetProperty(Object object, String propertyName, Object newValue) throws NoSuchFieldException {
        Assert.notNull(object);
        Assert.hasText(propertyName);
        Field field = getDeclaredField(object, propertyName);
        boolean accessible = field.isAccessible();
        field.setAccessible(true);
        try {
            field.set(object, newValue);
        } catch (IllegalAccessException e) {
            logger.info("Error won't happen");
        }
        field.setAccessible(accessible);
    }

    /**
     * <b>调用方法</b>
     * 
     * @param object 对象
     * @param methodName 方法名
     * @param params 方法参数值
     * 
     * @return Object
     * @throws NoSuchMethodException
     */
    public static Object invokePrivateMethod(Object object, String methodName, Object[] params) throws NoSuchMethodException {
        Assert.notNull(object);
        Assert.hasText(methodName);
        Class[] types = new Class[params.length];
        for (int i = 0; i < params.length; ++i) {
            types[i] = params[i].getClass();
        }
        Class clazz = object.getClass();
        Method method = null;
        for (Class superClass = clazz; superClass != Object.class; superClass = superClass.getSuperclass()) {
            try {
                method = superClass.getDeclaredMethod(methodName, types);
            } catch (NoSuchMethodException e) {}
        }
        if (method == null) {
            throw new NoSuchMethodException("No Such Method:" + clazz.getSimpleName() + methodName);
        }
        boolean accessible = method.isAccessible();
        method.setAccessible(true);
        Object result = null;
        try {
            result = method.invoke(object, params);
        } catch (Exception e) {
            ReflectionUtils.handleReflectionException(e);
        }
        method.setAccessible(accessible);
        return result;
    }

    /**
     * <b>获取某种类型的所有Field</b>
     * 
     * @param object 对象
     * @param type 类型
     * 
     * @return List<Field>
     */
    public static List<Field> getFieldsByType(Object object, Class type) {
        List list = new ArrayList();
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.getType().isAssignableFrom(type)) {
                list.add(field);
            }
        }
        return list;
    }

    /**
     * <b>获取字段Class类型</b>
     * 
     * @param type Class类型
     * @param name 字段名
     * 
     * @return 字段Class类型
     * @throws NoSuchFieldException
     */
    public static Class getPropertyType(Class type, String name) throws NoSuchFieldException {
        return getDeclaredField(type, name).getType();
    }

    /**
     * <b>获取字段的get方法名</b>
     * 
     * @param type
     * @param fieldName
     * 
     * @return get方法名
     */
    public static String getGetterName(Class type, String fieldName) {
        Assert.notNull(type, "Type required");
        Assert.hasText(fieldName, "FieldName required");
        if (type.getName().equals("boolean")) {
            return "is" + StringUtils.capitalize(fieldName);
        }
        return "get" + StringUtils.capitalize(fieldName);
    }

    /**
     * <b>获取字段的get对应的Method</b>
     * 
     * @param type
     * @param fieldName
     * 
     * @return Method
     */
    public static Method getGetterMethod(Class type, String fieldName) {
        try {
            return type.getMethod(getGetterName(type, fieldName), new Class[0]);
        } catch (NoSuchMethodException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * <b>调用方法</b>
     * 
     * @param className 类名
     * @param methodName 方法名
     * @param argsClass 方法参数类型
     * @param args 方法参数
     * 
     * @return Object
     * @throws ClassNotFoundException
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    public static Object invoke(String className, String methodName, Class[] argsClass, Object[] args) throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class cl = Class.forName(className);
        Method method = cl.getMethod(methodName, argsClass);
        return method.invoke(cl.newInstance(), args);
    }

    /**
     * <b>调用方法</b>
     * 
     * @param oldObject 对象
     * @param methodName 方法名
     * @param argsClass 方法参数类型
     * @param args 方法参数
     * 
     * @return Object
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static Object invoke(Object oldObject, String methodName, Class[] argsClass, Object[] args) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Class cl = oldObject.getClass();
        Method method = cl.getMethod(methodName, argsClass);
        return method.invoke(oldObject, args);
    }

    /**
     * <b>获取所有字段名称</b>
     * 
     * @param cl Class类型
     * 
     * @return String[]
     * @throws Exception
     */
    public static String[] getFieldsName(Class cl) throws Exception {
        Field[] fields = cl.getDeclaredFields();
        String[] fieldNames = new String[fields.length];
        for (int i = 0; i < fields.length; ++i) {
            fieldNames[i] = fields[i].getName();
        }
        return fieldNames;
    }

    /**
     * <b>获取所有字段名称（包括父类）</b>
     * 
     * @param cl Class类型
     * 
     * @return List<String>
     */
    public static List<String> getAllFieldName(Class cl) {
        List list = new ArrayList();
        Field[] fields = cl.getDeclaredFields();
        for (int i = 0; i < fields.length; ++i) {
            Field field = fields[i];
            if (field.getName().equals("serialVersionUID")) {
                continue;
            }
            list.add(field.getName());
        }
        while (true) {
            cl = cl.getSuperclass();
            if (cl == Object.class) {
                break;
            }
            list.addAll(getAllFieldName(cl));
        }
        return list;
    }

    /**
     * <b>获取所有Set方法（包括父类）</b>
     * 
     * @param cl  Class类型
     * 
     * @return List<Method>
     */
    public static List<Method> getSetter(Class cl) {
        List list = new ArrayList();
        Method[] methods = cl.getDeclaredMethods();
        for (int i = 0; i < methods.length; ++i) {
            Method method = methods[i];
            String methodName = method.getName();
            if (!(methodName.startsWith("set"))) {
                continue;
            }
            list.add(method);
        }
        while (true) {
            cl = cl.getSuperclass();
            if (cl == Object.class) {
                break;
            }
            list.addAll(getSetter(cl));
        }
        return list;
    }

    /**
     * <b>获取所有Get方法（包括父类）</b>
     * 
     * @param cl Class类型
     * 
     * @return List<Method> 
     */
    public static List<Method> getGetter(Class cl) {
        List list = new ArrayList();
        Method[] methods = cl.getDeclaredMethods();
        for (int i = 0; i < methods.length; ++i) {
            Method method = methods[i];
            String methodName = method.getName();
            if ((!(methodName.startsWith("get"))) && (!(methodName.startsWith("is")))) {
                continue;
            }
            list.add(method);
        }
        while (true) {
            cl = cl.getSuperclass();
            if (cl == Object.class) {
                break;
            }
            list.addAll(getGetter(cl));
        }
        return list;
    }

    /**
     * <b>获取类名（不包括包名）</b>
     * 
     * @param cl Class类型
     * @return String
     */
    public static String getClassNameWithoutPackage(Class cl) {
        String className = cl.getName();
        int pos = className.lastIndexOf(46) + 1;
        if (pos == -1) {
            pos = 0;
        }
        return className.substring(pos);
    }

    /**
     * <b>对象转换为字符串</b>
     * 
     * @param obj
     * @return String
     */
    public static String beanToString(Object obj) {
        return ToStringBuilder.reflectionToString(obj);
    }
}