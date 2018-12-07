package io.pivotal.data.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.gemfire.config.annotation.EnableEntityDefinedRegions;
import org.springframework.data.gemfire.config.annotation.EnableLogging;
import org.springframework.data.gemfire.repository.config.EnableGemfireRepositories;
import org.springframework.geode.config.annotation.UseMemberName;


//@EnableGemFireHttpSession(poolName = "DEFAULT")
@EnableEntityDefinedRegions(basePackages = "io.pivotal.data.domain")
@EnableGemfireRepositories(basePackages = "io.pivotal.data.repo")
@ComponentScan(basePackages = "io.pivotal.data.continuousquery")
@EnableLogging(logLevel = "info")
@UseMemberName("PccApiClient")
@Configuration
public class CloudCacheConfig {


}
