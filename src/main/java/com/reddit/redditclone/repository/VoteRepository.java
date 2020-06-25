package com.reddit.redditclone.repository;

import com.reddit.redditclone.model.Post;
import com.reddit.redditclone.model.User;
import com.reddit.redditclone.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoteRepository extends JpaRepository <Vote , Long> {
    Optional<Vote> findTopByPostAndUserOrderByVoteIDDesc(Post post, User currentUser);
}
