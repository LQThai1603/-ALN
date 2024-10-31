package com.example.hospitalControl.Controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.hospitalControl.Model.Account;
import com.example.hospitalControl.Model.AccountDto;
import com.example.hospitalControl.Model.CreateDoctorDto;
import com.example.hospitalControl.Model.CreateNuserDto;
import com.example.hospitalControl.Model.Doctor;
import com.example.hospitalControl.Model.Nuser;
import com.example.hospitalControl.Model.Role;
import com.example.hospitalControl.Model.Room;
import com.example.hospitalControl.Model.SearchDoctorDto;
import com.example.hospitalControl.Model.SearchNuserDto;
import com.example.hospitalControl.Model.Sex;
import com.example.hospitalControl.Model.Specialized;
import com.example.hospitalControl.Security.AESUtils;
import com.example.hospitalControl.Service.AccountRepository;
import com.example.hospitalControl.Service.DoctorRepository;
import com.example.hospitalControl.Service.NuserRepository;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin")
public class AdminController {
	private Account account = null;

	@Autowired
	AccountRepository accountRepo;

	@Autowired
	DoctorRepository doctorRepo;

	@Autowired
	NuserRepository nuserRepo;

	@GetMapping("")
	public String showAdminPage(@RequestParam("userName") String userName, @RequestParam("password") String password)
			throws Exception {
		account = accountRepo.findById(AESUtils.decrypt(userName)).orElse(null);
		System.out.println(account.getUserName());
		if (account == null) {
			return "redirect:/login";
		}
		return "/admin/admin";
	}

	@GetMapping("admin_home")
	public String showAdminPage() {
		if (account == null) {
			return "redirect:/login";
		}
		System.out.println(account.getUserName());

		return "/admin/admin";
	}

	@GetMapping("/create_account")
	public String showCreateAccountPage(Model model) {
		if (account == null) {
			return "redirect:/login";
		}
		CreateDoctorDto createDoctorDto = new CreateDoctorDto();
		CreateNuserDto createNuserDto = new CreateNuserDto();

		model.addAttribute("createDoctorDto", createDoctorDto);
		model.addAttribute("createNuserDto", createNuserDto);
		return "/admin/createAccount";
	}

