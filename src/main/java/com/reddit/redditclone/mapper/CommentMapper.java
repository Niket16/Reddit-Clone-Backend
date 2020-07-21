package com.reddit.redditclone.mapper;

import com.reddit.redditclone.dto.CommentDto;
import com.reddit.redditclone.model.Comments;
import com.reddit.redditclone.model.Post;
import com.reddit.redditclone.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "id" , ignore = true)
    @Mapping(target = "text" , source = "commentDto.text")
    @Mapping(target = "createdDate" , expression =  "java(java.time.Instant.now())")
    @Mapping(target = "post" , source = "post")
    @Mapping(target = "user" , source = "user")
    Comments map(CommentDto commentDto, Post post, User user);

    @Mapping(target = "postId" , expression = "java(comments.getPost().getPostId())")
    @Mapping(target = "userName" , expression = "java(comments.getUser().getUserName())")
    CommentDto mapToDto(Comments comments);
}
