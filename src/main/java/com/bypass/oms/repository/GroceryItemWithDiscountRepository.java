package com.bypass.oms.repository;

import java.util.List;

import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.bypass.oms.entity.GroceryItemWithDiscount;

public interface GroceryItemWithDiscountRepository extends CrudRepository<GroceryItemWithDiscount, String> {
	
	@Query(value = "SELECT \r\n"
			+ "    item.uuid AS uuid,\r\n"
			+ "    item.`name` AS `name`,\r\n"
			+ "    item.price AS price,\r\n"
			+ "    item.quantity AS quantity,\r\n"
			+ "    item.tax_rate AS tax_rate,\r\n"
			+ "    coalesce(discount.amount,0) AS `discount_amount`,\r\n"
			+ "    discount.apply_to AS apply_to,\r\n"
			+ "    discount.`type` AS `discount_type`,\r\n"
			+ "    item.order_id AS order_id\r\n"
			+ "FROM\r\n"
			+ "    grocery_item item\r\n"
			+ "        LEFT OUTER JOIN\r\n"
			+ "    grocery_discount discount ON item.uuid = discount.apply_to where item.order_id =:order_id", nativeQuery=true)
	List<GroceryItemWithDiscount> findAllGroceryItemWithDiscountByOrderId(@Param("order_id") String order_id);

}
