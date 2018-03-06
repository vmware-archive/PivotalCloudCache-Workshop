package io.pivotal.data.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.geode.pdx.ReflectionBasedAutoSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.gemfire.cache.config.EnableGemfireCaching;
import org.springframework.data.gemfire.config.annotation.ClientCacheApplication;
import org.springframework.data.gemfire.config.annotation.ClientCacheConfigurer;
import org.springframework.data.gemfire.config.annotation.EnableEntityDefinedRegions;
import org.springframework.data.gemfire.repository.config.EnableGemfireRepositories;
import org.springframework.data.gemfire.support.ConnectionEndpoint;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

@Configuration
@ClientCacheApplication(name = "PccApiClient", durableClientId = "pizza-store-api",
keepAlive = true, readyForEvents = true, subscriptionEnabled = true)
@EnableEntityDefinedRegions(basePackages = "io.pivotal.data.domain")
@EnableGemfireCaching
@EnableGemfireRepositories(basePackages = "io.pivotal.data.repo")
@EnableJpaRepositories(basePackages = "io.pivotal.data.jpa.repo")
@ComponentScan(basePackages = "io.pivotal.data.continuousquery")
@Profile("local")
public class ClientLocalConfiguration {

    @Bean
    ClientCacheConfigurer clientCacheSecurityConfigurer() {

        return (beanName, clientCacheFactoryBean) -> {

            Properties gemfireProperties = clientCacheFactoryBean.getProperties();

            clientCacheFactoryBean.addLocators(new ConnectionEndpoint("localhost", 10334));
            clientCacheFactoryBean.setProperties(gemfireProperties);
            clientCacheFactoryBean.setPdxSerializer(
                    new ReflectionBasedAutoSerializer(".*"));
        };
    }

    @Bean
	public DataSource dataSource() {

		EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
		EmbeddedDatabase dataSource = builder
			.setType(EmbeddedDatabaseType.HSQL)
			.addScript("sql/create_table.sql")
			.build();

		return dataSource;
	}

}
