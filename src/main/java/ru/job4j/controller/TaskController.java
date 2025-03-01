package ru.job4j.controller;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.job4j.dto.TaskDto;
import ru.job4j.model.Category;
import ru.job4j.model.TodoUser;
import ru.job4j.service.category.CategoryService;
import ru.job4j.service.priority.PriorityService;
import ru.job4j.service.task.TaskService;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@ThreadSafe
@Controller
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;
    private final PriorityService priorityService;

    private final CategoryService categoryService;

    public TaskController(TaskService taskService, PriorityService priorityService, CategoryService categoryService) {

        this.taskService = taskService;
        this.priorityService = priorityService;
        this.categoryService = categoryService;
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
        model.addAttribute("categories", categoryService.findAll());
        return "tasks/create";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute TaskDto task,
                         @RequestParam(value = "taskCategories", required = false) int[] taskCategories,
                         BindingResult bindingResult, HttpSession session, Model model) {
        var user = (TodoUser) session.getAttribute("user");
        task.setTodoUser(user);
        task.setPriority(priorityService.findById(task.getPriority().getId()).get());
        var categories = getCategoriesByIds(taskCategories);
        if (!categories.isEmpty()) {
            task.setCategories(categories);
        }
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
        List<Category> categories = taskOptional.get().getCategories();
        model.addAttribute("taskCategories", categories.stream().map(e -> e.getId()).toList());
        model.addAttribute("categories", categoryService.findAll());
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
        List<Category> categories = taskOptional.get().getCategories();
        model.addAttribute("taskCategories", categories.stream().map(e -> e.getId()).toList());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("priorities", priorityService.findAll());
        model.addAttribute("task", taskOptional.get());
        return "tasks/oneEdit";
    }

    @PostMapping("/update/{id}")
    public String update(@ModelAttribute TaskDto task,
                         @RequestParam(value = "taskCategories", required = false) int[] taskCategories,
                         BindingResult bindingResult, @PathVariable int id, Model model) {
        task.setName(task.getName().trim());
        task.setPriority(priorityService.findById(task.getPriority().getId()).get());
        var categories = getCategoriesByIds(taskCategories);
        if (!categories.isEmpty()) {
            task.setCategories(categories);
        }
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

    private List<Category> getCategoriesByIds(int[] taskCategories) {
        List<Category> result = new ArrayList<>();
        if (taskCategories != null) {
            for (int i = 0; i < taskCategories.length; i++) {
                if (categoryService.findById(taskCategories[i]).isPresent()) {
                    result.add(categoryService.findById(taskCategories[i]).get());
                }
            }
        }
        return result;
    }
}