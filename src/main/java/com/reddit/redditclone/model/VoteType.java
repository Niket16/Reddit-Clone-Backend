package com.reddit.redditclone.model;

import com.reddit.redditclone.exception.SpringCustomeException;

import java.util.Arrays;

public enum VoteType {

    UPVOTE(1),DOWNVOTE(-1);

    private int direction;

    VoteType(int direction){
    }


}
