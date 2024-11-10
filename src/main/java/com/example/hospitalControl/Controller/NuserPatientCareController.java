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
import com.example.hospitalControl.Model.Medicine;
import com.example.hospitalControl.Model.Nuser;
import com.example.hospitalControl.Model.Patient;
import com.example.hospitalControl.Model.PatientInformationForDoctor;
import com.example.hospitalControl.Model.SearchDoctorDto;
import com.example.hospitalControl.Model.SearchMedicineDto;
import com.example.hospitalControl.Model.SearchNuserDto;
import com.example.hospitalControl.Model.SearchPatientDto;
import com.example.hospitalControl.Model.Sex;
import com.example.hospitalControl.Model.Specialized;
import com.example.hospitalControl.Security.AESUtils;
import com.example.hospitalControl.Service.AccountRepository;
import com.example.hospitalControl.Service.DoctorRepository;
import com.example.hospitalControl.Service.MedicineRepository;
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
	
	@Autowired
	MedicineRepository medicineRepo;
	
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
	
	@GetMapping("control_patient")
	public String showControlPatientPage(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "3") int size, Model model) {
		if (account == null) {
			return "redirect:/login";
		}
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "time"));
		Page<Patient> patients = patientRepo.findAll(pageable);
		
		model.addAttribute("patients", patients);
		model.addAttribute("searchDto", new SearchPatientDto());
		model.addAttribute("currentPage", patients.getNumber());
		model.addAttribute("totalPages", patients.getTotalPages());
		model.addAttribute("totalItems", patients.getTotalElements());
		return "/nuserPatientCare/controlPatient";
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
		return "redirect:/nuser_patient_care/search_patient_continue";
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
			patients = patientRepo.findByIdPatientTimeName(idpatient, null, name, pageable);
			System.out.println("--=-=-=" + patients.getTotalElements());
		} else {
			LocalDateTime t = null;
			if (time != null) {
				t = time.atStartOfDay();
			}
			patients = patientRepo.findByIdPatientTimeName(idpatient, t, name, pageable);
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
		return "/nuserPatientCare/searchPatient";
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
		patientDto.setPaid(o.isPaid());
		
		o.setPrice(400000);
		for(Nuser i : o.getNuser()) {
			o.setPrice(o.getPrice() + i.getPrice());
		}
		
		for(Medicine i : o.getMedicine()) {
			o.setPrice(o.getPrice() + i.getPrice());
		}
		patientDto.setPrice(o.getPrice());
		
		
		model.addAttribute("doctor", o.getDoctor());
		model.addAttribute("patientDto", patientDto);
		model.addAttribute("nuserPatient", o.getNuser());
		model.addAttribute("medicinesPatient", o.getMedicine());
		model.addAttribute("searchDto", new SearchMedicineDto());
		model.addAttribute("idpatient", idpatient);

		return "/nuserPatientCare/showPatientInformation";
	}
	
	@GetMapping("logout")
	public String logout() {
		account = null;
		System.out.println("Đã đăng xuất");
		return "redirect:/login";
	}
}
