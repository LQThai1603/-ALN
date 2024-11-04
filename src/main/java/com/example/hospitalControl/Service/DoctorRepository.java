package com.example.hospitalControl.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.hospitalControl.Model.Doctor;
import com.example.hospitalControl.Model.Specialized;

public interface DoctorRepository extends JpaRepository<Doctor, String>{
	@Query("SELECT d FROM Doctor d " +
		       "WHERE d.idPerson LIKE %:idPerson% " +
		       "AND d.name LIKE %:name% " +
		       "AND d.phoneNumber LIKE %:phoneNumber% " +
		       "AND (:specialized IS NULL OR d.specialized = :specialized)")
	Page<Doctor> findByIpPersonUserNameSpecializedPhoneNumber(String idPerson, String name, Specialized specialized,
			String phoneNumber, Pageable pageable);
}
