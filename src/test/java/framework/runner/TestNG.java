package framework.runner;

import io.restassured.response.Response;
import org.testng.ITestResult;
import org.testng.annotations.*;
import utils.OutValidator;
import yamlModel.Comparator;
import yamlModel.Request;
import yamlModel.TestCase;
import yamlModel.Validator;

import java.util.*;

import static io.restassured.RestAssured.given;

public class TestNG {

    private TestCase testCase;

    public TestNG(TestCase testCase){
        testCase.replace();
        this.testCase = testCase;
    }

    @BeforeClass
    public void beforeClass(){
        System.out.println("start test: " + this.testCase.getName());
    }

    @AfterClass
    public void afterClass(){
        System.out.println("end test: " + this.testCase.getName());
    }

    @BeforeMethod
    public void beforeMethod(Object[] objects){
        for (Object object : objects) {
            Request request = (Request)object;
            System.out.println("start request：" + request.getUrl());
        }
    }

    @AfterMethod
    public void afterMethod(ITestResult tr, Object[] objects){

        for (Object object : objects) {
            Request request = (Request)object;
            tr.setAttribute("request", request);
            tr.setAttribute("testCaseName", this.testCase.getName());
        }

    }


    @DataProvider(name="requests")
    public Iterator<Object[]> initRequests() {

        List<Request> requests = this.testCase.getRequests();

        List<Object[]> item = new ArrayList<Object[]>();

        for(Object u : requests){
            item.add(new Object[]{u});
        }

        return item.iterator();
    }


    /***
     * 验证 检查结果
     */
    public void checkResponse(Response response, List<Validator> validators){

        for(Validator validator: validators){
            String check = validator.getCheck();
            Comparator comparator = validator.getComparator();
            String expected = validator.getExpected();
            String message = validator.getMessage();

            String checkValue = response.path(check);
            comparator.comparator(message, checkValue, expected);

        }

    }

    /***
     * 保存返回的变量
     * @param response
     * @param outs
     */
    public void saveOut(Response response, List<String> outs){
        Map outMap = new HashMap<>();
        for(String out: outs){
            String outValue = response.path(out);

            outMap.put(out, outValue);
        }

        OutValidator.OUT_VALIDATOR.outValidator.putAll(outMap);

    }

    @Test(dataProvider = "requests")
    public void testRequest(Request request){

        request.replace(OutValidator.OUT_VALIDATOR.outValidator);

        String url = request.getUrl();
        String method = request.getMethod();
        Map<String,?> headers = request.getHeaders();
        Map<String,?> query = request.getQuery();
        List<Validator> validators = request.getValidators();
        List<String> outs = request.getOuts();

        Response response = given().headers(headers).queryParams(query).when().request(method, url).then().extract().response();

        request.setResponse(response);

        response.body().print();

        // 检查结果
        checkResponse(response, validators);

        // 保存需要的结果
        saveOut(response, outs);

    }

}
