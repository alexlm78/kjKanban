package com.kanban.repository;

import com.kanban.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    List<Task> findByColumnIdOrderByPosition(Long columnId);
    
    @Query("SELECT MAX(t.position) FROM Task t WHERE t.column.id = :columnId")
    Integer findMaxPositionByColumnId(@Param("columnId") Long columnId);
    
    @Query("SELECT t FROM Task t WHERE t.column.board.id = :boardId ORDER BY t.createdAt DESC")
    List<Task> findByBoardIdOrderByCreatedAtDesc(@Param("boardId") Long boardId);
    
    @Query("SELECT t FROM Task t WHERE t.dueDate BETWEEN :start AND :end ORDER BY t.dueDate ASC")
    List<Task> findTasksDueBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    @Query("SELECT t FROM Task t WHERE t.priority = :priority ORDER BY t.createdAt DESC")
    List<Task> findByPriority(@Param("priority") Task.Priority priority);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.column.board.id = :boardId")
    Long countTasksByBoardId(@Param("boardId") Long boardId);
}