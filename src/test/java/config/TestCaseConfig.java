package config;

import yamlModel.Request;
import yamlModel.TestCase;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class TestCaseConfig extends BaseConfig {

    // 测试用例文件夹
    public static Path TestCaseDir = Paths.get(DataRoot.toString(),"cases" );

    /**
     * 获取用例文件夹下面的所有用例文件
     * @param obj
     */
    public static ArrayList<File> getListFiles(Object obj) {
        File directory = null;
        if (obj instanceof File) {
            directory = (File) obj;
        } else {
            directory = new File(obj.toString());
        }
        ArrayList<File> files = new ArrayList<File>();
        if (directory.isFile()) {
            files.add(directory);
            return files;
        } else if (directory.isDirectory()) {
            File[] fileArr = directory.listFiles();
            for (int i = 0; i < fileArr.length; i++) {
                File fileOne = fileArr[i];
                files.addAll(getListFiles(fileOne));
            }
        }
        return files;
    }


    /**
     * 解析测试文件
     * 生成testcase对象
     */
    public  List<TestCase> parseTestCase(){

        ArrayList<File> files = getListFiles(TestCaseDir.toString());

        List<TestCase > testCaseDesList = new ArrayList<>();

        for(File f: files){
            TestCase testCaseDes = (TestCase)parseYaml(f.getPath(), TestCase.class);
            testCaseDesList.add(testCaseDes);
        }

        return testCaseDesList;
    }


    public static void main(String [] args){

        TestCaseConfig config = new TestCaseConfig();

        for(TestCase j : config.parseTestCase()){
            System.out.println(j.getClass());
            List<Request> request = j.getRequests();
            System.out.println(request);
        }

    }
}
