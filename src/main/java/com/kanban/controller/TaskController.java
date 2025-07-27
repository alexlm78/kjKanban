package com.kanban.controller;

import com.kanban.model.Task;
import com.kanban.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class TaskController {
    
    private final TaskService taskService;
    
    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }
    
    @GetMapping("/columns/{columnId}/tasks")
    public ResponseEntity<List<Task>> getTasksByColumnId(@PathVariable String columnId) {
        List<Task> tasks = taskService.getTasksByColumnId(columnId);
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/boards/{boardId}/tasks")
    public ResponseEntity<List<Task>> getTasksByBoardId(@PathVariable String boardId) {
        List<Task> tasks = taskService.getTasksByBoardId(boardId);
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/tasks/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable String id) {
        return taskService.getTaskById(id)
                .map(task -> ResponseEntity.ok(task))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/columns/{columnId}/tasks")
    public ResponseEntity<?> createTask(@PathVariable String columnId, @Valid @RequestBody Task task) {
        try {
            Task createdTask = taskService.createTask(columnId, task);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new BoardController.ErrorResponse(e.getMessage()));
        }
    }
    
    @PutMapping("/tasks/{id}")
    public ResponseEntity<?> updateTask(@PathVariable String id, @Valid @RequestBody Task taskDetails) {
        try {
            Task updatedTask = taskService.updateTask(id, taskDetails);
            return ResponseEntity.ok(updatedTask);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new BoardController.ErrorResponse(e.getMessage()));
        }
    }
    
    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable String id) {
        try {
            taskService.deleteTask(id);
            return ResponseEntity.ok(new BoardController.SuccessResponse("Task deleted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new BoardController.ErrorResponse(e.getMessage()));
        }
    }
    
    @PutMapping("/tasks/{id}/move")
    public ResponseEntity<?> moveTask(@PathVariable String id, @RequestBody MoveTaskRequest request) {
        try {
            Task movedTask = taskService.moveTask(id, request.getNewColumnId(), request.getNewPosition());
            return ResponseEntity.ok(movedTask);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new BoardController.ErrorResponse(e.getMessage()));
        }
    }
    
    @GetMapping("/tasks/priority/{priority}")
    public ResponseEntity<List<Task>> getTasksByPriority(@PathVariable Task.Priority priority) {
        List<Task> tasks = taskService.getTasksByPriority(priority);
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/tasks/due")
    public ResponseEntity<List<Task>> getTasksDueBetween(
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end) {
        List<Task> tasks = taskService.getTasksDueBetween(start, end);
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/boards/{boardId}/tasks/count")
    public ResponseEntity<Long> getTaskCountByBoardId(@PathVariable String boardId) {
        Long count = taskService.getTaskCountByBoardId(boardId);
        return ResponseEntity.ok(count);
    }
    
    // Request DTOs
    public static class MoveTaskRequest {
        private String newColumnId;
        private int newPosition;
        
        public String getNewColumnId() {
            return newColumnId;
        }
        
        public void setNewColumnId(String newColumnId) {
            this.newColumnId = newColumnId;
        }
        
        public int getNewPosition() {
            return newPosition;
        }
        
        public void setNewPosition(int newPosition) {
            this.newPosition = newPosition;
        }
    }
}