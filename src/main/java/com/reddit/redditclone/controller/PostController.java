package com.reddit.redditclone.controller;

import com.reddit.redditclone.dto.PostRequest;
import com.reddit.redditclone.dto.PostResponse;
import com.reddit.redditclone.service.PostService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping("/api/posts")
@AllArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity createPost(@RequestBody PostRequest postRequest){
        postService.save(postRequest);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @GetMapping("/")
    public ResponseEntity<List<PostResponse>> getAllPosts(){
        return status(HttpStatus.OK).body(postService.getAllPosts());

    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long id){
        return status(HttpStatus.OK).body(postService.getPost(id));
    }

    @GetMapping("/by-subreddit/{id}")
    public ResponseEntity<List<PostResponse>> getPostsbySubreddit(@PathVariable Long id){
        return status(HttpStatus.OK).body(postService.getPostBySubredit(id));

    }

    @GetMapping("/by-user/{name}")
    public ResponseEntity<List<PostResponse>> getPostsbyUsername(@PathVariable  String name){
        return status(HttpStatus.OK).body(postService.getPostByUsername(name));
    }

}
