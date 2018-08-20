package com.gome.test.gui;

import com.gome.test.Constant;
import com.gome.test.plugin.Utils;
import com.gome.test.utils.JsonUtils;
import com.gome.test.utils.Logger;
import com.gome.test.utils.ResourceUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

public class GenPage extends GenBase {

    static Set<String> findByList = new HashSet<String>();
    public static String baseDirPath;


    @Override
    protected void genByFile(File xmlFile) throws Exception {
        if (xmlFile.exists() == false)
            throw new Exception(String.format("%s 不存在", xmlFile.getAbsolutePath()));

        if (xmlFile.getName().endsWith(Constant.DOT_XML) == false)
            return;

        if (xmlFile.getName().startsWith(xmlFile.getParentFile().getName()) == false)
            throw new Exception(String.format("%s 开头必须是 %s!!", xmlFile.getAbsolutePath(), xmlFile.getParentFile().getName()));

        Logger.info(String.format("%s 开始处理", xmlFile.getAbsolutePath()));
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Element root = db.parse(xmlFile).getDocumentElement();

        String packageName = Utils.getPageBasePackageName2(xmlFile);
        boolean isMainPage = xmlFile.getName().indexOf(Constant.DOT) == xmlFile.getName().lastIndexOf(Constant.DOT);

        String baseClass = generatePageBaseSource(root, xmlFile, packageName, isMainPage);
        generatePageSource(xmlFile, packageName, baseClass);
    }

    private static String generatePageBaseSource(Element root, File xmlFile, String packageName, boolean isMainPage) throws Exception {
        //因为传进来文件路径可能带有..路径，所以对比时需要替换..在进行判断
        String xmlFilePath=xmlFile.getAbsolutePath().indexOf("..")>-1? xmlFile.getAbsolutePath().replace("..","&&"):xmlFile.getAbsolutePath();
        //判断是否有parent
        if (xmlFile.isFile() && xmlFilePath.indexOf(".") != xmlFilePath.lastIndexOf(".")) {
            String xmlFileName = xmlFile.getName();
            File parentXmlFile = new File(String.format("%s.xml",
                    xmlFile.getAbsolutePath().substring(0, xmlFile.getAbsolutePath().substring(0, xmlFile.getAbsolutePath().length() - 4).lastIndexOf("."))));

            if (parentXmlFile.exists() == false) {
                System.out.println("getAbsolutePath:" +xmlFile.getAbsolutePath());
                throw new Exception(String.format("%s 的上级xml文件 %s 不存在", xmlFileName, parentXmlFile.getName()));
            }
        }

        String className = String.format("%s%s", Utils.getClassName(xmlFile), Constant.BASE);

        Map<String, String> children = new HashMap<String, String>();

        String webelement = getElement(xmlFile, root, children);
        StringBuffer importChildren = new StringBuffer();
        StringBuffer getChildren = new StringBuffer();
        for (Map.Entry<String, String> child : children.entrySet()) {
            if (importChildren.length() > 0)
                importChildren.append(Constant.LINE_SEPARATOR);

            importChildren.append(String.format("import %s;", child.getValue()));
            if (child.getKey().endsWith("s"))
                getChildren.append(getChildren(child.getValue().substring(child.getValue().lastIndexOf(".") + 1)));
            else
                getChildren.append(getChild(child.getValue().substring(child.getValue().lastIndexOf(".") + 1)));
        }

        String baseClassWithPackageName = getBaseClassWithPackageName(xmlFile, root);
        String pageBase = tpls.get(Constant.RESOURCE_PAGEBASE)
                .replace("@package@", packageName)
                .replaceAll("@class@", Matcher.quoteReplacement(className))
                .replace("@webelement@", webelement)
                .replace("@self@", tpls.get(Constant.RESOURCE_SELF))
                .replace("@parent@", isMainPage ? "" : tpls.get(Constant.RESOURCE_PARENT))
                .replace("@importchildren@", importChildren)
                .replace("@author@", root.getAttribute(Constant.OWNER))
                .replace("@children@", getChildren)
                .replace("@baseClass@", baseClassWithPackageName.substring(baseClassWithPackageName.lastIndexOf(Constant.DOT) + 1))
                .replace("@importbase@", baseClassWithPackageName);

        File sourceFile = new File(baseDirPath,
                String.format("%ssrc%smain%s%s%s%s%s%s%s%s",Constant.FILE_SEPARATOR,Constant.FILE_SEPARATOR,Constant.FILE_SEPARATOR, Constant.JAVA, Constant.FILE_SEPARATOR, Utils.getPageBasePackageName2(xmlFile).replace(Constant.DOT, Constant.FILE_SEPARATOR), Constant.FILE_SEPARATOR, className, Constant.DOT, Constant.JAVA));

        ResourceUtils.saveSourceFile(sourceFile, pageBase, true);

        return className;
    }

