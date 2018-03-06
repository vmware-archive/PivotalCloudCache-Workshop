package io.pivotal.data.repo;

import org.springframework.data.gemfire.repository.GemfireRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import io.pivotal.data.domain.PizzaOrder;

@RepositoryRestResource(path = "pizzaOrders")
public interface PizzaOrderRepo extends GemfireRepository<PizzaOrder, String> {

}
