package com.example.hospitalControl.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.hospitalControl.Model.Patient;

public interface PatientRepository extends JpaRepository<Patient, Integer>{
	@Query("SELECT o FROM Patient o " +
		       "WHERE o.name LIKE %:name% " +
		       "AND (:time IS NULL OR (o.time >= :time))" + 
		       "AND (:idPatient = 0 OR o.idPatient = :idPatient)")
	Page<Patient> findByIdPatientTimeName(int idPatient, LocalDateTime time, String name, Pageable pageable);
	
	@Query("SELECT o FROM Patient o " +
		       "WHERE o.name LIKE %:name% " +
		       "AND (:time IS NULL OR (o.time >= :time))" + 
		       "AND (:idPatient = 0 OR o.idPatient = :idPatient)" +
		       "AND (o.examined = :examined)")
	Page<Patient> findByIdPatientTimeNameExamined(int idPatient, LocalDateTime time, String name, boolean examined, Pageable pageable);

	Page<Patient> findByIdDoctor(String idDoctor, Pageable pageable);
	
	Page<Patient> findByExamined(boolean examined, Pageable pageable);
	
	@Query("SELECT o FROM Patient o " +
		       "WHERE o.name LIKE %:name% " +
			   "AND (:idPatient = 0 OR o.idPatient = :idPatient)")
	Page<Patient> findByNameIdPatient(String name, int idPatient, Pageable pageable);
	
	@Query("SELECT p FROM Patient p JOIN p.nuser n WHERE n.idPerson = :idNuser")
	Page<Patient> findByNuserId(String idNuser, Pageable pageable);
	
	@Query("SELECT p FROM Patient p JOIN p.nuser n WHERE "
			+ "n.idPerson = :idNuser "
			+ "AND p.name LIKE %:name% "
			+ "AND (:time IS NULL OR (p.time >= :time))"
			+ "AND (:idPatient = 0 OR p.idPatient = :idPatient)")
	Page<Patient> findByIdPatientTimeNameNuserId(String idNuser, int idPatient, LocalDateTime time, String name, Pageable pageable);
	
	@Query("SELECT p FROM Patient p JOIN p.medicine m WHERE "
			+ "m.idMedicine = :idMedicine")
	List<Patient> findByIdMedicine(int idMedicine);
	
	@Query("SELECT p FROM Patient p JOIN p.doctor d WHERE "
			+ "d.idPerson LIKE %:idDoctor%")
	List<Patient> findByIdDoctor(String idDoctor);

}
	