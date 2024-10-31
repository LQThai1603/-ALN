package com.example.hospitalControl.Model;

import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CreateNuserDto {
	@NotEmpty(message = "Yêu cầu tên tài khoản")
	private String userName;
	
	@NotEmpty(message = "Yêu cầu mật khẩu")
//    @Size(min = 9, message = "Mật khẩu phải có ít nhất 9 ký tự")
//    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/\\?]).+$", 
//             message = "Mật khẩu phải chứa ít nhất một ký tự viết hoa, một ký tự viết thường, một ký tự số và một ký tự đặc biệt")
	private String password;

	@NotEmpty(message = "Yêu cầu tên người dùng")
	private String name;
	
	@NotNull(message = "Yêu cầu tuổi")
    @Min(value = 1, message = "Tuổi phải lớn hơn 0")
    @Max(value = 99, message = "Tuổi phải nhỏ hơn 100")
	private int age;
	
	@NotEmpty(message = "Yêu cầu email")
    @Email(message = "Định dạng email không hợp lệ")
    @Column(name = "email")
	private String email;
	
	@NotEmpty(message = "Yêu cầu nhập giới tính")
	private String sex;
	 
	@NotNull(message = "Yêu cầu số điện thoại")
    @Pattern(regexp = "^\\d{10}$", message = "Số điện thoại phải có 10 chữ số")
	private String phoneNumber;
	
	@NotEmpty(message = "Yêu cầu nhập phòng làm việc")
	private String room;
	
	@NotNull(message = "Yêu cầu nhập số năm kinh nghiệm")
	@Min(value = 0, message = "số năm kinh nghiệm phải từ 0 trở lên")
	private int yearsExperience;
	
	@NotEmpty(message = "Yêu cầu nhập bằng cấp")
	private String degree;
	
	@NotNull(message = "Yêu cầu nhập giá phải trả nếu đến khám tại phòng y tá làm việc")
    @Min(value = 0, message = "Giá phải lớn hơn hoặc bằng 0")
	private int price;
	
	@NotNull(message = "Avatar không được bỏ trống")
	private MultipartFile avatarNuser;
	
	private boolean quit;
	

	public boolean isQuit() {
		return quit;
	}

	public void setQuit(boolean quit) {
		this.quit = quit;
	}

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getRoom() {
		return room;
	}

	public void setRoom(String room) {
		this.room = room;
	}

	public int getYearsExperience() {
		return yearsExperience;
	}

	public void setYearsExperience(int yearsExperience) {
		this.yearsExperience = yearsExperience;
	}

	public String getDegree() {
		return degree;
	}

	public void setDegree(String degree) {
		this.degree = degree;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public MultipartFile getAvatarNuser() {
		return avatarNuser;
	}

	public void setAvatarNuser(MultipartFile avatarNuser) {
		this.avatarNuser = avatarNuser;
	}

	
	
}
