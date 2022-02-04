package com.bypass.oms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.bypass.oms.entity.GroceryDiscount;

public interface GroceryDiscountRespository extends CrudRepository<GroceryDiscount, String> {
	
	@Query(value = "select * from grocery_discount where order_id = :order_id and apply_to = :order_id", nativeQuery = true)
	public List<GroceryDiscount> findDiscountAppliedToOrder(@Param("order_id") String order_id); 
}
