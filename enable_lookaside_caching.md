## Look-aside caching

#### Step 1: @EnableGemfireCaching annotation enables spring caching backed by PCC. This enables @Cachable annotation to presist the service response into the cache seamlessly

```
@ClientCacheApplication(name = "PccApiClient", durableClientId = "pizza-store-api",
keepAlive = true, readyForEvents = true, subscriptionEnabled = true)
@EnableEntityDefinedRegions(basePackages = "io.pivotal.data.domain")
@EnableGemfireRepositories(basePackages = "io.pivotal.data.repo")
@EnableGemfireCaching
@Configuration
public class ClientConfiguration {

```

#### Step 2: Annotate getCustomerByEmail service with @Cacheable annotation for enabling lookaside caching backed by Pivotal Cloud Cache

```
@Cacheable(value = "customer")
public Customer getCustomerByEmail(String email) {

	setCacheMiss();

	Customer customer = jpaCustomerRepository.findByEmail(email);

	return customer;
}

```