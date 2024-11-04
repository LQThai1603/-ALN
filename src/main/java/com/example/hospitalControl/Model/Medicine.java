package com.example.hospitalControl.Model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "medicine")
public class Medicine {
	@Id
	@Column(name = "idmedicine")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idMedicine;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "expirationdate")
	private LocalDate expirationDate;
	
	@Column(name = "quantity")
	private int quantity;
	
	@Column(name = "price")
	private int price;
	
	@Column(name = "image")
	private String image;
	
	@ManyToMany(mappedBy = "medicine")
	@JsonManagedReference
	private List<Patient> patient;

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

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public List<Patient> getPatient() {
		return patient;
	}

	public void setPatient(List<Patient> patient) {
		this.patient = patient;
	}

}
