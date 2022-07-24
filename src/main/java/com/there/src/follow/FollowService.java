package com.there.src.follow;

import com.there.src.user.config.BaseException;
import com.there.src.follow.model.PostFollowReq;
import com.there.src.follow.model.PostFollowRes;
import com.there.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.there.src.user.config.BaseResponseStatus.DATABASE_ERROR;
@Service
public class FollowService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final FollowProvider followProvider;
    private final FollowDao followDao;
    private final JwtService jwtService;

    @Autowired
    public FollowService(FollowDao followDao, FollowProvider followProvider, JwtService jwtService ){
        this.followDao = followDao;
        this.followProvider = followProvider;
        this.jwtService = jwtService;
    }

    // 팔로우
     public void follow(int userIdx, int followeeIdx) throws BaseException{
         try {
             int follow = followDao.follow(userIdx, followeeIdx);
         } catch (Exception exception) {
             throw new BaseException(DATABASE_ERROR);
         }
     }


    // 언팔로우
    public void unfollow(int followIdx) throws BaseException{
        try {
            int follow = followDao.unfollow(followIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
