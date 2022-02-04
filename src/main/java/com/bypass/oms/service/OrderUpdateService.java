package com.bypass.oms.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import javax.transaction.Transactional;

import com.bypass.oms.utils.Util; 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bypass.oms.entity.GroceryDiscount;
import com.bypass.oms.entity.GroceryItem;
import com.bypass.oms.entity.GroceryItemWithDiscount;
import com.bypass.oms.entity.GroceryOrder;
import com.bypass.oms.repository.GroceryDiscountRespository;
import com.bypass.oms.repository.GroceryItemWithDiscountRepository;
import com.bypass.oms.repository.OrderRepository;
import com.bypass.oms.component.OrdersCustomDao;


@Service
public class OrderUpdateService {
	
	public class OrderDiscount {
		float discountAsAmount; 
		float discountAsPercent;
		
		public float getDiscountAsAmount() {
			return discountAsAmount;
		}
		public void setDiscountAsAmount(float discountAsAmount) {
			this.discountAsAmount = discountAsAmount;
		}
		public float getDiscountAsPercent() {
			return discountAsPercent;
		}
		public void setDiscountAsPercent(float discountAsPercent) {
			this.discountAsPercent = discountAsPercent;
		} 
	}
	@PersistenceContext
    EntityManager entityManager;

	@Autowired 
	private OrderRepository ordersDb; 
	
	@Autowired 
	private GroceryItemWithDiscountRepository itemsWithDiscountRepository;
	
	@Autowired
	private OrdersCustomDao db; 
	
	@Autowired
	private GroceryDiscountRespository discountsDb; 
	
	private static final boolean INSERT = true;
	
	private static final boolean UPDATE = false;
	
	private OrderDiscount calculateOrderDiscount(GroceryOrder order) {
		
		List<GroceryDiscount> groceryDiscounts = discountsDb.findDiscountAppliedToOrder(order.getUuid());
		OrderDiscount orderDiscount = new OrderDiscount(); 
		float discountAsAmount = 0; 
		float discountAsPercent = 0; 
		for (GroceryDiscount item : groceryDiscounts) {
			if (item.getType()!= null && item.getType().equalsIgnoreCase("percent")) {
				discountAsPercent += item.getAmount(); 
			} else {
				discountAsAmount += item.getAmount();  
			}
		}
		orderDiscount.setDiscountAsAmount(discountAsAmount);
		orderDiscount.setDiscountAsPercent(discountAsPercent);
		
		return orderDiscount; 
	}
	
	private void calculate(GroceryOrder order) {
		//TODO: implement
		
		//		1. Apply order discounts
		//		2. Apply line item discounts
		//		2. Apply taxes
		//		3. Rounding should be done to the closest penny.
		
		// fetch all items left outer joined with their discounts. 
		List<GroceryItemWithDiscount> items = itemsWithDiscountRepository.findAllGroceryItemWithDiscountByOrderId(order.getUuid());
		
		// find if there are percent or amount discounts for the order itself. 
		OrderDiscount orderDiscount = calculateOrderDiscount(order); 
		
		float orderDiscountAsPercent = orderDiscount.getDiscountAsPercent(); 
		float total = 0; 
		float tax = 0; 
		for (GroceryItemWithDiscount item : items) {
			// Applying order discounts 
			float discountedItemPrice = orderDiscountAsPercent==0 ? item.getPrice() : Util.percentReduction(item.getPrice(), orderDiscountAsPercent) ;
			
			// float discountedItemPrice = item.getPrice(); 
			
			//Applying line discounts 
			if (item.getDiscount_type()!= null && item.getDiscount_type().equalsIgnoreCase("percent")) {
//				 discountedItemPrice = Util.percentReduction (discountedItemPrice, item.getDiscount_amount());
				 discountedItemPrice = Util.roundOff(Util.percentReduction (discountedItemPrice, item.getDiscount_amount()));   
			} else if (item.getDiscount_type()!= null && item.getDiscount_type().equalsIgnoreCase("amount")){
				 discountedItemPrice = Math.max(discountedItemPrice - item.getDiscount_amount(), 0);  
			} 
//			System.out.println(discountedItemPrice);
			total += discountedItemPrice;
			 
			 // calculating taxes.
//			float itemTax = Util.roundOff(Util.roundedPercent(discountedItemPrice, item.getTax_rate()));
			float itemTax = Util.percent(discountedItemPrice, item.getTax_rate());
//			System.out.println(itemTax);
			
			tax += itemTax;
		}
		
		// deducting any order amount discount
		System.out.println("order discount is -->" + orderDiscount.getDiscountAsAmount());
		total = Math.max(Util.roundOff(total - orderDiscount.getDiscountAsAmount()), 0); 
		System.out.println("total here is -->" + total);
		tax = Util.roundOff(tax);
		
		order.setTotal((int)(total + tax));
		order.setTax_total((int)tax);
	}
	
