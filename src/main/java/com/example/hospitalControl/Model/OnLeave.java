package com.example.hospitalControl.Model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
@Entity
@Table(name = "onleave")
public class OnLeave {
	@Id
	@Column(name = "id")
	private int id;
	
	@Column(name = "idperson")
	private String idPerson;
	
	@Column(name = "startdate")
	private LocalDateTime startDate;
	
	@Column(name = "enddate")
	private LocalDateTime endDate;
	
	@OneToOne
	@JsonBackReference
	@JoinColumn(name = "idperson", referencedColumnName = "idperson", insertable = false, updatable = false)
	private Doctor doctor;
	
	@OneToOne
	@JsonBackReference
	@JoinColumn(name = "idperson", referencedColumnName = "idperson", insertable = false, updatable = false)
	private Nuser nuser;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getIdPerson() {
		return idPerson;
	}

	public void setIdPerson(String idPerson) {
		this.idPerson = idPerson;
	}

	public LocalDateTime getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDateTime startDate) {
		this.startDate = startDate;
	}

	public LocalDateTime getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDateTime endDate) {
		this.endDate = endDate;
	}
	
}
