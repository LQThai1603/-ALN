package com.example.hospitalControl.Model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class AccountDto {
	@NotEmpty(message = "Yêu cầu tên người dùng")
	private String userName;
	
	@NotEmpty(message = "Yêu cầu mật khẩu")
//    @Size(min = 9, message = "Mật khẩu phải có ít nhất 9 ký tự")
//    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/\\?]).+$", 
//             message = "Mật khẩu phải chứa ít nhất một ký tự viết hoa, một ký tự viết thường, một ký tự số và một ký tự đặc biệt")
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
