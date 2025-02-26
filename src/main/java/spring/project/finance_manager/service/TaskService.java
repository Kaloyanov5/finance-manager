package spring.project.finance_manager.service;

import jakarta.servlet.http.HttpServletResponse;
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
    private final UtilService utilService;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository, JwtUtil jwtUtil,
                       UtilService utilService) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.utilService = utilService;
    }

    public ResponseEntity<?> getTasks(String accessToken, String refreshToken, HttpServletResponse response) {
        if (accessToken == null || !jwtUtil.validateToken(accessToken)) {
            if (refreshToken == null || !jwtUtil.validateToken(refreshToken))
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired tokens. Please log in again.");

            accessToken = utilService.refreshAccessToken(refreshToken, response);
            if (accessToken == null)
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Failed to refresh access token");
        }

        String email = jwtUtil.extractEmail(accessToken);
        User user = userRepository.findByEmail(email).get();

        return ResponseEntity.ok(taskRepository.findByUser(user));
    }

    public ResponseEntity<?> saveTask(String accessToken, String refreshToken,
            HttpServletResponse response, String description) {
        if (accessToken == null || !jwtUtil.validateToken(accessToken)) {
            if (refreshToken == null || !jwtUtil.validateToken(refreshToken))
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired tokens. Please log in again.");

            accessToken = utilService.refreshAccessToken(refreshToken, response);
            if (accessToken == null)
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Failed to refresh access token");
        }

        String email = jwtUtil.extractEmail(accessToken);
        User user = userRepository.findByEmail(email).get();

        Task task = new Task(description.trim(), user);

        taskRepository.save(task);
        return ResponseEntity.ok("Task saved successfully!");
    }

    public ResponseEntity<?> deleteTask(String accessToken, String refreshToken,
                                        HttpServletResponse response, Long id) {
        if (accessToken == null || !jwtUtil.validateToken(accessToken)) {
            if (refreshToken == null || !jwtUtil.validateToken(refreshToken))
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired tokens. Please log in again.");

            accessToken = utilService.refreshAccessToken(refreshToken, response);
            if (accessToken == null)
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Failed to refresh access token");
        }

        if (taskRepository.findById(id).isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found!");

        taskRepository.deleteById(id);
        return ResponseEntity.ok("Task removed successfully!");
    }
}
