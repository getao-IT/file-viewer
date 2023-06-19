package cn.aircas.airproject.config.aop;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    /**
     * 配置swagger
     * @return
     */
    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .enable(true) // 是否开启swagger，默认开启
                .select()
                .apis(RequestHandlerSelectors.basePackage("cn.aircas.airproject.controller")) // 配置扫描接口的方式
                //.paths(PathSelectors.ant("/message/**")) // 过滤，只扫描这样的controller
                .build();
    }

    /**
     * 配置swagger-->aopInfo，swagger文档的标题、描述、作者等信息
     * @return
     */
    public ApiInfo apiInfo() {
        // 作者信息
        Contact contact = new Contact("getao", "localhost", "123@qq.com");
        return new ApiInfo(
                "文件管理接口文档",
                "文件管理接口文档",
                "v0.0.1",
                "", contact,
                "",
                "",
                new ArrayList<>());
    }
}
