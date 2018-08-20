package com.gome.test.api.model;

import com.gome.test.api.model.Entities;

import org.testng.Assert;
import org.testng.annotations.Test;

public class EntitiesTest {

    @Test
    public void testLoadFromNull() {
        Entities entities = Entities.loadFrom(null);
        Assert.assertNull(entities);
    }
}
