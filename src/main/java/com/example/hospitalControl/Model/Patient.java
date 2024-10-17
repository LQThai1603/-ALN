package com.example.hospitalControl.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "patient")
public class Patient{
	@Id
	@Column(name = "idpatient")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idPatient;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "phonenumber")
	private String phoneNumber;
	
	@Column(name = "age")
	private int age;
	
	@Column(name = "address")
	private String address;
	
	@Enumerated(value = EnumType.STRING)
	private Sex sex;
	
	@OneToOne(mappedBy = "patient")
	@JsonManagedReference
	private MedicalRecord medicalRecord;
	
}
