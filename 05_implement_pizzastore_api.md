## Implement Pizza Store APIs

#### Step 1: Create a REST controller. All the necessary repositories which we created in earlier steps need to be Autowired in this controller

```
@RestController
@DependsOn({"gemfireCache"})
public class PizzaOrderController {

	@Autowired
	PizzaOrderRepo pccPizzaOrderRepository;

	@Autowired
	CustomerJpaRepository jpaCustomerRepository;

	@Autowired
	CustomerSearchService customerSearchService;

	Fairy fairy = Fairy.create();

}
```

#### Step 2: Implement API for pre-loading mysql Database with customer data

```
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
```

#### Step 3: Implement API for listing first 10 customers

```
@SuppressWarnings("deprecation")
@RequestMapping(method = RequestMethod.GET, path = "/showdb")
@ResponseBody
public String showDB() throws Exception {
	StringBuilder result = new StringBuilder();
	Pageable topTen = new PageRequest(0, 10);

	jpaCustomerRepository.findAll(topTen).forEach(item->result.append(item+"<br/>"));

	return "First 10 customers are show here: <br/>" + result.toString();
}

```

#### Step 4: Implement Pizza info API

```
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
```

#### Step 5: Implement API for Ordering Pizza


```
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

	pccPizzaOrderRepository.save(pizzaObject);

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

```