package io.pivotal.data.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.gemfire.config.annotation.EnableEntityDefinedRegions;
import org.springframework.data.gemfire.config.annotation.EnableLogging;
import org.springframework.data.gemfire.repository.config.EnableGemfireRepositories;
import org.springframework.geode.config.annotation.UseMemberName;
import org.springframework.session.data.gemfire.config.annotation.web.http.EnableGemFireHttpSession;

import io.pivotal.data.domain.Customer;
import io.pivotal.data.repo.CustomerRepo;


@EnableGemFireHttpSession(poolName = "DEFAULT",regionName = "ClusteredSpringSessions")
@EnableEntityDefinedRegions(basePackageClasses = Customer.class)
@EnableGemfireRepositories(basePackageClasses = CustomerRepo.class)
@ComponentScan(basePackages = "io.pivotal.data.continuousquery")
@EnableLogging(logLevel = "info")
@UseMemberName("PccApiClient")
@Configuration
public class CloudCacheConfig {


}
