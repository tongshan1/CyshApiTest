package utils;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Replace {


    public static String replaceOutValidator(String params){

        Map outValidator = OutValidator.OUT_VALIDATOR.outValidator;

        params = replaceValidator(outValidator, params);

        return   params;
    }

    public static Object replaceOutValidator(Object params){

        Map outValidator = OutValidator.OUT_VALIDATOR.outValidator;

        params = replaceValidator(outValidator, params);

        return   params;
    }

    public static  String replaceValidator(Map validators, String str){


        String rgex = "\\$\\((.*?)\\)";
        Pattern pattern = Pattern.compile(rgex);// 匹配的模式
        Matcher matcher = pattern.matcher(str);

        while (matcher.find()) {
            int i = 1;
            String key = matcher.group(i);
            if(validators.containsKey(key)){
                String value = (String) validators.get(key);
                String rgexKey = "$("+key+")";
                str = str.replace(rgexKey, value);

            }
            i++;
        }

        return   str;
    }

    public static  Object replaceValidator(Map validators, Object params){

        if(params instanceof String){
            params = replaceValidator(validators, (String)params);
            return params;
        }else if(params instanceof Map){
            Map<String, Object> paramMap = (Map<String, Object>) params;
            for(String key: paramMap.keySet()){
                Object value = paramMap.get(key);
                value = replaceValidator(validators, value);
                paramMap.put(key, value);
            }
            return paramMap;
        }else if(params instanceof List){

            List<Object> paramList = (List<Object>)params;

            for(int i = 0; i<paramList.size(); i++){
                Object value = paramList.get(i);
                value = replaceValidator(validators, value);
                paramList.set(i, value);
            }
            return paramList;
        }

        return params;
    }


    public static void main(String [] args){
//        OutValidator.OUT_VALIDATOR.outValidator.put("code", "success");
//        OutValidator.OUT_VALIDATOR.outValidator.put("name", "hahahahahah");
//
//        System.out.println(OutValidator.OUT_VALIDATOR.outValidator);
//        String tmp = "$$(code)asdhaks$(code111)jdhks$(name)ajd";
//        System.out.println(Replace.replaceOutValidator(tmp));

        Map validators = new HashMap();
        validators.put("string", "11111");
        validators.put("list", "2222");
        validators.put("stringListMapkey", "33333");

        Map<String, Object> testMap = new HashMap<String, Object>();
        List<Object> testList = new ArrayList<Object>();
        Map<String, Object> testListMap = new HashMap<String, Object>();
        testListMap.put("stringListMapkey", "$(stringListMapkey)");
        testList.add("$(list)");
        testList.add(testListMap);
        testMap.put("maplistkey", testList);
        testMap.put("stringkey", "$(string)");


        System.out.println(testMap);
        System.out.println(validators);

        replaceValidator(validators, testMap);

        System.out.println(testMap);

    }


//    private  String getOutValidator(String key){
//
//        String value="";
//
//        if(outValidator.containsKey(key)){
//            value = (String)outValidator.get(key);
//
//        }
//
//        return value;
//    }

}
