package com.reddit.redditclone.repository;

import com.reddit.redditclone.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository <Vote , Long> {
}
