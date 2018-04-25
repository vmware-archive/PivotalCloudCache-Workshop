package io.pivotal.data.domain;

import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.gemfire.mapping.annotation.Region;

@Region("pizza_orders")
public class PizzaOrder {

	@Id
    String orderId;
	String name;
    String sauce;
    Set<String> toppings;
    Customer customerInfo;

    @Override
    public String toString() {
        return "Pizza{" +
                "name='" + name + '\'' +
                ", toppings=" + toppings +
                ", sauce='" + sauce + '\'' +
                ", Customer='" + customerInfo + '\'' +
                '}';
    }

    public PizzaOrder() {

    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	public PizzaOrder(String name, Set toppings, String sauce) {
    	this.name = name;
        this.toppings = toppings;
        this.sauce = sauce;
    }

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<String> getToppings() {
		return toppings;
	}

	public void setToppings(Set<String> toppings) {
		this.toppings = toppings;
	}

	public String getSauce() {
		return sauce;
	}

	public void setSauce(String sauce) {
		this.sauce = sauce;
	}

	public Customer getCustomerInfo() {
		return customerInfo;
	}

	public void setCustomerInfo(Customer customerInfo) {
		this.customerInfo = customerInfo;
	}
}