## Look-aside caching

#### Step 1: Annotate getCustomerByEmail service with @Cacheable annotation for enabling lookaside caching backed by Pivotal Cloud Cache

```
@Cacheable(value = "customer")
public Customer getCustomerByEmail(String email) {

	setCacheMiss();

	Customer customer = jpaCustomerRepository.findByEmail(email);

	return customer;
}

```