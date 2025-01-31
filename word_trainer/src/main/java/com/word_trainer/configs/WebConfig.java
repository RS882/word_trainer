package com.word_trainer.configs;

import com.word_trainer.configs.annotations.bearer_token.BearerTokenResolver;
import com.word_trainer.configs.annotations.authentication_principal.CustomAuthenticationPrincipalResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final CustomAuthenticationPrincipalResolver customResolver;

    private final BearerTokenResolver bearerTokenResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(customResolver);
        resolvers.add(bearerTokenResolver);
    }
}

