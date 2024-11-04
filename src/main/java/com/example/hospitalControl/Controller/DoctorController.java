package com.example.hospitalControl.Controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.hospitalControl.Model.Account;
import com.example.hospitalControl.Model.CreateDoctorDto;
import com.example.hospitalControl.Model.Doctor;
import com.example.hospitalControl.Model.OnLeave;
import com.example.hospitalControl.Model.OnLeaveDto;
import com.example.hospitalControl.Security.AESUtils;
import com.example.hospitalControl.Service.AccountRepository;
import com.example.hospitalControl.Service.DoctorRepository;
import com.example.hospitalControl.Service.OnLeaveRepository;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/doctor")
public class DoctorController {
	private Account account = null;
	@Autowired
	AccountRepository accountRepo;
	
	@Autowired
	DoctorRepository doctorRepo;
	
	@Autowired
	OnLeaveRepository onLeaveRepo;
	
	@GetMapping("")
	public String showDoctorPage(@RequestParam("userName") String userName, @RequestParam("password") String password)
			throws Exception {
		account = accountRepo.findById(AESUtils.decrypt(userName)).orElse(null);
		System.out.println(account.getUserName());
		if (account == null) {
			return "redirect:/login";
		}
		System.out.println(account.getUserName());
		System.out.println(account.getPassword());
		return "/doctor/doctor";
	}
	
	@GetMapping("doctor_home")
	public String showDoctorPage() {
		if (account == null) {
			return "redirect:/login";
		}
		System.out.println(account.getUserName());

		return "/doctor/doctor";
	}
	
	@GetMapping("profile")
	public String showDoctorProfilePage(Model model) {
		if (account == null) {
			return "redirect:/login";
		}
		Doctor d = doctorRepo.findById(account.getUserName()).orElse(null);

		CreateDoctorDto createDoctorDto = new CreateDoctorDto();
		createDoctorDto.setAge(d.getAge());
		createDoctorDto.setDegree(d.getDegree());
		createDoctorDto.setEmail(d.getEmail());
		createDoctorDto.setName(d.getName());
		createDoctorDto.setPhoneNumber(d.getPhoneNumber());
		createDoctorDto.setSex(d.getSex().toString());
		createDoctorDto.setSpecialized(d.getSpecialized().toString());
		createDoctorDto.setUserName(d.getIdPerson());
		createDoctorDto.setYearsExperience(d.getYearsExperience());
		
		model.addAttribute("createDoctorDto", createDoctorDto);
		model.addAttribute("avatar", d.getAvatar());
		return "/doctor/doctorprofile";
	}
	
	@GetMapping("on_leave")
	public String showOnLeavePage(Model model) {
		if(account == null) {
			return "redirect:/login";
		}
		OnLeaveDto onLeaveDto = new OnLeaveDto();
		onLeaveDto.setConfirm(false);
		onLeaveDto.setIdPerson(account.getUserName());
		
		model.addAttribute("onLeaveDto", onLeaveDto);
		return "/doctor/doctorOnLeave";
	}
	
	@PostMapping("on_leave")
	public String createOnLeaveForm(@Valid @ModelAttribute OnLeaveDto onLeaveDto, BindingResult result) {
		if(account == null) {
			return "redirect:/login";
		}
		if(result.hasErrors()) {
			result.getFieldErrors().forEach(error -> {
				System.out.println("Field: " + error.getField()); // Tên trường bị lỗi
				System.out.println("Error: " + error.getDefaultMessage()); // Thông báo lỗi
			});
			return "/doctor/doctorOnLeave";
		}
		
		OnLeave onLeave = new OnLeave();
		onLeave.setConfirm(false);
		onLeave.setEndDate(onLeaveDto.getEndDate());
		onLeave.setStartDate(onLeaveDto.getStartDate());
		onLeave.setIdPerson(onLeaveDto.getIdPerson());
		onLeave.setReason(onLeaveDto.getReason());
		onLeave.setCreateDate(LocalDate.now());
//		System.out.println("idperson: " + onLeave.getIdPerson());
//		System.out.println(onLeave.getStartDate());
//		System.out.println(onLeave.getEndDate());
//		System.out.println(onLeave.getReason());
//		System.out.println(onLeave.isConfirm());
		onLeaveRepo.save(onLeave);
		
		return "/doctor/doctorOnLeave";
	}
	
	@GetMapping("logout")
	public String logout() {
		account = null;
		System.out.println("Đã đăng xuất");
		return "redirect:/login";
	}
}
