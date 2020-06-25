package com.reddit.redditclone.service;

import com.reddit.redditclone.dto.VoteDto;
import com.reddit.redditclone.exception.SpringCustomeException;
import com.reddit.redditclone.model.Post;
import com.reddit.redditclone.model.Vote;
import com.reddit.redditclone.model.VoteType;
import com.reddit.redditclone.repository.PostRepository;
import com.reddit.redditclone.repository.VoteRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static com.reddit.redditclone.model.VoteType.UPVOTE;

import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class VoteService {

    private final PostRepository postRepository;
    private final VoteRepository voteRepository;
    private final AuthService authService;

    @Transactional
    public void vote(VoteDto voteDto) {
        Post post = postRepository.findById(voteDto.getPostId())
                .orElseThrow(() -> new SpringCustomeException("Post not found with id:" + voteDto.getPostId() + " for vote "));

        Optional<Vote> voteByPostAndUser = voteRepository.findTopByPostAndUserOrderByVoteIDDesc(post, authService.getCurrentUser());

        if(voteByPostAndUser.isPresent() && voteByPostAndUser.get().getVoteType().equals(voteDto.getVoteType())){
            throw new SpringCustomeException("You have already " + voteDto.getVoteType() + "d this post");
        }
        if (UPVOTE.equals(voteDto.getVoteType())) {
            post.setVoteCount(post.getVoteCount() + 1);
        } else {
            log.info("logData"+String.valueOf(post.getVoteCount()));
            post.setVoteCount(post.getVoteCount() - 1);
        }
        voteRepository.save(mapToVote(voteDto, post));
        postRepository.save(post);
    }

    private Vote mapToVote(VoteDto voteDto, Post post) {
        return Vote.builder()
                .voteType(voteDto.getVoteType())
                .post(post)
                .user(authService.getCurrentUser())
                .build();
    }

}
