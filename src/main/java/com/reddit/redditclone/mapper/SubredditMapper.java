package com.reddit.redditclone.mapper;

import com.reddit.redditclone.dto.SubredditDto;
import com.reddit.redditclone.model.Post;
import com.reddit.redditclone.model.Subreddit;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SubredditMapper {

    @Mapping(target = "postCount", expression = "java(mapPosts(subreddit.getPosts()))")
    SubredditDto mapSubredditToDto(Subreddit subreddit);

    default Integer mapPosts(List<Post> postCount){
        return postCount.size();
    }

    @InheritInverseConfiguration
    @Mapping(target = "posts" , ignore = true)
    Subreddit mapDtoToSubreddit(SubredditDto subredditDto);
}
