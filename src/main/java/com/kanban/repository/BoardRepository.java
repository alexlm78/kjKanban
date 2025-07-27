package com.kanban.repository;

import com.kanban.model.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    
    @Query("SELECT DISTINCT b FROM Board b LEFT JOIN FETCH b.columns ORDER BY b.createdAt DESC")
    List<Board> findAllWithColumnsAndTasks();
    
    @Query("SELECT DISTINCT b FROM Board b LEFT JOIN FETCH b.columns WHERE b.id = ?1")
    Optional<Board> findByIdWithColumnsAndTasks(Long id);
    
    List<Board> findAllByOrderByCreatedAtDesc();
    
    boolean existsByName(String name);
}