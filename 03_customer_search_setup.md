## Initial setup

Lets implement the APIs and Database Service call required for pizza store app.

#### Step 1: Review Domain objects PizzaOrder and Customer.

#### Step 2: Create a new package `io.pivotal.data.repo` and Implement GemFire repositories for PizzaOrder and Customer domain objects.

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
@EnableEntityDefinedRegions(basePackageClasses = Customer.class)
@EnableGemfireRepositories(basePackageClasses = CustomerRepo.class)
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