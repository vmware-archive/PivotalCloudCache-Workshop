## Initial setup

Lets implement the APIs and Database Service call required for pizza store app.

#### Step 1: Create Domain objects PizzaOrder and Customer. Get the domain objects from pizza-store-pcc-client project

#### Step 2: Implement GemFire repositories for PizzaOrder and Customer domain objects.

```
package io.pivotal.data.repo;

@RepositoryRestResource(path = "pizzaOrders")
public interface PizzaOrderRepo extends GemfireRepository<PizzaOrder, String> {

}

```

```
package io.pivotal.data.repo;

@RepositoryRestResource(path = "customers")
public interface CustomerRepo extends GemfireRepository<Customer, String> {

}
```

#### Step 3: configuring spring boot app with REST repositories

@EnableGemfireRepositories annotation configures the client to create Spring Data GemFire repositories for all the domain objects annotated with @Region

```
@EnableLogging(logLevel = "info")
@UseMemberName("PccApiClient")
@EnableEntityDefinedRegions(basePackages = "io.pivotal.data.domain")
@EnableGemfireRepositories(basePackages = "io.pivotal.data.repo")
@ComponentScan(basePackages = "io.pivotal.data.continuousquery")
@Profile("cloud")
@Configuration
public class CloudCacheConfig {
}
```

#### Step 4: implement customer search service call for retreving customer information


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

	public Customer getCustomerByEmail(String email) {

		setCacheMiss();

		Customer customer = jpaCustomerRepository.findByEmail(email);

		return customer;
	}

}
```