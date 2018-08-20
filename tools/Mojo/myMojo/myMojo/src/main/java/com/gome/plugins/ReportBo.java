package com.gome.plugins;

import com.gome.dao.MongolDBDao;
import com.mongodb.WriteResult;

public class ReportBo {


    MongolDBDao mongol = new MongolDBDao();

    public WriteResult insert(String json, String collectionName) {
        System.out.println(json+" tableName:"+ collectionName);

        return mongol.insert(json, collectionName);
    }


}