    private static String getBaseClassWithPackageName(File xmlFile, Element root) throws Exception {
        String share = root.getAttribute(Constant.SHARE_PAGE);
        if (share != null && share.isEmpty() == false) {
            if (share.startsWith("/") == false)
                throw new Exception(String.format("%s=%s必须以/开头!", Constant.SHARE_PAGE, share));

            File baseFile = new File(xmlFile.getParentFile().getParentFile(), root.getAttribute(Constant.SHARE_PAGE));
            if (baseFile.exists() == false)
                throw new Exception(String.format("%s:%s不存在!", Constant.SHARE_PAGE, baseFile));

            return String.format("%s.%s%s", Utils.getPageBasePackageName2(baseFile), Utils.getClassName(baseFile), Constant.BASE);
        }

        return "com.gome.test.gui.helper.BasePage";
    }

    private static void generatePageSource(File xmlFile, String packageName, String baseClass) throws IOException {
        String className = baseClass.substring(0, baseClass.length() - Constant.BASE.length());

        String pageBase = tpls.get(Constant.RESOURCE_PAGE)
                .replace("@package@", packageName)
                .replace("@class@", className)
                .replace("@baseclass@", baseClass);

        File sourceFile = new File(baseDirPath,
                String.format("%ssrc%smain%s%s%s%s%s%s%s%s",Constant.FILE_SEPARATOR,Constant.FILE_SEPARATOR,Constant.FILE_SEPARATOR, Constant.JAVA, Constant.FILE_SEPARATOR, Utils.getPageBasePackageName2(xmlFile).replace(Constant.DOT, Constant.FILE_SEPARATOR), Constant.FILE_SEPARATOR, className, Constant.DOT, Constant.JAVA));

        ResourceUtils.saveSourceFile(sourceFile, pageBase, false);
    }

    /**
     * @param xmlFile
     * @param root
     * @param children key:childrenfield  value:childTypeWithPackage
     * @return
     * @throws Exception
     */
    private static String getElement(File xmlFile, Element root, Map<String, String> children) throws Exception {
        StringBuffer stringBuffer = new StringBuffer();

        NodeList webelements = root.getElementsByTagName(Constant.WEB_ELEMENT);
        for (int i = 0; i < webelements.getLength(); i++) {
            Node element = webelements.item(i);
            if (element.getNodeName().equals(Constant.WEB_ELEMENT)) {
                if (element.hasChildNodes())
                    stringBuffer.append(getFindBy(getAttr(element, Constant.DESC), getAttr(element, Constant.NAME),getAttr(element, Constant.CACHE), element.getChildNodes()));
                else
                    stringBuffer.append(getFindBy(element));

                String elementName = getAttr(element, Constant.NAME);

                //文件名寻找children
                File childFile = new File(xmlFile.getAbsolutePath().replace(Constant.DOT_XML, String.format("%s%s%s", Constant.DOT,
                        elementName.endsWith("s") ? elementName.substring(0, elementName.length() - 1) : elementName, Constant.DOT_XML)));

                if (childFile.exists() && childFile.isFile()) {
                    children.put(elementName, String.format("%s%s%s", Utils.getPageBasePackageName2(childFile), Constant.DOT, Utils.getClassName(childFile)));
                }

                String ref = getAttr(element, Constant.REF);
                if (ref.isEmpty() == false) { //只允许一级目录 所以可以寻找到根
                    childFile = new File(xmlFile.getParentFile().getParentFile().getParentFile(), ref);
                    Logger.info(String.format("REF: %s 开始处理", childFile.getAbsolutePath()));
                    if (childFile.exists())
                        children.put(elementName, String.format("%s%s%s", Utils.getPageBasePackageName2(childFile), Constant.DOT, Utils.getClassName(childFile)));

                    Logger.info(JsonUtils.toJson(children));
                }
            }

            stringBuffer.append(Constant.LINE_SEPARATOR);
            stringBuffer.append(Constant.LINE_SEPARATOR);
        }

        return stringBuffer.toString();
    }

