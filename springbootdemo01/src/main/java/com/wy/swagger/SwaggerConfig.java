package com.wy.swagger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * swagger的配置类。
 */
@Configuration  // 该注解代表 此类是个 配置类， 项目一加载就会执行！！！
@EnableSwagger2  // 开启 swagger 的配置
public class SwaggerConfig {
    // 配置 swagger的开关
    // springboot中如何 读取propertis中的 属性 赋值到  变量中呢 ？？？
    @Value("${swagger.enable}")
    private boolean enable;



    // springboot的配置类中如何 创建一个 对象？加什么注解？  @Bean
    // 为什么不 把 这里对象交给 spring来管理，（ @Controller）
    // 因要 提前 创建  @Bean
    // Docket 就是 swagger 文档的意思
    @Bean
    public Docket createRestApi(){
        // 1. 创建头信息  header
        // import springfox.documentation.service.Parameter;
        List<Parameter>  parameters = new ArrayList<>();
        // 放入 1个信息， token 。
        ParameterBuilder pb= new ParameterBuilder();
        pb.name("token").description("前端要穿的token描述").modelRef(new ModelRef("string")).parameterType("header").required(false);
        parameters.add(pb.build());

        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.xiexin.controller"))
                .paths(PathSelectors.any())
                .build()
                .globalOperationParameters(parameters)
                .enable(enable);
    }
    private ApiInfo apiInfo(){
        return new ApiInfoBuilder().title("谢欣的xx项目的接口文档").description("element UI 和 springboot的一个项目，为了xxx").version("1.10").build();
    }




}
