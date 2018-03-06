package io.pivotal.data.repo;

import org.springframework.data.gemfire.repository.GemfireRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import io.pivotal.data.domain.Customer;

@RepositoryRestResource(path = "customers")
public interface CustomerRepo extends GemfireRepository<Customer, String> {

}
