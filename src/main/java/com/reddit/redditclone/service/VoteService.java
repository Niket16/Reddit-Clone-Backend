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

import java.util.List;
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

        int addSub;
        if(voteByPostAndUser.toString().equals("Optional.empty")){
            //log.info("Null Done" );
            addSub = 1;
        }else {
            addSub = 2;
        }

        if(voteByPostAndUser.isPresent() && voteByPostAndUser.get().getVoteType().equals(voteDto.getVoteType())){
            throw new SpringCustomeException("You have already " + voteDto.getVoteType() + "d this post");
        }
        if (UPVOTE.equals(voteDto.getVoteType())) {
            //log.info("addSub" + addSub);
            post.setVoteCount(post.getVoteCount() + addSub );
        } else {
            //log.info("addSub" + addSub);
            post.setVoteCount(post.getVoteCount() - addSub);
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
