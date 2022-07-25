package com.there.src.history;

import com.there.src.history.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.List;

@Repository
public class HistoryDao {

    private JdbcTemplate jdbcTemplate;
    List<GetHistoryPicturesRes> getHistoryPicturesRes;

    @Resource
    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // 히스토리 조회 함수    - 기록물 사진 같은 경우 하나의 리스트가 칼럼으로 들어감   - updatedAt 기준으로 날짜 요일 조회
    public GetHistoryRes selectHistory(int historyIdx){
        String selectHistoryQuery = "select h.historyIdx, h.title, h.content,\n" +
                "      DATE(updated_At) as updatedAt,\n" +
                "    CASE DAYOFWEEK(updated_At)\n" +
                "    WHEN '1' THEN '(일)'\n" +
                "    WHEN '2' THEN '(월)'\n" +
                "    WHEN '3' THEN '(화)'\n" +
                "    WHEN '4' THEN '(수)'\n" +
                "    WHEN '5' THEN '(목)'\n" +
                "    WHEN '6' THEN '(금)'\n" +
                "    WHEN '7' THEN '(토)'\n" +
                "    END AS dayOfWeek\n" +
                "from History as h\n" +
                "where h.historyIdx = ? and h.status = 'ACTIVE';";
        int selectHistoryParam = historyIdx;
        return this.jdbcTemplate.queryForObject(selectHistoryQuery,
                (rs, rowNum) -> new GetHistoryRes(
                        rs.getInt("historyIdx"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getString("updatedAt"),
                        rs.getString("dayOfWeek"),
                        getHistoryPicturesRes = this.jdbcTemplate.query("select hp.pictureIdx as pictureIdx, hp.imgUrl as imgUrl\n" +
                                        "from historyPicture as hp\n" +
                                        "    join History as h on h.historyIdx = hp.historyIdx\n" +
                                        "where h.historyIdx = ? and hp.liked_ip = 'ACTIVE';",
                                (rk, rownum) -> new GetHistoryPicturesRes(
                                        rk.getInt("pictureIdx"),
                                        rk.getString("imgUrl")
                                ), rs.getInt("historyIdx"))
                ), selectHistoryParam);
    }

    // 히스토리 리스트 조회 함수
    public List<GetHistoryListRes> selectHistoryList(int postIdx){
        String selectHistoryListQuery = "select h.historyIdx as historyIdx, h.title as title,\n" +
                "       DATE(h.updated_At) as updatedAt,\n" +
                "        CASE DAYOFWEEK(h.updated_At)\n" +
                "        WHEN '1' THEN '(일)'\n" +
                "        WHEN '2' THEN '(월)'\n" +
                "        WHEN '3' THEN '(화)'\n" +
                "        WHEN '4' THEN '(수)'\n" +
                "        WHEN '5' THEN '(목)'\n" +
                "        WHEN '6' THEN '(금)'\n" +
                "        WHEN '7' THEN '(토)'\n" +
                "        END AS dayOfWeek\n" +
                "from History as h\n" +
                "    join Post as p on p.postIdx = h.postIdx\n" +
                "where p.postIdx = ? and h.status = 'ACTIVE';";
        int selectHistoryListParam = postIdx;
        return this.jdbcTemplate.query(selectHistoryListQuery,
                (rs, rowNum) -> new GetHistoryListRes(
                        rs.getInt("historyIdx"),
                        rs.getString("title"),
                        rs.getString("updatedAt"),
                        rs.getString("dayOfWeek")
                ), selectHistoryListParam);
    }

    // 히스토리 작성 API - 히스토리 작성 함수(이미지 제외)
    public int insertHistory(int userIdx, PostHistoryReq postHistoryReq){
        String insertHistoryQuery = "insert into History(userIdx, postIdx, title, content) VALUES (?,?,?,?)";;
        Object[] insertHistoryParams = new Object[] {userIdx, postHistoryReq.getPostIdx(), postHistoryReq.getTitle(), postHistoryReq.getContent()};
        this.jdbcTemplate.update(insertHistoryQuery,
                insertHistoryParams);
        String lastInsertIdxQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdxQuery, int.class);
    }


