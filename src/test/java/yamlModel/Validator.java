package yamlModel;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import utils.Replace;

import java.util.Map;

@Setter
@Getter
@ToString
public class Validator {

    // response ?
    //Todo

    //check
    private  String check;

    // comparator
    private  Comparator comparator;

    //expected
    private  String expected;

    //message
    private String message;

    public void replace(Map validators){
        // 替换 out 里面的参数
        this.check = Replace.replaceOutValidator(this.check);
        this.expected = Replace.replaceOutValidator(this.expected);
        this.message = Replace.replaceOutValidator(this.message);

        //替换 config里面的参数
        this.check = Replace.replaceValidator(validators, this.check);
        this.expected = Replace.replaceValidator(validators, this.expected);
        this.message = Replace.replaceValidator(validators, this.message);


    }

}
