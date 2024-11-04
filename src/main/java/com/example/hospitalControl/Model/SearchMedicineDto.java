package com.example.hospitalControl.Model;

import java.time.LocalDate;

public class SearchMedicineDto {
    private String nameMedicine;
    private LocalDate expirationDate;
    private int quantity;
    private int price;
	public String getNameMedicine() {
		return nameMedicine;
	}
	public void setNameMedicine(String nameMedicine) {
		this.nameMedicine = nameMedicine;
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
    
    
}