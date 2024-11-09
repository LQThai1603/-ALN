package com.example.hospitalControl.Model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "patient")
public class Patient {
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

	@Column(name = "iddoctor")
	private String idDoctor;

	@Column(name = "idnuser")
	private String idNuser;

	@Column(name = "conjecture", columnDefinition = "TEXT")
	private String conjecture;

	@Column(name = "conclusion", columnDefinition = "TEXT")
	private String conclusion;

	@Column(name = "examined")
	private boolean examined;

	@Column(name = "idmedicine")
	private int idMedicine;

	@Column(name = "price")
	private int price;

	@Column(name = "time")
	private LocalDateTime time;

	@ManyToOne(optional = true)
	@JsonBackReference
	@JoinColumn(name = "iddoctor", referencedColumnName = "idperson", insertable = false, updatable = false)
	private Doctor doctor;

	@ManyToMany
	@JsonBackReference
	@JoinTable(name = "patient_nuser", // Tên bảng kết nối
			joinColumns = @JoinColumn(name = "idpatient"), // Cột khóa chính trong bảng MedicalRecord
			inverseJoinColumns = @JoinColumn(name = "idperson") // Cột khóa chính trong bảng Nuser
	)
	private List<Nuser> nuser;

	@ManyToMany
	@JoinTable(name = "patient_medicine", // Tên bảng kết nối
			joinColumns = @JoinColumn(name = "idpatient"), // Cột khóa chính trong bảng Patient
			inverseJoinColumns = @JoinColumn(name = "idmedicine") // Cột khóa chính trong bảng Medicine
	)
	private List<Medicine> medicine;

	public int getIdPatient() {
		return idPatient;
	}

	public void setIdPatient(int idPatient) {
		this.idPatient = idPatient;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Sex getSex() {
		return sex;
	}

	public void setSex(Sex sex) {
		this.sex = sex;
	}

	public String getIdDoctor() {
		return idDoctor;
	}

	public void setIdDoctor(String idDoctor) {
		this.idDoctor = idDoctor;
	}

	public String getIdNuser() {
		return idNuser;
	}

	public void setIdNuser(String idNuser) {
		this.idNuser = idNuser;
	}

	public String getConjecture() {
		return conjecture;
	}

	public void setConjecture(String conjecture) {
		this.conjecture = conjecture;
	}

	public String getConclusion() {
		return conclusion;
	}

	public void setConclusion(String conclusion) {
		this.conclusion = conclusion;
	}

	public boolean isExamined() {
		return examined;
	}

	public void setExamined(boolean examined) {
		this.examined = examined;
	}

	public int getIdMedicine() {
		return idMedicine;
	}

	public void setIdMedicine(int idMedicine) {
		this.idMedicine = idMedicine;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public Doctor getDoctor() {
		return doctor;
	}

	public void setDoctor(Doctor doctor) {
		this.doctor = doctor;
	}

	public List<Nuser> getNuser() {
		return nuser;
	}

	public void setNuser(List<Nuser> nuser) {
		this.nuser = nuser;
	}

	public List<Medicine> getMedicine() {
		return medicine;
	}

	public void setMedicine(List<Medicine> medicine) {
		this.medicine = medicine;
	}

	public LocalDateTime getTime() {
		return time;
	}

	public void setTime(LocalDateTime time) {
		this.time = time;
	}
	
}
