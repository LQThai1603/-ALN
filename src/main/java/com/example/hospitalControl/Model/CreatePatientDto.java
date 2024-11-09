package com.example.hospitalControl.Model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class CreatePatientDto {
	
	@NotEmpty(message = "Thiếu tên người khám")
	private String name;
	
	@NotEmpty(message = "Thiếu số điện thoại")
	private String phoneNumber;
	
	@NotNull(message = "Thiếu tuổi")
	private int age;
	
	@NotEmpty(message = "Thiếu địa chỉ")
	private String address;

	@NotEmpty(message = "Thiếu giới tính")
	private String sex;
	
	@NotEmpty(message = "Thiếu id bác sĩ khám")
	private String idDoctor;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getIdDoctor() {
		return idDoctor;
	}

	public void setIdDoctor(String idDoctor) {
		this.idDoctor = idDoctor;
	}
}
