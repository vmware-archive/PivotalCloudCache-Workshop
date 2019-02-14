package io.pivotal.data.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "io.pivotal.data.jpa.repo")
@Profile("local")
public class DBConfigurationLocal {

}
