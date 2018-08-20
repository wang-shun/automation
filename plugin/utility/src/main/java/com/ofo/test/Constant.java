package com.ofo.test;

public class Constant {
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    public static final String FILE_SEPARATOR = System.getProperty("file.separator");
    public static final String RESOURCES = "resources";
    public static final String MAIN = "main";
    public static final String JAVA = "java";
    public static final String SRC = "src";
    public static final String DOT = ".";
    public static final String BASE = "Base";
    public static final String BY = "by";
    public static final String TAB = "\t";

    public static final String WEB_ELEMENT = "webelement";
    public static final String PAGE = "page";
    public static final String CASE = "case";
    public static final String BUSINESS = "business";
    public static final String FINDBY = "findby";
    public static final String NAME = "name";
    public static final String REF = "ref";
    public static final String WHERE = "where";
    public static final String CACHE = "cache";
    public static final String CAHCHE_LOOKUP = "@CacheLookup";
    public static final String OWNER = "owner";
    public static final String DESC = "desc";
    public static final String SHARE_PAGE = "sharepage";

    public static final String FINDBY_HOW = "how";
    public static final String FINDBY_USING = "using";
    public static final String FINDBY_ID = "id";
    public static final String FINDBY_NAME = "name";
    public static final String FINDBY_CLASS_NAME = "className";
    public static final String FINDBY_CSS = "css";
    public static final String FINDBY_TAG_NAME = "tagName";
    public static final String FINDBY_LINK_TEXT = "linkText";
    public static final String FINDBY_PARTIAL_LINK_TEXT = "partialLinkText";
    public static final String FINDBY_XPATH = "xpath";

    public static final String RESOURCE_CHILDREN = "/children.tpl";
    public static final String RESOURCE_CHILD = "/child.tpl";
    public static final String RESOURCE_PAGE = "/page.tpl";
    public static final String RESOURCE_PAGEBASE = "/pagebase.tpl";
    public static final String RESOURCE_PARENT = "/parent.tpl";
    public static final String RESOURCE_SELF = "/self.tpl";
    public static final String RESOURCE_DOMAIN = "/domain.tpl";
    public static final String RESOURCE_FIELD = "/field.tpl";

    public static final String TEST_PROJECT_FOLDER = "TestProject";
    public static final String HELPER_FOLDER = "Helper";
    public static final String CASE_CATEGORY_PATH = "TestCase/CaseCategory";
    public static final String DOT_XML = ".xml";
    public static final String DOT_XLSX = ".xlsx";
    public static final String DOMAIN = "domain";
    public static final String DOMAIN_PACKAGE = "com.ofo.test.gui.domain";
    public static final String OFO_BASE_PAGE = "BasePage";
    public static final String BUSINESS_PACKAGE = "com.ofo.test.gui.business";
    public static final String PAGE_PACKAGE = "com.ofo.test.gui.page";
    public static final String DATA_XLSX = "data.xlsx";
    public static final String SET = "set";


    public final static String LOCAL_REPOSITORY = "settings.localRepository";


    public static final String API_XML = "/api.xml";
    public static final String API_PROP = "/api.properties";
    public static final String SETUP_CLASS = "setUpClass";
    public static final String TEARDOWN_CLASS = "tearDownClass";
    public static final String SETUP_STEPS = "setUpSteps";
    public static final String TEARDOWN_STEPS = "tearDownSteps";
    public static final String SET_MESSAGE_CLIENT = "setMessageClient";
    public static final String HTTP_URL = "httpUrl";
    public static final String HTTP_METHOD = "httpMethod";
    public static final String HEADERS = "headers";
    public static final String URL_PARAMS = "urlParams";
    public static final String ENTITIES = "entities";
    public static final String VERIFY_CLASS = "verifyClass";
    public static final String SET_HTTP_CLIENT = "setHttpClient";
    public static final String SET_INPUT_PARAMS = "setInputParams";
    public static final String SET_RESPONSE = "setResponse";
    public static final String SET_STATUS_CODE = "setStatusCode";
    public static final String VERIFY_STEPS = "verifySteps";
    public static final String CASE_VARIABLES = "caseVariables";

    public static final String SET_COOKIE = "setCookies";
    public static final String IS_ORDER_CASE = "isOrderCase";

    public static final String DUBBO_SERVICE = "dubboService";
    public static final String DUBBO_METHOD = "dubboMethod";
    public static final String DUBBO_PARAM = "dubboParams";
    public static final String DUBBO_ADDRESS = "dubboAddress";

