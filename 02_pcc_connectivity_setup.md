## Pizza Store App

Let's incrementally build pizza store app which will showcase various features supported by PCC and Spring Data

Note: All the boilerplate code required for this demo app has been provided in Pizza-store-initial. We'll not be covering spring-data-jpa concepts in this workshop.

#### Step 1: Create Skeleton PCC Client project

a. Download the Pizza-store-initial project. For convenience we have configured the POM with required dependencies for mysql and we'll be adding only dependencies required for PCC.

###### Pivotal Cloud Cache Dependency

```
<dependency>
	<groupId>org.springframework.geode</groupId>
	<artifactId>spring-geode-starter</artifactId>
	<version>1.0.0.M3</version>
</dependency>

```

###### Spring Data REST Dependencies

```
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-data-rest</artifactId>
</dependency>

```

#### Step 2: configure PCC client with Spring Data GemFire(SDG) annotations

a. Create a configuration file which transforms this boot app into PCC Client cache. `spring-geode-starter` enables autoreconfiguration support for PCC and creates PCC client connection pool.

```
@EnableLogging(logLevel = "info")
@UseMemberName("PccApiClient")
@Configuration
public class CloudCacheConfig {

}
```
