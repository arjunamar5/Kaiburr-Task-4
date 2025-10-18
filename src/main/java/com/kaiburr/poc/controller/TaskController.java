package com.kaiburr.poc.controller;

import com.kaiburr.poc.model.Task;
import com.kaiburr.poc.model.TaskExecution;
import com.kaiburr.poc.repository.TaskRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskRepository taskRepository;

    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // GET all the tasks
    @GetMapping
    public ResponseEntity<?> getTasks(@RequestParam(required = false) String id) {
        if (id == null) {
            List<Task> tasks = taskRepository.findAll();
            if (tasks.isEmpty()) {
                return ResponseEntity.status(404).body("No tasks found-404 error");
            }
            return ResponseEntity.ok(tasks);
        } else {
            Optional<Task> task = taskRepository.findById(id);
            if (task.isPresent()) {
                return ResponseEntity.ok(task.get());
            } else {
                return ResponseEntity.status(404).body("Task not found-404 error");
            }
        }
    }

    // GET tasks by name
    @GetMapping("/search")
    public ResponseEntity<?> findTasksByName(@RequestParam String name) {
        List<Task> tasks = taskRepository.findByNameContainingIgnoreCase(name);
        if (tasks.isEmpty()) {
            return ResponseEntity.status(404).body("No tasks found with name containing: " + name);
        } else {
            return ResponseEntity.ok(tasks);
        }
    }

    // PUT a task (Creating a new Task)
    @PutMapping
    public ResponseEntity<?> saveTask(@RequestBody Task task) {
        String cmd = task.getCommand().toLowerCase();

        // Validation of shell commands and preventing unsafe commands
        if (cmd.contains("rm") || cmd.contains("del") || cmd.contains("shutdown") || cmd.contains("reboot")) {
            return ResponseEntity.badRequest().body("Unsafe commands");
        }

        Task saved = taskRepository.save(task);
        return ResponseEntity.ok(saved);
    }

    // DELETE a task by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable String id) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
            return ResponseEntity.ok("Task deleted successfully!");
        } else {
            return ResponseEntity.status(404).body("Task not found");
        }
    }

    // PUT TaskExecution (To run a task)
    // Runs the command and output is stored in TaskExecutions
    @PutMapping("/run/{id}")
    public ResponseEntity<?> runTask(@PathVariable String id) {
        Optional<Task> optionalTask = taskRepository.findById(id);
        if (!optionalTask.isPresent()) {
            return ResponseEntity.status(404).body("Task not found");
        }

        Task task = optionalTask.get();
        Date startTime = new Date();
        StringBuilder output = new StringBuilder();

        try {
            // Execute the command in the shell
            Process process = Runtime.getRuntime().exec(task.getCommand());
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            process.waitFor();
        } catch (Exception e) {
            output.append("Error executing command: ").append(e.getMessage());
        }

        Date endTime = new Date();
        TaskExecution exec = new TaskExecution(startTime, endTime, output.toString().trim());
        task.addTaskExecution(exec);

        taskRepository.save(task); // Updating in the mongodb database(taskdb here)
        return ResponseEntity.ok(exec);
    }
}



