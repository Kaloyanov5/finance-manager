package spring.project.finance_manager.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.project.finance_manager.request.TransactionRequest;
import spring.project.finance_manager.service.TaskService;

@RestController
@RequestMapping("/api")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/tasks")
    public ResponseEntity<?> getTasks(@RequestHeader("Authorization") String token) {
        return taskService.getTasks(token);
    }

    @PostMapping("/tasks")
    public ResponseEntity<?> saveTask(@RequestHeader("Authorization") String token,
                                             @RequestBody String description) {
        return taskService.saveTask(token, description);
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<?> deleteTask(@RequestHeader("Authorization") String token,
                                               @PathVariable Long id) {
        return taskService.deleteTask(token, id);
    }
}
