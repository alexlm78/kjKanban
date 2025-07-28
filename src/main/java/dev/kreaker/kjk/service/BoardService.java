package dev.kreaker.kjk.service;

import dev.kreaker.kjk.model.Board;
import dev.kreaker.kjk.model.BoardColumn;
import dev.kreaker.kjk.repository.BoardRepository;
import dev.kreaker.kjk.repository.BoardColumnRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class BoardService {
    
    private final BoardRepository boardRepository;
    private final BoardColumnRepository columnRepository;
    
    @Autowired
    public BoardService(BoardRepository boardRepository, BoardColumnRepository columnRepository) {
        this.boardRepository = boardRepository;
        this.columnRepository = columnRepository;
    }
    
    public List<Board> getAllBoards() {
        return boardRepository.findAllByOrderByCreatedAtDesc();
    }
    
    public Optional<Board> getBoardById(String id) {
        return boardRepository.findById(id);
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
    
    public Board updateBoard(String id, Board boardDetails) {
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
    
    public void deleteBoard(String id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Board not found with id: " + id));
        
        // Delete associated columns and tasks
        columnRepository.deleteByBoardId(id);
        boardRepository.delete(board);
    }
    
    private void createDefaultColumns(Board board) {
        String[] defaultColumnNames = {"To Do", "In Progress", "Done"};
        String[] defaultColors = {"#e74c3c", "#f39c12", "#27ae60"};
        
        for (int i = 0; i < defaultColumnNames.length; i++) {
            BoardColumn column = new BoardColumn(defaultColumnNames[i], i);
            column.setColor(defaultColors[i]);
            column.setBoardId(board.getId());
            columnRepository.save(column);
        }
    }
    
    public boolean boardExists(String id) {
        return boardRepository.existsById(id);
    }
    
    public boolean boardNameExists(String name) {
        return boardRepository.existsByName(name);
    }
}