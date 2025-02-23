package spring.project.finance_manager.service;

import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import spring.project.finance_manager.component.JwtUtil;
import spring.project.finance_manager.entity.Task;
import spring.project.finance_manager.entity.User;
import spring.project.finance_manager.repository.TaskRepository;
import spring.project.finance_manager.repository.UserRepository;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository, JwtUtil jwtUtil) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public ResponseEntity<?> getTasks(String token) {
        String email;
        try {
            email = jwtUtil.extractEmail(token.substring(7));
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token!");
        }
        User user = userRepository.findByEmail(email).get();
        return ResponseEntity.ok(taskRepository.findByUser(user));
    }

    public ResponseEntity<?> saveTask(String token, String description) {
        String email;
        try {
            email = jwtUtil.extractEmail(token.substring(7));
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token!");
        }
        User user = userRepository.findByEmail(email).get();

        Task task = new Task(description.trim(), user);

        taskRepository.save(task);
        return ResponseEntity.ok("Task saved successfully!");
    }

    public ResponseEntity<?> deleteTask(String token, Long id) {
        try {
            jwtUtil.validateToken(token.substring(7));
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token!");
        }

        if (!taskRepository.findById(id).isPresent())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found!");

        taskRepository.deleteById(id);
        return ResponseEntity.ok("Task removed successfully!");
    }
}
