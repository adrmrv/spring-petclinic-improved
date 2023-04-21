package io.github.adrmrv.springpetclinicimproved.appointment;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponentsBuilder;

import io.github.adrmrv.springpetclinicimproved.PaginationItem;
import io.github.adrmrv.springpetclinicimproved.client.Client;
import io.github.adrmrv.springpetclinicimproved.client.ClientService;
import io.github.adrmrv.springpetclinicimproved.pet.PetService;
import io.github.adrmrv.springpetclinicimproved.pettype.PetTypeService;
import io.github.adrmrv.springpetclinicimproved.veterinary.Veterinary;
import io.github.adrmrv.springpetclinicimproved.veterinary.VeterinaryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;


@Controller
public class AppointmentController {
    @Autowired
    AppointmentService appService;

    @Autowired
    VeterinaryService vservice;

    @GetMapping("/schedule")
    public String daily(Model model, @RequestParam(required = false) LocalDate Day) {
        Day = Day == null ? LocalDate.now() : Day;
        model.addAttribute("Day", Day);
        model.addAttribute("Id", 0);
        model.addAttribute("schedule", appService.getSchedule(Day));
        model.addAttribute("vets", vservice.getAllVets());
        return "Schedule";
    }

    @GetMapping("/schedule/{Id}")
    public String weekly(Model model, @PathVariable int Id, @RequestParam(required = false) LocalDate Day) {
        Day = Day == null ? LocalDate.now() : Day;
        Day = Day.plusDays(-Day.getDayOfWeek().ordinal());
        model.addAttribute("Day", Day);
        model.addAttribute("Days", appService.getWeeklyScheduleDays(Day));
        model.addAttribute("Id", Id);
        model.addAttribute("vets", vservice.getAllVets());
        model.addAttribute("schedule", appService.getWeeklySchedule(Day, Id));
        return "WeeklySchedule";
    }

    @Autowired
    HomeService homeService;

    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        model.addAttribute("vets", vservice.getAllVets());
        model.addAttribute("counts", homeService.getCounts());
        model.addAttribute("message", session.getAttribute("message"));
        session.removeAttribute("message");
        return "Home";
    }

    @Autowired
    ClientService cService;

    @GetMapping("/appointments/new")
    public String newAppointment(Model model, HttpSession session) {
        if(session.getAttribute("VetId") == null ||
            session.getAttribute("App") == null)
            {
                return "redirect:/appointments/search";
            }

        if(session.getAttribute("PetId") == null)  
        {
            return "redirect:/pet/select";
        }

        model.addAttribute("VetId", session.getAttribute("VetId"));
        model.addAttribute("App", session.getAttribute("App"));
        model.addAttribute("PetId", session.getAttribute("PetId"));
        model.addAttribute("Owner", cService.getOwnerOfPet((int)session.getAttribute("PetId")));
        model.addAttribute("Vet", vservice.getVet((int)session.getAttribute("VetId")));
        return "AppointmentConfirm";
    }

    @GetMapping("/appointments/search")
    public String search(Model model, HttpServletRequest request,
            @RequestParam(defaultValue = "1") int p,
            @RequestParam(defaultValue = "0") int VetId,
            @RequestParam(required = false) LocalDate Day,
            @RequestParam(defaultValue = "08:00") String hMin,
            @RequestParam(defaultValue = "19:00") String hMax,
            @RequestParam(required = false, value = "dow[]") Integer[] Dow) {
        Day = Day == null ? LocalDate.now() : Day;
        model.addAttribute("Day", Day);

        String[] hours = new String[22];
        for (int i = 8, k = 0; i < 19; ++i) {
            for (int j = 0; j < 60; j += 30, ++k) {
                hours[k] = String.format("%02d:%02d", i, j);
            }
        }

        model.addAttribute("hours", hours);

        DayOfWeek[] dayNames = new DayOfWeek[5];
        for (int i = 0; i < 5; ++i) {
            dayNames[i] = DayOfWeek.of(i + 1);
        }

        Dow = Dow == null ? new Integer[0] : Dow;
        model.addAttribute("dow", Dow);
        model.addAttribute("dayNames", dayNames);
        model.addAttribute("hMin", hMin);
        model.addAttribute("hMax", hMax);
        model.addAttribute("vets", vservice.getAllVets());
        model.addAttribute("VetId", VetId);
        model.addAttribute("availabilities", appService.getAvailabilities(Day, VetId, hMin, hMax, p, Dow));
        
        int count = appService.getAvailabilityCount(Day, VetId, hMin, hMax, p, Dow);
        String url = request.getRequestURL().toString() + "?" + request.getQueryString();
        var res = UriComponentsBuilder.fromUriString(url);
        url = res.replaceQueryParam("p").build().toUriString();
        model.addAttribute("pages", PaginationItem.of(p, 10, count, url));
        return "AppointmentSelect";
    }

    @PostMapping("/appointment/select")
    public String selectAppointment(int VetId, LocalDateTime App, HttpSession session)
    {
        session.setAttribute("App", App);
        session.setAttribute("VetId", VetId);
        return "redirect:/appointments/new";
    }

    @Autowired 
    PetService petService;

    @Autowired
    PetTypeService pTypeService;

    @GetMapping("/pet/select")
    public String list(
        @RequestParam(defaultValue = "") String name,
        @RequestParam(defaultValue = "0") int pt,
        @RequestParam(defaultValue = "1") int p,
        HttpSession session,
        Model model) throws UnsupportedEncodingException 
    {
        model.addAttribute("name", name);
        model.addAttribute("pets", petService.list(pt, name, (p-1)*10));
        
        int count = petService.countPets(pt, name);
        String url = "/pets?name=" + URLEncoder.encode(name, "UTF-8") + "&pt=" + pt;
        session.setAttribute("url", url);
        model.addAttribute("pages", PaginationItem.of(p, 10, count, url));
        model.addAttribute("petTypes", pTypeService.findAll());
        model.addAttribute("pt", pt);
        return "PetSelect";
    }

    @PostMapping("/pet/select")
    public String selectPet(HttpSession session, int PetId)
    {
        session.setAttribute("PetId", PetId);
        return "redirect:/appointments/new";
    }

    @PostMapping("/appointment/cancel")
    public String postMethodName(HttpSession session) {
        session.removeAttribute("PetId");
        session.removeAttribute("VetId");
        session.removeAttribute("App");
        return "redirect:/";
    }
    
    @PostMapping("/appointment/create")
    public String createAppointment(HttpSession session, int VetId, int PetId,  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime App)
    {
        appService.create(VetId, PetId, App);
        Veterinary vet = vservice.getVet(VetId);
        Client client = cService.getOwnerOfPet(PetId);
        String message = "Appointment set on " + App.format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:MM"))
            + " for " + client.getFirstName() + " " + client.getLastName() + " with Dr "
            + vet.getFirstName() + " " + vet.getLastName();
        session.setAttribute("message", message);
        return "redirect:/";
    }
}
