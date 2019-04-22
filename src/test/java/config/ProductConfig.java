package config;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ProductConfig extends BaseConfig{

    // 生产环境配置文件
    public static Path TestConfig = Paths.get(DataRoot.toString(), "productConfig.yaml");
}
