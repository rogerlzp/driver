package com.abc.driver.model;

import java.io.Serializable;

public class User  implements Serializable {
	
	//TODO: modify attrs for this user
	
	static public final Long INVALID_ID = 9999999L;
	private String username;
	private Long id = INVALID_ID;
	
	private String nickname;
	private String signature;
	
	private String title;
	private String company;
	private String school;
	private String birthdate;
	private String interest;
	
	
	private double latitude;
	private double longitude;
	public Boolean hasGotLoc = false;
	
	private Integer gender;
	private String profileImageUrl;
	
	
	private Truck myTruck;
	
	
	public User(){
		this.myTruck = new Truck(0); // 默认truck id ＝ 0， 表示没有新增车
	}
	
	
	public Truck getMyTruck() {
		return myTruck;
	}
	public void setMyTruck(Truck myTruck) {
		this.myTruck = myTruck;
	}
	public void setUsername(String username){
		this.username = username;
	}
	public String getUsername(){
		return this.username;
	}
	
	public void setId(Long id){
		this.id = id;
	}
	public long getId(){
		return this.id;
	}

}
