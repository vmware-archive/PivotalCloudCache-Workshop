package io.pivotal.data.continuousquery;

import org.apache.geode.cache.query.CqEvent;
import org.springframework.data.gemfire.listener.annotation.ContinuousQuery;
import org.springframework.stereotype.Component;

@Component
public class PizzaOrdersCQ {

	@ContinuousQuery(name = "PestoQuery", query = "SELECT * FROM /pizza_orders WHERE sauce = 'pesto'",
			durable = true)
    public void handlePizzaChanges(CqEvent event) {

		System.out.println("*********Logging CQ Event*********");
        System.out.println("CQ Event: \t" + event);
    }

}
