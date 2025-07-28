package dev.kreaker.kjk.controller;

import dev.kreaker.kjk.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class WebController {
    
    private final BoardService boardService;
    
    @Autowired
    public WebController(BoardService boardService) {
        this.boardService = boardService;
    }
    
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("boards", boardService.getAllBoards());
        return "index";
    }
    
    @GetMapping("/boards")
    public String boards(Model model) {
        model.addAttribute("boards", boardService.getAllBoards());
        return "boards";
    }
    
    @GetMapping("/boards/{id}")
    public String boardDetail(@PathVariable String id, Model model) {
        return boardService.getBoardById(id)
                .map(board -> {
                    model.addAttribute("board", board);
                    return "board-detail";
                })
                .orElse("redirect:/boards");
    }
    
    @GetMapping("/boards/new")
    public String newBoard() {
        return "board-form";
    }
    
    @GetMapping("/boards/{id}/edit")
    public String editBoard(@PathVariable String id, Model model) {
        return boardService.getBoardById(id)
                .map(board -> {
                    model.addAttribute("board", board);
                    return "board-form";
                })
                .orElse("redirect:/boards");
    }
}