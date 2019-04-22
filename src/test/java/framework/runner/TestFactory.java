package framework.runner;


import config.TestCaseConfig;
import org.testng.annotations.Factory;
import yamlModel.TestCase;

import java.util.List;

public class TestFactory {

    @Factory
    public Object[] createInstance(){
        TestCaseConfig config = new TestCaseConfig();
        List<TestCase> item  =  config.parseTestCase();

        Object[] testCases = new Object[item.size()];
        for(int i=0; i<item.size(); i++){
            testCases[i] = new TestNG(item.get(i));
        }

        return testCases;
    }
}
