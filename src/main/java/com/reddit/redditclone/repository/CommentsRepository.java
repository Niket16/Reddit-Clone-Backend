package com.reddit.redditclone.repository;

import com.reddit.redditclone.model.Comments;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentsRepository  extends JpaRepository <Comments , Long> {

}
