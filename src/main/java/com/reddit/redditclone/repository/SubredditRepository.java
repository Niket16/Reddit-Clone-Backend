package com.reddit.redditclone.repository;

import com.reddit.redditclone.model.Subreddit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubredditRepository extends JpaRepository <Subreddit , Long> {
}
