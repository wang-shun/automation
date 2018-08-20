package com.gome.test.gui.execute;

import com.gome.test.context.ContextUtils;
import com.gome.test.gui.BusinessBase;
import com.gome.test.Constant;
import com.gome.test.gui.annotation.Given;
import com.gome.test.gui.annotation.Then;
import com.gome.test.gui.annotation.When;
import com.gome.test.gui.helper.BasePage;
import com.gome.test.gui.helper.BrowserType;
import com.gome.test.gui.helper.Settings;
import com.gome.test.gui.model.Case;
import com.gome.test.gui.model.Step;
import com.gome.test.utils.*;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.TimeoutException;
import org.testng.Assert;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

public class GuiExecutor {
    List<Case> cases = new ArrayList<Case>();
    Set<String> caseIds = new HashSet<String>();
    Set<String> caseNames = new HashSet<String>();

    Map<String, MethodWithSubClass> keyAndMethod = new HashMap<String, MethodWithSubClass>();
    private String filePath;
    private Map<String, BusinessBase> businessMap = new HashMap<String, BusinessBase>();
    private List<String> urlList=null;
    private static final String BUSINESS_PACKAGE = "com.gome.test.gui.business";
    private File screenCaptureDir;

    private GuiExecutor() {
        urlList=new ArrayList<String>();
    }

    public GuiExecutor(String filePath) throws Exception {
        urlList=new ArrayList<String>();
        this.filePath = filePath;
        loadCase(new File(this.filePath), this.cases, this.caseIds, this.caseNames);
        initScreenCaptureDir();
        loadKeyAndMethod();
    }

    public GuiExecutor(List<Case> cases, String filePath) throws Exception {
        this.filePath = filePath;
        urlList=new ArrayList<String>();
        this.cases = cases;
//        initScreenCaptureDir();ide使用 不用截图
//        initScreenCaptureDir();
        loadKeyAndMethod();
    }

    private void initScreenCaptureDir() {
        String outPath=String.format("%s%sscreenCapture", this.getClass().getResource("/").getPath(), File.separator);
        this.screenCaptureDir = new File(outPath);
        ContextUtils.getContext().put("gui_PicturePath",outPath);
    }

    public void run(String caseId) throws Throwable {
        ContextUtils.BeginTestCase();

        run(caseId, true);

        ContextUtils.EndTestCase();

    }

    private boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            //递归删除目录中的子目录下
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

    public void run(String caseId, boolean closeBrowseAfterCompleted) throws Throwable {

        String key="gui_HasTimeOutError";

        for (Case c : cases) {

            //当前case加入上下文中
            ContextUtils.getContext().put(String.format("currentCase"),c);
            ContextUtils.getContext().put(key,false);

            if (caseId != null && caseId.isEmpty() == false && caseId.equals(c.getId()) == false)
                continue;
            Logger.info("Case [%s:%s] 开始", c.getId(), c.getName());
            String configFilePath = String.format("%ssrc%smain%sresources%s%s", getHelperPath(this.filePath), File.separator,File.separator,File.separator, Settings.SELENIUM_PROPERTIES);



            if (BasePage.driver == null) {
                BasePage.initDriver(configFilePath);
            } else {
                try {
                    if(BasePage.getDriverType()!= BrowserType.IPAD_SAFARI)
                        BasePage.driver.switchTo().window(ContextUtils.getFromContext(BasePage.START_BROWSE_ID));
                } catch (Exception ex) {
                    BasePage.initDriver(configFilePath);
                }
            }

            BasePage.openAndSwitchtoCaseWindow();
            String stepKey="";
            int index=0;

            try {
                for (Step step : c.getSteps()) {
                    stepKey =step.getKey();
                    if (this.keyAndMethod.containsKey(step.getKey()) == false)
                        throw new Exception(String.format("%s 未定义", step.getKey()));
                    //当前case加入上下文中
                    ContextUtils.getContext().put(String.format("currentStep"),step);
                    invokeMethod(step);
                    if(BasePage.isPrint() && screenCaptureDir != null ) {
                        String name =c.getName()+"_"+(++index)+"-"+stepKey+"-S" ;
                        BasePage.helper.captureScreenShot(name,1100,screenCaptureDir);
                    }

                }
            }
            catch (Throwable ex) {
                if( screenCaptureDir != null ) {
                    String name = c.getName()+"_"+ (++index) +"-"+stepKey+"-F" ;
                    BasePage.helper.captureScreenShot(name,1100,screenCaptureDir);
                }

                if (closeBrowseAfterCompleted) {
                    if (null == this.screenCaptureDir)
                        Logger.error("screenCaptureDir 未初始化");
                }

                ContextUtils.EndTestCase();

                throw ex;
            } finally {

                BasePage.closeAllCaseWindows();
            }

            if ((Boolean) ContextUtils.getContext().get(key)) {
                Assert.assertTrue(false, "页面加载超时");
            }

            Logger.info("Case [%s:%s] 结束", c.getId(), c.getName());
            ContextUtils.getContext().remove(String.format("currentCase"));
        }

        if (closeBrowseAfterCompleted)
            BasePage.closeAllCaseWindows();


    }

