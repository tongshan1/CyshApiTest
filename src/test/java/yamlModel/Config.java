package yamlModel;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import utils.Replace;

import java.util.Map;

@Setter
@Getter
@ToString
public class Config {

    // webRoot
    private String webRoot;

    // variables
    private Map<String, Object> variables;

    public void replaceWebRoot(){

        // out 内的变量
        this.webRoot = Replace.replaceOutValidator(this.webRoot);
        this.webRoot = Replace.replaceOutValidator(this.webRoot);
        this.variables = (Map<String, Object>)Replace.replaceOutValidator(this.variables);

    }
}
