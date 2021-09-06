package edu.strauteka.example.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Collections;
import java.util.List;

@Configuration
public class SwaggerConfiguration {

    private SecurityContext securityContext() {
        return SecurityContext.builder().securityReferences(defaultAuth()).build();
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Collections.singletonList(new SecurityReference("JWT", authorizationScopes));
    }

    @Bean
    public Docket api() {
        HttpAuthenticationScheme authenticationScheme = HttpAuthenticationScheme
                .JWT_BEARER_BUILDER
                .name("JWT")
                .build();

        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .securityContexts(Collections.singletonList(securityContext()))
                .securitySchemes((Collections.singletonList(authenticationScheme)))
                .select()
                .apis(RequestHandlerSelectors.basePackage("edu.strauteka.example.api"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                "DEMO Rest api",
                "Available Rest Api login on <HOST>/api/login",
                "1.0",
                "Terms of service",
                new Contact("Strauteka", "https://github.com/strauteka", null),
                null,///"License of API",
                null,//"API license URL",
                Collections.emptyList());
    }
}