    private void invokeMethod(Step step) throws Throwable {
        MethodWithSubClass methodWithSubClass = this.keyAndMethod.get(step.getKey());
        Method method = methodWithSubClass.getMethod();
        String param = step.getValue();

        String packageName = methodWithSubClass.getSubClass().getName();
        packageName = packageName.substring(0, packageName.lastIndexOf("."));

        Class<?>[] paramTypes = method.getParameterTypes();
        if (paramTypes.length > 1)
            throw new Exception(String.format("%s 最多只能有一个参数!", method.getName()));

        Class<?> cls = methodWithSubClass.getSubClass();
        BusinessBase business;
        if (businessMap.containsKey(cls.getName()) == false) {
            business = (BusinessBase) cls.newInstance();
            businessMap.put(cls.getName(), business);
        } else {
            business = businessMap.get(cls.getName());
        }

        if (paramTypes.length == 0) {
            GuiProxy.process(business, method, null, step.getKey());
        } else if (paramTypes.length == 1) {
            Object par = null ;
            if(param.startsWith("${")&& param.endsWith("}"))
            {
               par =   ContextUtils.loadValueWithContext(param);
            }
            else if (param.startsWith("$")) {
                par = getMethodValue(param.substring(1), packageName, paramTypes);
            }
            else {
                par = param;
            }

            if (par == null)
                throw new Exception(String.format("%s 参数获取失败!", method.getName()));

            GuiProxy.process(business, method, par, step.getKey());
        }
    }

