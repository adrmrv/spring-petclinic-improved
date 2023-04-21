package io.github.adrmrv.springpetclinicimproved.veterinary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class VeterinaryController {

	@Autowired
	NamedParameterJdbcTemplate jdbcTemplate;
	
	@Autowired
	private VeterinaryService vetService;
	
	@GetMapping("/veterinaries")
	public String list(Model model) {
		model.addAttribute("vets", vetService.getAllVets());
		
		return "VeterinaryList";
	}

	@GetMapping("/veterinary/edit/{Id}")
	public String editVeterinary(Model model, @PathVariable int Id) {
		model.addAttribute("vet", vetService.getVet(Id));
		
		return "VeterinaryEdit";
	}
	
	@PostMapping("/veterinary/edit/{Id}")
	public String edit(@PathVariable int Id, Veterinary vet) {
		vet.Id = Id;
		vetService.updateVet(vet);
		
		return "redirect:/veterinaries";
	}

	@GetMapping("/veterinary/add")
	public String addVet() {
		return "VeterinaryAdd";
	}	
	
	@PostMapping("/veterinary/add")
	public String add(Veterinary vet) {
		vetService.addVet(vet);
		return "redirect:/veterinaries";
	}	
	
	@GetMapping("/veterinary/delete/{Id}")
	public String delete(Model model, @PathVariable int Id) {
		vetService.deleteVet(Id);
		return "redirect:/veterinaries";
	}
	
}
