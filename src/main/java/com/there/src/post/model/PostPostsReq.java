package com.there.src.post.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostPostsReq {
    private String imgUrl;
    private String content;
    private String [] hashtag = null;

}
