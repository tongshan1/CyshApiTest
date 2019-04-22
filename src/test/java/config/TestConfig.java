package config;

import java.nio.file.Path;
import java.nio.file.Paths;

/***
 * 测试环境配置
 */
public class TestConfig extends BaseConfig {

    // 测试环境配置文件
    public static Path TestConfig = Paths.get(DataRoot.toString(), "testConfig.yaml");
}
