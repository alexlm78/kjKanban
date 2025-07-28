package dev.kreaker.kjk.repository;

import dev.kreaker.kjk.model.BoardColumn;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardColumnRepository extends MongoRepository<BoardColumn, String> {
    
    List<BoardColumn> findByBoardIdOrderByPosition(String boardId);
    
    Optional<BoardColumn> findById(String id);
    
    @Query("{ 'boardId': ?0 }")
    List<BoardColumn> findByBoardId(String boardId);
    
    boolean existsByBoardIdAndName(String boardId, String name);
    
    void deleteByBoardId(String boardId);
}