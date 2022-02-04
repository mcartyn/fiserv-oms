package com.bypass.oms.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
public class GroceryItem {
	@Id
	String uuid; 
	String name; 
	
	int price; 
	
	@Column(name = "quantity")
	int quantity; 
	
	@Column(name = "tax_rate")
	float tax_rate; 
	
	@JsonBackReference
	@ManyToOne(optional = false)
	@JoinColumn(name="order_id")
	private GroceryOrder groceryOrder; 

	public GroceryOrder getGroceryOrder() {
		return groceryOrder;
	}

	public void setGroceryOrder(GroceryOrder groceryOrder) {
		this.groceryOrder = groceryOrder;
	}

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

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
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
	
	
	
}
