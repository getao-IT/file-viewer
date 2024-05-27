package cn.aircas.airproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;



@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableAsync
@EnableTransactionManagement
public class FileProcessApplication {
    public static void main(String[] args) {

        // 正常启动
        ConfigurableApplicationContext run = SpringApplication.run(FileProcessApplication.class, args);
        System.out.println(run);

        // 增加应用试用期
        /*ConfigurableApplicationContext run = SpringApplication.run(FileProcessApplication.class, args);
        TomcatListener.encryption(run);*/
    }
}
