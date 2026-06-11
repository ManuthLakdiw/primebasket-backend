package dev.manuthlakdiw.primebasketbackend.config;

import dev.manuthlakdiw.primebasketbackend.annotation.ApiController;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static dev.manuthlakdiw.primebasketbackend.util.AppConstants.API_PREFIX;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix(API_PREFIX,
                HandlerTypePredicate.forAnnotation(ApiController.class)
                        .and(HandlerTypePredicate.forBasePackage("dev.manuthlakdiw.primebasketbackend"))
        );
    }
}
