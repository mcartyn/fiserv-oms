package com.bypass.oms.controller;


import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import com.bypass.oms.component.SomeComponent;
import com.bypass.oms.entity.GroceryOrder;
import com.bypass.oms.service.OrderUpdateService;

@RestController
@EnableWebMvc
public class OrdersController {
	
	@Autowired
	private SomeComponent someComponent; 
	
	@Autowired
	private OrderUpdateService orderService; 
	
	
	Logger logger = LogManager.getLogger(OrdersController.class);
	
	@RequestMapping(path = "/orders/{id}", method = RequestMethod.GET)
    public ResponseEntity<Object> getOrders(@PathVariable String id) {		
		GroceryOrder groceryOrder = orderService.findOrderById(id);
		
		if (groceryOrder==null) {
			throw new OrderNotFoundException(); 
		}
		
		return ResponseEntity.ok(groceryOrder);

    }
	
	@RequestMapping(path = "/orders", method = RequestMethod.POST)
    public ResponseEntity<GroceryOrder> postOrder(@RequestBody GroceryOrder groceryOrder) throws Exception{

		boolean success = orderService.postOrderV2(groceryOrder);
		
		if (!success) throw new SomeErrorOccurred();
		
		return ResponseEntity.ok(groceryOrder);
		
    
	}
	
	@RequestMapping(path = "/orders/{id}", method = RequestMethod.PUT)
    public ResponseEntity<GroceryOrder> modifyOrder(@PathVariable String id, @RequestBody GroceryOrder groceryOrder) throws Exception {
		//logger.info()
		
		groceryOrder.setUuid(id);
		groceryOrder = orderService.putOrderV2(groceryOrder);
		
		if (groceryOrder==null) throw new OrderNotFoundException();
		
		return ResponseEntity.ok(groceryOrder);
		
    }
	
	@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Unable to find order")
	public class OrderNotFoundException extends RuntimeException {
		private static final long serialVersionUID = 1L;
	}

	@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR, reason = "An error occured")
	public class SomeErrorOccurred extends RuntimeException {
		private static final long serialVersionUID = 1L;
	}
	
	@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR, reason = "An order already exists with that id. Modify existing orders with put")
	public class OrderAlreadyExists extends RuntimeException {
		private static final long serialVersionUID = 1L;
	}
	
	@ExceptionHandler(OrderNotFoundException.class)
	public ResponseEntity<Object> orderNotFoundException (OrderNotFoundException exception) {
	    return new ResponseEntity<>("Order not found", HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> exceptionH (Exception exception) {
	    return new ResponseEntity<>("An error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
