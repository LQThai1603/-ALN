package com.example.hospitalControl.Model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "medicalrecord")
public class MedicalRecord {
	@Id
	@Column(name = "idmedicalrecord")
	private int idMedicalRecord;
	
	@Column(name = "iddoctor")
	private String idDoctor;
	
	@Column(name = "idpatient")
	private int idPatient;
	
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
	
	@ManyToOne(optional = true)
	@JsonBackReference
	@JoinColumn(name = "iddoctor", referencedColumnName = "idperson", insertable = false, updatable = false)
	private Doctor doctor;
	
	@ManyToOne(optional = true)
	@JsonBackReference
	@JoinColumn(name = "idnuser", referencedColumnName = "idperson", insertable = false, updatable = false)
	private Nuser nuser;
	
	@OneToOne(optional = true)
	@JsonBackReference
	@JoinColumn(name = "idpatient", referencedColumnName = "idpatient", insertable = false, updatable = false)
	private Patient patient;
	

    @ManyToMany
    @JoinTable(
        name = "medicalrecord_medicine", // Tên bảng kết nối
        joinColumns = @JoinColumn(name = "idmedicalmecord"), // Cột khóa chính trong bảng MedicalRecord
        inverseJoinColumns = @JoinColumn(name = "idmedicine") // Cột khóa chính trong bảng Medicine
    )
	private List<Medicine> medicine;

	public int getId() {
		return idMedicalRecord;
	}

	public void setId(int idMedicalRecord) {
		this.idMedicalRecord = idMedicalRecord;
	}

	public String getIdDoctor() {
		return idDoctor;
	}

	public void setIdDoctor(String idDoctor) {
		this.idDoctor = idDoctor;
	}

	public int getIdPatient() {
		return idPatient;
	}

	public void setIdPatient(int idPatient) {
		this.idPatient = idPatient;
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
	
	
}
