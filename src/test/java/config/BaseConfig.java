package config;

import org.yaml.snakeyaml.Yaml;


import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;

public class BaseConfig {

    // 项目工程路径
    public static String ProjectRoot = System.getProperty("user.dir");

    // test src 路径
    public static Path TestSrcRoot =  Paths.get(ProjectRoot, "src","test","java");

    // 配置文件路径目录
    public  static Path DataRoot = Paths.get(TestSrcRoot.toString(), "testcase", "data");

    // report template 文件
    public static Path ReportTemplate = Paths.get(TestSrcRoot.toString(), "framework", "report", "template", "template");
    public static Path IndexTemplate = Paths.get(TestSrcRoot.toString(), "framework", "report", "template", "indexTemplate");

    // 报告输出路径
    public static Path ReportRoot = Paths.get(ProjectRoot.toString(), "report", getCurrentTime());

    // 日志输出
    public static Path logPath = Paths.get(ReportRoot.toString(), "log.txt");

    // 配置文件
    public static Path TestConfig;

    // 解析yaml 文件
    public  Object parseYaml(String filePath, Class c) {

        FileInputStream fileInputStream = null;
        Object map = null;

        try {
            Yaml dataYaml = new Yaml();

            File file = new File(filePath);//配置文件地址

            fileInputStream = new FileInputStream(file);
            map = dataYaml.loadAs(fileInputStream, c);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileInputStream != null) fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    private static String getCurrentTime(){
        SimpleDateFormat formatter = new SimpleDateFormat ("yyyyMMddHHmmssSSS");
        String currentTime = formatter.format(System.currentTimeMillis());

        return currentTime;
    }

    public BaseConfig(){

        System.setProperty("logPath", logPath.toString());

    }


}
