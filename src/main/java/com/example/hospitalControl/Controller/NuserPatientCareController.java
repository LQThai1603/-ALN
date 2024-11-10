package com.example.hospitalControl.Controller;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.hospitalControl.Model.Account;
import com.example.hospitalControl.Model.CreatePatientDto;
import com.example.hospitalControl.Model.Doctor;
import com.example.hospitalControl.Model.Nuser;
import com.example.hospitalControl.Model.Patient;
import com.example.hospitalControl.Model.PatientInformationForDoctor;
import com.example.hospitalControl.Model.SearchDoctorDto;
import com.example.hospitalControl.Model.SearchNuserDto;
import com.example.hospitalControl.Model.SearchPatientDto;
import com.example.hospitalControl.Model.Sex;
import com.example.hospitalControl.Model.Specialized;
import com.example.hospitalControl.Security.AESUtils;
import com.example.hospitalControl.Service.AccountRepository;
import com.example.hospitalControl.Service.DoctorRepository;
import com.example.hospitalControl.Service.PatientRepository;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/nuser_patient_care")
public class NuserPatientCareController {
	
	private Account account = null;

	@Autowired
	AccountRepository accountRepo;
	
	@Autowired
	DoctorRepository doctorRepo;
	
	@Autowired
	PatientRepository patientRepo;
	
	@GetMapping("")
	public String showNuserPatientCarePage(@RequestParam("userName") String userName, @RequestParam("password") String password)
			throws Exception {
		account = accountRepo.findById(AESUtils.decrypt(userName)).orElse(null);
		System.out.println(account.getUserName());
		if (account == null) {
			return "redirect:/login";
		}
		return "/nuserPatientCare/nuserPatientCare";
	}

	@GetMapping("nuser_patient_care_home")
	public String showNuserPatientCarePage() {
		if (account == null) {
			return "redirect:/login";
		}
		System.out.println(account.getUserName());

		return "/nuserPatientCare/nuserPatientCare";
	}
	
	@GetMapping("create_patient")
	public String showcreatePatientPage(Model model) {
		if(account == null) {
			return "redirect:/login";
		}
		CreatePatientDto createPatientDto = new CreatePatientDto();
		model.addAttribute("createPatientDto", createPatientDto);
		
		return "/nuserPatientCare/createPatient";
	}
	
	@PostMapping("create_patient")
	public String createPatient(@Valid @ModelAttribute CreatePatientDto createPatientDto, BindingResult result, RedirectAttributes redirectAttributes) {
		if(account == null) {
			return "redirect:/login";
		}
		if(result.hasErrors()) {
			result.getFieldErrors().forEach(error -> {
				System.out.println("Field: " + error.getField()); // Tên trường bị lỗi
				System.out.println("Error: " + error.getDefaultMessage()); // Thông báo lỗi
			});
			
			return "/nuserPatientCare/createPatient";
		}
		
		if(doctorRepo.findById(createPatientDto.getIdDoctor()).orElse(null) == null) {
			redirectAttributes.addFlashAttribute("error", "Không có  bác sĩ với idDoctor vừa nhập");
	        return "redirect:/nuser_patient_care/create_patient";
		}
		
		Patient p = new Patient();
		p.setAddress(createPatientDto.getAddress());
		p.setAge(createPatientDto.getAge());
		p.setTime(LocalDateTime.now());
		p.setIdDoctor(createPatientDto.getIdDoctor());
		p.setSex(Sex.valueOf(createPatientDto.getSex()));
		p.setName(createPatientDto.getName());
		p.setPhoneNumber(createPatientDto.getPhoneNumber());
		p.setPaid(false);
		
		patientRepo.save(p);
		return "redirect:/nuser_patient_care/create_patient";
	}
	
	@GetMapping("/control_account_doctor")
	public String showControlAccountDoctorPage(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "3") int size, Model model) {

		if (account == null) {
			return "redirect:/login";
		}
		SearchDoctorDto searchDto = new SearchDoctorDto();

		Pageable pageable = PageRequest.of(page, size);
		Page<Doctor> doctors = doctorRepo.findByNotOnleaveToday("", "", null, "", LocalDate.now(), pageable);

		model.addAttribute("searchDto", searchDto);
		model.addAttribute("doctors", doctors.getContent());
		model.addAttribute("currentPage", doctors.getNumber());
		model.addAttribute("totalPages", doctors.getTotalPages());
		model.addAttribute("totalItems", doctors.getTotalElements());
		return "/nuserPatientCare/controlAccountDoctor";
	}

	@GetMapping("search_account_doctor")
	public String showSearchAccountDoctorPage(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "3") int size, Model model, @ModelAttribute SearchDoctorDto searchDto,
			RedirectAttributes redirectAttributes) {
		if (account == null) {
			return "redirect:/login";
		}

		searchDto.setIdPerson(searchDto.getIdPerson().trim());
		searchDto.setName(searchDto.getName().trim());
		searchDto.setPhoneNumber(searchDto.getPhoneNumber().trim());

		redirectAttributes.addAttribute("idperson", searchDto.getIdPerson());
		redirectAttributes.addAttribute("name", searchDto.getName());
		redirectAttributes.addAttribute("phonenumber", searchDto.getPhoneNumber());
		redirectAttributes.addAttribute("specialized", searchDto.getSpecialized());
		return "redirect:/nuser_patient_care/search_account_doctor_continue";
	}

	@GetMapping("search_account_doctor_continue")
	public String showSearchAccountDoctorContinuePage(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "3") int size, @RequestParam String idperson, @RequestParam String name,
			@RequestParam String phonenumber, @RequestParam String specialized, RedirectAttributes redirectAttributes,
			Model model) {
		if (account == null) {
			return "redirect:/login";
		}
		Pageable pageable = PageRequest.of(page, size);
		Page<Doctor> doctors = null;

		if (idperson.equals("") && name.equals("") && phonenumber.equals("") && specialized.equals("")) {
			doctors = doctorRepo.findByNotOnleaveToday("", "", null, "", LocalDate.now(), pageable);

		} else {
			Specialized specializedValue;
			try {
				specializedValue = Specialized.valueOf(specialized);
			} catch (IllegalArgumentException e) {
				// Xử lý trường hợp không có giá trị trong enum
				specializedValue = null; // hoặc giá trị mặc định khác
			}
			doctors = doctorRepo.findByNotOnleaveToday(idperson, name, specializedValue, phonenumber, LocalDate.now(), pageable);
		}
		model.addAttribute("searchDto", new SearchDoctorDto());
		model.addAttribute("doctors", doctors.getContent());
		model.addAttribute("currentPage", doctors.getNumber());
		model.addAttribute("totalPages", doctors.getTotalPages());
		model.addAttribute("totalItems", doctors.getTotalElements());
		model.addAttribute("idperson", idperson);
		model.addAttribute("name", name);
		model.addAttribute("phonenumber", phonenumber);
		model.addAttribute("specialized", specialized);
		return "/nuserPatientCare/searchDoctor";
	}
	
	
	@GetMapping("logout")
	public String logout() {
		account = null;
		System.out.println("Đã đăng xuất");
		return "redirect:/login";
	}
}
