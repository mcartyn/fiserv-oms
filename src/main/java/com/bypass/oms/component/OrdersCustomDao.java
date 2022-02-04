package com.bypass.oms.component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;
import com.bypass.oms.entity.GroceryDiscount;
import com.bypass.oms.entity.GroceryItem;
import com.bypass.oms.entity.GroceryOrder;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

@Component
public class OrdersCustomDao {
	
	@Autowired
	private DataSource ds; 
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public boolean updateOrderDetails(GroceryOrder groceryOrder, boolean insert) throws Exception{
		String itemsSql = ""; 
		String discountsSql = "";
		String orderSql = "";
		if (groceryOrder.getLine_items()!= null && groceryOrder.getLine_items().size()>0) {
			itemsSql = createItemSql(groceryOrder); 
		}
		if (groceryOrder.getDiscounts() != null && groceryOrder.getDiscounts().size()>0) {
			discountsSql = createDiscountsSql(groceryOrder); 
		}
		if (insert) orderSql = createOrderSql(groceryOrder);
		return execute3Statements(itemsSql, discountsSql, orderSql);
		
	}
	
	private String createItemSql (GroceryOrder groceryOrder) {
		String order_id = groceryOrder.getUuid(); 
		StringBuilder grocery_item_sql = new StringBuilder("insert into grocery_item values "); 
		boolean more_than_one = false; 
		for(GroceryItem item : groceryOrder.getLine_items()) {
			if (more_than_one) {
				grocery_item_sql.append(",");
			}
			if (item.getQuantity() == 0 ) {
				item.setQuantity(1);
			}
			
			grocery_item_sql.append("('"+item.getUuid()+"','"+order_id+"','"+item.getName()+"',"+item.getQuantity()+","+item.getPrice()+","+ item.getTax_rate()+")");
			more_than_one = true; 
		}
		String sql = grocery_item_sql.toString()+";";
		System.out.println(sql);
		return sql;  
	}
	
	private String createDiscountsSql (GroceryOrder groceryOrder)	{
		boolean more_than_one = false;
		String order_id = groceryOrder.getUuid(); 
		StringBuilder grocery_discount_sql = new StringBuilder("insert into grocery_discount values"); 
		for(GroceryDiscount item : groceryOrder.getDiscounts()) {
			if (more_than_one) {
				grocery_discount_sql.append(",");
			}
			grocery_discount_sql.append("('"+item.getUuid()+"','"+order_id+"','"+item.getName()+"','"+item.getType()+"',"+item.getAmount()+",'"+item.getApply_to()+"')");
			more_than_one = true; 
		}
		String sql = grocery_discount_sql.toString() + ";";
		System.out.println(sql);
//			db.insert(sql);
		return sql; 
	}
		
	private String createOrderSql (GroceryOrder groceryOrder)	{
		String order_id = groceryOrder.getUuid();
		StringBuilder grocery_order_sql = new StringBuilder("insert IGNORE into grocery_order values"); 
		
		grocery_order_sql.append("('"+order_id+"',0,0);");
		String sql = grocery_order_sql.toString();
		System.out.println(sql);
//		db.insert(grocery_order_sql.toString());
		return sql; 
	}
	
	public boolean deleteOrderDetails(String order_id) throws SQLException {
		String delete_order = "delete from grocery_item where order_id = '" + order_id + "';";
		String delete_items= "delete from grocery_discount where order_id = '" + order_id + "';";
		String delete_discounts = "delete from grocery_order where uuid = '" + order_id + "';";
		
		return execute3Statements(delete_items, delete_discounts, delete_order); 
	}
	
