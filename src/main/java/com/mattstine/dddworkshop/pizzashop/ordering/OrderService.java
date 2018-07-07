package com.mattstine.dddworkshop.pizzashop.ordering;

import com.mattstine.dddworkshop.pizzashop.infrastructure.Amount;
import com.mattstine.dddworkshop.pizzashop.infrastructure.EventLog;
import com.mattstine.dddworkshop.pizzashop.payments.PaymentService;

/**
 * @author Matt Stine
 */
public class OrderService {
	private final EventLog eventLog;
	private final OrderRepository repository;
	private final PaymentService paymentService;

	OrderService(EventLog eventLog, OrderRepository repository, PaymentService paymentService) {
		this.eventLog = eventLog;
		this.repository = repository;
		this.paymentService = paymentService;
	}

	public OrderRef createOrder(OrderType type) {
		OrderRef orderRef = repository.nextIdentity();

		Order order = Order.withType(type)
				.withEventLog(eventLog)
				.withId(orderRef)
				.build();

		repository.add(order);

		return orderRef;
	}

	public void addPizza(OrderRef orderRef, Pizza pizza) {
		Order order = repository.findById(orderRef);
		order.addPizza(pizza);
		eventLog.publish(new PizzaAddedEvent(orderRef, pizza));
	}

	public void requestPayment(OrderRef orderRef) {
		paymentService.requestPaymentFor(orderRef, Amount.of(10, 0));
	}
}
