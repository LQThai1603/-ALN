package com.example.hospitalControl.Controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
import com.example.hospitalControl.Model.CreateMedicineDto;
import com.example.hospitalControl.Model.CreateNuserDto;
import com.example.hospitalControl.Model.Doctor;
import com.example.hospitalControl.Model.Medicine;
import com.example.hospitalControl.Model.Nuser;
import com.example.hospitalControl.Model.OnLeave;
import com.example.hospitalControl.Model.OnLeaveConfirm;
import com.example.hospitalControl.Model.OnLeaveDto;
import com.example.hospitalControl.Model.Patient;
import com.example.hospitalControl.Model.PatientInformationForDoctor;
import com.example.hospitalControl.Model.Role;
import com.example.hospitalControl.Model.Room;
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
import com.example.hospitalControl.Service.NuserRepository;
import com.example.hospitalControl.Service.OnLeaveRepository;
import com.example.hospitalControl.Service.PatientRepository;

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

	@Autowired
	MedicineRepository medicineRepo;
	
	@Autowired
	OnLeaveRepository onLeaveRepo;
	
	@Autowired
	PatientRepository patientRepo;

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
		return "redirect:/admin/search_account_doctor_continue";
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
			doctors = doctorRepo.findAll(pageable);

		} else {
			Specialized specializedValue;
			try {
				specializedValue = Specialized.valueOf(specialized);
			} catch (IllegalArgumentException e) {
				// Xử lý trường hợp không có giá trị trong enum
				specializedValue = null; // hoặc giá trị mặc định khác
			}
			doctors = doctorRepo.findByIpPersonUserNameSpecializedPhoneNumber(idperson, name, specializedValue,
					phonenumber, pageable);
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
		return "/admin/searchDoctor";
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
			// xu ly xoa tai day
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
			@RequestParam(defaultValue = "3") int size, Model model, @ModelAttribute SearchNuserDto searchDto,
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
		redirectAttributes.addAttribute("room", searchDto.getRoom());
		return "redirect:/admin/search_account_nuser_continue";
	}

	@GetMapping("search_account_nuser_continue")
	public String showSearchAccountNuserContinuePage(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "3") int size, @RequestParam String idperson, @RequestParam String name,
			@RequestParam String phonenumber, @RequestParam String room, RedirectAttributes redirectAttributes,
			Model model) {
		if (account == null) {
			return "redirect:/login";
		}
		Pageable pageable = PageRequest.of(page, size);
		Page<Nuser> nusers = null;

		if (idperson.equals("") && name.equals("") && phonenumber.equals("") && room.equals("")) {
			nusers = nuserRepo.findAll(pageable);

		} else {
			Room roomValue;
			try {
				roomValue = Room.valueOf(room);
			} catch (IllegalArgumentException e) {
				// Xử lý trường hợp không có giá trị trong enum
				roomValue = null; // hoặc giá trị mặc định khác
			}
			nusers = nuserRepo.findByIpPersonUserNameRoomPhoneNumber(idperson, name, roomValue, phonenumber, pageable);
		}
		model.addAttribute("searchDto", new SearchNuserDto());
		model.addAttribute("nusers", nusers.getContent());
		model.addAttribute("currentPage", nusers.getNumber());
		model.addAttribute("totalPages", nusers.getTotalPages());
		model.addAttribute("totalItems", nusers.getTotalElements());
		model.addAttribute("idperson", idperson);
		model.addAttribute("name", name);
		model.addAttribute("phonenumber", phonenumber);
		model.addAttribute("room", room);
		return "/admin/searchNuser";
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
			// xu ly xoa tai day
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

	@GetMapping("control_medicine")
	public String showControlMedicinePage(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "3") int size, Model model) {
		if (account == null) {
			return "redirect:/login";
		}
		CreateMedicineDto createMedicineDto = new CreateMedicineDto();

		Pageable pageable = PageRequest.of(page, size);
		Page<Medicine> medicines = medicineRepo.findAll(pageable);

		model.addAttribute("createMedicineDto", createMedicineDto);
		model.addAttribute("medicines", medicines);
		model.addAttribute("searchDto", new SearchMedicineDto());
		model.addAttribute("currentPage", medicines.getNumber());
		model.addAttribute("totalPages", medicines.getTotalPages());
		model.addAttribute("totalItems", medicines.getTotalElements());
		return "/admin/controlMedicine";
	}

	@PostMapping("create_medicine")
	public String createMedicine(@Valid @ModelAttribute CreateMedicineDto createMedicineDto, BindingResult result,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "3") int size, Model model,
			RedirectAttributes redirectAttributes) {
		if (account == null) {
			return "redirect:/login";
		}
		if (result.hasErrors()) {
			SearchMedicineDto searchDto = new SearchMedicineDto();
			Pageable pageable = PageRequest.of(page, size);
			Page<Medicine> medicines = medicineRepo.findAll(pageable);
			model.addAttribute("medicines", medicines);
			model.addAttribute("searchDto", searchDto);
			model.addAttribute("currentPage", medicines.getNumber());
			model.addAttribute("totalPages", medicines.getTotalPages());
			model.addAttribute("totalItems", medicines.getTotalElements());
			result.getFieldErrors().forEach(error -> {
				System.out.println("Field: " + error.getField()); // Tên trường bị lỗi
				System.out.println("Error: " + error.getDefaultMessage()); // Thông báo lỗi
			});
			return "/admin/controlMedicine";
		}
		Medicine m = new Medicine();
		m.setName(createMedicineDto.getName());
		m.setPrice(createMedicineDto.getPrice());
		m.setQuantity(createMedicineDto.getQuantity());
		m.setExpirationDate(createMedicineDto.getExpirationDate());
		String upLoadDir = "public/medicine/";
		MultipartFile image = createMedicineDto.getImage();
		if (!image.isEmpty()) {
			try (InputStream inputStream = image.getInputStream()) {
				Files.copy(inputStream, Paths.get(upLoadDir + m.getName() + ".png"),
						StandardCopyOption.REPLACE_EXISTING);
				m.setImage(m.getName() + ".png");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			m.setImage("default.png");
		}
		medicineRepo.save(m);
		return "redirect:/admin/control_medicine";
	}

	@GetMapping("search_medicine")
	public String showSearchMedicinePage(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "3") int size, Model model, @ModelAttribute SearchMedicineDto searchDto,
			RedirectAttributes redirectAttributes) {
		if (account == null) {
			return "redirect:/login";
		}

		searchDto.setNameMedicine(searchDto.getNameMedicine().trim());

		System.out.println("date: " + searchDto.getExpirationDate());
		System.out.println("name: " + searchDto.getNameMedicine());
		System.out.println("price: " + searchDto.getPrice());
		System.out.println("quantity: " + searchDto.getQuantity());

		if (searchDto.getExpirationDate() == null) {
			searchDto.setExpirationDate(LocalDate.of(2099, 1, 30));
		}

		redirectAttributes.addAttribute("name", searchDto.getNameMedicine());
		redirectAttributes.addAttribute("expirationdate", searchDto.getExpirationDate());
		redirectAttributes.addAttribute("quantity", searchDto.getQuantity());
		redirectAttributes.addAttribute("price", searchDto.getPrice());
		return "redirect:/admin/search_medicine_continue";
	}

	@GetMapping("search_medicine_continue")
	public String showSearchMedicineContinuePage(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "3") int size, @RequestParam String name,
			@RequestParam LocalDate expirationdate, @RequestParam int quantity, @RequestParam int price,
			RedirectAttributes redirectAttributes, Model model) {
		if (account == null) {
			return "redirect:/login";
		}
		Pageable pageable = PageRequest.of(page, size);
		Page<Medicine> medicines = null;

		if (name.equals("") && expirationdate.equals(LocalDate.of(2099, 1, 30)) && quantity == 0 && price == 0) {
			medicines = medicineRepo.findAll(pageable);

		} else {
			medicines = medicineRepo.findByNameExpirationDateQuantityPrice(name, expirationdate, quantity, price,
					pageable);
		}
		model.addAttribute("createMedicineDto", new CreateMedicineDto());
		model.addAttribute("searchDto", new SearchMedicineDto());
		model.addAttribute("medicines", medicines.getContent());
		model.addAttribute("currentPage", medicines.getNumber());
		model.addAttribute("totalPages", medicines.getTotalPages());
		model.addAttribute("totalItems", medicines.getTotalElements());
		model.addAttribute("name", name);
		model.addAttribute("expirationdate", expirationdate);
		model.addAttribute("quantity", quantity);
		model.addAttribute("price", price);
		return "/admin/searchMedicine";
	}

	@GetMapping("/delete_medicine")
	public String deleteMedicine(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "3") int size,
			@RequestParam int idmedicine, Model model) {
		if (account == null) {
			return "redirect:/login";
		}

		Medicine m = medicineRepo.findById(idmedicine).orElse(null);
		System.out.println(m == null);

		if (m == null) {
			System.out.println("không có thuốc này");
		} else {
			List<Patient> p = patientRepo.findByIdMedicine(idmedicine);
			for(Patient i : p) {
				i.getMedicine().remove(m);
			}
			patientRepo.saveAll(p);
			medicineRepo.delete(m);
			System.out.println("xóa thuốc thành công");
		}

		Pageable pageable = PageRequest.of(page, size);
		Page<Medicine> medicines = medicineRepo.findAll(pageable);

		model.addAttribute("createMedicineDto", new CreateMedicineDto());
		model.addAttribute("medicines", medicines);
		model.addAttribute("searchDto", new SearchMedicineDto());
		model.addAttribute("currentPage", medicines.getNumber());
		model.addAttribute("totalPages", medicines.getTotalPages());
		model.addAttribute("totalItems", medicines.getTotalElements());
		return "/admin/controlMedicine";
	}

	@GetMapping("medicine_information")
	public String showMedicineInformationPage(@RequestParam int idmedicine, Model model) {
		if (account == null) {
			return "redirect:/login";
		}
		Medicine m = medicineRepo.findById(idmedicine).orElse(null);
		if (m == null) {
			System.out.println("không có thuốc này");
		}

		CreateMedicineDto createMedicineDto = new CreateMedicineDto();
		createMedicineDto.setId(m.getIdMedicine());
		createMedicineDto.setExpirationDate(m.getExpirationDate());
		createMedicineDto.setName(m.getName());
		createMedicineDto.setPrice(m.getPrice());
		createMedicineDto.setQuantity(m.getQuantity());

		model.addAttribute("createMedicineDto", createMedicineDto);
		model.addAttribute("imageMedicine", m.getImage());
		return "/admin/showMedicineInformation";
	}

	@PostMapping("change_medicine_information")
	public String changeMedicineInformation(@Valid @ModelAttribute CreateMedicineDto createMedicineDto,
			BindingResult result, RedirectAttributes redirectAttributes) {
		if (account == null) {
			return "redirect:/login";
		}
		if (result.hasErrors()) {
			result.getFieldErrors().forEach(error -> {
				System.out.println("Field: " + error.getField()); // Tên trường bị lỗi
				System.out.println("Error: " + error.getDefaultMessage()); // Thông báo lỗi
			});
			return "/admin/showMedicineInformation";
		}

		Medicine m = new Medicine();
		m.setIdMedicine(createMedicineDto.getId());
		m.setExpirationDate(createMedicineDto.getExpirationDate());
		m.setName(createMedicineDto.getName());
		m.setPrice(createMedicineDto.getPrice());
		m.setQuantity(createMedicineDto.getQuantity());
		MultipartFile image = createMedicineDto.getImage();
		String upLoadDir = "public/medicine/";
		if (!image.isEmpty()) {
			try (InputStream inputStream = image.getInputStream()) {
				Files.copy(inputStream, Paths.get(upLoadDir + m.getName() + ".png"),
						StandardCopyOption.REPLACE_EXISTING);
				m.setImage(m.getName() + ".png");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			m.setImage("default.png");
		}
		medicineRepo.save(m);
		redirectAttributes.addAttribute("idmedicine", m.getIdMedicine());
		return "redirect:/admin/medicine_information";
	}
	
	@GetMapping("control_onleave")
	public String showControlOnLeavePage(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "3") int size, Model model) {
		if (account == null) {
			return "redirect:/login";
		}
		OnLeaveConfirm onLeaveConfirm = new OnLeaveConfirm();

		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createDate"));
		Page<OnLeave> onLeaves = onLeaveRepo.findAll(pageable);
		

		model.addAttribute("onLeaveConfirm", onLeaveConfirm);
		model.addAttribute("onLeaves", onLeaves);
		model.addAttribute("searchDto", new SearchMedicineDto());
		model.addAttribute("currentPage", onLeaves.getNumber());
		model.addAttribute("totalPages", onLeaves.getTotalPages());
		model.addAttribute("totalItems", onLeaves.getTotalElements());
		return "/admin/controlOnLeave";
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

		return "admin/showOnLeaveInformation";
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
			return "/admin/showOnLeaveInformation";
		}

		OnLeave o = new OnLeave();
		o.setConfirm(onLeaveConfirm.isConfirm());
		o.setEndDate(onLeaveConfirm.getEndDate());
		o.setId(onLeaveConfirm.getId());
		o.setIdPerson(onLeaveConfirm.getIdPerson());
		o.setReason(onLeaveConfirm.getReason());
		o.setStartDate(onLeaveConfirm.getStartDate());
		o.setCreateDate(onLeaveConfirm.getDate());
		onLeaveRepo.save(o);
		redirectAttributes.addAttribute("idonleave", o.getId());
		return "redirect:/admin/onleave_information";
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
		onLeaveConfirm.setIdPerson(onLeaveConfirm.getIdPerson().trim());

		redirectAttributes.addAttribute("idperson", onLeaveConfirm.getIdPerson());
		redirectAttributes.addAttribute("createdate", onLeaveConfirm.getDate());
		redirectAttributes.addAttribute("startdate", onLeaveConfirm.getStartDate());
		redirectAttributes.addAttribute("enddate", onLeaveConfirm.getEndDate());
		return "redirect:/admin/search_onleave_continue";
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
			onleaves = onLeaveRepo.findByIdPersonCreateDateStartDateEndDate(idperson, createdate, startdate, enddate, pageable);
		}
		
		if(createdate == null) {
			createdate = LocalDate.of(2000, 01, 01);
		}
		if(startdate == null) {
			startdate = LocalDate.of(2000, 01, 01);
		}
		if(enddate == null) {
			enddate = LocalDate.of(2000, 01, 01);
		}
		
		System.out.println( "------" +onleaves.getTotalElements());
		System.out.println( "------" +idperson);
		System.out.println( "------" +createdate);
		System.out.println( "------" +startdate);
		System.out.println( "------" +enddate);
		model.addAttribute("onLeaveConfirm", new OnLeaveConfirm());
		model.addAttribute("onLeaves", onleaves.getContent());
		model.addAttribute("currentPage", onleaves.getNumber());
		model.addAttribute("totalPages", onleaves.getTotalPages());
		model.addAttribute("totalItems", onleaves.getTotalElements());
		model.addAttribute("idperson", idperson);
		model.addAttribute("createdate", createdate);
		model.addAttribute("startdate", startdate);
		model.addAttribute("enddate", enddate);
		return "/admin/searchOnLeave";
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
		return "/admin/controlPatient";
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
		return "redirect:/admin/search_patient_continue";
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
		return "/admin/searchPatient";
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

		return "/admin/showPatientInformation";
	}
	
	@GetMapping("logout")
	public String logout() {
		account = null;
		System.out.println("Đã đăng xuất");
		return "redirect:/login";
	}
}