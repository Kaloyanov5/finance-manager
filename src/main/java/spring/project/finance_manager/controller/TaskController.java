package spring.project.finance_manager.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.project.finance_manager.service.TaskService;

@RestController
@RequestMapping("/api")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/tasks")
    public ResponseEntity<?> getTasks(@CookieValue(name = "access_token", required = false) String accessToken,
                                      @CookieValue(name = "refresh_token", required = false) String refreshToken,
                                      HttpServletResponse response) {
        return taskService.getTasks(accessToken, refreshToken, response);
    }

    @PostMapping("/tasks")
    public ResponseEntity<?> saveTask(@CookieValue(name = "access_token", required = false) String accessToken,
                                      @CookieValue(name = "refresh_token", required = false) String refreshToken,
                                      HttpServletResponse response,
                                      @RequestBody String description) {
        return taskService.saveTask(accessToken, refreshToken, response, description);
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<?> deleteTask(@CookieValue(name = "access_token", required = false) String accessToken,
                                        @CookieValue(name = "refresh_token", required = false) String refreshToken,
                                        HttpServletResponse response, @PathVariable Long id) {
        return taskService.deleteTask(accessToken, refreshToken, response, id);
    }
}
