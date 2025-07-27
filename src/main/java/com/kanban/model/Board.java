package com.kanban.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "boards")
public class Board {
    
    @Id
    private String id;
    
    @NotBlank(message = "Board name is required")
    @Size(max = 100, message = "Board name must not exceed 100 characters")
    private String name;
    
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @DBRef
    @JsonManagedReference
    private List<BoardColumn> columns = new ArrayList<>();
    
    public Board() {
        this.createdAt = LocalDateTime.now();
    }
    
    public Board(String name, String description) {
        this();
        this.name = name;
        this.description = description;
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
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
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
    
    public List<BoardColumn> getColumns() {
        return columns;
    }
    
    public void setColumns(List<BoardColumn> columns) {
        this.columns = columns;
    }
    
    public void addColumn(BoardColumn column) {
        columns.add(column);
        column.setBoardId(this.id);
    }
    
    public void removeColumn(BoardColumn column) {
        columns.remove(column);
        column.setBoardId(null);
    }
}