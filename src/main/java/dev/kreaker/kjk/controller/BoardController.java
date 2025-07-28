package dev.kreaker.kjk.controller;

import dev.kreaker.kjk.model.Board;
import dev.kreaker.kjk.service.BoardService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boards")
@CrossOrigin(origins = "*")
public class BoardController {
    
    private final BoardService boardService;
    
    @Autowired
    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }
    
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Board>> getAllBoards() {
        List<Board> boards = boardService.getAllBoards();
        return ResponseEntity.ok(boards);
    }
    
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Board> getBoardById(@PathVariable String id) {
        return boardService.getBoardById(id)
                .map(board -> ResponseEntity.ok(board))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createBoard(@Valid @RequestBody Board board) {
        try {
            Board createdBoard = boardService.createBoard(board);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdBoard);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to create board: " + e.getMessage()));
        }
    }
    
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateBoard(@PathVariable String id, @Valid @RequestBody Board boardDetails) {
        try {
            Board updatedBoard = boardService.updateBoard(id, boardDetails);
            return ResponseEntity.ok(updatedBoard);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to update board: " + e.getMessage()));
        }
    }
    
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteBoard(@PathVariable String id) {
        try {
            boardService.deleteBoard(id);
            return ResponseEntity.ok(new SuccessResponse("Board deleted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to delete board: " + e.getMessage()));
        }
    }
    
    @GetMapping(value = "/{id}/exists", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> boardExists(@PathVariable String id) {
        boolean exists = boardService.boardExists(id);
        return ResponseEntity.ok(exists);
    }
    
    // Response DTOs
    public static class ErrorResponse {
        private String error;
        
        public ErrorResponse(String error) {
            this.error = error;
        }
        
        public String getError() {
            return error;
        }
        
        public void setError(String error) {
            this.error = error;
        }
    }
    
    public static class SuccessResponse {
        private String message;
        
        public SuccessResponse(String message) {
            this.message = message;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
    }
}