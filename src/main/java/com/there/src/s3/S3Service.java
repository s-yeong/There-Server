package com.there.src.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.there.config.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ListIterator;
import java.util.Optional;
import java.util.UUID;

import static com.there.config.BaseResponseStatus.DATABASE_ERROR;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3Service {

    public final AmazonS3Client amazonS3Client;
    public final S3Dao s3Dao;

    @Value("${cloud.aws.s3.bucket}")
    public String bucket;


    public String uploadHistoryPicture(MultipartFile multipartFile, String dirName) throws IOException {
        File uploadFile = convert(multipartFile).orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File로 전환이 실패했습니다."));

        return uploadHistoryPicture(uploadFile, dirName);
    }
    // S3로 파일 업로드하기
    public String uploadHistoryPicture(File uploadFile, String dirName) {
        String fileName = dirName + "/" + UUID.randomUUID() + uploadFile.getName();   // S3에 저장된 파일 이름
        String uploadImageUrl = putS3(uploadFile, fileName); // s3로 업로드
        removeNewFile(uploadFile);
        return uploadImageUrl;
    }
    // S3로 업로드
    public String putS3(File uploadFile, String fileName) {
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile).withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }
    // 로컬에 저장된 이미지 지우기
    public void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("File delete success");
            return;
        }
        log.info("File delete fail");
    }
    public Optional<File> convert(MultipartFile multipartFile) throws IOException{
        File convertFile = new File(System.getProperty("user.dir") + "/" + multipartFile.getOriginalFilename());
        // 바로 위에서 지정한 경로에 File이 생성됨 (경로가 잘못되었다면 생성 불가능)
        if (convertFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertFile)) { // FileOutputStream 데이터를 파일에 바이트 스트림으로 저장하기 위함
                fos.write(multipartFile.getBytes());
            }
            return Optional.of(convertFile);
        }
        return Optional.empty();
    }

    public void removeFolder(String folderName){
        ListObjectsV2Request listObjectsV2Request = new ListObjectsV2Request().withBucketName(bucket).withPrefix(folderName+"/");
        ListObjectsV2Result listObjectsV2Result = amazonS3Client.listObjectsV2(listObjectsV2Request);
        ListIterator<S3ObjectSummary> listIterator = listObjectsV2Result.getObjectSummaries().listIterator();

        while (listIterator.hasNext()){
            S3ObjectSummary objectSummary = listIterator.next();
            DeleteObjectRequest request = new DeleteObjectRequest(bucket,objectSummary.getKey());
            amazonS3Client.deleteObject(request);
            System.out.println("Deleted " + objectSummary.getKey());
        }
    }



    /**
     * 히스토리 사진 업로드
     */
    @Transactional(rollbackFor = BaseException.class)
    public void uploadHistoryPicture(String imgPath, int historyIdx) throws BaseException {
        try {
            s3Dao.uploadHistoryPicture(imgPath, historyIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 히스토리 사진 삭제 (선택 삭제)
     */
/*
    public void delHistoryPicture(int historyIdx, String[] del_picture) throws BaseException {
        try {
            for(int i=0;i<del_picture.length;i++) {
                s3Dao.delHistoryPicture(historyIdx, del_picture[i]);
            }
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
*/


    /**
     * 히스토리 사진 삭제 (일괄 삭제)
     */
    public void delHistoryPicture(int historyIdx) throws BaseException {
        try {
            s3Dao.delHistoryAllPicture(historyIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    
}
