package cn.aircas.airproject;

import cn.aircas.airproject.listener.TomcatListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class FileProcessApplication {
    public static void main(String[] args) {

        // 正常启动
        SpringApplication.run(FileProcessApplication.class, args);

        // 增加应用试用期
        /*ConfigurableApplicationContext run = SpringApplication.run(FileProcessApplication.class, args);
        TomcatListener.encryption(run);*/
    }
}
