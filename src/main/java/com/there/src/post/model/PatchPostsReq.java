package com.there.src.post.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PatchPostsReq {

    private String ImgUrl;
    private String content;

}
