package com.kanban.service;

import com.kanban.model.BoardColumn;
import com.kanban.model.Task;
import com.kanban.repository.BoardColumnRepository;
import com.kanban.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TaskService {
    
    private final TaskRepository taskRepository;
    private final BoardColumnRepository columnRepository;
    
    @Autowired
    public TaskService(TaskRepository taskRepository, BoardColumnRepository columnRepository) {
        this.taskRepository = taskRepository;
        this.columnRepository = columnRepository;
    }
    
    @Transactional(readOnly = true)
    public List<Task> getTasksByColumnId(Long columnId) {
        return taskRepository.findByColumnIdOrderByPosition(columnId);
    }
    
    @Transactional(readOnly = true)
    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public List<Task> getTasksByBoardId(Long boardId) {
        return taskRepository.findByBoardIdOrderByCreatedAtDesc(boardId);
    }
    
    public Task createTask(Long columnId, Task task) {
        BoardColumn column = columnRepository.findById(columnId)
                .orElseThrow(() -> new IllegalArgumentException("Column not found with id: " + columnId));
        
        // Set position to the end of the column
        Integer maxPosition = taskRepository.findMaxPositionByColumnId(columnId);
        task.setPosition(maxPosition != null ? maxPosition + 1 : 0);
        task.setColumn(column);
        
        return taskRepository.save(task);
    }
    
    public Task updateTask(Long id, Task taskDetails) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + id));
        
        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        task.setPriority(taskDetails.getPriority());
        task.setColor(taskDetails.getColor());
        task.setDueDate(taskDetails.getDueDate());
        
        return taskRepository.save(task);
    }
    
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + id));
        
        Long columnId = task.getColumn().getId();
        taskRepository.delete(task);
        
        // Reorder remaining tasks in the column
        reorderTasks(columnId);
    }
    
    public Task moveTask(Long taskId, Long newColumnId, int newPosition) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + taskId));
        
        BoardColumn newColumn = columnRepository.findById(newColumnId)
                .orElseThrow(() -> new IllegalArgumentException("Column not found with id: " + newColumnId));
        
        Long oldColumnId = task.getColumn().getId();
        
        // Update task's column
        task.setColumn(newColumn);
        
        // Get tasks in the new column
        List<Task> tasksInNewColumn = taskRepository.findByColumnIdOrderByPosition(newColumnId);
        
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
    
    private void reorderTasks(Long columnId) {
        List<Task> tasks = taskRepository.findByColumnIdOrderByPosition(columnId);
        for (int i = 0; i < tasks.size(); i++) {
            tasks.get(i).setPosition(i);
            taskRepository.save(tasks.get(i));
        }
    }
    
    @Transactional(readOnly = true)
    public List<Task> getTasksDueBetween(LocalDateTime start, LocalDateTime end) {
        return taskRepository.findTasksDueBetween(start, end);
    }
    
    @Transactional(readOnly = true)
    public List<Task> getTasksByPriority(Task.Priority priority) {
        return taskRepository.findByPriority(priority);
    }
    
    @Transactional(readOnly = true)
    public Long getTaskCountByBoardId(Long boardId) {
        return taskRepository.countTasksByBoardId(boardId);
    }
    
    @Transactional(readOnly = true)
    public boolean taskExists(Long id) {
        return taskRepository.existsById(id);
    }
}