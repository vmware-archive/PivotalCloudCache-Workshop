## Pizza Store App

Let's incrementally build pizza store app which will showcase various features supported by PCC and Spring Data

Note: All the boilerplate code required for this demo app has been provided in Pizza-store-initial. We'll not be covering Spring-Data-JPA concepts in this workshop.

#### Step 1: Create Skeleton PCC Client project

a. Download the Pizza-store-initial project. For convenience we have configured the POM with required dependencies and logic required for configuring client for connecting with PCC instance

###### Spring Data Gemfire Dependencies

```
<dependency>
	<groupId>org.springframework.data</groupId>
	<artifactId>spring-data-gemfire</artifactId>
	<exclusions>
		<exclusion>
			<groupId>io.pivotal.gemfire</groupId>
			<artifactId>geode-wan</artifactId>
		</exclusion>
		<exclusion>
			<groupId>io.pivotal.gemfire</groupId>
			<artifactId>geode-lucene</artifactId>
		</exclusion>
	</exclusions>
</dependency>

<dependency>
	<groupId>io.pivotal.gemfire</groupId>
	<artifactId>geode-core</artifactId>
	<version>9.3.0</version>
</dependency>

<dependency>
	<groupId>io.pivotal.gemfire</groupId>
	<artifactId>geode-cq</artifactId>
	<version>9.3.0</version>
</dependency>

```

###### Spring Data REST Dependencies

```
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-data-rest</artifactId>
</dependency>

```

#### Step 2: Add logic for parsing VCAP services and creating client connection pool

For connecting to PCC cluster, VCAP exposes two important information - PCC locators and user credentials. 

VcapEnvParser.java is responsible for reading the environment variable <strong>VCAP_SERVICES</strong> present in Cloud Foundry environment.

1. To parse VCAP_SERVICES environment variable
2. To set security properties of Spring Data GemFire


#### Step 3: configure PCC client with SDG annotations

a. Create a configuration file which transforms this boot app into PCC Client cache. @ClientCacheApplication configures the boot application to treat this app as PCC Client and automatically generates a client pool.

```
@ClientCacheApplication(name = "PccApiClient", durableClientId = "pizza-store-api",
keepAlive = true, readyForEvents = true, subscriptionEnabled = true)
@EnableEntityDefinedRegions(basePackages = "io.pivotal.data.domain")
@EnableGemfireCaching
@EnableGemfireRepositories(basePackages = "io.pivotal.data.repo")
@EnableJpaRepositories(basePackages = "io.pivotal.data.jpa.repo")
@EnableSecurity
@EnablePdx
@Profile("cloud")
@ComponentScan(basePackages = "io.pivotal.data.continuousquery")
@Configuration
public class ClientConfiguration {

}

```
