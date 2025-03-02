package ru.job4j.controller;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.model.TodoUser;
import ru.job4j.service.todouser.TodoUserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.ZoneId;
import java.util.*;

@ThreadSafe
@Controller
@RequestMapping("/users")
public class TodoUserController {

    private final TodoUserService todoUserService;

    public TodoUserController(TodoUserService todoUserService) {
        this.todoUserService = todoUserService;
    }

    @GetMapping("/register")
    public String getCreationPage(Model model) {
        model.addAttribute("userTimezoneDef", TimeZone.getDefault().getDisplayName());
        model.addAttribute("timezones", getTimeZones());
        return "users/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute TodoUser todoUser, Model model) {
        var savedUser = todoUserService.save(todoUser);
        if (savedUser.isEmpty()) {
            model.addAttribute("message", "Пользователь с таким логином уже существует");
            return "users/register";
        }
        return "redirect:/";
    }

    @GetMapping("/register/{login}/{password}")
    public String getUserByLoginPass(Model model, @PathVariable String login, @PathVariable String password) {
        var userOptional = todoUserService.findByLogin(login, password);
        if (userOptional.isEmpty()) {
            model.addAttribute("message", "Пользователь с указанными login и password не найден");
            return "errors/404";
        }
        model.addAttribute("user", userOptional.get());
        return "users/register";
    }

    @GetMapping("/login")
    public String getLoginPage() {

        return "users/login";
    }

    @PostMapping("/login")
    public String loginUser(@ModelAttribute TodoUser user, Model model, HttpServletRequest request) {
        var userOptional = todoUserService.findByLogin(user.getLogin(), user.getPassword());
        if (userOptional.isEmpty()) {
            model.addAttribute("error", "Логин или пароль введены неверно");
            return "users/login";
        }
        var session = request.getSession();
        session.setAttribute("user", userOptional.get());
        return "redirect:/tasks";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/users/login";
    }

    private List<TimeZone> getTimeZones0() {
        var zones = new ArrayList<TimeZone>();
        for (String timeId : TimeZone.getAvailableIDs()) {
            zones.add(TimeZone.getTimeZone(timeId));
        }
        for (TimeZone zone : zones) {
            System.out.println(zone.getID() + " : " + zone.getDisplayName());
        }
        return zones;
    }

    private List<String> getTimeZones1() {
        var zones = new ArrayList<String>();
        for (String timeId : TimeZone.getAvailableIDs()) {
            zones.add(TimeZone.getTimeZone(timeId).getDisplayName());
        }
        return zones;
    }

    private List<String> getTimeZones() {
        var zones = new ArrayList<String>();
        Set<String> timeIds = ZoneId.getAvailableZoneIds();
        Iterator<String> namesIterator = timeIds.iterator();
        while (namesIterator.hasNext()) {
            zones.add(namesIterator.next());
        }
        return zones.stream().sorted().toList();
    }
}