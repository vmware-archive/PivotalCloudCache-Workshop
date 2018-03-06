package io.pivotal.data.config;

import java.net.URI;
import java.util.Properties;

import org.apache.geode.pdx.ReflectionBasedAutoSerializer;
import org.springframework.cloud.Cloud;
import org.springframework.cloud.CloudFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.gemfire.cache.config.EnableGemfireCaching;
import org.springframework.data.gemfire.config.annotation.ClientCacheApplication;
import org.springframework.data.gemfire.config.annotation.ClientCacheConfigurer;
import org.springframework.data.gemfire.config.annotation.EnableEntityDefinedRegions;
import org.springframework.data.gemfire.config.annotation.EnableSecurity;
import org.springframework.data.gemfire.repository.config.EnableGemfireRepositories;
import org.springframework.data.gemfire.support.ConnectionEndpoint;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@ClientCacheApplication(name = "PccApiClient", durableClientId = "pizza-store-api",
keepAlive = true, readyForEvents = true, subscriptionEnabled = true)
@EnableEntityDefinedRegions(basePackages = "io.pivotal.data.domain")
@EnableGemfireCaching
@EnableGemfireRepositories(basePackages = "io.pivotal.data.repo")
@EnableJpaRepositories(basePackages = "io.pivotal.data.jpa.repo")
@EnableSecurity
@Profile("cloud")
@ComponentScan(basePackages = "io.pivotal.data.continuousquery")
@Configuration
public class ClientConfiguration {

	private static final String SECURITY_CLIENT = "security-client-auth-init";
    private static final String SECURITY_USERNAME = "security-username";
    private static final String SECURITY_PASSWORD = "security-password";

    @Bean
    ClientCacheConfigurer clientCacheSecurityConfigurer() {

        return (beanName, clientCacheFactoryBean) -> {

            Cloud cloud = new CloudFactory().getCloud();
            ServiceInfo serviceInfo = null;
            for(Object si : cloud.getServiceInfos()) {
            	if( si instanceof io.pivotal.data.config.ServiceInfo) {
            		serviceInfo = (ServiceInfo) si;
            	}
            }

            Properties gemfireProperties = clientCacheFactoryBean.getProperties();

            gemfireProperties.setProperty(SECURITY_USERNAME, serviceInfo.getUsername());
            gemfireProperties.setProperty(SECURITY_PASSWORD, serviceInfo.getPassword());
            gemfireProperties.setProperty(SECURITY_CLIENT, "io.pivotal.data.config.UserAuthInitialize.create");

            for (URI locator : serviceInfo.getLocators()) {
                clientCacheFactoryBean.addLocators(new ConnectionEndpoint(locator.getHost(), locator.getPort()));
            }

            clientCacheFactoryBean.setProperties(gemfireProperties);
            clientCacheFactoryBean.setPdxSerializer(
                    new ReflectionBasedAutoSerializer(".*"));
        };
    }

//	@Bean
//	public RepositoryRestConfigurer repositoryRestConfigurer() {
//
//		return new RepositoryRestConfigurerAdapter() {
//
//			@Override
//			public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
//				config.setBasePath("/api/v1");
//			}
//		};
//	}

}
