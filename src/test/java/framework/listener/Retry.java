package framework.listener;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class Retry implements IRetryAnalyzer {
    private int retryCnt = 0;
    private int maxRetryCnt = 2;

    // 重新自己定义 重跑规则
    @Override
    public boolean retry(ITestResult result) {
        if (retryCnt < maxRetryCnt) {

            retryCnt++;
            return true;
        }
        return false;
    }

    // 用于重置retryCnt
    public void reset() {
        retryCnt = 0;
    }
}
