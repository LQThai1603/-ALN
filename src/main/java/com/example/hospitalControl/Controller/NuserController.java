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
import com.example.hospitalControl.Model.CreateNuserDto;
import com.example.hospitalControl.Model.Nuser;
import com.example.hospitalControl.Model.OnLeave;
import com.example.hospitalControl.Model.OnLeaveConfirm;
import com.example.hospitalControl.Model.OnLeaveDto;
import com.example.hospitalControl.Model.Patient;
import com.example.hospitalControl.Model.PatientInformationForDoctor;
import com.example.hospitalControl.Model.SearchMedicineDto;
import com.example.hospitalControl.Model.SearchPatientDto;
import com.example.hospitalControl.Security.AESUtils;
import com.example.hospitalControl.Service.AccountRepository;
import com.example.hospitalControl.Service.NuserRepository;
import com.example.hospitalControl.Service.OnLeaveRepository;
import com.example.hospitalControl.Service.PatientRepository;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/nuser")
public class NuserController {
	private Account account = null;
	@Autowired
	AccountRepository accountRepo;
	
	@Autowired
	NuserRepository nuserRepo;
	
	@Autowired
	OnLeaveRepository onLeaveRepo;
	
	@Autowired
	PatientRepository patientRepo;
	
	@GetMapping("")
	public String showNuserPage(@RequestParam("userName") String userName, @RequestParam("password") String password)
			throws Exception {
		account = accountRepo.findById(AESUtils.decrypt(userName)).orElse(null);
		System.out.println(account.getUserName());
		if (account == null) {
			return "redirect:/login";
		}
		System.out.println(account.getUserName());
		System.out.println(account.getPassword());
		return "/nuser/nuser";
	}
	
	@GetMapping("nuser_home")
	public String showNuserPage() {
		if (account == null) {
			return "redirect:/login";
		}
		System.out.println(account.getUserName());

		return "/nuser/nuser";
	}
	
	@GetMapping("profile")
	public String showNuserProfilePage(Model model) {
		Nuser n = nuserRepo.findById(account.getUserName()).orElse(null);

		CreateNuserDto createNuserDto = new CreateNuserDto();
		createNuserDto.setAge(n.getAge());
		createNuserDto.setDegree(n.getDegree());
		createNuserDto.setEmail(n.getEmail());
		createNuserDto.setName(n.getName());
		createNuserDto.setPhoneNumber(n.getPhoneNumber());
		createNuserDto.setSex(n.getSex().toString());
		createNuserDto.setRoom(n.getRoom().toString());
		createNuserDto.setUserName(n.getIdPerson());
		createNuserDto.setYearsExperience(n.getYearsExperience());
		createNuserDto.setPrice(n.getPrice());

		model.addAttribute("createNuserDto", createNuserDto);
		model.addAttribute("avatar", n.getAvatar());
		
		return "/nuser/nuserprofile";
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
		return "/nuser/doctorOnLeave";
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
			return "/nuser/doctorOnLeave";
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
		
		return "redirect:/nuser/on_leave";
	}
	
