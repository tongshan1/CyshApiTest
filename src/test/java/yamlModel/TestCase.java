package yamlModel;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import utils.Replace;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Setter
@Getter
@ToString
public class TestCase {

    // name
    private  String name;

    //config
    private Config config;

    //request
    private List<Request> requests;

    public void replace(){

        // 先替换config里面的变量
        Map variables = (this.config.getVariables() != null)?(this.config.getVariables()):(new HashMap());
        this.name = Replace.replaceOutValidator(this.name);
        this.name = Replace.replaceValidator(variables, this.name);

        if(this.requests != null && !this.requests.isEmpty()){
            for(int i=0; i<this.requests.size(); i++){
                this.requests.get(i).replace(variables);
            }
        }

    }

}
