package com.gome.test.mock.bo;


import java.lang.reflect.Method;
import java.util.*;


import com.gome.test.api.annotation.Step;
import com.gome.test.api.model.MenuDescriptor;
import org.springframework.stereotype.Component;

/**
 * Created by Administrator on 2015/10/22.
 */

@Component
public class MenuBo {

    public List<MenuDescriptor> getStepMenu(String stepClass) throws Exception {

       List<MenuDescriptor>   result=new ArrayList<>();
        Class cc = Class.forName(stepClass);
        Method[] methods = cc.getMethods();
        for (Method method : methods) {
            Step annotation = (Step) method.getAnnotation(Step.class);
            if (null != annotation) {
                List<String> params = new ArrayList<String>();
                String des=annotation.description();
                String methonds=method.getName();
                String type=Step.class.getSimpleName();
                for (int i = 0; i < method.getParameterTypes().length; ++i) {
                    params.add(method.getParameterTypes()[i].getSimpleName());
                }
                result.add(new MenuDescriptor(des,methonds,type,params));
            }
        }

        Collections.sort(result);
        return result;
    }


}