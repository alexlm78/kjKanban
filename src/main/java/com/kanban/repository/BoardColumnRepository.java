package com.kanban.repository;

import com.kanban.model.BoardColumn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardColumnRepository extends JpaRepository<BoardColumn, Long> {
    
    List<BoardColumn> findByBoardIdOrderByPosition(Long boardId);
    
    @Query("SELECT c FROM BoardColumn c LEFT JOIN FETCH c.tasks WHERE c.board.id = :boardId ORDER BY c.position")
    List<BoardColumn> findByBoardIdWithTasks(@Param("boardId") Long boardId);
    
    @Query("SELECT c FROM BoardColumn c LEFT JOIN FETCH c.tasks WHERE c.id = :id")
    Optional<BoardColumn> findByIdWithTasks(@Param("id") Long id);
    
    @Query("SELECT MAX(c.position) FROM BoardColumn c WHERE c.board.id = :boardId")
    Integer findMaxPositionByBoardId(@Param("boardId") Long boardId);
    
    boolean existsByBoardIdAndName(Long boardId, String name);
}