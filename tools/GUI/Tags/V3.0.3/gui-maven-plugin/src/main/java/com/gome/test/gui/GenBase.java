package com.gome.test.gui;

import com.gome.test.Constant;
import com.gome.test.utils.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class GenBase {
    protected static Map<String, String> tpls = new HashMap<String, String>();

    private synchronized void initTpls() throws IOException {
        if (tpls.isEmpty()) {
            tpls.put(Constant.RESOURCE_CHILDREN, ResourceUtils.getSourceTemplateString(this.getClass(), Constant.RESOURCE_CHILDREN));
            tpls.put(Constant.RESOURCE_PAGE, ResourceUtils.getSourceTemplateString(this.getClass(), Constant.RESOURCE_PAGE));
            tpls.put(Constant.RESOURCE_PAGEBASE, ResourceUtils.getSourceTemplateString(this.getClass(), Constant.RESOURCE_PAGEBASE));
            tpls.put(Constant.RESOURCE_PARENT, ResourceUtils.getSourceTemplateString(this.getClass(), Constant.RESOURCE_PARENT));
            tpls.put(Constant.RESOURCE_SELF, ResourceUtils.getSourceTemplateString(this.getClass(), Constant.RESOURCE_SELF));
            tpls.put(Constant.RESOURCE_DOMAIN, ResourceUtils.getSourceTemplateString(this.getClass(), Constant.RESOURCE_DOMAIN));
            tpls.put(Constant.RESOURCE_FIELD, ResourceUtils.getSourceTemplateString(this.getClass(), Constant.RESOURCE_FIELD));
            tpls.put(Constant.RESOURCE_CHILD, ResourceUtils.getSourceTemplateString(this.getClass(), Constant.RESOURCE_CHILD));
        }
    }

    public void genByFolder(File resourceFolder) throws Exception {
        initTpls();

        if (resourceFolder.exists() == false)
            throw new Exception(String.format("%s 不存在", resourceFolder.getAbsolutePath()));

        for (File file : resourceFolder.listFiles()) {

            if (file.isDirectory())
                genByFolder(file);
            else if (file.getName().endsWith(Constant.DOT_XML) || file.getName().endsWith(Constant.DOT_XLSX) )
                genByFile(file);
        }
    }

    protected abstract void genByFile(File resourceFile) throws Exception;
}
