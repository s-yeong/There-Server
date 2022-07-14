package com.there.src.like;

import com.there.src.like.config.BaseException;
import com.there.src.like.config.BaseResponse;
import com.there.src.like.model.*;
import com.there.utils.JwtService;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.there.src.like.config.BaseResponseStatus.*;

@Api
@RestController
@RequestMapping("/likes")
public class LikeController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final LikeProvider likeProvider;
    private final LikeService likeService;
    private final JwtService jwtService;

    @Autowired
    public LikeController(LikeProvider likeProvider, LikeService likeService, JwtService jwtService) {

        this.likeProvider = likeProvider;
        this.likeService = likeService;
        this.jwtService = jwtService;
    }

    /**
     * 좋아요 및 감정표현 생성 API
     * /lieks/users/:userIdx
     */
    @ResponseBody
    @PostMapping("/users/{usedIdx}")
    public BaseResponse<PostLikeRes> createLikes(@PathVariable("userIdx")int userIdx, @RequestBody PostLikeReq postLikeReq) throws com.there.config.BaseException {

        int userIdxByJwt = jwtService.getUserIdx();

        // User 권한 확인
        if (userIdxByJwt != userIdx) return new BaseResponse<>(INVALID_USER_JWT);

        try {
            PostLikeRes postLikeRes = likeService.createLikes(userIdx, postLikeReq);
            return new BaseResponse<>(postLikeRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 좋아요 및 감정표현 조회 API
     * likes/posts/:postIdx
     */
    @ResponseBody
    @GetMapping("posts/{postIdx}")
    public BaseResponse<List<GetLikeRes>> selectLikes(@PathVariable("postIdx")int postIdx) {
        try {
            List<GetLikeRes> getLikes = likeProvider.retrieveLikes(postIdx);
            return new BaseResponse<>(getLikes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 좋아요 및 감정표현 수정 API
     * likes/users/:userIdx/post/:postIdx
     */
    @ResponseBody
    @PatchMapping("/users/{userIdx}/posts/{postIdx}")
    public BaseResponse<String> updateLikes
    (@PathVariable("userIdx") int userIdx, @PathVariable("postIdx") int postIdx, @RequestBody PatchLikeReq patchLikeReq) throws com.there.config.BaseException {

        int userIdxByJwt = jwtService.getUserIdx();

        // User 권한 확인
        if (userIdxByJwt != userIdx) return new BaseResponse<>(INVALID_USER_JWT);

        try {
            likeService.updateLikes(userIdx, postIdx, patchLikeReq.getEmotion());
            String result = "변경 완료";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }

    }

    /**
     * 좋아요 및 감정표현 삭제 API
     * likes/:likesIdx
     */
    @ResponseBody
    @DeleteMapping("/{likesIdx}")
    public BaseResponse<String> deleteLikes
    (@PathVariable("likesIdx")int likesIdx, @RequestBody DeleteLikeReq deleteLikeReq) {
        try {
            likeService.deleteLikes(likesIdx, deleteLikeReq);
            String result = "삭제 완료";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }

    }



}