    private static String getFindBy(String desc, String name,String cache, NodeList bys) throws Exception {
        StringBuffer stringBuffer = new StringBuffer(String.format("\t/**\n" +
                "\t * %s\n" +
                "\t */\n" +
                "%s\t@FindBys({",
                desc,
                cache.toLowerCase().equals("true") ? String.format("\t%s\n", Constant.CAHCHE_LOOKUP) : ""));
        stringBuffer.append(Constant.LINE_SEPARATOR);
        int index = 0;

        for (int i = 0; i < bys.getLength(); i++) {
            Node by = bys.item(i);

            if (by.getNodeName().equals(Constant.BY) == false)
                continue;

            String findBy = getAttr(by, Constant.FINDBY);
            checkFindBy(findBy);

            if (index > 0) {
                stringBuffer.append(",");
                stringBuffer.append(Constant.LINE_SEPARATOR);
            }

            stringBuffer.append(String.format("%s%s%s@FindBy(%s = \"%s\")",
                    Constant.TAB, Constant.TAB, Constant.TAB,
                    findBy,
                    getAttr(by, Constant.WHERE)));

            index++;
        }

        stringBuffer.append(Constant.LINE_SEPARATOR);
        stringBuffer.append(String.format("%s})", Constant.TAB));

        stringBuffer.append(String.format("%s%s%s", Constant.LINE_SEPARATOR, Constant.TAB, getElementParam(name)));
        return stringBuffer.toString();
    }

    private static String getFindBy(Node pageNode) throws Exception {
        String findBy = getAttr(pageNode, Constant.FINDBY);
        checkFindBy(findBy);

        NamedNodeMap map = pageNode.getAttributes();

        return String.format("\t/**\n" +
                        "\t * %s\n" +
                        "\t */\n%s\t@FindBy(%s = \"%s\")%s%s%s",
                map.getNamedItem(Constant.DESC).getNodeValue(),
                map.getNamedItem(Constant.CACHE).getNodeValue().toLowerCase().equals("true") ? String.format("\t%s\n", Constant.CAHCHE_LOOKUP) : "",
                findBy,
                map.getNamedItem(Constant.WHERE).getNodeValue(),
                Constant.LINE_SEPARATOR,
                Constant.TAB,
                getElementParam(map.getNamedItem(Constant.NAME).getNodeValue()));
    }

    private static String getElementParam(String name) {
        if (name.endsWith("s"))
            return String.format("public List<WebElement> %s;", name);
        else
            return String.format("public WebElement %s;", name);
    }

    private static String getAttr(Node node, String attr) throws Exception {
        NamedNodeMap map = node.getAttributes();
        Node n = map.getNamedItem(attr);
        if (n == null)
            return "";
        else
            return map.getNamedItem(attr).getNodeValue();
    }

    private static void checkFindBy(String findBy) throws Exception {
        initFindByList();

        if (findByList.contains(findBy) == false)
            throw new Exception(String.format("不存在 %s 的findby", findBy));
    }

    private static synchronized void initFindByList() {
        if (findByList.isEmpty()) {
            findByList.add(Constant.FINDBY_HOW);
            findByList.add(Constant.FINDBY_USING);
            findByList.add(Constant.FINDBY_ID);
            findByList.add(Constant.FINDBY_NAME);
            findByList.add(Constant.FINDBY_CLASS_NAME);
            findByList.add(Constant.FINDBY_CSS);
            findByList.add(Constant.FINDBY_TAG_NAME);
            findByList.add(Constant.FINDBY_LINK_TEXT);
            findByList.add(Constant.FINDBY_PARTIAL_LINK_TEXT);
            findByList.add(Constant.FINDBY_XPATH);
        }
    }

    private static String getChildren(String className) throws IOException {
        if (className != null && className.isEmpty() == false) {
            return tpls.get(Constant.RESOURCE_CHILDREN).replaceAll("@class@", Matcher.quoteReplacement(className))
                    .replaceAll("@children@", Matcher.quoteReplacement(String.format("%ss", Utils.formatFieldName(className))));
        }

        return "";
    }

    private static String getChild(String className) throws IOException {
        if (className != null && className.isEmpty() == false) {
            return tpls.get(Constant.RESOURCE_CHILD).replaceAll("@class@", Matcher.quoteReplacement(className))
                    .replaceAll("@child@", Matcher.quoteReplacement(String.format("%s", Utils.formatFieldName(className))));
        }

        return "";
    }

    public static void main(String[] strings) throws Exception {
        new GenPage().genByFolder(new File("/Users/zhangliang/SourceCode/sample/Trunk/GUITest/Helper/src/main/resources/"));
//        GenPage.getChildrenClassName(new File("/Users/zhangliang/SourceCode/sample/Trunk/GUITest/Helper/src/main/resources/com/gome/test/gui/page/home/home.xml"));
    }
}
