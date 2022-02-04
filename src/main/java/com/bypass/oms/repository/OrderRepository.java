/**
 * 
 */
package com.bypass.oms.repository;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.bypass.oms.entity.GroceryOrder;

/**
 * @author rgb764
 *
 */

public interface OrderRepository extends CrudRepository<GroceryOrder, String> {
	
	public GroceryOrder findGroceryOrderByUuid(String uuid); 
	
	@Transactional
	@Modifying
	@Query(value = "update grocery_order set total = :total, tax_total = :tax_total where uuid = :uuid", nativeQuery = true)
	public void updateTotalAndTax(@Param("uuid") String uuid, @Param("total") float total, @Param("tax_total") float tax) ; 
}
