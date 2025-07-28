package dev.kreaker.kjk.service;

import dev.kreaker.kjk.model.BoardColumn;
import dev.kreaker.kjk.model.Task;
import dev.kreaker.kjk.repository.BoardColumnRepository;
import dev.kreaker.kjk.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    
    private final TaskRepository taskRepository;
    private final BoardColumnRepository columnRepository;
    
    @Autowired
    public TaskService(TaskRepository taskRepository, BoardColumnRepository columnRepository) {
        this.taskRepository = taskRepository;
        this.columnRepository = columnRepository;
    }
    
    public List<Task> getTasksByColumnId(String columnId) {
        return taskRepository.findByColumnId(columnId);
    }
    
    public Optional<Task> getTaskById(String id) {
        return taskRepository.findById(id);
    }
    
    public List<Task> getTasksByBoardId(String boardId) {
        return taskRepository.findByColumnId(boardId);
    }
    
    public Task createTask(String columnId, Task task) {
        BoardColumn column = columnRepository.findById(columnId)
                .orElseThrow(() -> new IllegalArgumentException("Column not found with id: " + columnId));
        
        // Set position to the end of the column
        List<Task> existingTasks = taskRepository.findByColumnId(columnId);
        task.setPosition(existingTasks.size());
        task.setColumnId(columnId);
        
        return taskRepository.save(task);
    }
    
    public Task updateTask(String id, Task taskDetails) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + id));
        
        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        task.setPriority(taskDetails.getPriority());
        task.setColor(taskDetails.getColor());
        task.setDueDate(taskDetails.getDueDate());
        
        return taskRepository.save(task);
    }
    
    public void deleteTask(String id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + id));
        
        String columnId = task.getColumnId();
        taskRepository.delete(task);
        
        // Reorder remaining tasks in the column
        reorderTasks(columnId);
    }
    
    public Task moveTask(String taskId, String newColumnId, int newPosition) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + taskId));
        
        BoardColumn newColumn = columnRepository.findById(newColumnId)
                .orElseThrow(() -> new IllegalArgumentException("Column not found with id: " + newColumnId));
        
        String oldColumnId = task.getColumnId();
        
        // Update task's column
        task.setColumnId(newColumnId);
        
        // Get tasks in the new column
        List<Task> tasksInNewColumn = taskRepository.findByColumnId(newColumnId);
        
        // Remove task if it was already in this column
        tasksInNewColumn.removeIf(t -> t.getId().equals(taskId));
        
        // Insert at new position
        if (newPosition >= tasksInNewColumn.size()) {
            tasksInNewColumn.add(task);
        } else {
            tasksInNewColumn.add(newPosition, task);
        }
        
        // Update positions in new column
        for (int i = 0; i < tasksInNewColumn.size(); i++) {
            tasksInNewColumn.get(i).setPosition(i);
            taskRepository.save(tasksInNewColumn.get(i));
        }
        
        // Reorder tasks in old column if it's different
        if (!oldColumnId.equals(newColumnId)) {
            reorderTasks(oldColumnId);
        }
        
        return task;
    }
    
    private void reorderTasks(String columnId) {
        List<Task> tasks = taskRepository.findByColumnId(columnId);
        for (int i = 0; i < tasks.size(); i++) {
            tasks.get(i).setPosition(i);
            taskRepository.save(tasks.get(i));
        }
    }
    
    public List<Task> getTasksDueBetween(LocalDateTime start, LocalDateTime end) {
        return taskRepository.findByDueDateBetween(start, end);
    }
    
    public List<Task> getTasksByPriority(Task.Priority priority) {
        return taskRepository.findByPriority(priority);
    }
    
    public Long getTaskCountByBoardId(String boardId) {
        return (long) taskRepository.findByColumnId(boardId).size();
    }
    
    public boolean taskExists(String id) {
        return taskRepository.existsById(id);
    }
}