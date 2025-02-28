package ru.job4j.controller;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.dto.TaskDto;
import ru.job4j.model.TodoUser;
import ru.job4j.service.priority.PriorityService;
import ru.job4j.service.task.TaskService;

import javax.servlet.http.HttpSession;

@ThreadSafe
@Controller
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;
    private final PriorityService priorityService;

    public TaskController(TaskService taskService, PriorityService priorityService) {

        this.taskService = taskService;
        this.priorityService = priorityService;
    }

    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("tasks", taskService.findAll());
        model.addAttribute("addVisible", true);
        return "tasks/list";
    }

    @GetMapping("/done")
    public String getAllDone(Model model) {
        model.addAttribute("tasks", taskService.findAllDone());
        model.addAttribute("addVisible", false);
        return "tasks/list";
    }

    @GetMapping("/new")
    public String getAllNew(Model model) {
        model.addAttribute("tasks", taskService.findAllNew());
        model.addAttribute("addVisible", false);
        return "tasks/list";
    }

    @GetMapping("/create")
    public String getCreationPage(Model model) {
        model.addAttribute("priorities", priorityService.findAll());
        return "tasks/create";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute TaskDto task, Model model, HttpSession session) {
        var user = (TodoUser) session.getAttribute("user");
        task.setTodoUser(user);
        task.setPriority((task == null || task.getPriority() == null || task.getPriority().getName() == null)
                ? null : priorityService.findById(task.getPriority().getId()).get());
        TaskDto createdTask = taskService.create(task);
        if (createdTask == null) {
            model.addAttribute("message", "Возникла ошибка при создании задания");
            return "errors/404";
        }
        model.addAttribute("task", createdTask);
        model.addAttribute("message", "Задание было успешно создано");
        return "tasks/success";
    }

    @GetMapping("/{id}")
    public String getCreationPage(Model model, @PathVariable int id) {
        var taskOptional = taskService.findById(id);
        if (taskOptional.isEmpty()) {
            model.addAttribute("message", "Задание с указанным идентификатором не найдено");
            return "errors/404";
        }
        model.addAttribute("task", taskOptional.get());
        return "tasks/one";
    }

    @PostMapping("/{id}")
    public String setDonePage(@ModelAttribute TaskDto task, @PathVariable int id, Model model) {
        var isSet = taskService.setDone(id);
        if (!isSet) {
            model.addAttribute("message", "При переводе в статус 'Выполнено' задания с указанным идентификатором '"
                    + id + "' произошла ошибка");
            return "errors/404";
        }
        model.addAttribute("task", task);
        model.addAttribute("message", "Задание было успешно переведено в статус 'Выполнено'");
        return "tasks/success";
    }

    @GetMapping("/update/{id}")
    public String getUpdatingPage(Model model, @PathVariable int id) {
        var taskOptional = taskService.findById(id);
        if (taskOptional.isEmpty()) {
            model.addAttribute("message", "Задание с указанным идентификатором не найдено");
            return "errors/404";
        }
        model.addAttribute("priorities", priorityService.findAll());
        model.addAttribute("task", taskOptional.get());
        return "tasks/oneEdit";
    }

    @PostMapping("/update/{id}")
    public String update(@ModelAttribute TaskDto task, @PathVariable int id, Model model) {
        task.setName(task.getName().trim());
        task.setPriority((task == null || task.getPriority() == null || task.getPriority().getName() == null)
                ? null : priorityService.findById(task.getPriority().getId()).get());
        var isUpdated = taskService.update(task);
        if (!isUpdated) {
            model.addAttribute("message", "При обновленми задания с указанным идентификатором '"
                    + id + "' произошла ошибка");
            return "errors/404";
        }
        model.addAttribute("task", task);
        model.addAttribute("message", "Задание было успешно обновлено");
        return "tasks/success";
    }

    @PostMapping("/delete/{id}")
    public String delete(@ModelAttribute TaskDto task, @PathVariable int id, Model model) {
        var isDeleted = taskService.delete(id);
        if (!isDeleted) {
            model.addAttribute("message", "Задание с указанным идентификатором не найдено");
            return "errors/404";
        }
        return "redirect:/tasks";
    }
}