	@PostMapping("create_account_doctor")
	public String createAccount(@Valid @ModelAttribute CreateDoctorDto createDoctorDto, BindingResult result,
			@ModelAttribute CreateNuserDto createNuserDto,

			Model model, RedirectAttributes redirectAttributes) {
		if (account == null) {
			return "redirect:/login";
		}
		if (result.hasErrors()) {
			System.out.println("Có lỗi trong form tạo tài khoản");
			return "/admin/createAccount"; // Điều hướng lại trang tạo tài khoản
		}
		if (accountRepo.findById(createDoctorDto.getUserName()).orElse(null) != null) {
			redirectAttributes.addFlashAttribute("error", "tài khoản đã tồn tại");
			return "redirect:/admin/create_account";
		}
		Account a = new Account();
		a.setUserName(createDoctorDto.getUserName());
		a.setPassword(createDoctorDto.getPassword());
		a.setQuit(false);
		a.setRole(Role.DOCTOR);
		accountRepo.save(a);

		Doctor doctor = new Doctor();

		doctor.setIdPerson(createDoctorDto.getUserName());
		doctor.setAge(createDoctorDto.getAge());
		doctor.setDegree(createDoctorDto.getDegree());
		doctor.setEmail(createDoctorDto.getEmail());
		doctor.setName(createDoctorDto.getName());
		doctor.setPhoneNumber(createDoctorDto.getPhoneNumber());
		doctor.setYearsExperience(createDoctorDto.getYearsExperience());
		doctor.setSex(Sex.valueOf(createDoctorDto.getSex()));
		doctor.setSpecialized(Specialized.valueOf(createDoctorDto.getSpecialized()));

		String upLoadDir = "public/avatar/";
		MultipartFile avatar = createDoctorDto.getAvatarDoctor();
		if (!avatar.isEmpty()) {
			try (InputStream inputStream = avatar.getInputStream()) {
				Files.copy(inputStream, Paths.get(upLoadDir + doctor.getIdPerson() + ".png"),
						StandardCopyOption.REPLACE_EXISTING);
				doctor.setAvatar(doctor.getIdPerson() + ".png");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			doctor.setAvatar("default.png");
		}

		doctorRepo.save(doctor);
		System.out.println("tạo tài khoản bác sĩ thành công");
		return "redirect:/admin/create_account";
	}

	@PostMapping("create_account_nuser")
	public String createNuser(@Valid @ModelAttribute CreateNuserDto createNuserDto, BindingResult result,
			@ModelAttribute CreateDoctorDto createDoctorDto, Model model, RedirectAttributes redirectAttributes) {
		if (account == null) {
			return "redirect:/login";
		}
		if (result.hasErrors()) {
			System.out.println("Có lỗi trong form tạo tài khoản");
			return "/admin/createAccount"; // Điều hướng lại trang tạo tài khoản
		}
		if (accountRepo.findById(createNuserDto.getUserName()).orElse(null) != null) {
			redirectAttributes.addFlashAttribute("error", "tài khoản đã tồn tại");
			return "redirect:/admin/create_account";
		}

		Account account = new Account();
		account.setUserName(createNuserDto.getUserName());
		account.setPassword(createNuserDto.getPassword());
		account.setQuit(false);
		account.setRole(Role.NURSE);
		accountRepo.save(account);

		Nuser nuser = new Nuser();
		nuser.setIdPerson(createNuserDto.getUserName());
		nuser.setAge(createNuserDto.getAge());
		nuser.setDegree(createNuserDto.getDegree());
		nuser.setEmail(createNuserDto.getEmail());
		nuser.setName(createNuserDto.getName());
		nuser.setPhoneNumber(createNuserDto.getPhoneNumber());
		nuser.setYearsExperience(createNuserDto.getYearsExperience());
		nuser.setSex(Sex.valueOf(createNuserDto.getSex()));
		nuser.setRoom(Room.valueOf(createNuserDto.getRoom()));
		nuser.setPrice(createNuserDto.getPrice());

		String upLoadDir = "public/avatar/";
		MultipartFile avatar = createNuserDto.getAvatarNuser();
		if (!avatar.isEmpty()) {
			try (InputStream inputStream = avatar.getInputStream()) {
				Files.copy(inputStream, Paths.get(upLoadDir + nuser.getIdPerson() + ".png"),
						StandardCopyOption.REPLACE_EXISTING);
				nuser.setAvatar(nuser.getIdPerson() + ".png");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			nuser.setAvatar("default.png");
		}

		nuserRepo.save(nuser);
		System.out.println("tạo tài khoản y tá thành công");
		return "redirect:/admin/create_account";
	}

	@GetMapping("/control_account_doctor")
	public String showControlAccountDoctorPage(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "3") int size, Model model) {

		if (account == null) {
			return "redirect:/login";
		}
		SearchDoctorDto searchDto = new SearchDoctorDto();

		Pageable pageable = PageRequest.of(page, size);
		Page<Doctor> doctors = doctorRepo.findAll(pageable);

		model.addAttribute("searchDto", searchDto);
		model.addAttribute("doctors", doctors.getContent());
		model.addAttribute("currentPage", doctors.getNumber());
		model.addAttribute("totalPages", doctors.getTotalPages());
		model.addAttribute("totalItems", doctors.getTotalElements());
		return "/admin/controlAccountDoctor";
	}

	@GetMapping("search_account_doctor")
	public String showSearchAccountDoctorPage(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "3") int size, Model model, @ModelAttribute SearchDoctorDto searchDto) {
		if (account == null) {
			return "redirect:/login";
		}

		searchDto.setIdPerson(searchDto.getIdPerson().trim());
		searchDto.setName(searchDto.getName().trim());
		searchDto.setPhoneNumber(searchDto.getPhoneNumber().trim());

		Pageable pageable = PageRequest.of(page, size);
		Page<Doctor> doctors = null;

		if (searchDto.getIdPerson().equals("") && searchDto.getName().equals("")
				&& searchDto.getPhoneNumber().equals("") && searchDto.getSpecialized().equals("")) {
			doctors = doctorRepo.findAll(pageable);

		} else {
			Specialized specializedValue;
			try {
				specializedValue = Specialized.valueOf(searchDto.getSpecialized());
			} catch (IllegalArgumentException e) {
				// Xử lý trường hợp không có giá trị trong enum
				specializedValue = null; // hoặc giá trị mặc định khác
			}
			doctors = doctorRepo.findByIpPersonUserNameSpecializedPhoneNumber(searchDto.getIdPerson(),
					searchDto.getName(), specializedValue, searchDto.getPhoneNumber(), pageable);
		}

		model.addAttribute("searchDto", searchDto);
		model.addAttribute("doctors", doctors.getContent());
		model.addAttribute("currentPage", doctors.getNumber());
		model.addAttribute("totalPages", doctors.getTotalPages());
		model.addAttribute("totalItems", doctors.getTotalElements());
		return "/admin/controlAccountDoctor";
	}