	private void setUpNewGroceryOrder(GroceryOrder groceryOrder) {
		//TODO: implement
		for(GroceryItem item : groceryOrder.getLine_items()) {
			if (item.getQuantity() == 0 ) {
				item.setQuantity(1);
			}
			if (item.getGroceryOrder()==null) {
				item.setGroceryOrder(groceryOrder);
			}
		}
		for(GroceryDiscount item : groceryOrder.getDiscounts()) {
//		item.setOrder(groceryOrder);
			if (item.getGroceryOrder()==null) {
				item.setGroceryOrder(groceryOrder);;
			}
//			System.out.println("apply to -->" + item.getApply_to());
		}
		
//		calculate(groceryOrder); 
	}
	
	private void update(GroceryOrder oldOrder, GroceryOrder newOrder) {
		Set<GroceryItem> newItems = newOrder.getLine_items(); 
		if (newItems!= null && newItems.size() > 0) {
			oldOrder.getLine_items().addAll(newItems); 
		}
		
		Set<GroceryDiscount> newDiscounts = newOrder.getDiscounts(); 
		if (newDiscounts!= null && newDiscounts.size() > 0) {
			oldOrder.getDiscounts().addAll(newDiscounts); 
		}
	}
	
	@Transactional
	public void postOrder(GroceryOrder groceryOrder) throws Exception {
		
		GroceryOrder oldOrder = ordersDb.findGroceryOrderByUuid(groceryOrder.getUuid()); 
		
		if ( oldOrder != null) {
//			ordersDb.delete(oldOrder);
			throw new Exception("Order already Exists"); 
		}
		
		setUpNewGroceryOrder(groceryOrder); 
		ordersDb.save(groceryOrder);
		calculate(groceryOrder);
		ordersDb.updateTotalAndTax(groceryOrder.getUuid(), groceryOrder.getTotal(), groceryOrder.getTax_total());
	}
	
	/**
	 * Retrieves an entity by its id.
	 * 
	 * @param id must not be {@literal null}.
	 * @return the entity with the given id or {@literal null} if none found
	 * @throws IllegalArgumentException if {@code id} is {@literal null}
	 */
	@Transactional
	public GroceryOrder putOrder(GroceryOrder groceryOrder) {
		
		GroceryOrder oldOrder = findOrderById(groceryOrder.getUuid()); 
		if (oldOrder == null) {
			return null; 
		}
		update(oldOrder, groceryOrder); 
		ordersDb.save(oldOrder);
		//		GroceryOrder updatedOrder = findOrderById(groceryOrder.getUuid());
		calculate(oldOrder);
		ordersDb.updateTotalAndTax(oldOrder.getUuid(), oldOrder.getTotal(), oldOrder.getTax_total());
//		GroceryOrder updatedOrder = ordersDb.findGroceryOrderByUuid(oldOrder.getUuid()); 
		return oldOrder; 
	}
	
	public GroceryOrder findOrderById(String id) {
//		return ordersDb.findOne(id);
		return db.getGroceryOrder(id);
	}
	
	public void checkDbConnection(DataSource dataSource) throws SQLException {
		System.out.println("data source is here and it is valid = "+ dataSource.getConnection().isValid(1000));
	}
	
	
	public boolean postOrderV2(GroceryOrder groceryOrder) throws Exception{
		GroceryOrder oldOrder = findOrderById(groceryOrder.getUuid()); 
		if ( oldOrder != null) {
			// TODO: implement custom delete
			// return false; 
			boolean deleted = db.deleteOrderDetails(groceryOrder.getUuid());
			if (!deleted) return false;
			System.out.println("delete completed");
		}
		
		
		
		boolean inserted = db.updateOrderDetails(groceryOrder, INSERT);  
		if (!inserted) return false; 
		System.out.println("insert completed");
		
		calculate(groceryOrder);
		ordersDb.updateTotalAndTax(groceryOrder.getUuid(), groceryOrder.getTotal(), groceryOrder.getTax_total());
		
		System.out.println("calculate completed");
		
		return true;
	}
	
	
	public GroceryOrder putOrderV2(GroceryOrder groceryOrder) throws Exception{
		GroceryOrder oldOrder = findOrderById(groceryOrder.getUuid());
//		GroceryOrder oldOrder = db.getGroceryOrder(groceryOrder.getUuid()); 
		
		if ( oldOrder == null) {
			System.out.println("lookup err");
			return null; 
		}
		
		System.out.println("lookup completed");
		
		boolean updated = db.updateOrderDetails(groceryOrder, UPDATE);  
		
		if (!updated) throw new Exception(); 
		
		GroceryOrder updatedOrder = db.getGroceryOrder(groceryOrder.getUuid());
		
		System.out.println("order fetch completed");
		
		calculate(updatedOrder);
		System.out.println("total ->" + updatedOrder.getTotal() + ", tax_total -> " +  updatedOrder.getTax_total());
		ordersDb.updateTotalAndTax(updatedOrder.getUuid(), updatedOrder.getTotal(), updatedOrder.getTax_total());
		
		System.out.println("calculate completed");
		
		return updatedOrder;
	}
	
}
