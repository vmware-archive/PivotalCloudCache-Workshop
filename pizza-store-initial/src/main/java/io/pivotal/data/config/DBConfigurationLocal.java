package io.pivotal.data.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import io.pivotal.data.jpa.repo.CustomerJpaRepository;

@Configuration
@EnableJpaRepositories(basePackageClasses = CustomerJpaRepository.class)
@Profile("local")
public class DBConfigurationLocal {

}
