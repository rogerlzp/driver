package com.abc.driver.model;

import java.io.Serializable;

public class User implements Serializable {

	// TODO: modify attrs for this user

	static public final Long INVALID_ID = 9999999L;
	private String username;
	private Long id = INVALID_ID;

	public String name; // 姓名
	public String company;

	public String mobileNum;

	public String getMobileNum() {
		return mobileNum;
	}

	public void setMobileNum(String mobileNum) {
		this.mobileNum = mobileNum;
	}

	public String profileImageUrl;
	public String identityFrontImageUrl;
	public String identityBackImageUrl;

	public String getIdentityFrontImageUrl() {
		return identityFrontImageUrl;
	}

	public void setIdentityFrontImageUrl(String identityFrontImageUrl) {
		this.identityFrontImageUrl = identityFrontImageUrl;
	}

	public String getIdentityBackImageUrl() {
		return identityBackImageUrl;
	}

	public void setIdentityBackImageUrl(String identityBackImageUrl) {
		this.identityBackImageUrl = identityBackImageUrl;
	}

	public String identityCode;
	public String driverLicense;
	public String driverLicenseImageUrl;

	public Truck myTruck;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getProfileImageUrl() {
		return profileImageUrl;
	}

	public void setProfileImageUrl(String profileImageUrl) {
		this.profileImageUrl = profileImageUrl;
	}

	public String getIdentityCode() {
		return identityCode;
	}

	public void setIdentityCode(String identityCode) {
		this.identityCode = identityCode;
	}

	public User() {
		this.myTruck = new Truck(0); // 默认truck id ＝ 0， 表示没有新增车
	}

	public Truck getMyTruck() {
		return myTruck;
	}

	public void setMyTruck(Truck myTruck) {
		this.myTruck = myTruck;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUsername() {
		return this.username;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public long getId() {
		return this.id;
	}

	public String getDriverLicense() {
		return driverLicense;
	}

	public void setDriverLicense(String driverLicense) {
		this.driverLicense = driverLicense;
	}

	public String getDriverLicenseImageUrl() {
		return driverLicenseImageUrl;
	}

	public void setDriverLicenseImageUrl(String driverLicenseImageUrl) {
		this.driverLicenseImageUrl = driverLicenseImageUrl;
	}

}