	private boolean execute3Statements(String sql1, String sql2, String sql3) throws SQLException {
		boolean ret = false; 
		
		Connection con = null;
		PreparedStatement pst_items = null;
		PreparedStatement pst_discounts = null;
		PreparedStatement pst_order = null;
		int i = 0;
		try {
//			System.out.println("here"); 
			con = ds.getConnection(); 
			con.setAutoCommit(false);
			
			if (sql3 != null && !sql3.equalsIgnoreCase("")) {
				pst_order = con.prepareStatement(sql3); 
				i = pst_order.executeUpdate();
			}
			
			if (sql1 != null && !sql1.equalsIgnoreCase("")) {
				pst_items = con.prepareStatement(sql1); 
				i = pst_items.executeUpdate();
			}
			
			if (sql2 != null && !sql2.equalsIgnoreCase("")) {
				pst_discounts = con.prepareStatement(sql2); 
				i = pst_discounts.executeUpdate();
			}
			
			System.out.println(i);
			con.commit();
			ret = true; 
			
		} catch (Exception ex) {
			System.out.println(ex);
			if (con!= null) con.rollback();
			throw ex; 
		}
		finally {
			if (pst_items != null) {
				pst_items.close();
			}
			
			if (pst_discounts != null) {
				pst_discounts.close();
			}
			
			if (pst_order != null) {
				pst_order.close();
			}
			
			if (con != null) {
				con.close();
			}
		}
		
		return ret; 
	}
	
	public GroceryOrder getGroceryOrder(String order_id) {
		
		String select_order = "select `uuid` as order_id, total, tax_total  from grocery_order where `uuid`= ?";
		String select_items = "select * from grocery_item where order_id =?";
		String select_discounts = "select * from grocery_discount where order_id = ? "; 
		GroceryOrder order; 
		
		try {
		 order = jdbcTemplate.queryForObject(select_order, new String [] {order_id}, new GroceryOrderMapper());
		} catch (EmptyResultDataAccessException e) {
			return null; 
		}
		
		List<GroceryItem> items = jdbcTemplate.query(select_items, new String [] {order_id}, new GroceryItemMapper());
		
		List<GroceryDiscount> discountList = jdbcTemplate.query(select_discounts, new String [] {order_id}, new GroceryDiscountMapper());
		
		Set<GroceryItem> line_items = new HashSet<>(); 
		line_items.addAll(items); 
		order.setLine_items(line_items);
		
		Set<GroceryDiscount> discounts = new HashSet<>(); 
		discounts.addAll(discountList); 
		order.setDiscounts(discounts);
		
		return order; 
		
			
	}
	
	private static final class GroceryItemMapper implements RowMapper<GroceryItem> {
	    public GroceryItem mapRow(ResultSet rs, int rowNum) throws SQLException {
	    	GroceryItem model = new GroceryItem();
		    model.setUuid(rs.getString("UUID"));
		    model.setName(rs.getString("NAME"));
		    model.setPrice(rs.getInt("PRICE"));
		    model.setQuantity(rs.getInt("QUANTITY"));
		    model.setTax_rate(rs.getFloat("TAX_RATE"));
	        return  model;
	    }
	}
	
	private static final class GroceryDiscountMapper implements RowMapper<GroceryDiscount> {
	    public GroceryDiscount mapRow(ResultSet rs, int rowNum) throws SQLException {
	    	GroceryDiscount model = new GroceryDiscount();
		    model.setUuid(rs.getString("UUID"));
		    model.setName(rs.getString("NAME"));
		    model.setAmount(rs.getInt("AMOUNT"));
		    model.setApply_to(rs.getString("APPLY_TO"));
		    model.setType(rs.getString("TYPE"));
	        return  model;
	    }
	}
	
	private static final class GroceryOrderMapper implements RowMapper<GroceryOrder> {
	    public GroceryOrder mapRow(ResultSet rs, int rowNum) throws SQLException {
	    	GroceryOrder model = new GroceryOrder();
		    model.setUuid(rs.getString("ORDER_ID"));
		    model.setTax_total(rs.getInt("TAX_TOTAL"));
		    model.setTotal(rs.getInt("TOTAL"));
	        return  model;
	    }
	}

}
