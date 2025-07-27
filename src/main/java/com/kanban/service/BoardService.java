package com.kanban.service;

import com.kanban.model.Board;
import com.kanban.model.BoardColumn;
import com.kanban.repository.BoardRepository;
import com.kanban.repository.BoardColumnRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BoardService {
    
    private final BoardRepository boardRepository;
    private final BoardColumnRepository columnRepository;
    
    @Autowired
    public BoardService(BoardRepository boardRepository, BoardColumnRepository columnRepository) {
        this.boardRepository = boardRepository;
        this.columnRepository = columnRepository;
    }
    
    @Transactional(readOnly = true)
    public List<Board> getAllBoards() {
        return boardRepository.findAllByOrderByCreatedAtDesc();
    }
    
    @Transactional(readOnly = true)
    public Optional<Board> getBoardById(Long id) {
        return boardRepository.findByIdWithColumnsAndTasks(id);
    }
    
    public Board createBoard(Board board) {
        if (boardRepository.existsByName(board.getName())) {
            throw new IllegalArgumentException("Board with name '" + board.getName() + "' already exists");
        }
        
        Board savedBoard = boardRepository.save(board);
        
        // Create default columns
        createDefaultColumns(savedBoard);
        
        return savedBoard;
    }
    
    public Board updateBoard(Long id, Board boardDetails) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Board not found with id: " + id));
        
        // Check if name is being changed and if new name already exists
        if (!board.getName().equals(boardDetails.getName()) && 
            boardRepository.existsByName(boardDetails.getName())) {
            throw new IllegalArgumentException("Board with name '" + boardDetails.getName() + "' already exists");
        }
        
        board.setName(boardDetails.getName());
        board.setDescription(boardDetails.getDescription());
        
        return boardRepository.save(board);
    }
    
    public void deleteBoard(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Board not found with id: " + id));
        
        boardRepository.delete(board);
    }
    
    private void createDefaultColumns(Board board) {
        String[] defaultColumnNames = {"To Do", "In Progress", "Done"};
        String[] defaultColors = {"#e74c3c", "#f39c12", "#27ae60"};
        
        for (int i = 0; i < defaultColumnNames.length; i++) {
            BoardColumn column = new BoardColumn(defaultColumnNames[i], i);
            column.setColor(defaultColors[i]);
            column.setBoard(board);
            columnRepository.save(column);
        }
    }
    
    @Transactional(readOnly = true)
    public boolean boardExists(Long id) {
        return boardRepository.existsById(id);
    }
    
    @Transactional(readOnly = true)
    public boolean boardNameExists(String name) {
        return boardRepository.existsByName(name);
    }
}