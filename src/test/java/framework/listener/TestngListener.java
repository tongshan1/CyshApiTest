package framework.listener;

import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

public class TestngListener extends TestListenerAdapter {

    @Override
    public void onTestSuccess(ITestResult tr) {
        super.onTestSuccess(tr);
        // 对于dataProvider的用例，每次成功后，重置Retry次数
        Retry retry = (Retry) tr.getMethod().getRetryAnalyzer();
        retry.reset();
    }

    @Override
    public void onTestFailure(ITestResult tr) {
        super.onTestFailure(tr);
        // 对于dataProvider的用例，每次失败后，重置Retry次数
        Retry retry = (Retry) tr.getMethod().getRetryAnalyzer();
        retry.reset();
    }
}
