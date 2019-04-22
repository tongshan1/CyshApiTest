package yamlModel;

import io.restassured.response.Response;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import utils.Replace;

import java.util.List;
import java.util.Map;

@Setter
@Getter
@ToString
public class Request {

    // url
    private  String url;

    // method
    private  String method;

    // path
    private  Map<String,Object> path;
    // header
    private  Map<String,Object> headers;
    // query
    private  Map<String,Object> query;
    // body
    private  Map<String,Object> body;

    //Validator
    private List<Validator> validators;

    // out
    private List<String> outs;

    // response
    private Response response;

    public Request() {

    }


    public void replace(Map validators){

        // 替换out里的变量
        this.url = Replace.replaceOutValidator(this.url );
        this.path = (Map<String, Object>)Replace.replaceOutValidator(this.path);
        this.headers = (Map<String, Object>)Replace.replaceOutValidator(this.headers);
        this.query = (Map<String, Object>)Replace.replaceOutValidator(this.query);
        this.body = (Map<String, Object>)Replace.replaceOutValidator(this.body);

        if(this.outs != null && !this.outs.isEmpty()){
            for(int i=0; i<this.outs.size(); i++){
                this.outs.set(i, Replace.replaceOutValidator(this.outs.get(i)));
            }
        }


        //替换config里的变量
        this.url = Replace.replaceValidator(validators, this.url );
        this.path = (Map<String, Object>)Replace.replaceValidator(validators, this.path);
        this.headers = (Map<String, Object>)Replace.replaceValidator(validators, this.headers);
        this.query = (Map<String, Object>)Replace.replaceValidator(validators, this.query);
        this.body = (Map<String, Object>)Replace.replaceValidator(validators, this.body);

        if(this.outs != null && !this.outs.isEmpty()){
            for(int i=0; i<this.outs.size(); i++){
                this.outs.set(i, Replace.replaceValidator(validators, this.outs.get(i)));
            }
        }

        if(this.validators != null && !this.validators.isEmpty()){
            for(int i =0; i<this.validators.size(); i++){
                this.validators.get(i).replace(validators);
            }
        }

    }




}
