package io.github.adrmrv.springpetclinicimproved.client;

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

@Controller
@RequestMapping("/clients")
public class ClientController {

	@Autowired
	ClientService service;
	
	@GetMapping
	public Object list(
			@RequestParam(defaultValue = "") String name,
			@RequestParam(defaultValue = "1") int p,
			Model model) throws UnsupportedEncodingException
	{
		model.addAttribute("clients", service.listClients(name, (p-1)*10));
		model.addAttribute("name", name);
		
		int count = service.countClients(name);
		String url = "/clients?name=" + URLEncoder.encode(name, "UTF-8");
		model.addAttribute("pages", PaginationItem.of(p, 10, count, url));
		
		return "ClientList";
	}

	@GetMapping("/{Id}")
	public String editClient(Model model, @PathVariable int Id)
	{
		model.addAttribute("client", service.getClient(Id));
		return "ClientEdit";
	}
	
	@PostMapping("/edit/{Id}")
	public String edit(Model model, @PathVariable int Id, Client c)
	{
		c.Id = Id;
		service.updateClient( c);
		return "redirect:/clients";
	}
	
	@GetMapping("/add")
	public String addClient(Model model) {
		model.addAttribute("client", new Client());
		return "ClientAdd";
	}
	
	@PostMapping("/add")
	public String add(Client c) {
		service.addClient(c);
		return "redirect:/clients";
	}
	
	@PostMapping("/delete/{Id}")
	public String delete(@PathVariable int Id,
			@RequestHeader(value = "referer", required = false) final String referer)
	{
		service.deleteClient(Id);
		return "redirect:" + referer;
	}

	// This is used to select a client prior to adding a pet
	// See PetController.java for the rest of the flow
	@GetMapping("/select")
	public String select(
			@RequestParam(defaultValue = "") String name,
			@RequestParam(defaultValue = "1") int p,
			Model model) throws UnsupportedEncodingException
	{
		model.addAttribute("clients", service.listClients(name, (p-1)*10));
		model.addAttribute("name", name);
		
		int count = service.countClients(name);
		String url = "/clients/select?name=" + URLEncoder.encode(name, "UTF-8");
		model.addAttribute("pages", PaginationItem.of(p, 10, count, url));
		
		return "ClientSelect";
	}

	@PostMapping("/select/{Id}")
	public String selected(@PathVariable int Id) 
	{
		return "redirect:/pets/add?ClientId=" + Id;
	}
}
