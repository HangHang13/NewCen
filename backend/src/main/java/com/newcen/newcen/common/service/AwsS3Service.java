package com.newcen.newcen.common.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class AwsS3Service {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;

    //s3 파일 업로드
    public List<String> uploadFile(List<MultipartFile> multipartFile) {
        List<String> fileNameList = new ArrayList<>();

        // forEach 구문을 통해 multipartFile로 넘어온 파일들 하나씩 fileNameList에 추가
        multipartFile.forEach(file -> {
            String fileName = createFileName(file.getOriginalFilename()); //파일 이름 uuid로 난수화시킴
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(file.getSize());
            objectMetadata.setContentType(file.getContentType());

            try(InputStream inputStream = file.getInputStream()) {
                amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead));
            } catch(IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다.");
            }
            fileNameList.add(fileName);
        });
        return fileNameList;
    }

    public void deleteFile(String fileName) {
        amazonS3.deleteObject(new DeleteObjectRequest(bucket, fileName));
    }
    private String createFileName(String fileName) { // 먼저 파일 업로드 시, 파일명을 난수화하기 위해 random으로 돌립니다.
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }

    private String getFileExtension(String fileName) { // file 형식이 잘못된 경우를 확인하기 위해 만들어진 로직이며, 파일 타입과 상관없이 업로드할 수 있게 하기 위해 .의 존재 유무만 판단하였습니다.
        try {
            return fileName.substring(fileName.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 형식의 파일(" + fileName + ") 입니다.");
        }
    }

    /**
     * S3 bucket 파일 다운로드
     *
     */
    public ByteArrayOutputStream downloadFile(String storedFileName) throws IOException {
        S3Object o = amazonS3.getObject(new GetObjectRequest(bucket, storedFileName));
        InputStream is = o.getObjectContent();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int len;
        byte[] buffer = new byte[4096];
        while ((len = is.read(buffer, 0, buffer.length)) != -1) {
            outputStream.write(buffer, 0, len);
        }
        return outputStream;
    }
}
