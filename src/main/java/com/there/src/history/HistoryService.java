package com.there.src.history;

import com.there.src.history.cofig.BaseException;
import com.there.src.history.model.*;
import com.there.src.s3.S3Service;
import com.there.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.there.src.history.cofig.BaseResponseStatus.*;

@Service
public class HistoryService {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final HistoryDao historyDao;
    private final HistoryProvider historyProvider;
    private final JwtService jwtService;
    private final S3Service s3Service;


    @Autowired
    public HistoryService(HistoryDao historyDao, HistoryProvider historyProvider, JwtService jwtService, S3Service s3Service) {
        this.historyDao = historyDao;
        this.historyProvider = historyProvider;
        this.jwtService = jwtService;
        this.s3Service = s3Service;
    }

    // 히스토리 작성
    @Transactional(rollbackFor = BaseException.class)
    public PostHistoryRes createHistory(int userIdx, PostHistoryReq postHistoryReq, List<MultipartFile> MultipartFiles) throws BaseException {

        try{

            // 이 게시물이 userIdx가 맞는지
            if(historyProvider.checkUserPostExist(userIdx, postHistoryReq.getPostIdx()) == 0){
                throw new BaseException(USERS_POSTS_INVALID_ID);
            }

            // 히스토리 DB에서 생성시 히스토리 식별자 Dao에서 가져옴
            int historyIdx = historyDao.insertHistory(userIdx, postHistoryReq);

            if (MultipartFiles != null) {
                for (int i = 0; i < MultipartFiles.size(); i++) {

                    // s3에 업로드
                    String s3path = "historyPicture/historyIdx : " + Integer.toString(historyIdx);
                    String imgPath = s3Service.uploadHistoryPicture(MultipartFiles.get(i), s3path);

                    // db 업로드
                    s3Service.uploadHistoryPicture(imgPath, historyIdx);
                }
            }

            return new PostHistoryRes(historyIdx);
        }
        catch (Exception exception) {

            throw new BaseException(DATABASE_ERROR);

        }
    }

    // 히스토리 삭제
    public void deleteHistory(int userIdx, int historyIdx) throws BaseException{


        if(historyProvider.checkUserExist(userIdx) == 0){
            throw new BaseException(USERS_EMPTY_USER_ID);
        }

        if(historyProvider.checkHistoryExist(historyIdx) == 0){
            throw new BaseException(HISTORYS_EMPTY_HISTORY_ID);
        }

        // 이 히스토리의 userIdx가 맞는지
        if(historyProvider.checkUserHistoryExist(userIdx, historyIdx) == 0){
            throw new BaseException(USERS_HISTORYS_INVALID_ID);
        }

        try{

            int result = historyDao.deleteHistory(historyIdx);
            if(result == 0){
                throw new BaseException(DELETE_FAIL_HISTORY);
            }

        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }



    // 히스토리 수정
    @Transactional(rollbackFor = BaseException.class)
    public void modifyHistory(int userIdx, int historyIdx, PatchHistoryReq patchHistoryReq, List<MultipartFile> MultipartFiles)
            throws BaseException {

        try{
            if(historyProvider.checkUserExist(userIdx) == 0){
                throw new BaseException(USERS_EMPTY_USER_ID);
            }

            if(historyProvider.checkHistoryExist(historyIdx) == 0){
                throw new BaseException(HISTORYS_EMPTY_HISTORY_ID);
            }

            // 이 히스토리의 userIdx가 맞는지
            if(historyProvider.checkUserHistoryExist(userIdx, historyIdx) == 0){
                throw new BaseException(USERS_HISTORYS_INVALID_ID);
            }

            int updateResult = historyDao.updateHistory(historyIdx, patchHistoryReq);

            if(updateResult == 0){
                throw new BaseException(MODIFY_FAIL_HISTORY);
            }

                if (MultipartFiles != null) {

                    s3Service.removeFolder("historyPicture/historyIdx : " + Integer.toString(historyIdx));
                    s3Service.delHistoryPicture(historyIdx);

                    for (int i = 0; i < MultipartFiles.size(); i++) {

                        // s3에 업로드
                        String s3path = "historyPicture/historyIdx : " + Integer.toString(historyIdx);
                        String imgPath = s3Service.uploadHistoryPicture(MultipartFiles.get(i), s3path);

                        // db 업로드
                        s3Service.uploadHistoryPicture(imgPath, historyIdx);
                    }
                }



        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


}