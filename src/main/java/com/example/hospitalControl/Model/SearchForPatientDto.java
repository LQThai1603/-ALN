package com.example.hospitalControl.Model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class SearchForPatientDto {
	@NotEmpty(message = "Thiếu tên")
	private String name;
	
	@NotNull(message = "Thiếu id bệnh nhân")
	private int idPatient;
	
	@NotEmpty(message = "Thiếu Số điện thoại")
	@Pattern(regexp = "^\\d{10}$", message = "Số điện thoại phải có 10 chữ số")
	private String phoneNumber;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getIdPatient() {
		return idPatient;
	}

	public void setIdPatient(int idPatient) {
		this.idPatient = idPatient;
	}
	
	

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
}
