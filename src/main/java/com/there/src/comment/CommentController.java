package com.there.src.comment;


import com.there.src.comment.config.BaseException;
import com.there.src.comment.config.BaseResponse;
import com.there.src.comment.model.GetCommentListRes;
import com.there.src.comment.model.PostCommentReq;
import com.there.src.comment.model.PostCommentRes;
import com.there.utils.JwtService;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.there.src.comment.config.BaseResponseStatus.*;

@Api
@RestController
@RequestMapping("/comments")
public class CommentController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final CommentProvider commentProvider;
    private final CommentService commentService;
    private final JwtService jwtService;

    @Autowired
    public CommentController(CommentProvider commentProvider, CommentService commentService, JwtService jwtService) {
        this.commentProvider = commentProvider;
        this.commentService = commentService;
        this.jwtService = jwtService;
    }

    /**
     * λκΈ μμ± API
     * comments/:postIdx
     */
    @ResponseBody
    @PostMapping("/{postIdx}/{userIdx}")
    public BaseResponse<PostCommentRes> createComment
    (@PathVariable("postIdx")int postIdx, @PathVariable("userIdx") int userIdx,
     @RequestBody PostCommentReq postCommentReq) throws com.there.config.BaseException {
        try {

            int userIdxByJwt = jwtService.getUserIdx();

            if (userIdxByJwt != userIdx) return new BaseResponse<>(INVALID_USER_JWT);
            if (postCommentReq.getContent() == null) return new BaseResponse<>(COMMENTS_EMPTY_CONTENT);

            PostCommentRes postCommentRes = commentService.createComment(postIdx,userIdx, postCommentReq);
            return new BaseResponse<>(postCommentRes);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /** λκΈ λ¦¬μ€νΈ μ‘°ν API
     * comments/:commentIdx
     */
    @ResponseBody
    @GetMapping("{postIdx}")
    public BaseResponse<List<GetCommentListRes>> getCommentList(@PathVariable("postIdx") int postIdx)
            throws com.there.config.BaseException {
        List<GetCommentListRes> getCommentListResList = commentProvider.retrieveComment(postIdx);
        return new BaseResponse<>(getCommentListResList);

    }

    /**
     * λκΈ μ­μ  API
     * comments/:commentIdx/status
     */
    @ResponseBody
    @PatchMapping("/{commentIdx}/status")
    public BaseResponse<String> deleteComment(@PathVariable ("commentIdx") int commentIdx)
            throws com.there.config.BaseException, BaseException {
        int userIdxByJwt = jwtService.getUserIdx();
        commentService.deleteComment(userIdxByJwt, commentIdx);

        String result ="λκΈμ΄ μ­μ λμμ΅λλ€. ";
        return new BaseResponse<>(result);
    }
}
