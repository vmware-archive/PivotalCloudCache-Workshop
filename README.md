## PivotalCloudCache-Workshop

This workshop will provide developers with hands on experience in building Pivotal Cloud Cache(PCC) clients using Spring Data GemFire (SDG), Spring Data REST, Spring Cloud and Spring Boot. In this session we'll be implementing a pizza store app for ordering pizza backed by PCC. Session includes presentations, demos and hands on labs.

Demo App: http://pizza-store-pcc-client.apps.numerounocloud.com/orderPizza?email=lucynorton@gmail.com&type=fancy

###### Result

Cache Miss Scenario

```
Result [Pizza{name='fancy', toppings=[arugula, chicken], sauce='pesto', Customer='Customer [id=05eKpgOFA, name=Lucy Norton, email=lucynorton@gmail.com, address=48665 Washington, birthday=1965-02-10T06:20:27.828Z]'}] 
Cache Miss for Customer [true] 
Read from [MYSQL] 
Elapsed Time [234 ms]
```

Data Returned From Cache 
```
Result [Pizza{name='fancy', toppings=[arugula, chicken], sauce='pesto', Customer='Customer [id=05eKpgOFA, name=Lucy Norton, email=lucynorton@gmail.com, address=48665 Washington, birthday=1965-02-10T06:20:27.828Z]'}] 
Cache Miss for Customer [false] 
Read from [PCC] 
Elapsed Time [2 ms]
```

## Prerequisites

