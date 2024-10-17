package com.example.hospitalControl.Model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

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
	@Enumerated(value = EnumType.STRING)
	private Specialized specialized;
	
	@Column(name = "yearsexperience")
	private int yearsExperience;
	
	@Column(name = "degree")
	private String degree;
	
	@OneToOne(mappedBy = "doctor")
	@JsonManagedReference
	private OnLeave onLeave;
	
	@OneToMany(mappedBy = "doctor")
	@JsonManagedReference
	private List<MedicalRecord> medicalRecord;

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
	
	
}
