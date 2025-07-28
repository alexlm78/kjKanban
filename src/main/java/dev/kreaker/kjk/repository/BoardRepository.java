package dev.kreaker.kjk.repository;

import dev.kreaker.kjk.model.Board;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRepository extends MongoRepository<Board, String> {
    
    List<Board> findAllByOrderByCreatedAtDesc();
    
    boolean existsByName(String name);
    
    Optional<Board> findById(String id);
}