package com.example.hospitalControl.Service;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.hospitalControl.Model.Account;

public interface AccountRepository extends JpaRepository<Account, String> {

}
