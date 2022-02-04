package com.bypass.oms.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;


@Entity
public class GroceryDiscount {
	@Id
	private String uuid; 
	private String name; 
	private String type;
	private float amount; 
	
	@Column(name = "apply_to")
	private String apply_to;
	
	@JsonBackReference
	@ManyToOne(optional = false)
	@JoinColumn(name="order_id")
	private GroceryOrder groceryOrder; 
	
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public float getAmount() {
		return amount;
	}
	public void setAmount(float amount) {
		this.amount = amount;
	}
	public String getApply_to() {
		return apply_to;
	}
	public void setApply_to(String apply_to) {
		this.apply_to = apply_to;
	}
	public GroceryOrder getGroceryOrder() {
		return groceryOrder;
	}
	public void setGroceryOrder(GroceryOrder groceryorder) {
		this.groceryOrder = groceryorder;
	}
}
