package cn.aircas.airproject;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
@MapperScan("cn.aircas.airproject.mapper")
public class FileProcessApplication {
    public static void main(String[] args) {
        SpringApplication.run(FileProcessApplication.class,args);
    }
}
