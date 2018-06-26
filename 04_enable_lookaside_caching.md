## Look-aside caching

#### Step 1: @EnableGemfireCaching annotation enables spring caching backed by PCC. This enables @Cachable annotation to persist the service response into the cache seamlessly

```
...
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