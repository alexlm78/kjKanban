package dev.kreaker.kjk.repository;

import dev.kreaker.kjk.model.Task;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends MongoRepository<Task, String> {
    
    List<Task> findByColumnId(String columnId);
    
    List<Task> findByColumnIdOrderByPosition(String columnId);
    
    List<Task> findByDueDateBetween(LocalDateTime start, LocalDateTime end);
    
    List<Task> findByPriority(Task.Priority priority);
    
    List<Task> findByPriorityOrderByCreatedAtDesc(Task.Priority priority);
    
    void deleteByColumnId(String columnId);
}