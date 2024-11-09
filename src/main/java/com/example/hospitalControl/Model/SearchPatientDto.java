package com.example.hospitalControl.Model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class SearchPatientDto {
	private String name;
	
	private int idPatient;
	
	private LocalDate time;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getIdPatient() {
		return idPatient;
	}

	public void setIdPatient(int idPatient) {
		this.idPatient = idPatient;
	}

	public LocalDate getTime() {
		return time;
	}

	public void setTime(LocalDate time) {
		this.time = time;
	}

	@Override
	public String toString() {
		return "SearchPatientDto [name=" + name + ", idPatient=" + idPatient + ", time=" + time + "]";
	}
	
	
}
