package com.example.hospitalControl.Service;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.hospitalControl.Model.Medicine;

public interface MedicineRepository extends JpaRepository<Medicine, Integer> {
	@Query("SELECT m FROM Medicine m "
			+ "WHERE "
			+ "m.name LIKE %:name% "
			+ "AND m.expirationDate < :expirationDate "
			+ "AND m.quantity BETWEEN :quantity - 50 AND :quantity + 50 "
			+ "AND m.price BETWEEN :price - 50000 AND :price + 50000")
	Page<Medicine> findByNameExpirationDateQuantityPrice(String name, LocalDate expirationDate, int quantity, int price,
			Pageable pageable);
}
