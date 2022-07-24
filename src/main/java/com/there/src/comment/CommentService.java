package com.there.src.comment;


import com.there.src.comment.config.BaseException;
import com.there.src.comment.model.*;
import com.there.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.there.src.comment.config.BaseResponseStatus.*;

@Service
public class CommentService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final CommentDao commentDao;

    private final CommentProvider commentProvider;
    private final JwtService jwtService;

    @Autowired
    public CommentService(CommentDao commentDao, CommentProvider commentProvider, JwtService jwtService){
        this.commentDao = commentDao;
        this.commentProvider = commentProvider;
        this.jwtService = jwtService;
    }

    // 댓글 작성
    public PostCommentRes createComment(int postIdx, int userIdx,PostCommentReq postCommentReq) throws BaseException{
        try {
            int commentIdx = commentDao.createComment(postIdx, userIdx, postCommentReq);
            return new PostCommentRes(commentIdx);
        } catch (Exception exception) {
            throw new BaseException(CREATE_FAIL_COMMENT);
        }
    }

    // 댓글 삭제
    public void deleteComment(int userIdx, int commentIdx) throws com.there.config.BaseException, BaseException {

        if(commentProvider.checkUserCommentExist(userIdx, commentIdx) == 0){
            throw new BaseException(USERS_COMMENT_INVALID_ID);
        }
        try {
            int result = commentDao.deleteComment(commentIdx);
            if(result == 0) {
                throw new BaseException(DELETE_FAIL_COMMENT);
            }
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 댓글 수정
    public void updateComment( int commentIdx, PatchCommentReq patchCommentReq) throws BaseException {

        int result = 0;


        try{
            if (patchCommentReq.getContent() != null){
                result = commentDao.updateComment(commentIdx, patchCommentReq);
            }

            if (result == 0) throw new BaseException(UPDATE_FAIL_COMMENT);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }

    }

    // 대댓글 작성
    public PostReCommentRes createReComment(int postIdx, int userIdx, int commentIdx , PostReCommentReq postReCommentReq) throws com.there.config.BaseException, BaseException{

        if(commentDao.checkReComment(commentIdx) == 0){
            throw new BaseException(COMMENT_INVALID);
        }
        try {
            int reply_id = commentDao.createReComment(postIdx, userIdx,commentIdx, postReCommentReq);
            return new PostReCommentRes(reply_id);
        } catch (Exception exception) {
            throw new BaseException(CREATE_FAIL_RECOMMENT);
        }
    }

    // 대댓글 삭제
    public void deleteReComment(int userIdx, int commentIdx) throws com.there.config.BaseException, BaseException {

        if(commentProvider.checkUserCommentExist(userIdx, commentIdx) == 0){
            throw new BaseException(USERS_COMMENT_INVALID_ID);
        }
        try {
            int result = commentDao.deleteReComment(commentIdx);
            if(result == 0) {
                throw new BaseException(DELETE_FAIL_COMMENT);
            }
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 대댓글 수정
    public void updateReComment( int commentIdx, PatchCommentReq patchCommentReq) throws BaseException {

        int result = 0;


        try{
            if (patchCommentReq.getContent() != null){
                result = commentDao.updateComment(commentIdx, patchCommentReq);
            }

            if (result == 0) throw new BaseException(UPDATE_FAIL_COMMENT);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }

    }
}
