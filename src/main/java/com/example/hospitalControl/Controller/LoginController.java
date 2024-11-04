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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.hospitalControl.Model.Account;
import com.example.hospitalControl.Model.AccountDto;
import com.example.hospitalControl.Model.Nuser;
import com.example.hospitalControl.Model.Role;
import com.example.hospitalControl.Model.Room;
import com.example.hospitalControl.Security.AESUtils;
import com.example.hospitalControl.Service.AccountRepository;
import com.example.hospitalControl.Service.NuserRepository;

import jakarta.validation.Valid;

@Controller
@RequestMapping("")
public class LoginController {
	@Autowired
	AccountRepository acRepo;
	@Autowired
	NuserRepository nuserRepo;
	@GetMapping({""})
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
	public String access(@Valid @ModelAttribute AccountDto accountDto, 
			BindingResult result,
			RedirectAttributes redirectAttributes) throws Exception {
		if(result.hasErrors()) {
			return "login/login";
		}
		
		Account ac = acRepo.findById(accountDto.getUserName()).orElse(null);
		System.out.println(ac == null);
		
		if(ac == null || !ac.getPassword().equals(accountDto.getPassword())) {
			redirectAttributes.addFlashAttribute("error", "Tài khoản hoặc mật khẩu không đúng");
	        return "redirect:/login";
		}
		
		if(ac.isQuit()) {
			redirectAttributes.addFlashAttribute("error", "tài khoản đã bị vô hiệu hóa");
	        return "redirect:/login";
		}
		
		redirectAttributes.addAttribute("userName", AESUtils.encrypt(ac.getUserName()));
		redirectAttributes.addAttribute("password", AESUtils.encrypt(ac.getPassword()));
		if(ac.getRole() == Role.ADMIN) {
			return "redirect:/admin";
		}
		if(ac.getRole() == Role.DOCTOR) {
			return "redirect:/doctor";
		}
		if(ac.getRole() == Role.NURSE) {
			Nuser n = nuserRepo.findById(ac.getUserName()).orElse(null);
			if(n == null) {
				System.out.println("khong co y ta nay");
				return "redirect:/login";
			}
			Room room = n.getRoom();
			if(room == Room.MEDICATION_MANAGEMENT) {
				return "redirect:/nuser_medicine_management";
			}
			if(room == Room.PATIENT_CARE) {
				return "redirect:/nuser_patient_care";
			}
			return "redirect:/nuser";
		}
		return "redirect:/reception";
	}
}
