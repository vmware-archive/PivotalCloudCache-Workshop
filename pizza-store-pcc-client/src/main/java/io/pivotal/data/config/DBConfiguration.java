package io.pivotal.data.config;

import org.springframework.cloud.config.java.AbstractCloudConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "io.pivotal.data.jpa.repo")
@Profile("cloud")
public class DBConfiguration extends AbstractCloudConfig {


}