1. PCF Org/Space with Pivotal Cloud Cache and Mysql
2. GFSH cli
( https://gemfire.docs.pivotal.io/geode/tools_modules/gfsh/starting_gfsh.html ) 

## Pizza Store App

Let's incrementally build pizza store app which will showcase various features supported by PCC and Spring Data,

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
	<version>9.2.0</version>
</dependency>

<dependency>
	<groupId>io.pivotal.gemfire</groupId>
	<artifactId>geode-cq</artifactId>
	<version>9.2.0</version>
</dependency>

```

###### Spring Data REST Dependencies

```
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-data-rest</artifactId>
</dependency>

```

Note: All the boilerplate code required for this demo app has been provided in Pizza-store-initial. We'll not be covering Spring-Data-JPA concepts in this workshop.

#### Step 2: configure PCC client with SDG annotations

a. Create a configuration file which transforms this boot app into PCC Client cache. @ClientCacheApplication configures the boot application to treat this app as PCC Client and automatically generates a client pool.

```
@ClientCacheApplication(name = "PccApiClient", durableClientId = "pizza-store-api",
keepAlive = true, readyForEvents = true, subscriptionEnabled = true)
@Configurations
public class ClientConfiguration {

}

```

b. Add logic for parsing VCAP services and creating client connection pool

```
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

```

c. Add annotation for enabling client side region creation based on the available domain objects. @EnableEntityDefinedRegions annotation scans the base packages for domain objects and creates the corresponding Client side region bean.

```
@ClientCacheApplication(name = "PccApiClient", durableClientId = "pizza-store-api",
keepAlive = true, readyForEvents = true, subscriptionEnabled = true)
@EnableEntityDefinedRegions(basePackages = "io.pivotal.data.domain")
@Configuration
public class ClientConfiguration {

```

#### Step 3: Create Domain objects PizzaOrder and Customer. Get the domain objects from pizza-store-pcc-client project


#### Step 4: Enabling PCC client with REST repositories

@EnableGemfireRepositories annotation configures the client to create Spring Data GemFire repositories for all the domain objects annotated with @Region

```
@ClientCacheApplication(name = "PccApiClient", durableClientId = "pizza-store-api",
keepAlive = true, readyForEvents = true, subscriptionEnabled = true)
@EnableEntityDefinedRegions(basePackages = "io.pivotal.data.domain")
@EnableGemfireRepositories(basePackages = "io.pivotal.data.repo")
@Configuration
public class ClientConfiguration {

```

```
@RepositoryRestResource(path = "pizzaOrders")
public interface PizzaOrderRepo extends GemfireRepository<PizzaOrder, String> {

}

```

```
@RepositoryRestResource(path = "customers")
public interface CustomerRepo extends GemfireRepository<Customer, String> {

}
```

#### Step 5: Look-aside caching

a. @EnableGemfireCaching annotation enables spring caching backed by PCC. This enables @Cachable annotation to presist the service response into the cache seamlessly

```
@ClientCacheApplication(name = "PccApiClient", durableClientId = "pizza-store-api",
keepAlive = true, readyForEvents = true, subscriptionEnabled = true)
@EnableEntityDefinedRegions(basePackages = "io.pivotal.data.domain")
@EnableGemfireRepositories(basePackages = "io.pivotal.data.repo")
@EnableGemfireCaching
@Configuration
public class ClientConfiguration {

```

b. create the customer search service

```
@Service
public class CustomerSearchService {

	@Autowired
	CustomerJpaRepository jpaCustomerRepository;

	private volatile boolean cacheMiss = false;

	public boolean isCacheMiss() {
		boolean isCacheMiss = this.cacheMiss;
		this.cacheMiss = false;
		return isCacheMiss;
	}

	protected void setCacheMiss() {
		this.cacheMiss = true;
	}

	@Cacheable(value = "customer")
	public Customer getCustomerByEmail(String email) {

		setCacheMiss();

		Customer customer = jpaCustomerRepository.findByEmail(email);

		return customer;
	}

}
```

#### Step 6: Implement Pizza Store APIs

a. Create a REST controller. All the necessary repositories which we created in earlier steps need to be Autowired in this controller

```
@RestController
@DependsOn({"gemfireCache"})
public class PizzaOrderController {

	@Autowired
	CustomerRepo customer;

	@Autowired
	PizzaOrderRepo pizzaOrderRepo;

	@Autowired
	CustomerJpaRepository jpaCustomerRepository;

	@Autowired
	CustomerSearchService customerSearchService;

	Fairy fairy = Fairy.create();

}
```

b. Implement API for pre-loading mysql Database with customer data

```
@RequestMapping(method = RequestMethod.GET, path = "/loaddb")
@ResponseBody
public String loadDB(@RequestParam(value = "amount", required = true) String amount) throws Exception {

	Integer num = Integer.parseInt(amount);

	for (int i=0; i<num; i++) {
		Person person = fairy.person();
		Customer customer = new Customer(person.passportNumber(), person.fullName(), person.email(), person.getAddress().toString(), person.dateOfBirth().toString());
		jpaCustomerRepository.save(customer);
	}

	return "New customers successfully saved into Database";
}
```

c. Implement API for listing first 10 customers

```
@SuppressWarnings("deprecation")
@RequestMapping(method = RequestMethod.GET, path = "/showdb")
@ResponseBody
public String showDB() throws Exception {
	StringBuilder result = new StringBuilder();
	Pageable topTen = new PageRequest(0, 10);

	jpaCustomerRepository.findAll(topTen).forEach(item->result.append(item+"<br/>"));

	return "First 10 customers are show here: <br/>" + result.toString();
}

```

d. Implement Pizza info API

```
@RequestMapping(method = RequestMethod.GET, path = "/pizzas")
@ResponseBody
public String showAvailablePizzas() throws Exception {

	return "<b>Lets Order Some Pizza <br/></b>"
			+ "-------------------------------"
			+ "<br/>"
			+ "<h3>types: plain, fancy</h3>"
			+ "<br/>"
			+ "GET /orderPizza?email={emailId}&type={pizzaType}  - Order a pizza <br/>"
			+ "GET /orders?email={emailId}               - get specific value <br/>";

}
```

e. Implement API for Ordering Pizza

Eg. http://pizza-store-pcc-client.apps.numerounocloud.com/orderPizza?email=lucynorton@gmail.com&type=fancy

###### Result

Cache Miss Scenario

```
Result [Pizza{name='fancy', toppings=[arugula, chicken], sauce='pesto', Customer='Customer [id=05eKpgOFA, name=Lucy Norton, email=lucynorton@gmail.com, address=48665 Washington, birthday=1965-02-10T06:20:27.828Z]'}] 
Cache Miss for Customer [true] 
Read from [MYSQL] 
Elapsed Time [234 ms]
```

Data Returned From Cache 
```
Result [Pizza{name='fancy', toppings=[arugula, chicken], sauce='pesto', Customer='Customer [id=05eKpgOFA, name=Lucy Norton, email=lucynorton@gmail.com, address=48665 Washington, birthday=1965-02-10T06:20:27.828Z]'}] 
Cache Miss for Customer [false] 
Read from [PCC] 
Elapsed Time [2 ms]
```


```
@RequestMapping(method = RequestMethod.GET, path = "/orderPizza")
@ResponseBody
public String orderPizza(@RequestParam(value = "email", required = true) String email,
		@RequestParam(value = "type", required = true) String pizzaType)
		throws Exception {

	long startTime = System.currentTimeMillis();
	Customer customer = customerSearchService.getCustomerByEmail(email);
	long elapsedTime = System.currentTimeMillis();
	Boolean isCacheMiss = customerSearchService.isCacheMiss();
	String sourceFrom = isCacheMiss ? "MySQL" : "PCC";


	PizzaOrder pizzaObject = createPizzaObject(pizzaType);
	String orderId = customer.getEmail() + Calendar.getInstance().getTimeInMillis();
	pizzaObject.setOrderId(orderId);
	pizzaObject.setCustomerInfo(customer);

	pizzaOrderRepo.save(pizzaObject);

	return String.format("Result [<b>%1$s</b>] <br/>"
			+ "Cache Miss for Customer [<b>%2$s</b>] <br/>"
			+ "Read from [<b>%3$s</b>] <br/>"
			+ "Elapsed Time [<b>%4$s ms</b>]%n", pizzaObject, isCacheMiss, sourceFrom, (elapsedTime - startTime));
}

private PizzaOrder createPizzaObject(String pizzaType) {

	PizzaOrder pizza = null;

	if (pizzaType != null) {
		if(pizzaType.equalsIgnoreCase("plain")) {
			pizza = makePlainPizza();
		} else if(pizzaType.equalsIgnoreCase("fancy")) {
			pizza = makeFancyPizza();
		}
	}
	return pizza;
}

private PizzaOrder makeFancyPizza() {
    Set<String> toppings = new HashSet<>();
    toppings.add("chicken");
    toppings.add("arugula");
    return new PizzaOrder("fancy", toppings, "pesto");
}

private PizzaOrder makePlainPizza() {
    Set<String> toppings = new HashSet<>();
    toppings.add("cheese");
    return new PizzaOrder("plain", toppings, "red");
}

```

#### Step 7: Implement Continuous Queries (CQ)

SDG makes it extremely easy to configure Continuous Queries with help of @ContinousQuery annotation. All the logic required for event handling will be handled behind the scene by SDG, App developer only needs to worry about business logic. Example CQ below - 

```
@ContinuousQuery(name = "PestoQuery", query = "SELECT * FROM /pizza_orders WHERE sauce = 'pesto'",
			durable = true)
    public void handlePizzaChanges(CqEvent event) {

	System.out.println("*********Logging CQ Event*********");
    System.out.println("CQ Event: \t" + event);
}

```


## Create PCC Instance
Services can be created through Apps Manager Marketplace or by executing cf cli commands

###### Step 1: create a PCC OnDemand service in your org & space

```
cf create-service p-cloudcache extra-small pcc-dev-cluster

```

###### Step 2: Create service key for retrieving connection information for GFSH cli

```
cf create-service-key workshop-pcc devkey
```

###### Step 3: Retrieve url for PCC cli (GFSH) and corresponding credentials 

```
cf service-key workshop-pcc devkey
```

###### Step 4: Login into to PCC cli (GFSH)

```
connect --use-http=true --url=http://gemfire-xxxx-xxx-xx-xxxx.system.excelsiorcloud.com/gemfire/v1 --user=cluster_operator --password=*******
```

###### Step 5: create PCC regions

Note: Region name created on PCC server and client should match

```
create region --name=customer --type=PARTITION_REDUNDANT_PERSISTENT
create region --name=pizza_orders --type=PARTITION_REDUNDANT_PERSISTENT
```

## Bind and deploy PCC Client with PCC service

###### Bind to PCC service by specifying service name in the manifest.yml

```
---
applications:
- name: pizza-store-pcc-client
  random-route: false
  path: target/pizza-store-pcc-client-0.0.1-SNAPSHOT.jar
  services:
  - test-pcc-wan
  - workshop-db
```

