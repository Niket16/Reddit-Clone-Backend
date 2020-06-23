package com.reddit.redditclone.repository;

import com.reddit.redditclone.model.Post;
import com.reddit.redditclone.model.Subreddit;
import com.reddit.redditclone.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post , Long> {
    List<Post> findByUser(User user);

    List<Post> findAllBySubreddit(Subreddit subreddit);
}
