package com.bypass.oms.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
//import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonManagedReference; 

@Entity
public class GroceryOrder {
	
	@Id
	String uuid; 
	
	@JsonManagedReference
	@OneToMany(mappedBy="groceryOrder", cascade=CascadeType.ALL)
	Set<GroceryItem> line_items;
	
	@JsonManagedReference
	@OneToMany(mappedBy="groceryOrder", cascade=CascadeType.ALL)
	Set<GroceryDiscount> discounts;
	
	@Column(name = "tax_total")
	int tax_total; 
	
	@Column(name = "total")
	int total;
	
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public Set<GroceryItem> getLine_items() {
		return line_items;
	}
	public void setLine_items(Set<GroceryItem> line_items) {
		this.line_items = line_items;
	}
	public Set<GroceryDiscount> getDiscounts() {
		return discounts;
	}
	public void setDiscounts(Set<GroceryDiscount> discounts) {
		this.discounts = discounts;
	}
	public int getTax_total() {
		return tax_total;
	}
	public void setTax_total(int tax_total) {
		this.tax_total = tax_total;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	
//	public void addItem( GroceryItem item) {
//		if (item != null) {
//	        if (line_items == null) {
//	        	line_items = new HashSet<GroceryItem>();          
//	        }
//	        line_items.add(item);
//	        item.setGroceryOrder(this);
//	     }
//	}
//	
//	public void addDiscount( GroceryDiscount discount) {
//		if (discount != null) {
//	        if (line_items == null) {
//	        	line_items = new HashSet<GroceryItem>();          
//	        }
//	        discounts.add(discount);
//	        discount.setOrder(this);
//	     }
//	}
	
}
