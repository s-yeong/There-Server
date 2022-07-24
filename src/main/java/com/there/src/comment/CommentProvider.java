package com.there.src.comment;

import com.there.config.BaseException;
import com.there.src.comment.model.GetCommentListRes;
import com.there.src.comment.model.GetReCommentListRes;
import com.there.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.there.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class CommentProvider {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final CommentDao commentDao;
    private final JwtService jwtService;

    @Autowired
    public CommentProvider(CommentDao commentDao, JwtService jwtService){
        this.commentDao = commentDao;
        this.jwtService = jwtService;
    }

    // 댓글 리스트 조회
    public List<GetCommentListRes> retrieveComment(int postIdx) throws BaseException{
        try {
            List<GetCommentListRes> getCommentListResList = commentDao.selectCommentList(postIdx);
            return getCommentListResList;
        } catch (Exception exception){
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }

    }

    // 대댓글 리스트 조회
    public List<GetReCommentListRes> ReComment(int postIdx, int commentIdx) throws BaseException{
        try {
            List<GetReCommentListRes> getReCommentListResList = commentDao.selectReCommentList(postIdx, commentIdx);
            return getReCommentListResList;
        } catch (Exception exception) {
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public int checkUserCommentExist(int userIdx, int commentIdx) throws BaseException {
        try {
            return commentDao.checkUserCommentExist(userIdx, commentIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkReComment(int commentIdx) throws BaseException {
        try {
            return commentDao.checkReComment(commentIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