	@GetMapping("/delete_doctor")
	public String deleteDoctor(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "3") int size,
			@RequestParam String iddoctor, Model model) {
		if (account == null) {
			return "redirect:/login";
		}

		SearchDoctorDto searchDto = new SearchDoctorDto();

		Doctor d = doctorRepo.findById(iddoctor).orElse(null);
		Account a = accountRepo.findById(iddoctor).orElse(null);
		
		if (d == null && a == null) {
			System.out.println("không có bác sĩ này");
		} else {
			doctorRepo.delete(d);
			accountRepo.delete(a);
			System.out.println("xóa bác sĩ thành công");
		}

		Pageable pageable = PageRequest.of(page, size);
		Page<Doctor> doctors = doctorRepo.findAll(pageable);

		model.addAttribute("searchDto", searchDto);
		model.addAttribute("doctors", doctors.getContent());
		model.addAttribute("currentPage", doctors.getNumber());
		model.addAttribute("totalPages", doctors.getTotalPages());
		model.addAttribute("totalItems", doctors.getTotalElements());
		return "/admin/controlAccountDoctor";
	}

	@GetMapping("/doctor_information")
	public String showDocrotInformation(@RequestParam String iddoctor, Model model) {
		if (account == null) {
			return "redirect:/login";
		}
		
		Account a = accountRepo.findById(iddoctor).orElse(null);
		Doctor d = doctorRepo.findById(iddoctor).orElse(null);

		CreateDoctorDto createDoctorDto = new CreateDoctorDto();
		createDoctorDto.setAge(d.getAge());
		createDoctorDto.setDegree(d.getDegree());
		createDoctorDto.setEmail(d.getEmail());
		createDoctorDto.setName(d.getName());
		createDoctorDto.setPassword(a.getPassword());
		createDoctorDto.setPhoneNumber(d.getPhoneNumber());
		createDoctorDto.setSex(d.getSex().toString());
		createDoctorDto.setSpecialized(d.getSpecialized().toString());
		createDoctorDto.setUserName(d.getIdPerson());
		createDoctorDto.setYearsExperience(d.getYearsExperience());
		createDoctorDto.setQuit(a.isQuit());	
		
		model.addAttribute("createDoctorDto", createDoctorDto);
		model.addAttribute("avatar", d.getAvatar());

		return "admin/showDoctorInformation";
	}

	@PostMapping("/change_doctor_information")
	public String changDoctorInformation(@Valid @ModelAttribute CreateDoctorDto createDoctorDto, BindingResult result,
			Model model, RedirectAttributes redirectAttributes) {
		if (account == null) {
			return "redirect:/login";
		}
		if (result.hasErrors()) {
			System.out.println("Nhập thiếu trường dữ liệu");
			result.getFieldErrors().forEach(error -> {
				System.out.println("Field: " + error.getField()); // Tên trường bị lỗi
				System.out.println("Error: " + error.getDefaultMessage()); // Thông báo lỗi
			});
			return "/admin/showDoctorInformation";
		}
		Account a = accountRepo.findById(createDoctorDto.getUserName()).orElse(null);
		Doctor d = doctorRepo.findById(createDoctorDto.getUserName()).orElse(null);

		a.setQuit(createDoctorDto.isQuit());
		a.setPassword(createDoctorDto.getPassword());

		d.setAge(createDoctorDto.getAge());
		d.setDegree(createDoctorDto.getDegree());
		d.setEmail(createDoctorDto.getEmail());
		d.setIdPerson(createDoctorDto.getUserName());
		d.setName(createDoctorDto.getName());
		d.setPhoneNumber(createDoctorDto.getPhoneNumber());
		d.setSex(Sex.valueOf(createDoctorDto.getSex()));
		d.setSpecialized(Specialized.valueOf(createDoctorDto.getSpecialized()));
		d.setYearsExperience(createDoctorDto.getYearsExperience());

		String upLoadDir = "public/avatar/";
		MultipartFile avatar = createDoctorDto.getAvatarDoctor();
		if (!avatar.isEmpty()) {
			try (InputStream inputStream = avatar.getInputStream()) {
				Files.copy(inputStream, Paths.get(upLoadDir + d.getIdPerson() + ".png"),
						StandardCopyOption.REPLACE_EXISTING);
				d.setAvatar(d.getIdPerson() + ".png");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			d.setAvatar(d.getAvatar());
		}

		accountRepo.save(a);
		doctorRepo.save(d);
		System.out.println("chỉnh sửa thông tin bác sĩ thành công");
		redirectAttributes.addAttribute("iddoctor", d.getIdPerson());

		return "redirect:/admin/doctor_information";
	}
	
	@GetMapping("/control_account_nurse")
	public String showControlAccountNuserPage(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "3") int size, Model model) {

		if (account == null) {
			return "redirect:/login";
		}
		SearchNuserDto searchDto = new SearchNuserDto();

		Pageable pageable = PageRequest.of(page, size);
		Page<Nuser> nusers = nuserRepo.findAll(pageable);
		
		model.addAttribute("searchDto", searchDto);
		model.addAttribute("nusers", nusers.getContent());
		model.addAttribute("currentPage", nusers.getNumber());
		model.addAttribute("totalPages", nusers.getTotalPages());
		model.addAttribute("totalItems", nusers.getTotalElements());
		return "/admin/controlAccountNuser";
	}
	
	@GetMapping("search_account_nuser")
	public String showSearchAccountNuserPage(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "3") int size, Model model, @ModelAttribute SearchNuserDto searchDto) {
		if (account == null) {
			return "redirect:/login";
		}

		searchDto.setIdPerson(searchDto.getIdPerson().trim());
		searchDto.setName(searchDto.getName().trim());
		searchDto.setPhoneNumber(searchDto.getPhoneNumber().trim());

		Pageable pageable = PageRequest.of(page, size);
		Page<Nuser> nusers = null;

		if (searchDto.getIdPerson().equals("") && searchDto.getName().equals("")
				&& searchDto.getPhoneNumber().equals("") && searchDto.getRoom().equals("")) {
			nusers = nuserRepo.findAll(pageable);
			System.out.println("---" + nusers.getTotalElements());

		} else {
			Room roomValue;
			try {
				roomValue = Room.valueOf(searchDto.getRoom());
			} catch (IllegalArgumentException e) {
				// Xử lý trường hợp không có giá trị trong enum
				roomValue = null; // hoặc giá trị mặc định khác
			}
			nusers = nuserRepo.findByIpPersonUserNameSpecializedPhoneNumber(searchDto.getIdPerson(),
					searchDto.getName(), roomValue, searchDto.getPhoneNumber(), pageable);
			System.out.println("+++" + nusers.getTotalElements());
		}

		model.addAttribute("searchDto", searchDto);
		model.addAttribute("nusers", nusers.getContent());
		model.addAttribute("currentPage", nusers.getNumber());
		model.addAttribute("totalPages", nusers.getTotalPages());
		model.addAttribute("totalItems", nusers.getTotalElements());
		return "/admin/controlAccountNuser";
	}
	
	@GetMapping("/delete_nuser")
	public String deleteNuser(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "3") int size,
			@RequestParam String idnuser, Model model) {
		if (account == null) {
			return "redirect:/login";
		}

		SearchNuserDto searchDto = new SearchNuserDto();

		Nuser n = nuserRepo.findById(idnuser).orElse(null);
		Account a = accountRepo.findById(idnuser).orElse(null);
		
		if (n == null && a == null) {
			System.out.println("không có y tá này");
		} else {
			nuserRepo.delete(n);
			accountRepo.delete(a);
			System.out.println("xóa y tá thành công");
		}

		Pageable pageable = PageRequest.of(page, size);
		Page<Nuser> nusers = nuserRepo.findAll(pageable);

		model.addAttribute("searchDto", searchDto);
		model.addAttribute("nusers", nusers.getContent());
		model.addAttribute("currentPage", nusers.getNumber());
		model.addAttribute("totalPages", nusers.getTotalPages());
		model.addAttribute("totalItems", nusers.getTotalElements());
		return "/admin/controlAccountNuser";
	}
	
	@GetMapping("/nuser_information")
	public String showNusertInformation(@RequestParam String idnuser, Model model) {
		if (account == null) {
			return "redirect:/login";
		}
		
		Account a = accountRepo.findById(idnuser).orElse(null);
		Nuser n = nuserRepo.findById(idnuser).orElse(null);

		CreateNuserDto createNuserDto = new CreateNuserDto();
		createNuserDto.setAge(n.getAge());
		createNuserDto.setDegree(n.getDegree());
		createNuserDto.setEmail(n.getEmail());
		createNuserDto.setName(n.getName());
		createNuserDto.setPassword(a.getPassword());
		createNuserDto.setPhoneNumber(n.getPhoneNumber());
		createNuserDto.setSex(n.getSex().toString());
		createNuserDto.setRoom(n.getRoom().toString());
		createNuserDto.setUserName(n.getIdPerson());
		createNuserDto.setYearsExperience(n.getYearsExperience());
		createNuserDto.setQuit(a.isQuit());
		createNuserDto.setPrice(n.getPrice());
		
		model.addAttribute("createNuserDto", createNuserDto);
		model.addAttribute("avatar", n.getAvatar());

		return "admin/showNuserInformation";
	}
	
	@PostMapping("/change_nuser_information")
	public String changeNuserInformation(@Valid @ModelAttribute CreateNuserDto createNuserDto, BindingResult result,
			Model model, RedirectAttributes redirectAttributes) {
		if (account == null) {
			return "redirect:/login";
		}
		if (result.hasErrors()) {
			System.out.println("Nhập thiếu trường dữ liệu");
			result.getFieldErrors().forEach(error -> {
				System.out.println("Field: " + error.getField()); // Tên trường bị lỗi
				System.out.println("Error: " + error.getDefaultMessage()); // Thông báo lỗi
			});
			return "/admin/showDoctorInformation";
		}
		Account a = accountRepo.findById(createNuserDto.getUserName()).orElse(null);
		Nuser n = nuserRepo.findById(createNuserDto.getUserName()).orElse(null);

		a.setQuit(createNuserDto.isQuit());
		a.setPassword(createNuserDto.getPassword());

		n.setAge(createNuserDto.getAge());
		n.setDegree(createNuserDto.getDegree());
		n.setEmail(createNuserDto.getEmail());
		n.setIdPerson(createNuserDto.getUserName());
		n.setName(createNuserDto.getName());
		n.setPhoneNumber(createNuserDto.getPhoneNumber());
		n.setSex(Sex.valueOf(createNuserDto.getSex()));
		n.setRoom(Room.valueOf(createNuserDto.getRoom()));
		n.setYearsExperience(createNuserDto.getYearsExperience());
		n.setPrice(createNuserDto.getPrice());
		
		String upLoadDir = "public/avatar/";
		MultipartFile avatar = createNuserDto.getAvatarNuser();
		if (!avatar.isEmpty()) {
			try (InputStream inputStream = avatar.getInputStream()) {
				Files.copy(inputStream, Paths.get(upLoadDir + n.getIdPerson() + ".png"),
						StandardCopyOption.REPLACE_EXISTING);
				n.setAvatar(n.getIdPerson() + ".png");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			n.setAvatar(n.getAvatar());
		}

		accountRepo.save(a);
		nuserRepo.save(n);
		System.out.println("chỉnh sửa thông tin bác sĩ thành công");
		redirectAttributes.addAttribute("idnuser", n.getIdPerson());

		return "redirect:/admin/nuser_information";
	}

	@GetMapping("logout")
	public String logout() {
		account = null;
		System.out.println("Đã đăng xuất");
		return "redirect:/login";
	}
}