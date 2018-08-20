package com.gome.test.gtp;

import com.gome.test.gtp.model.TaskList;
import com.gome.test.gtp.utils.Constant;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.w3c.dom.*;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Mojo(name = "gtp")
 * @goal spiltCase
 * @requiresDependencyResolution compile+runtime
 * @requiresProject false
 * jenkins slave 执行的第一个步骤。更新Task状态
 * 示例 mvn gtp:spiltCase -DtaskListId=1
 */
@SpringApplicationConfiguration(classes = Application.class)
public class SpiltCaseMojo extends AbstractMojo {

    /**
     * @parameter
     * expression="${project.basedir}"
     */
    private File basedir;

    /**
     * @parameter
     * expression="${taskListId}"
     */
    private int taskListId;

    private TaskInfoBo taskInfoBo = new TaskInfoBo();

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        System.out.println("---------------------SpiltCaseMojo begin---------------------");

        TaskList list = taskInfoBo.getTaskListByIdAndCheck(taskListId, Constant.JOB_RUNNING);

        String log = null;

        try {
            log = removeNoNeedCase(list);
            System.out.println(log);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            log = ex.getMessage();
        }

        System.out.println("---------------------SpiltCaseMojo end---------------------");
    }

    private String removeNoNeedCase(TaskList list) throws Exception {
        File file = new File(basedir.getParent(), Constant.TESTNG_XML_DIR);
        List<String> caseToKeep = Arrays.asList(list.getCaseLists().split(","));

        StringBuffer log = new StringBuffer();

        if (file.exists()) {
            log.append(String.format("taskListId : %d 将保留以下case:%s\n", this.taskListId, list.getCaseLists()));

            List<File> fileToDelete = new ArrayList<File>();
            if (file.isDirectory()) {
                for (File f : file.listFiles()) {
                    if (f.getName().endsWith("testng.xml")) { //OrderCase暂时不处理
                        removeCases(f, caseToKeep);
                        log.append(String.format("%s 处理完毕\n", f.getAbsolutePath()));
                    } else if (f.getName().endsWith(".xml") && caseToKeep.contains(f.getName().replace("testng-", "").replace(".xml", "")) == false) {
                        fileToDelete.add(f);
                    }
                }
            }

            for (File f : fileToDelete) {
                f.delete();
                System.out.println(String.format("%s 已删除", f.getAbsolutePath()));
            }

            log.append(String.format("taskListId : %s 删除case完毕", this.taskListId));

            removeCaseInPom(fileToDelete);
        } else {
            log.append(String.format("taskListId : %s ,%s不存在\n", this.taskListId, file.getAbsolutePath()));
        }

        return log.toString();
    }

    private void removeCaseInPom(List<File> fileToDelete) throws ParserConfigurationException, IOException, SAXException, TransformerException {
        File pom = new File(basedir.getParent(), Constant.POM_FILE);

        if (pom.isFile() && pom.exists()) {
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document xmldoc = db.parse(pom);
            Element root = xmldoc.getDocumentElement();
            NodeList xmlFiles = root.getElementsByTagName("suiteXmlFile");
            List<Node> nodeToDelete = new ArrayList<Node>();

            for (int i = 0; i < xmlFiles.getLength(); i++) {
                String content = xmlFiles.item(i).getTextContent();
                if (content != null && content.isEmpty() == false) {
                    for (File xml : fileToDelete) {
                        if (content.endsWith(xml.getName()))
                            nodeToDelete.add(xmlFiles.item(i));
                    }
                }
            }

            for (Node node : nodeToDelete) {
                node.getParentNode().removeChild(node);
                System.out.println(String.format("%s 删除完毕", node.getTextContent()));
            }

            saveXmlFile(xmldoc, pom);

        } else {
            System.out.println(String.format("%s 不存在", pom.getAbsolutePath()));
        }
    }

    private void removeCases(File testNGXml, List<String> caseToKeep) throws Exception {
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        db.setEntityResolver(new EntityResolver() {
            public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
                return new InputSource(new StringBufferInputStream(""));
            }
        });
        if (testNGXml.isFile() && testNGXml.exists()) {
            Document xmldoc = db.parse(testNGXml);
            Element root = xmldoc.getDocumentElement();
            List<Node> nodeToDelete = new ArrayList<Node>();
            NodeList children = root.getElementsByTagName("include");
            for (int i = 0; i < children.getLength(); i++) {
                NamedNodeMap map = children.item(i).getAttributes();
                if (map != null && map.getLength() > 0) {
                    if (caseToKeep.contains(map.getNamedItem("name").getNodeValue()) == false)
                        nodeToDelete.add(children.item(i).getParentNode().getParentNode().getParentNode().getParentNode());
                }
            }

            System.out.println(String.format("testNGXml:%s 开始处理", testNGXml.getAbsolutePath()));

            for (Node node : nodeToDelete) {
                root.removeChild(node);
                System.out.println(String.format("caseID:%s 删除完毕", node.getAttributes().getNamedItem("name").getNodeValue()));
            }

            saveXmlFile(xmldoc, testNGXml);
        } else {
            System.out.println(String.format("testNGXml : %s 不存在", testNGXml.getAbsolutePath()));
        }
    }

    private void saveXmlFile(Document xmldoc, File xmlFile) throws TransformerException, FileNotFoundException {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty("indent", "yes");
        DOMSource source = new DOMSource();
        source.setNode(xmldoc);
        StreamResult result = new StreamResult();
        result.setOutputStream(new FileOutputStream(xmlFile.getAbsoluteFile()));
        transformer.transform(source, result);
    }

    private Node findChildNodeByTagName(Node parent, String nodeName) {
        NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            {
                if (children.item(i).getNodeName().equals(nodeName)) {
                    return children.item(i);
                }
            }
        }
        return null;
    }

    public static void main(String[] strings) throws Exception {
//        SpiltCaseMojo m = new SpiltCaseMojo();
//        m.basedir = new File("/Users/zhangliang/SourceCode/sample//Branches/APITestV2.0.0/TestProject/");
//        m.taskListId = 2950;
//        m.execute();
//        File testNGXml = new File("D:\\svn\\SVNCode\\SourceCode\\sample\\Trunk\\APITest\\TestProject\\src\\test\\resources\\testng\\testng.xml");
        File testNGXml = new File("D:\\svn\\SVNCode\\NewGUITest\\GUITest\\ProductSample\\TestProject\\src\\test\\resources\\testng\\testng.xml");
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        db.setEntityResolver(new EntityResolver() {
            public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
                return null;
            }
        });
        if (testNGXml.isFile() && testNGXml.exists()) {
            Document xmldoc = db.parse(testNGXml);
            Element root = xmldoc.getDocumentElement();
            NodeList children = root.getElementsByTagName("include");
            System.out.println(children.toString());
        }
    }
}
