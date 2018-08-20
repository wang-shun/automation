package test.Jsonpath;


import com.jayway.jsonpath.JsonPath;

/**
 * Created by liangwei-ds on 2016/9/23.
 */
public class Json {

    public String readJson(String response,String jsonPath) throws Exception {
        String value = readValueFrom(response, jsonPath);
//        Logger.info("得到Value：" + value);
        return value;
    }

    public String readValueFrom(String response,String jsonpath) throws Exception{
        return readValueFrom(response,jsonpath,true);
    }

    private String readValueFrom(String response, String jsonPath, boolean bErrorIfNotExists) throws Exception {
        String value = null;
        try {
            value = JsonPath.read(response, jsonPath).toString();
        } catch (Exception ex) {
            if (bErrorIfNotExists) {
                throw new Exception(String.format("json path [%s]不存在", jsonPath));
            }
        }
        return value;
    }
}
