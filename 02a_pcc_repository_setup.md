## Spring Data Repository setup for PCC

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