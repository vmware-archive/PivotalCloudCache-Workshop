package io.pivotal.data.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.gemfire.cache.config.EnableGemfireCaching;
import org.springframework.data.gemfire.config.annotation.EnableEntityDefinedRegions;
import org.springframework.data.gemfire.config.annotation.EnableLogging;
import org.springframework.data.gemfire.repository.config.EnableGemfireRepositories;
import org.springframework.geode.config.annotation.EnableDurableClient;
import org.springframework.geode.config.annotation.UseMemberName;
import org.springframework.session.data.gemfire.config.annotation.web.http.EnableGemFireHttpSession;


@EnableDurableClient(id = "PccApiClient")
@EnableLogging(logLevel = "info")
@UseMemberName("PccApiClient")
@EnableGemfireCaching
@EnableGemFireHttpSession(poolName = "DEFAULT")
@EnableEntityDefinedRegions(basePackages = "io.pivotal.data.domain")
@EnableGemfireRepositories(basePackages = "io.pivotal.data.repo")
@ComponentScan(basePackages = "io.pivotal.data.continuousquery")
@Profile("cloud")
@Configuration
public class CloudCacheConfig {


}
