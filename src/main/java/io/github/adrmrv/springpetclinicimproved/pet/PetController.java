package io.github.adrmrv.springpetclinicimproved.pet;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.github.adrmrv.springpetclinicimproved.PaginationItem;
import io.github.adrmrv.springpetclinicimproved.client.Client;
import io.github.adrmrv.springpetclinicimproved.client.ClientService;
import io.github.adrmrv.springpetclinicimproved.pettype.PetTypeService;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/pets")
public class PetController {
    @Autowired
    PetService service;

    @Autowired
    PetTypeService pTypeService;

    @GetMapping
    public String list(
        @RequestParam(defaultValue = "") String name,
        @RequestParam(defaultValue = "0") int pt,
        @RequestParam(defaultValue = "1") int p,
        HttpSession session,
        Model model) throws UnsupportedEncodingException 
    {
        model.addAttribute("name", name);
        model.addAttribute("pets", service.list(pt, name, (p-1)*10));
        
        int count = service.countPets(pt, name);
        String url = "/pets?name=" + URLEncoder.encode(name, "UTF-8") + "&pt=" + pt;
        session.setAttribute("url", url);
        model.addAttribute("pages", PaginationItem.of(p, 10, count, url));
        model.addAttribute("petTypes", pTypeService.findAll());
        model.addAttribute("pt", pt);
        return "PetList";
    }

    @PostMapping("/delete/{Id}")
    public String delete(@PathVariable int Id,
        @RequestHeader String referer) {
        service.delete(Id);
        return "redirect:" + referer;
    }

    @Autowired
    ClientService cService;

    @GetMapping("/add")
    public String addPet(Integer ClientId, Model model) {
        if(ClientId != null && ClientId > 0)
        {
            Client c = cService.getClient(ClientId);
            model.addAttribute("Client", c);
        }
        else
        {
            return "redirect:/clients/select";
        }

        model.addAttribute("pet", new Pet());
        model.addAttribute("petTypes", pTypeService.findAll());
        return "PetAdd";
    }

    @PostMapping("/add")
    public String add(Pet p) {
        service.add(p);
        return "redirect:/pets";
    }

    @GetMapping("/{Id}")
    public String edit(Model model, @PathVariable int Id)
    {
        model.addAttribute("pet", service.get(Id));
        model.addAttribute("petTypes", pTypeService.findAll());
        return "PetEdit";
    }

    @PostMapping("/edit/{Id}")
    public String save(@PathVariable int Id, Pet p, HttpSession session)
    {
        p.setId(Id);
        service.save(p);
        return "redirect:" + session.getAttribute("url");
    }
}
