## Initial setup

Lets implement the APIs and Database Service call required for pizza store app.

#### Step 1: Create Domain objects PizzaOrder and Customer. Get the domain objects from pizza-store-pcc-client project


#### Step 2: Enabling PCC client with REST repositories

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