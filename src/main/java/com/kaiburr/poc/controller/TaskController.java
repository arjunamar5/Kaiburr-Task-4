package com.kaiburr.poc.controller;

import com.kaiburr.poc.model.Task;
import com.kaiburr.poc.model.TaskExecution;
import com.kaiburr.poc.repository.TaskRepository;
import com.kaiburr.poc.service.KubernetesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskRepository taskRepository;

    @Autowired
    private KubernetesService kubernetesService;

    @Value("${KUBERNETES_NAMESPACE:default}")
    private String namespace;

    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @GetMapping("/test")
    public String checkHealth(){
        System.out.println("The server is listening");
        return "The Server is active";
    }
    @GetMapping("/check")
    public String getTasks() {
        return "Tasks endpoint working!";
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

    @PostMapping("/{id}/executions")
    public ResponseEntity<TaskExecution> addTaskExecutionsk8(
            @PathVariable String id,
            @RequestBody CommandRequest commandRequest){
        String command = commandRequest.getCommand();
        System.out.println("Adding TaskExecution for Task ID: " + id + " with command: " + command);
        Optional<Task> optionalTask = taskRepository.findById(id);
        if (optionalTask.isPresent()) {
            Task task = optionalTask.get();

            // Create Kubernetes pod to execute command
            String executionResult = kubernetesService.createCommandPod(command, namespace);
            Date start = Date.from(Instant.now());
            Date end = Date.from(Instant.now());


            TaskExecution execution = new TaskExecution();
            execution.setStartTime(start);
            execution.setEndTime(end); // Simulate end time
            execution.setOutput(executionResult);

            task.getTaskExecutions().add(execution);
            taskRepository.save(task);
            return ResponseEntity.ok(execution);
        }
        throw new RuntimeException("Task not found with id: " + id);
    }
    public static class CommandRequest {
        private String command;

        public String getCommand() {
            return command;
        }

        public void setCommand(String command) {
            this.command = command;
        }
    }

}



