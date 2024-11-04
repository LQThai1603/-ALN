package com.example.hospitalControl.Controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.hospitalControl.Model.Account;
import com.example.hospitalControl.Model.CreateMedicineDto;
import com.example.hospitalControl.Model.Medicine;
import com.example.hospitalControl.Model.SearchMedicineDto;
import com.example.hospitalControl.Security.AESUtils;
import com.example.hospitalControl.Service.AccountRepository;
import com.example.hospitalControl.Service.MedicineRepository;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/nuser_medicine_management")
public class NuserMedicineManegement {
	private Account account = null;
	@Autowired
	AccountRepository accountRepo;
	
	@Autowired
	MedicineRepository medicineRepo;
	
	@GetMapping("")
	public String showAdminPage(@RequestParam("userName") String userName, @RequestParam("password") String password)
			throws Exception {
		account = accountRepo.findById(AESUtils.decrypt(userName)).orElse(null);
		System.out.println(account.getUserName());
		if (account == null) {
			return "redirect:/login";
		}
		System.out.println(account.getUserName());
		System.out.println(account.getPassword());
		return "/nuserMedicineManagement/nuserMedicineManagement";
	}
	
	@GetMapping("nuser_medicine_management_home")
	public String showAdminPage() {
		if (account == null) {
			return "redirect:/login";
		}
		System.out.println(account.getUserName());

		return "/nuserMedicineManagement/nuserMedicineManagement";
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
		return "/nuserMedicineManagement/controlMedicine";
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
			return "/nuserMedicineManagement/controlMedicine";
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
		return "redirect:/nuser_medicine_management/control_medicine";
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
			searchDto.setExpirationDate(LocalDate.of(3030, 1, 30));
		}

		redirectAttributes.addAttribute("name", searchDto.getNameMedicine());
		redirectAttributes.addAttribute("expirationdate", searchDto.getExpirationDate());
		redirectAttributes.addAttribute("quantity", searchDto.getQuantity());
		redirectAttributes.addAttribute("price", searchDto.getPrice());
		return "redirect:/nuser_medicine_management/search_medicine_continue";
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

		if (name.equals("") && expirationdate == null && quantity == 0 && price == 0) {
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
		return "/nuserMedicineManagement/searchMedicine";
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
			// xu ly xoa lien quan tai day
			medicineRepo.delete(m);
			System.out.println("xóa bác thuốc thành công");
		}

		Pageable pageable = PageRequest.of(page, size);
		Page<Medicine> medicines = medicineRepo.findAll(pageable);

		model.addAttribute("createMedicineDto", new CreateMedicineDto());
		model.addAttribute("medicines", medicines);
		model.addAttribute("searchDto", new SearchMedicineDto());
		model.addAttribute("currentPage", medicines.getNumber());
		model.addAttribute("totalPages", medicines.getTotalPages());
		model.addAttribute("totalItems", medicines.getTotalElements());
		return "/nuserMedicineManagement/controlMedicine";
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
		return "/nuserMedicineManagement/showMedicineInformation";
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
			return "/nuserMedicineManagement/showMedicineInformation";
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
		return "redirect:/nuser_medicine_management/medicine_information";
	}
	@GetMapping("logout")
	public String logout() {
		account = null;
		System.out.println("Đã đăng xuất");
		return "redirect:/login";
	}
}