	@GetMapping("control_onleave")
	public String showControlOnLeavePage(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "3") int size, Model model) {
		if (account == null) {
			return "redirect:/login";
		}
		OnLeaveConfirm onLeaveConfirm = new OnLeaveConfirm();

		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createDate"));
		Page<OnLeave> onLeaves = onLeaveRepo.findByIdPersonCreateDateStartDateEndDate(account.getUserName(), null, null, null, pageable);
		

		model.addAttribute("onLeaveConfirm", onLeaveConfirm);
		model.addAttribute("onLeaves", onLeaves);
		model.addAttribute("searchDto", new SearchMedicineDto());
		model.addAttribute("currentPage", onLeaves.getNumber());
		model.addAttribute("totalPages", onLeaves.getTotalPages());
		model.addAttribute("totalItems", onLeaves.getTotalElements());
		return "/nuser/controlOnLeave";
	}
	
	@GetMapping("/onleave_information")
	public String showOnLeavetInformation(@RequestParam int idonleave, Model model) {
		if (account == null) {
			return "redirect:/login";
		}

		OnLeave o = onLeaveRepo.findById(idonleave).orElse(null);
		
		OnLeaveConfirm onLeaveConfirm = new OnLeaveConfirm();
		onLeaveConfirm.setConfirm(o.isConfirm());
		onLeaveConfirm.setIdPerson(o.getIdPerson());
		onLeaveConfirm.setId(o.getId());
		onLeaveConfirm.setEndDate(o.getEndDate());
		onLeaveConfirm.setStartDate(o.getStartDate());
		onLeaveConfirm.setReason(o.getReason());
		onLeaveConfirm.setDate(o.getCreateDate());
		model.addAttribute("onLeaveDto", onLeaveConfirm);

		return "nuser/showOnLeaveInformation";
	}
	
	@PostMapping("/onleave_confirm")
	public String confirmOnLeave(@Valid @ModelAttribute OnLeaveConfirm onLeaveConfirm,
			BindingResult result, RedirectAttributes redirectAttributes) {
		if (account == null) {
			return "redirect:/login";
		}
		if (result.hasErrors()) {
			result.getFieldErrors().forEach(error -> {
				System.out.println("Field: " + error.getField()); // Tên trường bị lỗi
				System.out.println("Error: " + error.getDefaultMessage()); // Thông báo lỗi
			});
			return "/nuser/showOnLeaveInformation";
		}

		OnLeave o = new OnLeave();
		o.setConfirm(onLeaveConfirm.isConfirm());
		o.setEndDate(onLeaveConfirm.getEndDate());
		o.setId(onLeaveConfirm.getId());
		o.setIdPerson(onLeaveConfirm.getIdPerson());
		o.setReason(onLeaveConfirm.getReason());
		o.setStartDate(onLeaveConfirm.getStartDate());
		onLeaveRepo.save(o);
		redirectAttributes.addAttribute("idonleave", o.getId());
		return "redirect:/nuser/onleave_information";
	}
	
	@GetMapping("search_onleave")
	public String showSearchOPage(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "3") int size, Model model, @ModelAttribute OnLeaveConfirm onLeaveConfirm,
			RedirectAttributes redirectAttributes) {
		if (account == null) {
			return "redirect:/login";
		}
		if (onLeaveConfirm.getDate() == null) {
			onLeaveConfirm.setDate(LocalDate.of(2000, 01, 01));
		}
		if (onLeaveConfirm.getStartDate() == null) {
			onLeaveConfirm.setStartDate(LocalDate.of(2000, 01, 01));
		}
		if (onLeaveConfirm.getEndDate() == null) {
			onLeaveConfirm.setEndDate(LocalDate.of(2000, 01, 01));
		}
		System.out.println(LocalDate.of(1999, 01, 01));
		System.out.println(onLeaveConfirm.getDate());
		System.out.println(onLeaveConfirm.getStartDate());
		System.out.println(onLeaveConfirm.getEndDate());
		onLeaveConfirm.setIdPerson(account.getUserName().trim());

		redirectAttributes.addAttribute("idperson", onLeaveConfirm.getIdPerson());
		redirectAttributes.addAttribute("createdate", onLeaveConfirm.getDate());
		redirectAttributes.addAttribute("startdate", onLeaveConfirm.getStartDate());
		redirectAttributes.addAttribute("enddate", onLeaveConfirm.getEndDate());
		return "redirect:/nuser/search_onleave_continue";
	}

	@GetMapping("search_onleave_continue")
	public String showSearchOnLeaveContinuePage(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "3") int size, @RequestParam String idperson,
			@RequestParam LocalDate createdate, @RequestParam LocalDate startdate, @RequestParam LocalDate enddate,
			RedirectAttributes redirectAttributes, Model model) {
		if (account == null) {
			return "redirect:/login";
		}
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createDate"));
		Page<OnLeave> onleaves = null;

		if (createdate.isEqual(LocalDate.of(2000, 01, 01))) {
			createdate = null;
		}
		if (startdate.isEqual(LocalDate.of(2000, 01, 01))) {
			startdate = null;
		}
		if (enddate.isEqual(LocalDate.of(2000, 01, 01))) {
			enddate = null;
		}

		if (idperson.equals("") && createdate == null && startdate == null && enddate == null) {
			onleaves = onLeaveRepo.findAll(pageable);
			System.out.println(onleaves.getTotalElements());
		} else {
			onleaves = onLeaveRepo.findByIdPersonCreateDateStartDateEndDate(idperson, createdate, startdate, enddate,
					pageable);
		}

		if (createdate == null) {
			createdate = LocalDate.of(2000, 01, 01);
		}
		if (startdate == null) {
			startdate = LocalDate.of(2000, 01, 01);
		}
		if (enddate == null) {
			enddate = LocalDate.of(2000, 01, 01);
		}

		model.addAttribute("onLeaveConfirm", new OnLeaveConfirm());
		model.addAttribute("onLeaves", onleaves.getContent());
		model.addAttribute("currentPage", onleaves.getNumber());
		model.addAttribute("totalPages", onleaves.getTotalPages());
		model.addAttribute("totalItems", onleaves.getTotalElements());
		model.addAttribute("idperson", idperson);
		model.addAttribute("createdate", createdate);
		model.addAttribute("startdate", startdate);
		model.addAttribute("enddate", enddate);
		return "/nuser/searchOnLeave";
	}
	
	@GetMapping("control_patient")
	public String showControlPatientPage(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "3") int size, Model model) {
		if (account == null) {
			return "redirect:/login";
		}
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "time"));
		Page<Patient> patients = patientRepo.findByNuserId(account.getUserName(), pageable);

		model.addAttribute("patients", patients);
		model.addAttribute("searchDto", new SearchPatientDto());
		model.addAttribute("currentPage", patients.getNumber());
		model.addAttribute("totalPages", patients.getTotalPages());
		model.addAttribute("totalItems", patients.getTotalElements());
		return "/nuser/controlPatient";
	}
	
	@GetMapping("search_patient")
	public String showSearchPatient(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "3") int size, Model model, @ModelAttribute SearchPatientDto searchPatientDto,
			RedirectAttributes redirectAttributes) {
		if (account == null) {
			return "redirect:/login";
		}
		if (searchPatientDto.getTime() == null) {
			searchPatientDto.setTime(LocalDate.of(2000, 01, 01));
		}
		System.out.println(searchPatientDto.getTime());
		System.out.println(searchPatientDto.getName());
		System.out.println(searchPatientDto.getIdPatient());
		searchPatientDto.setName(searchPatientDto.getName().trim());
		searchPatientDto.setIdPatient(searchPatientDto.getIdPatient());

		redirectAttributes.addAttribute("idpatient", searchPatientDto.getIdPatient());
		redirectAttributes.addAttribute("time", searchPatientDto.getTime());
		redirectAttributes.addAttribute("name", searchPatientDto.getName());
		return "redirect:/nuser/search_patient_continue";
	}

	@GetMapping("search_patient_continue")
	public String showSearchPatientContinuePage(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "3") int size, @RequestParam int idpatient, @RequestParam LocalDate time,
			@RequestParam String name, RedirectAttributes redirectAttributes, Model model) {
		if (account == null) {
			return "redirect:/login";
		}
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "time"));
		Page<Patient> patients = null;

		if (time.isEqual(LocalDate.of(2000, 01, 01))) {
			time = null;
		}
		System.out.println(name + "-" + time + "-" + idpatient);

		if (name.equals("") && time == null && idpatient == 0) {
			patients = patientRepo.findByNuserId(account.getUserName(), pageable);
			System.out.println("--=-=-=" + patients.getTotalElements());
		} else {
			LocalDateTime t = null;
			if (time != null) {
				t = time.atStartOfDay();
			}
			patients = patientRepo.findByIdPatientTimeNameNuserId(account.getUserName(), idpatient, t, name, pageable);
//			patients = patientRepo.findByNameIdPatient(name,idpatient, pageable);
			System.out.println("idpatient " + idpatient);
			System.out.println("time " + time);
			System.out.println("name " + name);
		}

		if (time == null) {
			time = LocalDate.of(2000, 01, 01);
		}
		model.addAttribute("searchDto", new SearchPatientDto());
		model.addAttribute("patients", patients.getContent());
		model.addAttribute("currentPage", patients.getNumber());
		model.addAttribute("totalPages", patients.getTotalPages());
		model.addAttribute("totalItems", patients.getTotalElements());
		model.addAttribute("idpatient", idpatient);
		model.addAttribute("time", time);
		model.addAttribute("name", name);
		return "/nuser/searchPatient";
	}
	
	@GetMapping("/patient_information")
	public String showPatientInformation(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "3") int size, @RequestParam int idpatient, Model model,
			RedirectAttributes redirectAttributes) {
		if (account == null) {
			return "redirect:/login";
		}

		Patient o = patientRepo.findById(idpatient).orElse(null);
		PatientInformationForDoctor patientDto = new PatientInformationForDoctor();
		patientDto.setAddress(o.getAddress());
		patientDto.setAge(o.getAge());
		patientDto.setConclusion(o.getConclusion());
		patientDto.setConjecture(o.getConjecture());
		patientDto.setExamined(o.isExamined());
		patientDto.setIdDoctor(o.getIdDoctor());
		patientDto.setIdPatient(o.getIdPatient());
		patientDto.setName(o.getName());
		patientDto.setPhoneNumber(o.getPhoneNumber());
		patientDto.setSex(o.getSex());
		patientDto.setTime(o.getTime());

		model.addAttribute("patientDto", patientDto);
		model.addAttribute("nuserPatient", o.getNuser());
		model.addAttribute("idpatient", idpatient);

		return "/nuser/showPatientInformation";
	}
	
	@GetMapping("logout")
	public String logout() {
		account = null;
		System.out.println("Đã đăng xuất");
		return "redirect:/login";
	}
}
