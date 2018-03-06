package io.pivotal.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import io.pivotal.data.domain.Customer;
import io.pivotal.data.jpa.repo.CustomerJpaRepository;


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

	@Cacheable(value = "customer")
	public Customer getCustomerByEmail(String email) {

		setCacheMiss();

		Customer customer = jpaCustomerRepository.findByEmail(email);

		return customer;
	}

}
