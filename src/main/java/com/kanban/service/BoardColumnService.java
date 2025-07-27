package com.kanban.service;

import com.kanban.model.Board;
import com.kanban.model.BoardColumn;
import com.kanban.repository.BoardColumnRepository;
import com.kanban.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BoardColumnService {
    
    private final BoardColumnRepository columnRepository;
    private final BoardRepository boardRepository;
    
    @Autowired
    public BoardColumnService(BoardColumnRepository columnRepository, BoardRepository boardRepository) {
        this.columnRepository = columnRepository;
        this.boardRepository = boardRepository;
    }
    
    @Transactional(readOnly = true)
    public List<BoardColumn> getColumnsByBoardId(Long boardId) {
        return columnRepository.findByBoardIdWithTasks(boardId);
    }
    
    @Transactional(readOnly = true)
    public Optional<BoardColumn> getColumnById(Long id) {
        return columnRepository.findByIdWithTasks(id);
    }
    
    public BoardColumn createColumn(Long boardId, BoardColumn column) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("Board not found with id: " + boardId));
        
        if (columnRepository.existsByBoardIdAndName(boardId, column.getName())) {
            throw new IllegalArgumentException("Column with name '" + column.getName() + "' already exists in this board");
        }
        
        // Set position to the end
        Integer maxPosition = columnRepository.findMaxPositionByBoardId(boardId);
        column.setPosition(maxPosition != null ? maxPosition + 1 : 0);
        column.setBoard(board);
        
        return columnRepository.save(column);
    }
    
    public BoardColumn updateColumn(Long id, BoardColumn columnDetails) {
        BoardColumn column = columnRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Column not found with id: " + id));
        
        // Check if name is being changed and if new name already exists in the same board
        if (!column.getName().equals(columnDetails.getName()) && 
            columnRepository.existsByBoardIdAndName(column.getBoard().getId(), columnDetails.getName())) {
            throw new IllegalArgumentException("Column with name '" + columnDetails.getName() + "' already exists in this board");
        }
        
        column.setName(columnDetails.getName());
        column.setColor(columnDetails.getColor());
        
        return columnRepository.save(column);
    }
    
    public void deleteColumn(Long id) {
        BoardColumn column = columnRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Column not found with id: " + id));
        
        columnRepository.delete(column);
        
        // Reorder remaining columns
        reorderColumns(column.getBoard().getId());
    }
    
    public void moveColumn(Long columnId, int newPosition) {
        BoardColumn column = columnRepository.findById(columnId)
                .orElseThrow(() -> new IllegalArgumentException("Column not found with id: " + columnId));
        
        List<BoardColumn> columns = columnRepository.findByBoardIdOrderByPosition(column.getBoard().getId());
        
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
    
    private void reorderColumns(Long boardId) {
        List<BoardColumn> columns = columnRepository.findByBoardIdOrderByPosition(boardId);
        for (int i = 0; i < columns.size(); i++) {
            columns.get(i).setPosition(i);
            columnRepository.save(columns.get(i));
        }
    }
    
    @Transactional(readOnly = true)
    public boolean columnExists(Long id) {
        return columnRepository.existsById(id);
    }
}