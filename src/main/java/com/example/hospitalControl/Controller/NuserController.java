package com.example.hospitalControl.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.hospitalControl.Model.Account;
import com.example.hospitalControl.Model.CreateNuserDto;
import com.example.hospitalControl.Model.Nuser;
import com.example.hospitalControl.Security.AESUtils;
import com.example.hospitalControl.Service.AccountRepository;
import com.example.hospitalControl.Service.NuserRepository;

@Controller
@RequestMapping("/nuser")
public class NuserController {
	private Account account = null;
	@Autowired
	AccountRepository accountRepo;
	
	@Autowired
	NuserRepository nuserRepo;
	
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
	
	@GetMapping("logout")
	public String logout() {
		account = null;
		System.out.println("Đã đăng xuất");
		return "redirect:/login";
	}
}
