package com.example.hospitalControl.Model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "doctor")
public class Doctor extends Person{
	@Column(name = "avatar")
	private String avatar;
	
	@Enumerated(value = EnumType.STRING)
	private Specialized specialized;
	
	@Column(name = "yearsexperience")
	private int yearsExperience;
	
	@Column(name = "degree")
	private String degree;
	
	@OneToMany(mappedBy = "doctor")
	@JsonManagedReference
	private List<Patient> patient;

	public Specialized getSpecialized() {
		return specialized;
	}

	public void setSpecialized(Specialized specialized) {
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

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public List<Patient> getPatient() {
		return patient;
	}

	public void setPatient(List<Patient> patient) {
		this.patient = patient;
	}
	
}