    // 히스토리 삭제 함수
    public int deleteHistory(int historyIdx){
        String deleteHistoryQuery = "update History SET status = 'DELETED' where historyIdx = ?;" ;
        int deleteHistoryParam = historyIdx;
        return this.jdbcTemplate.update(deleteHistoryQuery,
                deleteHistoryParam);

    }

    // 히스토리 수정 화면 조회 함수
    public GetHistoryScreenRes selectModifyHistory(int historyIdx){
        String selectModifyHistoryQuery = "select h.historyIdx, h.title, h.content\n" +
                "from History as h\n" +
                "where h.historyIdx = ? and h.status = 'ACTIVE';";
        int selectModifyHistoryParam = historyIdx;
        return this.jdbcTemplate.queryForObject(selectModifyHistoryQuery,
                (rs, rowNum) -> new GetHistoryScreenRes(
                        rs.getInt("historyIdx"),
                        rs.getString("title"),
                        rs.getString("content"),
                        getHistoryPicturesRes = this.jdbcTemplate.query("select hp.pictureIdx as pictureIdx, hp.imgUrl as imgUrl\n" +
                                        "from historyPicture as hp\n" +
                                        "    join History as h on h.historyIdx = hp.historyIdx\n" +
                                        "where h.historyIdx = ? and hp.liked_ip = 'ACTIVE';",
                                (rk, rownum) -> new GetHistoryPicturesRes(
                                        rk.getInt("pictureIdx"),
                                        rk.getString("imgUrl")
                                ), rs.getInt("historyIdx"))
                ), selectModifyHistoryParam);
    }

    // 히스토리 수정 API - 히스토리 수정 함수(이미지 제외)
    public int updateHistory(int historyIdx, PatchHistoryReq patchHistoryReq){
        String updateHistoryQuery = "update History set title=?,content=? where historyIdx = ?;\n" ;
        Object []updateHistoryParams = new Object[] {patchHistoryReq.getTitle(), patchHistoryReq.getContent(), historyIdx};
        return this.jdbcTemplate.update(updateHistoryQuery,
                updateHistoryParams);

    }

    // 유저 체크 함수
    public int checkUserExist(int userIdx){
        String checkUserExistQuery = "select exists(select userIdx\n" +
                "from User\n" +
                "where userIdx = ?);" ;
        int checkUserExistParam = userIdx;
        return this.jdbcTemplate.queryForObject(checkUserExistQuery,
                int.class,
                checkUserExistParam);

    }

    // 히스토리 체크 함수
    public int checkHistoryExist(int historyIdx){
        String checkHistoryExistQuery = "select exists(select historyIdx\n" +
                "from History\n" +
                "where historyIdx = ?);" ;
        int checkHistoryExistParam = historyIdx;
        return this.jdbcTemplate.queryForObject(checkHistoryExistQuery,
                int.class,
                checkHistoryExistParam);

    }

    // 히스토리 유저 체크 함수
    public int checkUserHistoryExist(int userIdx, int historyIdx){
        String checkUserHistoryExistQuery = "select exists(\n" +
                "    select h.historyIdx, u.userIdx\n" +
                "    from History as h\n" +
                "        join User as u on u.userIdx = h.userIdx\n" +
                "    where u.userIdx=? and h.historyIdx = ?);" ;
        Object []checkUserHistoryExistParams = new Object[] {userIdx, historyIdx};
        return this.jdbcTemplate.queryForObject(checkUserHistoryExistQuery,
                int.class,
                checkUserHistoryExistParams);

    }

    // 게시물 유저 체크 함수
    public int checkUserPostExist(int userIdx, int postIdx){
        String checkUserHistoryExistQuery = "select exists(\n" +
                "    select p.postIdx, u.userIdx\n" +
                "    from Post as p\n" +
                "        join User as u on u.userIdx = p.userIdx\n" +
                "    where u.userIdx=? and p.postIdx = ?);" ;
        Object []checkUserHistoryExistParams = new Object[] {userIdx, postIdx};
        return this.jdbcTemplate.queryForObject(checkUserHistoryExistQuery,
                int.class,
                checkUserHistoryExistParams);

    }


}
