package com.there.src.history;

import com.there.src.history.cofig.*;
import com.there.src.history.model.*;
import com.there.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/historys")
public class HistoryController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final HistoryProvider historyProvider;
    @Autowired
    private final HistoryService historyService;
    @Autowired
    private final JwtService jwtService;




    public HistoryController(HistoryProvider historyProvider, HistoryService historyService, JwtService jwtService){
        this.historyProvider = historyProvider;
        this.historyService = historyService;
        this.jwtService = jwtService;
    }

    /**
     * 히스토리 조회 API   -- ex) "히스토리 제목" 누르면 그 히스토리 조회
     * [GET] /historys/:historyIdx
     * @return BaseResponse<getHistoryRes>
     */
    @ResponseBody
    @GetMapping("/{historyIdx}")
    public BaseResponse<GetHistoryRes> getHistory(@PathVariable("historyIdx")int historyIdx) {
        try {

            GetHistoryRes getHistoryRes = historyProvider.findHistory(historyIdx);
            return new BaseResponse<>(getHistoryRes);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 히스토리 리스트 조회 API  -- ex) "히스토리의 제목" + 날짜들이 리스트로 조회 (일단, 첫번쨰 히스토리가 나오게 하지는 않음)
     * [GET] /historys?postIdx=
     * @return BaseResponse<getHistoryListRes>
     */
    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<GetHistoryListRes>> getHistoryList(@RequestParam int postIdx) {
        try{

            List<GetHistoryListRes> getHistoryListRes = historyProvider.retrieveHistorys(postIdx);
            return new BaseResponse<>(getHistoryListRes);

        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 히스토리 작성 API
     * [POST] /historys
     * @return BaseResponse<postHistoryRes>
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostHistoryRes> createHistory(@RequestBody PostHistoryReq postHistoryReq) {


        try {

            // 유저 로그인 API 작성 완료시 주석해제
            // int userIdxByJwt = jwtService.getUserIdx();
            int userIdxByJwt = 1;

            if(postHistoryReq.getTitle() == null){
                return new BaseResponse<>(BaseResponseStatus.HISTORYS_EMPTY_TITLES);
            }

            if(postHistoryReq.getTitle().length() > 45){
                return new BaseResponse<>(BaseResponseStatus.HISTORYS_INVALID_TITLES);
            }

            if(postHistoryReq.getContent() == null){
                return new BaseResponse<>(BaseResponseStatus.HISTORYS_EMPTY_CONTENTS);
            }

            if(postHistoryReq.getContent().length() > 500){
                return new BaseResponse<>(BaseResponseStatus.HISTORYS_INVALID_CONTENTS);
            }

            if(postHistoryReq.getPostHistoryPictures().size() < 1) {
                return new BaseResponse<>(BaseResponseStatus.HISTORYS_EMPTY_IMGURL);
            }

            PostHistoryRes postHistoryRes = historyService.createHistory(userIdxByJwt, postHistoryReq);

            return new BaseResponse<>(postHistoryRes);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 히스토리 삭제 API
     * [PATCH] /historys/:historyIdx/status
     * @return BaseResponse<result>
     */
    @ResponseBody
    @PatchMapping("/{historyIdx}/status")
    public BaseResponse<String> deleteHistory(@PathVariable ("historyIdx") int historyIdx) {

        try {

            // 유저 로그인 API 작성 완료시 주석해제
            // int userIdxByJwt = jwtService.getUserIdx();
            //historyService.deleteHistory(userIdxByJwt, historyIdx);
            int userIdxByJwt = 1;   // 유저 로그인 API 작성 완료시 삭제
            historyService.deleteHistory(userIdxByJwt, historyIdx);

            String result = "히스토리가 삭제되었습니다.";
            return new BaseResponse<>(result);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 히스토리 수정 화면 API
     * [GET] /historys/:historyIdx
     * @return BaseResponse<getHistoryScreenRes>
     */
    // 히스토리 조회와 분리한 이유는 히스토리의 주인만 수정해야하기 떄문
    @ResponseBody
    @GetMapping("/modify/{historyIdx}")
    public BaseResponse<GetHistoryScreenRes> getModifyHistory(@PathVariable("historyIdx")int historyIdx) {
        try {
            // 유저 로그인 API 작성 완료시 주석해제
            // int userIdxByJwt = jwtService.getUserIdx();
            //historyService.deleteHistory(userIdxByJwt, historyIdx);
            int userIdxByJwt = 1;   // 유저 로그인 API 작성 완료시 삭제
            GetHistoryScreenRes getHistoryScreenRes = historyProvider.findModifyHistory(userIdxByJwt, historyIdx);
            return new BaseResponse<>(getHistoryScreenRes);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }



    /**
     * 히스토리 수정 API =>> 제목, 내용 수정 -> 사진 삭제 -> 사진 생성
     * [PATCH] /historys/:historyIdx
     * @return BaseResponse<patchHistoryRes>
     */


    @ResponseBody
    @PatchMapping("/modify/{historyIdx}")
    public BaseResponse<String> modifyHistory(@PathVariable ("historyIdx") int historyIdx, @RequestBody PatchHistoryReq patchHistoryReq) {

        try {

            if(patchHistoryReq.getTitle() == null){
                return new BaseResponse<>(BaseResponseStatus.HISTORYS_EMPTY_TITLES);
            }

            if(patchHistoryReq.getTitle().length() > 45){
                return new BaseResponse<>(BaseResponseStatus.HISTORYS_INVALID_TITLES);
            }

            if(patchHistoryReq.getContent() == null){
                return new BaseResponse<>(BaseResponseStatus.HISTORYS_EMPTY_CONTENTS);
            }

            if(patchHistoryReq.getContent().length() > 500){
                return new BaseResponse<>(BaseResponseStatus.HISTORYS_INVALID_CONTENTS);
            }

            if(patchHistoryReq.getPatchHistoryPictures().size() < 1) {
                return new BaseResponse<>(BaseResponseStatus.HISTORYS_EMPTY_IMGURL);
            }

            // 유저 로그인 API 작성 완료시 주석해제
            // int userIdxByJwt = jwtService.getUserIdx();
            //historyService.deleteHistory(userIdxByJwt, historyIdx);
            int userIdxByJwt = 1;   // 유저 로그인 API 작성 완료시 삭제

            historyService.modifyHistory(userIdxByJwt, historyIdx, patchHistoryReq);

            String result = "히스토리가 수정되었습니다.";
            return new BaseResponse<>(result);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


}

