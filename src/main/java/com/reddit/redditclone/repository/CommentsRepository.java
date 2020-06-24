package com.reddit.redditclone.repository;

import com.reddit.redditclone.model.Comments;
import com.reddit.redditclone.model.Post;
import com.reddit.redditclone.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentsRepository  extends JpaRepository <Comments , Long> {
    List<Comments> findByPost(Post post);

    List<Comments> findAllByUser(User user);
}
