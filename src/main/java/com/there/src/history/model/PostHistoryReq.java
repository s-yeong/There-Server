package com.there.src.history.model;


import lombok.*;

import java.util.List;
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@ToString
public class PostHistoryReq {

    private int postIdx;
    private String title;
    private String content;


}
