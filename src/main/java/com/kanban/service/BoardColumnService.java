package com.kanban.service;

import com.kanban.model.Board;
import com.kanban.model.BoardColumn;
import com.kanban.repository.BoardColumnRepository;
import com.kanban.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class BoardColumnService {
    
    private final BoardColumnRepository columnRepository;
    private final BoardRepository boardRepository;
    
    @Autowired
    public BoardColumnService(BoardColumnRepository columnRepository, BoardRepository boardRepository) {
        this.columnRepository = columnRepository;
        this.boardRepository = boardRepository;
    }
    
    public List<BoardColumn> getColumnsByBoardId(String boardId) {
        return columnRepository.findByBoardId(boardId);
    }
    
    public Optional<BoardColumn> getColumnById(String id) {
        return columnRepository.findById(id);
    }
    
    public BoardColumn createColumn(String boardId, BoardColumn column) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("Board not found with id: " + boardId));
        
        if (columnRepository.existsByBoardIdAndName(boardId, column.getName())) {
            throw new IllegalArgumentException("Column with name '" + column.getName() + "' already exists in this board");
        }
        
        // Set position to the end
        List<BoardColumn> existingColumns = columnRepository.findByBoardId(boardId);
        column.setPosition(existingColumns.size());
        column.setBoardId(boardId);
        
        return columnRepository.save(column);
    }
    
    public BoardColumn updateColumn(String id, BoardColumn columnDetails) {
        BoardColumn column = columnRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Column not found with id: " + id));
        
        // Check if name is being changed and if new name already exists in the same board
        if (!column.getName().equals(columnDetails.getName()) && 
            columnRepository.existsByBoardIdAndName(column.getBoardId(), columnDetails.getName())) {
            throw new IllegalArgumentException("Column with name '" + columnDetails.getName() + "' already exists in this board");
        }
        
        column.setName(columnDetails.getName());
        column.setColor(columnDetails.getColor());
        
        return columnRepository.save(column);
    }
    
    public void deleteColumn(String id) {
        BoardColumn column = columnRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Column not found with id: " + id));
        
        columnRepository.delete(column);
        
        // Reorder remaining columns
        reorderColumns(column.getBoardId());
    }
    
    public void moveColumn(String columnId, int newPosition) {
        BoardColumn column = columnRepository.findById(columnId)
                .orElseThrow(() -> new IllegalArgumentException("Column not found with id: " + columnId));
        
        List<BoardColumn> columns = columnRepository.findByBoardId(column.getBoardId());
        
        // Remove column from current position
        columns.remove(column);
        
        // Insert at new position
        if (newPosition >= columns.size()) {
            columns.add(column);
        } else {
            columns.add(newPosition, column);
        }
        
        // Update positions
        for (int i = 0; i < columns.size(); i++) {
            columns.get(i).setPosition(i);
            columnRepository.save(columns.get(i));
        }
    }
    
    private void reorderColumns(String boardId) {
        List<BoardColumn> columns = columnRepository.findByBoardId(boardId);
        for (int i = 0; i < columns.size(); i++) {
            columns.get(i).setPosition(i);
            columnRepository.save(columns.get(i));
        }
    }
    
    public boolean columnExists(String id) {
        return columnRepository.existsById(id);
    }
}