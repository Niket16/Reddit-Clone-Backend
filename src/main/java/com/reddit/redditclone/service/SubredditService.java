package com.reddit.redditclone.service;

import com.reddit.redditclone.dto.SubredditDto;
import com.reddit.redditclone.exception.SpringCustomeException;
import com.reddit.redditclone.model.Subreddit;
import com.reddit.redditclone.repository.SubredditRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static java.time.Instant.now;
import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
@Slf4j
public class SubredditService {

    private final SubredditRepository subredditRepository;
    private final AuthService authService;

    @Transactional
    public SubredditDto save(SubredditDto subredditDto){
        Subreddit subreddit = subredditRepository.save(mapSubredditToDto(subredditDto));
        subredditDto.setId(subreddit.getId());
        return subredditDto;
    }

    private Subreddit mapSubredditToDto(SubredditDto subredditDto) {

        return Subreddit.builder().name(subredditDto.getName())
                .description(subredditDto.getDescription()).build();
    }

    @Transactional(readOnly = true)
    public List<SubredditDto> getAll() {
        return subredditRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(toList());
    }


    private SubredditDto mapToDto(Subreddit subreddit) {
        return SubredditDto.builder().name(subreddit.getName())
                .id(subreddit.getId())
                .postCount(subreddit.getPosts().size())
                .build();
    }

    @Transactional(readOnly = true)
    public SubredditDto getSubreddit(Long id) {
        Subreddit subreddit = subredditRepository.findById(id)
                .orElseThrow(() -> new SpringCustomeException("Subreddit not found with id -" + id));
        return mapToDto(subreddit);
    }

//    private Subreddit mapToSubreddit(SubredditDto subredditDto) {
//        return Subreddit.builder().name("/r/" + subredditDto.getName())
//                .description(subredditDto.getDescription())
//                .user(authService.getCurrentUser())
//                .createdDate(now()).build();
//    }


}
