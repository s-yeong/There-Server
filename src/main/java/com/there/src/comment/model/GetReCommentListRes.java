package com.there.src.comment.model;


import lombok.AllArgsConstructor;
import lombok.Data;


import java.util.List;

@Data
@AllArgsConstructor
public class GetReCommentListRes {
    private String nickName;
    private String content;
    private String created_At;

}
