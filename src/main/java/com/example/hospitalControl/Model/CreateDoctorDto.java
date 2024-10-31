package com.example.hospitalControl.Model;

import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CreateDoctorDto {
	@NotEmpty(message = "Yêu cầu tên tài khoản")
	private String userName;
	
	@NotEmpty(message = "Yêu cầu mật khẩu")
//    @Size(min = 9, message = "Mật khẩu phải có ít nhất 9 ký tự")
//    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/\\?]).+$", 
//             message = "Mật khẩu phải chứa ít nhất một ký tự viết hoa, một ký tự viết thường, một ký tự số và một ký tự đặc biệt")
	private String password;

	@NotEmpty(message = "Yêu cầu tên người dùng")
	private String name;
//	
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
	 
	@NotEmpty(message = "Yêu cầu số điện thoại")
    @Pattern(regexp = "^\\d{10}$", message = "Số điện thoại phải có 10 chữ số")
	private String phoneNumber;
	
	@NotEmpty(message = "Yêu cầu nhập chuyên môn")
	private String specialized;
	
	@NotNull(message = "Yêu cầu nhập số năm kinh nghiệm")
	@Min(value = 0, message = "số năm kinh nghiệm phải từ 0 trở lên")
	private int yearsExperience;
	
	@NotEmpty(message = "Yêu cầu nhập bằng cấp")
	private String degree;
	
	private MultipartFile avatarDoctor;

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

	public String getSpecialized() {
		return specialized;
	}

	public void setSpecialized(String specialized) {
		this.specialized = specialized;
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

	public MultipartFile getAvatarDoctor() {
		return avatarDoctor;
	}

	public void setAvatarDoctor(MultipartFile avatarDoctor) {
		this.avatarDoctor = avatarDoctor;
	}
	
	
}