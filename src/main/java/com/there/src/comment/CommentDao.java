package com.there.src.comment;

import com.there.src.comment.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class CommentDao {
    private JdbcTemplate jdbcTemplate;
    private List<GetReCommentListRes> getReCommentListRes;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // 댓글 생성
    public int createComment(int postIdx, int userIdx,PostCommentReq postCommentReq) {
        String createCommentQuery = "insert into Comment (postIdx,userIdx,  content) values ( ?,?, ?);";
        Object[] createCommentParams = new Object[]{postIdx, userIdx,postCommentReq.getContent()};

        this.jdbcTemplate.update(createCommentQuery, createCommentParams);

        String lastcommentIdxQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastcommentIdxQuery, int.class);
    }

    // 댓글 조회
    public List<GetCommentListRes> selectCommentList(int postIdx) {
        String selectCommentQuery = "select nickName, profileImgUrl, content, created_At, \n" +
                "from Comment c join User as u on u.userIdx = c.userIdx\n" +
                "where postIdx = ? ;";
        int selectCommentParam = postIdx;

        return this.jdbcTemplate.query(selectCommentQuery, (rs, rowNum) -> new GetCommentListRes(
                rs.getString("nickName"),
                rs.getString("profileImgUrl"),
                rs.getString("content"),
                rs.getString("created_At")
        ), selectCommentParam);
    }

    // 댓글 삭제
    public int deleteComment(int commentIdx) {
        String deleteCommentQuery = "update Comment SET status = 'DELETED' where commentIdx = ?;";
        int deleteCommentParam = commentIdx;
        return this.jdbcTemplate.update(deleteCommentQuery,
                deleteCommentParam);
    }

    //댓글 수정
    public int updateComment(int commentIdx, PatchCommentReq patchCommentReq){
        String updateCommentQuery = "update Comment set content =? where commentIdx =?";
        Object[] updateCommentParam = new Object[]{patchCommentReq.getContent(), commentIdx};
        return this.jdbcTemplate.update(updateCommentQuery, updateCommentParam);
    }

    public int checkUserCommentExist(int userIdx, int commentIdx) {
        String checkUserCommentExistQuery = "select exists(select c.commentIdx, u.userIdx\n" +
                "    from Comment as c join User as u on u.userIdx = c.userIdx\n" +
                "    where u.userIdx =? and c.commentIdx=?);";
        Object []checkUserCommentExistParam = new Object[] {userIdx, commentIdx};
        return this.jdbcTemplate.queryForObject(checkUserCommentExistQuery, int.class, checkUserCommentExistParam);
    }

    // 대댓글 작성
    public int createReComment(int postIdx, int userIdx, int commentIdx, PostReCommentReq postReCommentReq) {
        String createReCommentQuery = "insert into Comment (postIdx,userIdx, reply_id, content) values (?,?, ?,?);";
        Object[] createReCommentParams = new Object[]{postIdx, userIdx, commentIdx, postReCommentReq.getContent()};

        this.jdbcTemplate.update(createReCommentQuery, createReCommentParams);

        String lastcommentIdxQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastcommentIdxQuery, int.class);
    }

    // 대댓글 조회
    public List<GetReCommentListRes> selectReCommentList(int postIdx, int commentIdx){
        String selectReCommentQuery ="select  u.nickName, Re.content, Re.created_At\n" +
                "from Comment c\n" +
                "inner join Comment as Re on Re.reply_id = c.commentIdx\n" +
                "join User as u on u.userIdx = Re.userIdx\n" +
                "and c.status='ACTIVE'\n" +
                "where c.postIdx =? and c.commentIdx=? ;";
        Object[] selectReCommentParam = new Object[]{postIdx, commentIdx};
        return this.jdbcTemplate.query(selectReCommentQuery,
                (rs, rowNum) -> new GetReCommentListRes(
                        rs.getString("nickName"),
                        rs.getString("content"),
                        rs.getString("created_At")), selectReCommentParam);
    }

    // 대댓글 삭제
    public int deleteReComment(int commentIdx) {
        String deleteReCommentQuery = "update Comment SET status = 'DELETED' where commentIdx = ?;";
        int deleteReCommentParam = commentIdx;
        return this.jdbcTemplate.update(deleteReCommentQuery,
                deleteReCommentParam);
    }

    // 대댓글 수정
    public int updateReComment(int commentIdx, PatchCommentReq patchCommentReq){
        String updateCommentQuery = "update Comment set content =? where commentIdx =?";
        Object[] updateCommentParam = new Object[]{patchCommentReq.getContent(), commentIdx};
        return this.jdbcTemplate.update(updateCommentQuery, updateCommentParam);
    }

    // 상위 댓글이 유효한 댓글인지
    public int checkReComment(int commentIdx) {
        String checkReCommentQuery = "select exists(select commentIdx from Comment where commentIdx =? and status ='ACTIVE');";
        int checkReCommentParam = commentIdx;

        return this.jdbcTemplate.queryForObject(checkReCommentQuery, int.class, checkReCommentParam);
    }
}