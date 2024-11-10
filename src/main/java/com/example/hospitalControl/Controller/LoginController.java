package com.example.hospitalControl.Controller;

import java.util.Optional;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.example.hospitalControl.Model.AccountDto;
import com.example.hospitalControl.Model.Medicine;
import com.example.hospitalControl.Model.Nuser;
import com.example.hospitalControl.Model.Patient;
import com.example.hospitalControl.Model.PatientInformationForDoctor;
import com.example.hospitalControl.Model.Role;
import com.example.hospitalControl.Model.Room;
import com.example.hospitalControl.Model.SearchForPatientDto;
import com.example.hospitalControl.Model.SearchMedicineDto;
import com.example.hospitalControl.Model.SearchPatientDto;
import com.example.hospitalControl.Security.AESUtils;
import com.example.hospitalControl.Service.AccountRepository;
import com.example.hospitalControl.Service.NuserRepository;
import com.example.hospitalControl.Service.PatientRepository;

import jakarta.validation.Valid;

@Controller
@RequestMapping("")
public class LoginController {
	@Autowired
	AccountRepository acRepo;
	@Autowired
	NuserRepository nuserRepo;
	@Autowired
	PatientRepository patientRepo;

	@GetMapping({ "" })
	public String showLoginPage() {
		return "redirect:/login";
	}

	@GetMapping("/login")
	public String login(Model model) {
		AccountDto accountDto = new AccountDto();
		model.addAttribute("accountDto", accountDto);
		return "/login/login";
	}

	@PostMapping("/login")
	public String access(@Valid @ModelAttribute AccountDto accountDto, BindingResult result,
			RedirectAttributes redirectAttributes) throws Exception {
		if (result.hasErrors()) {
			return "login/login";
		}

		Account ac = acRepo.findById(accountDto.getUserName()).orElse(null);
		System.out.println(ac == null);

		if (ac == null || !ac.getPassword().equals(accountDto.getPassword())) {
			redirectAttributes.addFlashAttribute("error", "Tài khoản hoặc mật khẩu không đúng");
			return "redirect:/login";
		}

		if (ac.isQuit()) {
			redirectAttributes.addFlashAttribute("error", "tài khoản đã bị vô hiệu hóa");
			return "redirect:/login";
		}

		redirectAttributes.addAttribute("userName", AESUtils.encrypt(ac.getUserName()));
		redirectAttributes.addAttribute("password", AESUtils.encrypt(ac.getPassword()));
		if (ac.getRole() == Role.ADMIN) {
			return "redirect:/admin";
		}
		if (ac.getRole() == Role.DOCTOR) {
			return "redirect:/doctor";
		}
		if (ac.getRole() == Role.NURSE) {
			Nuser n = nuserRepo.findById(ac.getUserName()).orElse(null);
			if (n == null) {
				System.out.println("khong co y ta nay");
				return "redirect:/login";
			}
			Room room = n.getRoom();
			if (room == Room.MEDICATION_MANAGEMENT) {
				return "redirect:/nuser_medicine_management";
			}
			if (room == Room.PATIENT_CARE) {
				return "redirect:/nuser_patient_care";
			}
			return "redirect:/nuser";
		}
		return "redirect:/reception";
	}

	@GetMapping("/patient")
	public String showPatientInformationPage(Model model) {
		model.addAttribute("searchDto", new SearchForPatientDto());
		return "/login/searchPatient";
	}

	@PostMapping("/patient")
	public String showPatientInformation(@Valid @ModelAttribute SearchForPatientDto searchDto, BindingResult result,
			Model model, RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			model.addAttribute("searchDto", searchDto);
			result.getFieldErrors().forEach(error -> {
				System.out.println("Field: " + error.getField()); // Tên trường bị lỗi
				System.out.println("Error: " + error.getDefaultMessage()); // Thông báo lỗi
			});
			return "login/searchPatient";
		}

		Patient o = patientRepo.findById(searchDto.getIdPatient()).orElse(null);
		if (o == null) {
			redirectAttributes.addFlashAttribute("error", "Thông tin điền vào không đúng");
			System.out.println("Thông tin điền vào không đúng");
			return "redirect:/patient";
		}

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
		for (Nuser i : o.getNuser()) {
			o.setPrice(o.getPrice() + i.getPrice());
		}

		for (Medicine i : o.getMedicine()) {
			o.setPrice(o.getPrice() + i.getPrice());
		}
		patientDto.setPrice(o.getPrice());

		model.addAttribute("doctor", o.getDoctor());
		model.addAttribute("patientDto", patientDto);
		model.addAttribute("nuserPatient", o.getNuser());
		model.addAttribute("medicinesPatient", o.getMedicine());
		model.addAttribute("searchDto", new SearchMedicineDto());
		model.addAttribute("idpatient", searchDto.getIdPatient());

		return "/login/showPatientInformation";
	}

}
