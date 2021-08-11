package br.com.bbrazsilveira.payment.v1.configuration.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.servlet.ServletContext;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket apiV1(ServletContext servletContext) {
        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .groupName("v1")
                .select()
                .apis(RequestHandlerSelectors.basePackage("br.com.bbrazsilveira"))
                .paths(PathSelectors.any())
                .build()
                .directModelSubstitute(LocalDate.class, java.sql.Date.class)
                .directModelSubstitute(LocalDateTime.class, java.util.Date.class)
                .apiInfo(apiInfo())
                .tags(new Tag("Boletos", "REST API de boletos"),
                        new Tag("OAuth", "REST API de autenticação"));
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Payment")
                .description("REST API de Pagamentos.")
                .contact(new Contact("Bruno Silveira", "https://www.encodec.dev", "bruno.silveira@encodec.com.br"))
                .version("1.0")
                .build();
    }
}