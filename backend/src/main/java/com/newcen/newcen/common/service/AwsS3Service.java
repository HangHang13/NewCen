package com.newcen.newcen.common.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
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

    public List<String> uploadFile(List<MultipartFile> multipartFile) {
        List<String> fileNameList = new ArrayList<>();

        // forEach 구문을 통해 multipartFile로 넘어온 파일들 하나씩 fileNameList에 추가
        multipartFile.forEach(file -> {
            String fileName = createFileName(file.getOriginalFilename());
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
     */
    public ResponseEntity<byte[]> getObject(String storedFileName) throws IOException {
        S3Object o = amazonS3.getObject(new GetObjectRequest(bucket, storedFileName));

        S3ObjectInputStream objectInputStream = o.getObjectContent();
        byte[] bytes = IOUtils.toByteArray(objectInputStream);


        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(contentType(storedFileName));
        httpHeaders.setContentLength(bytes.length);
        String[]arr = storedFileName.split("/");
        String type = arr[arr.length-1];
        String fileName = URLEncoder.encode(type,"UTF-8").replace("\\+","%20");
        httpHeaders.setContentDispositionFormData("attachment", fileName);

        return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK);

    }

    private MediaType contentType(String keyname){
        String[] arr = keyname.split("\\.");
        String type = arr[arr.length-1];
        switch (type){
            case "txt":
                return MediaType.TEXT_PLAIN;
            case "md":
                return MediaType.TEXT_MARKDOWN;
            case "png":
                return MediaType.IMAGE_PNG;
            case "jpg":
                return MediaType.IMAGE_JPEG;
            default:
                return MediaType.APPLICATION_OCTET_STREAM;
        }
    }


}
