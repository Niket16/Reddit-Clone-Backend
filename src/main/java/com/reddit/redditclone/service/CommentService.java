package com.reddit.redditclone.service;

import com.reddit.redditclone.dto.CommentDto;
import com.reddit.redditclone.exception.SpringCustomeException;
import com.reddit.redditclone.mapper.CommentMapper;
import com.reddit.redditclone.model.Comments;
import com.reddit.redditclone.model.NotificationEmail;
import com.reddit.redditclone.model.Post;
import com.reddit.redditclone.model.User;
import com.reddit.redditclone.repository.CommentsRepository;
import com.reddit.redditclone.repository.PostRepository;
import com.reddit.redditclone.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
public class CommentService {
    private static final String POST_URL = "";
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final CommentMapper commentMapper;
    private final CommentsRepository commentsRepository;
    private final MailService mailService;
    private final MailContentBuilder mailContentBuilder;


    public void save(CommentDto commentDto){
        Post post = postRepository.findById(commentDto.getPostId())
                .orElseThrow(() -> new SpringCustomeException("Post id :" + commentDto.getPostId().toString()+ "not found for comment "));

        Comments comments = commentMapper.map(commentDto, post, authService.getCurrentUser());
        commentsRepository.save(comments);

        String message = mailContentBuilder.build(post.getUser().getUserName() + " posted a comment on your post." + POST_URL);
        sendCommentNotification(message, post.getUser());

    }

    private void sendCommentNotification(String message, User user) {
        mailService.sendMail(new NotificationEmail(user.getUserName() + " Commented on your post", user.getEmail(), message));
    }


    public List<CommentDto> getAllCommentsForPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new SpringCustomeException("Post not Found id : " + id.toString()));

        return commentsRepository.findByPost(post)
                .stream()
                .map(commentMapper :: mapToDto)
                .collect(toList());
    }

    public List<CommentDto> getALlCommentsForuser(String username) {
        User user = userRepository.findByUserName(username)
                .orElseThrow(()-> new SpringCustomeException("User not found with name:" + username));
        return commentsRepository.findAllByUser(user)
                .stream()
                .map(commentMapper :: mapToDto)
                .collect(toList());
    }
}
