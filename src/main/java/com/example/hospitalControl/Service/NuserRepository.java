package com.example.hospitalControl.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.hospitalControl.Model.Nuser;
import com.example.hospitalControl.Model.Room;

public interface NuserRepository extends JpaRepository<Nuser, String>{
	@Query("SELECT n FROM Nuser n " +
		       "WHERE n.idPerson LIKE %:idPerson% " +
		       "AND n.name LIKE %:name% " +
		       "AND n.phoneNumber LIKE %:phoneNumber% " +
		       "AND (:room IS NULL OR n.room = :room)")
	Page<Nuser> findByIpPersonUserNameSpecializedPhoneNumber(String idPerson, String name, Room room,
			String phoneNumber, Pageable pageable);
}
