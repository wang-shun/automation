package com.gome.dao;

import com.gome.util.AppConfiguration;
import com.gome.util.Content;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;
import com.mongodb.util.JSON;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import javax.annotation.PostConstruct;
import java.net.UnknownHostException;


/**
 * Created by liangwei on 2016/10/29.
 */
public class MongolDBDao {


    private AppConfiguration evn = new AppConfiguration();
    private MongoTemplate mongo;
    private final static String DEFAULT_ADDRESS="10.144.32.129";
    private final static int DEFAULT_PORT=27017;



    @PostConstruct
    private synchronized void init(){
        if(mongo==null){
            String address = evn.getConfig("mongodb.address")==null?DEFAULT_ADDRESS:evn.getConfig("mongodb.address");
            int port = evn.getConfig("mongodb.port")==null?DEFAULT_PORT:Integer.valueOf(evn.getConfig("mongodb.port"));
            init(address,port);
        }
    }


    public void init(String address,int port){
        if (mongo==null){
            try {
                mongo = new MongoTemplate(new SimpleMongoDbFactory(new MongoClient(address,port), Content.DBname),null);
            }catch (UnknownHostException e){
                e.printStackTrace();
            }

        }
    }

    public MongolDBDao(){
        init();
    }

    public WriteResult insert(String json, String collectionName) {
         WriteResult dd =mongo.getCollection(collectionName).insert((DBObject) JSON.parse(json));
        return dd;

    }
}
