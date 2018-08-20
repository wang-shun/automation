package test.share;

/**
 * Created by liangwei-ds on 2016/9/22.
 */

public class Verify {
    public String doverify(String response,String expect){
        String is;
        if (response != null){
            if (response.contains(expect)){
                //Logger.info(response);
                 is ="success";
            }else {
                is="failed_notcontains_expect";
            }
        }else {
            is="failed_response_null";
        }
        return is;
    }

}
