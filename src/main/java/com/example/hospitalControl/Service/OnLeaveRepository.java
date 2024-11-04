package com.example.hospitalControl.Service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.hospitalControl.Model.OnLeave;

public interface OnLeaveRepository extends JpaRepository<OnLeave, Integer>{
	@Query("SELECT o FROM OnLeave o " +
		       "WHERE o.idPerson LIKE %:idPerson% " +
		       "AND (:createDate IS NULL OR o.createDate = :createDate)" + 
		       "AND (:startDate IS NULL OR o.startDate = :startDate)" +
		       "AND (:endDate IS NULL OR o.endDate = :endDate)")
	Page<OnLeave> findByIdPersonCreateDateStartDateEndDate(String idPerson, LocalDate createDate, LocalDate startDate, LocalDate endDate, Pageable pageable);
}
