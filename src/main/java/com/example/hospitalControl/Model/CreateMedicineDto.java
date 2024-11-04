package com.example.hospitalControl.Model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class CreateMedicineDto {
	
	private int id;
	
	@NotNull(message = "Thiếu tên thuốc")
    private String name;

    @Future(message = "Ngày hết hạn phải trong tương lai")
    @NotNull(message = "thiếu ngày hết hạn")
    private LocalDate expirationDate;

    @Min(value = 0, message = "Số lượng phải lớn hơn hoặc bằng 0")
    private int quantity;

    @Min(value = 0, message = "Giá phải lớn hơn hoặc bằng 0")
    private int price;
    
    MultipartFile image;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LocalDate getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(LocalDate expirationDate) {
		this.expirationDate = expirationDate;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public MultipartFile getImage() {
		return image;
	}

	public void setImage(MultipartFile image) {
		this.image = image;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
}
