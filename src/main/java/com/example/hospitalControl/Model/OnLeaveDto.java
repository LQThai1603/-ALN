package com.example.hospitalControl.Model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;

public class OnLeaveDto {
	private int id;
	
	@NotNull(message = "thiếu idPerson")
	private String idPerson;
	
	@NotNull(message = "chưa điền ngày bắt đầu")
	private LocalDate startDate;
	
	@NotNull(message = "chưa điền ngày kết thúc")
	private LocalDate endDate;
	
	@NotNull(message = "chưa điền lý do")
	private String reason;
	
	
	private boolean confirm;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getIdPerson() {
		return idPerson;
	}

	public void setIdPerson(String idPerson) {
		this.idPerson = idPerson;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public boolean isConfirm() {
		return confirm;
	}

	public void setConfirm(boolean confirm) {
		this.confirm = confirm;
	}
	
	
}