    private Object getMethodValue(String param, String packageName, Class<?>[] paramTypes) throws Exception {

        String dataFilePath = String.format("%s%sdata.xlsx", filePath.substring(0, filePath.lastIndexOf(File.separator)), File.separator);
        dataFilePath=dataFilePath.replace(String.format("%scase%s", File.separator, File.separator),String.format("%sdata%s", File.separator, File.separator));

        File file = new File(dataFilePath);

        if (file.exists() == false)
            throw new Exception(String.format("%s 不存在!", dataFilePath));

        InputStream in = new FileInputStream(file);

        try {
            Workbook workbook = new XSSFWorkbook(in);
            int sheetNum = workbook.getNumberOfSheets();
            for (int i = 0; i < sheetNum; ++i) {
                Iterator<Row> rowIter = workbook.getSheetAt(i).iterator();
                List<String> heads = ExcelUtils.getHeadersFrom(rowIter.next());
                for (int j = 0; j < heads.size(); j++)
                    heads.set(j, heads.get(j).toLowerCase());

                if (heads.get(0).equals("name") == false)
                    throw new Exception(String.format("[%s]第一列不是Name!", dataFilePath));

                Row row = null;
                if (param.contains(Constant.DOT))
                    row = findDataRowByName(rowIter, param.substring(0, param.indexOf(Constant.DOT)));
                else
                    row = findDataRowByName(rowIter, param);

                if (row != null)
                    return generateParam(paramTypes, row, heads,
                            param.contains(Constant.DOT) ? param.substring(param.indexOf(Constant.DOT) + 1).toLowerCase() : null);
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (in != null)
                in.close();
        }

        Logger.error("name为[%s]的数据在不存在,返回null!", param);
        return null;
    }

    private Row findDataRowByName(Iterator<Row> rowIter, String name) {
        while (rowIter.hasNext()) {
            Row row = rowIter.next();
            if (ExcelUtils.getValueOfCell(row.getCell(0)).toString().equals(name)) {
                return row;
            }
        }

        return null;
    }

    private Object generateParam(Class<?>[] paramTypes, Row row, List<String> heads, String colName) throws Exception {
        if (paramTypes.length > 0) {
            if (colName != null)//认为是String
            {
                if (heads.contains(colName) == false) {
                    throw new Exception(String.format("列[%s]不存在!", colName));
                }

                for (int i = 0; i < heads.size(); i++) {
                    if (heads.get(i).equals(colName))
                        return ContextUtils.loadValueWithContext(ExcelUtils.getValueOfCell(row.getCell(i)).toString()) ;
                }
            } else {
                Object param = paramTypes[0].newInstance();
                for (Method m : paramTypes[0].getDeclaredMethods()) {
                    if (m.getName().startsWith(Constant.SET)) {
                        m.setAccessible(true);
                        String propName = m.getName().substring(3).toLowerCase();
                        if (heads.contains(propName) == false) {
                            throw new Exception(String.format("列[%s]不存在!", propName));
                        }

                        for (int i = 0; i < heads.size(); i++) {
                            if (heads.get(i).equals(propName))
                                m.invoke(param, ContextUtils.loadValueWithContext(ExcelUtils.getValueOfCell(row.getCell(i)).toString()));
                        }
                    }
                }

                return param;
            }
        }

        return null;
    }

    private void loadKeyAndMethod() throws Exception {
        List<Class> subClasses = ReflectionUtils.scanSubclassInPackage(BUSINESS_PACKAGE, BusinessBase.class);

        for (Class<?> subType : subClasses) {
            for (Method method : subType.getMethods())
                appendMethod(new MethodWithSubClass(method, subType));
        }
    }

    private void appendMethod(MethodWithSubClass method) throws Exception {
        Annotation[] annotations = method.getMethod().getDeclaredAnnotations();
        for (Annotation annotation : annotations) {
            Class cls = annotation.annotationType();
            if (cls == Given.class || cls == Then.class || cls == When.class) {
                Method m = cls.getMethod(Constant.DESCRIPTION, new Class<?>[0]);
                String description = m.invoke(annotation, new Object[0]).toString();

                if (this.keyAndMethod.containsKey(description) == false)
                    this.keyAndMethod.put(description, method);
                else if (method.getMethod().getDeclaringClass().getName().equals(BusinessBase.class.getName()) == false)
                    Logger.info("关键字 [%s] 已经存在", description);
            }
        }
    }

    public static void loadCase(File resourceFolder, List<Case> cases, Set<String> caseIds, Set<String> caseNames) throws Exception {
        if (resourceFolder.exists() == false)
            throw new Exception(String.format("%s 不存在", resourceFolder.getAbsolutePath()));

        if (resourceFolder.isFile()) {
            if (Constant.GLOBLE_XLSX.equals(resourceFolder.getName()) == false
                    && Constant.ORDERCASE_XLSX.equals(resourceFolder.getName()) == false)
                TestCaseUtils.loadCaseByFile(resourceFolder, cases, caseIds, caseNames);
        } else {
            for (File file : resourceFolder.listFiles()) {
                TestCaseUtils.loadCaseByFile(file, cases, caseIds, caseNames);
            }
        }
    }



    class MethodWithSubClass {
        Method method;
        Class<?> subClass;

        public MethodWithSubClass(Method method, Class subClass) {
            this.method = method;
            this.subClass = subClass;
        }

        public Method getMethod() {
            return method;
        }

        public void setMethod(Method method) {
            this.method = method;
        }

        public Class<?> getSubClass() {
            return subClass;
        }

        public void setSubClass(Class<?> subClass) {
            this.subClass = subClass;
        }
    }

    private static String getResourcesPath(String folderPath) {
        return folderPath.substring(0, folderPath.lastIndexOf(Constant.RESOURCES) + Constant.RESOURCES.length());
    }

    private static String getHelperPath(String folderPath)
    {
        return folderPath.substring(0, folderPath.lastIndexOf(String.format("%sHelper%s",File.separator,File.separator)) + String.format("%sHelper%s", File.separator, File.separator).length());
    }

    private static String getRelativePath(File xmlFile) {
        String folderPath = xmlFile.getParentFile().getAbsolutePath();
        return folderPath.substring(folderPath.lastIndexOf(Constant.RESOURCES) + Constant.RESOURCES.length() + 1);
    }

    public static void main(String[] strings) throws Throwable {
        GuiExecutor executor = new GuiExecutor("/Users/zhangliang/SourceCode/sample/Trunk/GUITest/Helper/src/main/resources/com/gome/test/gui/case/login");
        executor.run(null);
    }


}
