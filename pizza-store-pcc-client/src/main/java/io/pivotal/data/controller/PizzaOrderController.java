package io.pivotal.data.controller;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.codearte.jfairy.Fairy;
import io.codearte.jfairy.producer.person.Person;
import io.pivotal.data.domain.Customer;
import io.pivotal.data.domain.PizzaOrder;
import io.pivotal.data.jpa.repo.CustomerJpaRepository;
import io.pivotal.data.repo.CustomerRepo;
import io.pivotal.data.repo.PizzaOrderRepo;
import io.pivotal.data.service.CustomerSearchService;

@RestController
@DependsOn({"gemfireCache"})
public class PizzaOrderController {

	@Autowired
	CustomerRepo customer;

	@Autowired
	PizzaOrderRepo pizzaOrderRepo;

	@Autowired
	CustomerJpaRepository jpaCustomerRepository;

	@Autowired
	CustomerSearchService customerSearchService;

	Fairy fairy = Fairy.create();


	@RequestMapping(method = RequestMethod.GET, path = "/loaddb")
	@ResponseBody
	public String loadDB(@RequestParam(value = "amount", required = true) String amount) throws Exception {

		Integer num = Integer.parseInt(amount);

		for (int i=0; i<num; i++) {
			Person person = fairy.person();
			Customer customer = new Customer(person.passportNumber(), person.fullName(), person.email(), person.getAddress().toString(), person.dateOfBirth().toString());
			jpaCustomerRepository.save(customer);
		}

		return "New customers successfully saved into Database";
	}

	@SuppressWarnings("deprecation")
	@RequestMapping(method = RequestMethod.GET, path = "/showdb")
	@ResponseBody
	public String showDB() throws Exception {
		StringBuilder result = new StringBuilder();
		Pageable topTen = new PageRequest(0, 10);

		jpaCustomerRepository.findAll(topTen).forEach(item->result.append(item+"<br/>"));

		return "First 10 customers are show here: <br/>" + result.toString();
	}

	@RequestMapping(method = RequestMethod.GET, path = "/pizzas")
	@ResponseBody
	public String showAvailablePizzas() throws Exception {

		return "<b>Lets Order Some Pizza <br/></b>"
				+ "-------------------------------"
				+ "<br/>"
				+ "<h3>types: plain, fancy</h3>"
				+ "<br/>"
				+ "GET /orderPizza?email={emailId}&type={pizzaType}  - Order a pizza <br/>"
				+ "GET /orders?email={emailId}               - get specific value <br/>";

	}

	@RequestMapping(method = RequestMethod.GET, path = "/orderPizza")
	@ResponseBody
	public String orderPizza(@RequestParam(value = "email", required = true) String email,
			@RequestParam(value = "type", required = true) String pizzaType)
			throws Exception {

		long startTime = System.currentTimeMillis();
		Customer customer = customerSearchService.getCustomerByEmail(email);
		long elapsedTime = System.currentTimeMillis();
		Boolean isCacheMiss = customerSearchService.isCacheMiss();
		String sourceFrom = isCacheMiss ? "MySQL" : "PCC";


		PizzaOrder pizzaObject = createPizzaObject(pizzaType);
		String orderId = customer.getEmail() + Calendar.getInstance().getTimeInMillis();
		pizzaObject.setOrderId(orderId);
		pizzaObject.setCustomerInfo(customer);

		pizzaOrderRepo.save(pizzaObject);

		return String.format("Result [<b>%1$s</b>] <br/>"
				+ "Cache Miss for Customer [<b>%2$s</b>] <br/>"
				+ "Read from [<b>%3$s</b>] <br/>"
				+ "Elapsed Time [<b>%4$s ms</b>]%n", pizzaObject, isCacheMiss, sourceFrom, (elapsedTime - startTime));
	}

	private PizzaOrder createPizzaObject(String pizzaType) {

		PizzaOrder pizza = null;

		if (pizzaType != null) {
			if(pizzaType.equalsIgnoreCase("plain")) {
				pizza = makePlainPizza();
			} else if(pizzaType.equalsIgnoreCase("fancy")) {
				pizza = makeFancyPizza();
			}
		}
		return pizza;
	}

	private PizzaOrder makeFancyPizza() {
        Set<String> toppings = new HashSet<>();
        toppings.add("chicken");
        toppings.add("arugula");
        return new PizzaOrder("fancy", toppings, "pesto");
    }

    private PizzaOrder makePlainPizza() {
        Set<String> toppings = new HashSet<>();
        toppings.add("cheese");
        return new PizzaOrder("plain", toppings, "red");
    }

}
