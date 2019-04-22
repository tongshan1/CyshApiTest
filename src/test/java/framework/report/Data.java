package framework.report;

import lombok.Getter;
import lombok.Setter;
import yamlModel.Request;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class Data {

    private String testBeginTime;

    private Long spendTime;

    private String status;

    // request 信息
    private String url;

    private String method;

    private Map params;

    private List<String> log;


}
