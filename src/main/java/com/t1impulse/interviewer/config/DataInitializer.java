package com.t1impulse.interviewer.config;

import com.t1impulse.interviewer.entity.AlgorithmTask;
import com.t1impulse.interviewer.entity.Difficulty;
import com.t1impulse.interviewer.entity.Role;
import com.t1impulse.interviewer.entity.TaskTest;
import com.t1impulse.interviewer.entity.User;
import com.t1impulse.interviewer.repository.AlgorithmTaskRepository;
import com.t1impulse.interviewer.repository.TaskTestRepository;
import com.t1impulse.interviewer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AlgorithmTaskRepository algorithmTaskRepository;
    private final TaskTestRepository taskTestRepository;

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        initializeAdminUser();
        initializeTasks();
    }

    private void initializeAdminUser() {
        if (!userRepository.existsByUsername(adminUsername)) {
            User admin = User.builder()
                    .username(adminUsername)
                    .password(passwordEncoder.encode(adminPassword))
                    .role(Role.ADMIN)
                    .build();
            
            userRepository.save(admin);
            log.info("Admin user created: username={}", adminUsername);
        } else {
            log.info("Admin user already exists: username={}", adminUsername);
        }
    }

    private void initializeTasks() {
        if (algorithmTaskRepository.count() > 0) {
            log.info("Tasks already loaded, skipping initialization");
            return;
        }

        try {
            // Load tasks
            Map<Long, AlgorithmTask> tasksMap = loadTasksFromCSV();
            
            // Load tests
            loadTestsFromCSV(tasksMap);
            
            log.info("Successfully loaded {} tasks with their tests", tasksMap.size());
        } catch (Exception e) {
            log.error("Error loading tasks from CSV", e);
        }
    }

    private Map<Long, AlgorithmTask> loadTasksFromCSV() throws Exception {
        ClassPathResource resource = new ClassPathResource("tasks.csv");
        Map<Long, AlgorithmTask> tasksMap = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            
            String line = reader.readLine(); // Read first line
            // Check if it's sep= line
            if (line != null && line.startsWith("sep=")) {
                line = reader.readLine(); // Read header after sep=
            }
            
            // Skip header line (id;title_ru;description_ru;difficulty)
            if (line != null && line.contains("id;title_ru")) {
                line = reader.readLine(); // Read first data line
            }
            
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                
                // Parse CSV line with quoted fields
                // Format: id;title_ru;"description_ru";difficulty
                String[] parts = parseCSVLine(line);
                if (parts.length < 4) {
                    continue;
                }
                
                try {
                    Long id = Long.parseLong(parts[0].trim());
                    String titleRu = parts[1].trim();
                    // Remove surrounding quotes from description if present
                    String descriptionRu = parts[2].trim();
                    if (descriptionRu.startsWith("\"") && descriptionRu.endsWith("\"")) {
                        descriptionRu = descriptionRu.substring(1, descriptionRu.length() - 1);
                    }
                    String difficultyStr = parts[3].trim();
                    
                    Difficulty difficulty;
                    try {
                        difficulty = Difficulty.valueOf(difficultyStr);
                    } catch (IllegalArgumentException e) {
                        log.warn("Unknown difficulty: {}, skipping task {}", difficultyStr, id);
                        continue;
                    }
                    
                    AlgorithmTask task = AlgorithmTask.builder()
                            .id(id)
                            .titleRu(titleRu)
                            .descriptionRu(descriptionRu)
                            .difficulty(difficulty)
                            .build();
                    
                    tasksMap.put(id, task);
                } catch (NumberFormatException e) {
                    log.warn("Invalid task ID in line: {}", line);
                }
            }
        }
        
        // Save all tasks
        algorithmTaskRepository.saveAll(tasksMap.values());
        log.info("Loaded {} tasks", tasksMap.size());
        
        return tasksMap;
    }

    private void loadTestsFromCSV(Map<Long, AlgorithmTask> tasksMap) throws Exception {
        ClassPathResource resource = new ClassPathResource("tests.csv");
        List<TaskTest> tests = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            
            String line = reader.readLine(); // Read header
            // Skip header line (id;test_input;test_output)
            if (line != null && line.contains("id;test_input")) {
                line = reader.readLine(); // Read first data line
            }
            
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                
                String[] parts = line.split(";", -1);
                if (parts.length < 3) {
                    continue;
                }
                
                try {
                    Long taskId = Long.parseLong(parts[0].trim());
                    String testInput = parts[1].trim();
                    String testOutput = parts[2].trim();
                    
                    AlgorithmTask task = tasksMap.get(taskId);
                    if (task == null) {
                        log.warn("Task with ID {} not found, skipping test", taskId);
                        continue;
                    }
                    
                    TaskTest test = TaskTest.builder()
                            .task(task)
                            .testInput(testInput)
                            .testOutput(testOutput)
                            .build();
                    
                    tests.add(test);
                } catch (NumberFormatException e) {
                    log.warn("Invalid task ID in test line: {}", line);
                }
            }
        }
        
        // Save all tests
        taskTestRepository.saveAll(tests);
        log.info("Loaded {} tests", tests.size());
    }

    /**
     * Parse CSV line handling quoted fields.
     * Simple implementation: split by semicolon, but handle quoted fields.
     */
    private String[] parseCSVLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean inQuotes = false;
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                inQuotes = !inQuotes;
                currentField.append(c);
            } else if (c == ';' && !inQuotes) {
                fields.add(currentField.toString());
                currentField = new StringBuilder();
            } else {
                currentField.append(c);
            }
        }
        
        // Add last field
        if (currentField.length() > 0 || line.endsWith(";")) {
            fields.add(currentField.toString());
        }
        
        return fields.toArray(new String[0]);
    }
}
