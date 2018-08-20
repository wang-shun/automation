package com.gome.test.mock.db.base;

import com.gome.test.mock.db.common.DaoManager;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 基类Service
 */
public class BaseService {

    @Autowired
    protected DaoManager daoManager;

}
