package com.reddit.redditclone.controller;

import com.reddit.redditclone.dto.CommentDto;
import com.reddit.redditclone.service.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping( "/api/comments")
@AllArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<Void> createComment(@RequestBody CommentDto commentDto){
        commentService.save(commentDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/by-post/{id}")
    public ResponseEntity<List<CommentDto>> getAllCommentsForPost(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK)
                .body(commentService.getAllCommentsForPost(id));
    }

    @GetMapping("/by-user/{username}")
    public ResponseEntity<List<CommentDto>> getALlCommentsForuser(@PathVariable String username){
        return ResponseEntity.status(HttpStatus.OK)
                .body(commentService.getALlCommentsForuser(username));
    }
}
