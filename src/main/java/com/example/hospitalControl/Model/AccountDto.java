package com.example.hospitalControl.Model;

import jakarta.validation.constraints.NotEmpty;

public class AccountDto {
	@NotEmpty(message = "Yêu cầu tên người dùng")
	private String userName;
	
	@NotEmpty(message = "Yêu cầu mật khẩu")
	private String password;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}