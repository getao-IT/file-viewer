package cn.aircas.airproject.config.aop.common;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
//@MapperScan({"cn.aircas.airproject.mapper"})
public class MyBatisPlusConfig {
    /*@Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor page = new PaginationInterceptor();
        page.setDialectType("postgresql");
        return page;
    }*/
}
