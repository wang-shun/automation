package com.gome.test.mock.db.base;

import org.springframework.orm.hibernate4.SessionHolder;

public class MySynchronizationManager {

    private static ThreadLocal<SessionHolder> sessionHolderThreadLocal=new ThreadLocal<SessionHolder>();
    public static SessionHolder getSessionHolder(){
        return sessionHolderThreadLocal.get();
    }
    public static void setSessionHolder(SessionHolder sessionHolder){
        sessionHolderThreadLocal.set(sessionHolder);
    }
    public static void clearSessionHolder(){
        sessionHolderThreadLocal.remove();
    }

    private static ThreadLocal<SessionHolder> readonlySessionHolderThreadLocal=new ThreadLocal<SessionHolder>();
    public static SessionHolder getReadonlySessionHolder(){
        return readonlySessionHolderThreadLocal.get();
    }
    public static void setReadonlySessionHolder(SessionHolder sessionHolder){
        readonlySessionHolderThreadLocal.set(sessionHolder);
    }
    public static void clearReadonlySessionHolder(){
        readonlySessionHolderThreadLocal.remove();
    }

    private static ThreadLocal<Boolean> readonlyThreadLocal=new ThreadLocal<Boolean>();
    public static boolean isReadonly() {
        Boolean bool=readonlyThreadLocal.get();
        return (bool!=null && bool.booleanValue()==true);
    }

    public static void setReadonly(boolean bool) {
        readonlyThreadLocal.set(bool);
    }
    public static void clearReadonlyFlag(){
        readonlyThreadLocal.remove();
    }




}
