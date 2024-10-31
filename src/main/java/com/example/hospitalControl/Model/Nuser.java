package com.example.hospitalControl.Model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
@Entity
@Table(name = "nuser")
public class Nuser extends Person{
	@Column(name = "avatar")
	private String avatar;
	
	@Column(name = "degree")
	private String degree;
	
	@Enumerated(value = EnumType.STRING)
	private Room room;
	
	@Column(name = "yearsexperience")
	private int yearsExperience;
	
	@Column(name = "price")
	private int price;
	
	@OneToOne(mappedBy = "nuser")
	@JsonManagedReference
	private OnLeave onLeave;

	@OneToMany(mappedBy = "nuser")
	@JsonManagedReference
	private List<MedicalRecord> medicalRecord;
	
	public String getDegree() {
		return degree;
	}

	public void setDegree(String degree) {
		this.degree = degree;
	}

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	public int getYearsExperience() {
		return yearsExperience;
	}

	public void setYearsExperience(int yearsExperience) {
		this.yearsExperience = yearsExperience;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public OnLeave getOnLeave() {
		return onLeave;
	}

	public void setOnLeave(OnLeave onLeave) {
		this.onLeave = onLeave;
	}

	public List<MedicalRecord> getMedicalRecord() {
		return medicalRecord;
	}

	public void setMedicalRecord(List<MedicalRecord> medicalRecord) {
		this.medicalRecord = medicalRecord;
	}
	
	
}
