package com.example.hospitalControl.Model;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "medicine")
public class Medicine {
	@Id
	@Column(name = "idmedicine")
	private int idMedicine;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "expirationdate")
	private LocalDateTime expirationDate;
	
	@Column(name = "quantity")
	private int quantity;
	
	@Column(name = "price")
	private int price;
	
	@ManyToMany(mappedBy = "medicine")
	@JsonManagedReference
	private List<MedicalRecord> medicalRecord;

	public int getIdMedicine() {
		return idMedicine;
	}

	public void setIdMedicine(int idMedicine) {
		this.idMedicine = idMedicine;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LocalDateTime getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(LocalDateTime expirationDate) {
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
