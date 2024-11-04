package com.example.hospitalControl.Model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "account")
public class Account {
	@Id
	@Column(name = "username")
	private String userName;

	@Column(name = "password")
	private String password;

	@Enumerated(value = EnumType.STRING)
	private Role role;

	@Column(name = "quit")
	private boolean quit;

	@OneToMany(mappedBy = "account")
	@JsonManagedReference
	private List<OnLeave> onLeave;
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public boolean isQuit() {
		return quit;
	}

	public void setQuit(boolean quit) {
		this.quit = quit;
	}

	public List<OnLeave> getOnLeave() {
		return onLeave;
	}

	public void setOnLeave(List<OnLeave> onLeave) {
		this.onLeave = onLeave;
	}
	
}
