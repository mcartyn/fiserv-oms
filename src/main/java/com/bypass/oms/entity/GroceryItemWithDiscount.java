package com.bypass.oms.entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class GroceryItemWithDiscount {
	
	@Id
	private String uuid; 
	
	private String name; 
	
	private float price; 

	private int quantity; 
	
	private float tax_rate;
	
	@Column(nullable=true)
	private float discount_amount; 
	
	@Column(nullable=true)
	private String apply_to;
	
	@Column(nullable=true)
	private String discount_type; 
	
	private String order_id; 
	
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public float getTax_rate() {
		return tax_rate;
	}

	public void setTax_rate(float tax_rate) {
		this.tax_rate = tax_rate;
	}

	public float getDiscount_amount() {
		return discount_amount;
	}

	public void setDiscount_amount(float discount_amount) {
		this.discount_amount = discount_amount;
	}

	public String getApply_to() {
		return apply_to;
	}

	public void setApply_to(String apply_to) {
		this.apply_to = apply_to;
	}

	public String getDiscount_type() {
		return discount_type;
	}

	public void setDiscount_type(String discount_type) {
		this.discount_type = discount_type;
	}

	public String getOrder_id() {
		return order_id;
	}

	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}

	
}
