package io.github.adrmrv.springpetclinicimproved.operatinghours;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.github.adrmrv.springpetclinicimproved.veterinary.VeterinaryService;

@Controller
public class OperatingHoursController {

    @Autowired
    OperatingHoursService service;

    @Autowired
    VeterinaryService vService;

    @GetMapping("/hours")
    public String defaultHours() {
        var vets = vService.getAllVets();
        int Id = vets.get(0).getId();
        return "redirect:/hours/" + Id;
    }

    @GetMapping("/hours/{Id}")
    public String hours(Model model, @PathVariable int Id) {
        model.addAttribute("DayHourSlot", service.getDayHourSlots(Id));
        model.addAttribute("vets", vService.getAllVets());
        model.addAttribute("Id", Id);
        return "OperatingHours";
    }

    @PostMapping("/hours/{Id}")
    public String save(@PathVariable int Id, @RequestParam Map<String, String> body) {
        service.updateHourSlots(body, Id);
        return "redirect:/hours/" + Id;
    }
}
