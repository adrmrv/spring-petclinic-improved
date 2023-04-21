package io.github.adrmrv.springpetclinicimproved.pettype;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class PetTypeController {

	@Autowired
	private PetTypeService petTypeService;
	
	@GetMapping("/pet/types")
	public String list(Model model)
	{
		model.addAttribute("petTypes", petTypeService.findAll());
		return "PetTypeList.html";
	}

	@GetMapping("/pet/types/{Id}")
	public String show(Model model, @PathVariable("Id") int Id)
	{
        model.addAttribute("petType", petTypeService.findById(Id));
        return "PetTypeDetail";
    }

	@PostMapping("/pet/types/delete/{Id}")
    public String delete(@PathVariable("Id") int Id)
	{
        petTypeService.delete(Id);
        return "redirect:/pet/types";
    }

	@PostMapping("/pet/types/add")
    public String add(PetType petType)
	{
        petTypeService.add(petType);
        return "redirect:/pet/types";
    }

	@PostMapping("/pet/types/update")
	public String update(PetType petType)
	{
        petTypeService.update(petType);
        return "redirect:/pet/types";
    }
}
