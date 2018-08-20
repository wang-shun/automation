package com.gome.test.api;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.*;

import com.gome.test.api.GlobalSettings;
import org.testng.annotations.*;

public class GlobalSettingsExcelTest {

    @Test
    public void testLoadFrom() throws ParseException {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("{$basic.name_001.nameCn}", "外币兑换服务");
        m.put("{$basic.name_001.mhotelid}", "42407057");
        m.put("{$basic.name_001.propertyid}", 19);
        m.put("{$basic.name_001.ppropertyid}", "true");
        m.put("{$basic.name_001.note}", "hello, world");
        m.put("{$basic.name_002.nameCn}", "代客泊车服务");
        m.put("{$basic.name_002.mhotelid}", "42407059");
        m.put("{$basic.name_002.propertyid}", 23.01);
        m.put("{$basic.name_002.ppropertyid}", "false");
        m.put("{$basic.name_002.note}", "hello, world");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        m.put("{$basic.name_003.propertyid}", format.parse("2013-02-01"));
        m.put("{$basic.name_004}", "越界");
        m.put("{$中文.name_001.nameCn}", "外币兑换服务");
        m.put("{$中文.name_001.propertyid}", "19");
        m.put("{$中文.name_001.ppropertyid}", "7");
        m.put("{$中文.name_002.nameCn}", "代客泊车服务");
        m.put("{$中文.name_002.国}", "42407057");
        m.put("{$中文.name_002.ppropertyid}", "7");
        m.put("{$中文.name_003.nameCn}", "a");
        m.put("{$中文.name_003.国}", "a");
        m.put("{$中文.name_003}", "mysql");
        m.put("{$中文.name_003.pp}", "c");
        m.put("{$中文.name_003.propertyid}", "d");
        m.put("{$中文.name_003.ppropertyid}", "d");
        m.put("{$中文.name_003.备注}", "d");
        m.put("{$中文.name_004.nameCn}", "g");
        m.put("{$中文.name_004.国}", "h");
        m.put("{$中文.name_004.pp}", "i");
        m.put("{$中文.name_004.propertyid}", "d");
        m.put("{$中文.name_004.ppropertyid}", "d");
        m.put("{$中文.name_004.备注}", "d");
        m.put("{$Common.SN0002.filed1}", 1.02);
        m.put("{$Common.SN0004.filed5}", 30);
        m.put("{$Common.SN0012.filed1}", "hello, world");
        for (String key : m.keySet()) {
            assertEquals(settings.get(key).toString(), m.get(key).toString(), "key=" + key);
        }
        assertEquals(settings.toString(), m.toString());
    }

    @BeforeMethod
    public void setUpBeforeMethod() throws IOException {
        settings = new GlobalSettings();
        settings.loadFrom(getClass().getResourceAsStream(
                "/global.xlsx"));
    }

    private GlobalSettings settings;
}
