package cn.aircas.airproject.config.aop.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
public class WebMVCConfiguration extends WebMvcConfigurationSupport {

    @Value("${value.static.path-pattern}")
    private String staticPattern;

    @Value("${value.static.static-locations}")
    private String staticLocation;

    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry){
       /* registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");*/
        /*registry.addResourceHandler("/cfile/**")
                .addResourceLocations("file:c:/");*/
        if (this.staticPattern==null || this.staticLocation==null)
            return;

        String[] staticPatternArray = staticPattern.split(",");
        String[] staticLocationArray = staticLocation.split(",");

        for (int index = 0; index < staticPatternArray.length; index++) {
            registry.addResourceHandler(staticPatternArray[index])
                    .addResourceLocations(staticLocationArray[index]);
        }
    }
}
