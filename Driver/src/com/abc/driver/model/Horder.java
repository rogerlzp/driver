package com.abc.driver.model;


import java.io.Serializable;
import java.util.Date;

public class Horder  implements Serializable {
	
	static public final int HORDER_ID = 0;
	
	private String shipper_username;
	private String shipper_phone;
	private String consignee_username;
	private String consignee_phone;
	private Date  shipper_date;
	private Date delivery_time;
	
	private String shipper_address_code;
	private String consignee_address_code;

	private int truck_type_id;
	private float truck_length;
	private int cargo_type;
	private int cargo_volume;
	private float cargo_weight;
	private String horder_desc;
	private int user_id;
	private int status_id;
	

	protected String getShipper_username() {
		return shipper_username;
	}


	protected void setShipper_username(String shipper_username) {
		this.shipper_username = shipper_username;
	}


	protected String getShipper_phone() {
		return shipper_phone;
	}


	protected void setShipper_phone(String shipper_phone) {
		this.shipper_phone = shipper_phone;
	}


	protected String getConsignee_username() {
		return consignee_username;
	}


	protected void setConsignee_username(String consignee_username) {
		this.consignee_username = consignee_username;
	}


	protected String getConsignee_phone() {
		return consignee_phone;
	}


	protected void setConsignee_phone(String consignee_phone) {
		this.consignee_phone = consignee_phone;
	}


	protected Date getShipper_date() {
		return shipper_date;
	}


	protected void setShipper_date(Date shipper_date) {
		this.shipper_date = shipper_date;
	}


	protected Date getDelivery_time() {
		return delivery_time;
	}


	protected void setDelivery_time(Date delivery_time) {
		this.delivery_time = delivery_time;
	}


	protected String getShipper_address_code() {
		return shipper_address_code;
	}


	protected void setShipper_address_code(String shipper_address_code) {
		this.shipper_address_code = shipper_address_code;
	}


	protected String getConsignee_address_code() {
		return consignee_address_code;
	}


	protected void setConsignee_address_code(String consignee_address_code) {
		this.consignee_address_code = consignee_address_code;
	}


	protected int getTruck_type_id() {
		return truck_type_id;
	}


	protected void setTruck_type_id(int truck_type_id) {
		this.truck_type_id = truck_type_id;
	}


	protected float getTruck_length() {
		return truck_length;
	}


	protected void setTruck_length(float truck_length) {
		this.truck_length = truck_length;
	}


	protected int getCargo_type() {
		return cargo_type;
	}


	protected void setCargo_type(int cargo_type) {
		this.cargo_type = cargo_type;
	}


	protected int getCargo_volume() {
		return cargo_volume;
	}


	protected void setCargo_volume(int cargo_volume) {
		this.cargo_volume = cargo_volume;
	}


	protected float getCargo_weight() {
		return cargo_weight;
	}


	protected void setCargo_weight(float cargo_weight) {
		this.cargo_weight = cargo_weight;
	}


	protected String getHorder_desc() {
		return horder_desc;
	}


	protected void setHorder_desc(String horder_desc) {
		this.horder_desc = horder_desc;
	}


	protected int getUser_id() {
		return user_id;
	}


	protected void setUser_id(int user_id) {
		this.user_id = user_id;
	}


	protected int getStatus_id() {
		return status_id;
	}


	protected void setStatus_id(int status_id) {
		this.status_id = status_id;
	}


	public Horder() {

	}
	
	
	
}
