package com.kanban.controller;

import com.kanban.model.BoardColumn;
import com.kanban.service.BoardColumnService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class BoardColumnController {
    
    private final BoardColumnService columnService;
    
    @Autowired
    public BoardColumnController(BoardColumnService columnService) {
        this.columnService = columnService;
    }
    
    @GetMapping("/boards/{boardId}/columns")
    public ResponseEntity<List<BoardColumn>> getColumnsByBoardId(@PathVariable String boardId) {
        List<BoardColumn> columns = columnService.getColumnsByBoardId(boardId);
        return ResponseEntity.ok(columns);
    }
    
    @GetMapping("/columns/{id}")
    public ResponseEntity<BoardColumn> getColumnById(@PathVariable String id) {
        return columnService.getColumnById(id)
                .map(column -> ResponseEntity.ok(column))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/boards/{boardId}/columns")
    public ResponseEntity<?> createColumn(@PathVariable String boardId, @Valid @RequestBody BoardColumn column) {
        try {
            BoardColumn createdColumn = columnService.createColumn(boardId, column);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdColumn);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new BoardController.ErrorResponse(e.getMessage()));
        }
    }
    
    @PutMapping("/columns/{id}")
    public ResponseEntity<?> updateColumn(@PathVariable String id, @Valid @RequestBody BoardColumn columnDetails) {
        try {
            BoardColumn updatedColumn = columnService.updateColumn(id, columnDetails);
            return ResponseEntity.ok(updatedColumn);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new BoardController.ErrorResponse(e.getMessage()));
        }
    }
    
    @DeleteMapping("/columns/{id}")
    public ResponseEntity<?> deleteColumn(@PathVariable String id) {
        try {
            columnService.deleteColumn(id);
            return ResponseEntity.ok(new BoardController.SuccessResponse("Column deleted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new BoardController.ErrorResponse(e.getMessage()));
        }
    }
    
    @PutMapping("/columns/{id}/move")
    public ResponseEntity<?> moveColumn(@PathVariable String id, @RequestBody MoveColumnRequest request) {
        try {
            columnService.moveColumn(id, request.getNewPosition());
            return ResponseEntity.ok(new BoardController.SuccessResponse("Column moved successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new BoardController.ErrorResponse(e.getMessage()));
        }
    }
    
    // Request DTOs
    public static class MoveColumnRequest {
        private int newPosition;
        
        public int getNewPosition() {
            return newPosition;
        }
        
        public void setNewPosition(int newPosition) {
            this.newPosition = newPosition;
        }
    }
}