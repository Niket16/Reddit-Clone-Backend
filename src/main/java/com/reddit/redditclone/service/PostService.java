package com.reddit.redditclone.service;

import com.reddit.redditclone.dto.PostRequest;
import com.reddit.redditclone.dto.PostResponse;
import com.reddit.redditclone.exception.SpringCustomeException;
import com.reddit.redditclone.mapper.PostMapper;
import com.reddit.redditclone.model.Post;
import com.reddit.redditclone.model.Subreddit;
import com.reddit.redditclone.model.User;
import com.reddit.redditclone.repository.PostRepository;
import com.reddit.redditclone.repository.SubredditRepository;
import com.reddit.redditclone.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class PostService {

    private final SubredditRepository subredditRepository;
    private final AuthService authService;
    private final PostMapper postMapper;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public void save(PostRequest postRequest) {
        Subreddit subreddit = subredditRepository.findByName(postRequest.getSubredditName())
                .orElseThrow(() -> new SpringCustomeException(postRequest.getSubredditName() + "subreddit not found"));

        postRepository.save(postMapper.map(postRequest, subreddit, authService.getCurrentUser()));

//        User curentUser = authService.getCurrentUser();
//
//        return postMapper.map(postRequest,subreddit,curentUser);
    }

    @Transactional(readOnly = true)
    public PostResponse getPost(Long id) {

        Post post = postRepository.findById(id)
                .orElseThrow(()-> new SpringCustomeException("post id : " + id.toString() + "not found"));
        return postMapper.mapToDto(post);
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getAllPosts() {
        return postRepository.findAll()
                .stream()
                .map(postMapper :: mapToDto)
                .collect(toList());
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getPostBySubredit(Long id) {
        Subreddit subreddit = subredditRepository.findById(id)
                .orElseThrow(() -> new SpringCustomeException("Subreddit Not found with id :" + id.toString()));
        List<Post> posts = postRepository.findAllBySubreddit(subreddit);
        return posts.stream().map(postMapper :: mapToDto).collect(toList());
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getPostByUsername(String userName) {
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new UsernameNotFoundException(userName));

        return postRepository.findByUser(user)
                .stream()
                .map(postMapper :: mapToDto)
                .collect(toList());
    }
}
