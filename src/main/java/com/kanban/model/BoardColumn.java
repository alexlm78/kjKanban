package com.kanban.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "board_columns")
public class BoardColumn {
    
    @Id
    private String id;
    
    @NotBlank(message = "Column name is required")
    @Size(max = 100, message = "Column name must not exceed 100 characters")
    private String name;
    
    private Integer position;
    
    @Size(max = 7, message = "Color must be a valid hex color")
    private String color = "#3498db";
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private String boardId;
    
    @DBRef
    @JsonManagedReference
    private List<Task> tasks = new ArrayList<>();
    
    public BoardColumn() {
        this.createdAt = LocalDateTime.now();
    }
    
    public BoardColumn(String name, Integer position) {
        this();
        this.name = name;
        this.position = position;
    }
    
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Integer getPosition() {
        return position;
    }
    
    public void setPosition(Integer position) {
        this.position = position;
    }
    
    public String getColor() {
        return color;
    }
    
    public void setColor(String color) {
        this.color = color;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public String getBoardId() {
        return boardId;
    }
    
    public void setBoardId(String boardId) {
        this.boardId = boardId;
    }
    
    public List<Task> getTasks() {
        return tasks;
    }
    
    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
    
    public void addTask(Task task) {
        tasks.add(task);
        task.setColumnId(this.id);
    }
    
    public void removeTask(Task task) {
        tasks.remove(task);
        task.setColumnId(null);
    }
}