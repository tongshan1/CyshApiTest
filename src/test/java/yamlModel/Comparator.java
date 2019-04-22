package yamlModel;

import org.testng.Assert;

public enum  Comparator {



    EQUAL{

        @Override
        public void comparator(String message, Object expected, Object actual) {

            Assert.assertEquals(expected, actual, message);

        }

    },

    NOT_EQU{

        @Override
        public void comparator(String message, Object expected, Object actual) {
            Assert.assertNotEquals(expected, actual, message);

        }
    };

    //为该枚举类定义一个抽象方法，枚举类中所有的枚举值都必须实现这个方法
    public abstract void comparator(String message, Object expected, Object actual);


}
