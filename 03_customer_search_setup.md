## Initial setup

Lets implement the APIs and Database Service call required for pizza store app.

#### Step 1: implement customer search service call for retreving customer information


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