    public static final String SHOW_DETAIL_LOG = "showDetailLogs";

    public static final String MAIN_DIR_JAVA = "/src/main/java/";
    public static final String MAIN_DIR_TEST = "/src/main/test/";
    public final static String ORDERCASE_XLSX = "OrderCase.xlsx";
    public final static String DUBBO = "dubbo";
    public final static String IMPORT = "import";
    public final static String SPACE = " ";
    public final static String TEST_RESULT_CONTEXT = "TEST_RESULT_CONTEXT";
    public final static String TEST_CONTEXT = "TEST_CONTEXT";
    public final static String APPLICATION_ALL_XML = "classpath:applicationContext-all.xml";
    public final static String XX_WWW_FORM_URLENCODED = "x-www-form-urlencoded";
    public final static String SKIP_ONCE_FAIL = "skipOnceFail";
    public final static String PASSED = "pass";
    public final static String FAILED = "fail";
    public final static String NOT_EXECUTED = "NotExecuted";
    public final static String TEST_CASE_NAME = "testCaseName";
    public final static String CASE_DESC = "caseDesc";
    public final static String RE_RUN = "rerun";
    public final static String RE_RUN_COUNT = "rerunCount";
    public final static String COMPUTER_NAME = "computerName";
    public final static String START_TIME = "startTime";
    public final static String END_TIME = "endTime";
    public final static String DURATION = "duration";
    public final static String TEST_RESULT = "testResult";
    public final static String ERROR_MESSAGE = "errorMessage";
    public final static String STACK_TRACE = "stackTrace";
    public final static String CHILDREN = "children";
    public final static String DESCRIPTION = "description";
    public final static String SUMMARY = "summary";
    public final static String DETAILS = "details";
    public final static String ABORD = "aborted";
    public final static String TOTAL_CASES = "totalCases";
    public final static String UTF_8 = "UTF-8";
    public final static String CASE_OWNER = "caseOwner";
    public final static String UNKNOW = "UnKnow";
    public final static String _NONE = "_None";

    public final static String RESULT_FILE_PATH = "resultFilePath";
    public final static String GENERATE_TIME = "generateTime";
    public final static String DATE = "date";
    public final static String TASK_TYPE = "taskType";
    public final static String TASK_ID = "taskId";
    public final static String SPLIT_TIME = "splitTime";
    public final static String DUBBO_SPRINT_CONFIG = " <dubbo:reference id=\"%s\" interface=\"%s\" loadbalance=\"random\" timeout=\"5000\" version=\"1.0\"/>";
    public final static String DUBBO_SPRINT_CONFIG_FILE = "/src/main/resources/applicationContext-dubbo-consumer.xml";
    public final static String WEBSERVICE_PACKNAME = "com.founder.bbc.webinterface.webservice";
    public final static String TESTS_PATH = "tests.path";
    public final static String WORKSPACE_PATH = "workspace.path";
    public final static String APP_CLASSPATH = "app.classpath";
    public final static String BASE_DIR = "base.dir";
    public final static String BUILD_DIR = "build.dir";
    public final static String SVN_USERNAME = "svn.username";
    public final static String SVN_PASSWORD = "svn.password";
    public final static String HELPER_JAVAAPIPATH = String.format("%scom%sofo%stest%sapi", System.getProperty("file.separator"), System.getProperty("file.separator"), System.getProperty("file.separator"), System.getProperty("file.separator"));

    public final static String GLOBLE_XLSX = "Globle.xlsx";
    public final static String STATUS_SUCC = "SUCCESS";
    public final static String STATUS_FAIL = "FAILURE";

    public final static String ORDER_CASE = "ordercase";
    public final static String SUITE = "suite";
    public final static String ORDER_LIST = "OrderList";
    public final static String ORDER_SUITE = "ordersuite";
    public final static String TEST_SUITES = "testsuites";
    public final static String P_SUITES_ = "p-suites-";
    public final static String P_SUITES = "p-suites";
    public final static String TEST_SUITES_FOR_DEV = "testsuitesForDev";
    public final static String P_SUITES_TPL_ = "p-suites-tpl-";
    public final static String DUBBO_SUITES = "dubbo-";

    public final static String OWNER_FIRST_UPPER = "Owner";
    public final static String CASE_NO = "用例编号";
    public final static String CASE_NAME = "用例名称";
    public final static String CASE_LEVEL = "用例级别";
    public final static String CASE_TAG = "用例标签";
    public final static String CASE_PARAM = "用例参数";
    public final static int TESTNGCOUNT=100;
}
