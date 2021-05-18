package com.danshow.danshowserver.service.video_service;

import com.danshow.danshowserver.domain.video.AttachFile;
import com.danshow.danshowserver.web.dto.response.Response;
import net.bramp.ffmpeg.FFmpeg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class AnalyzeService {

    private final String DL_SERVER_URL1 = "";

    private final String DL_SERVER_URL2 = "";

    private VideoServiceInterface videoServiceInterface;

    private VideoFileUtils videoFileUtils;

    private ApiService<Response> apiService;

    @Autowired
    public AnalyzeService(ApiService<Response> apiService, VideoServiceInterface videoServiceInterface, VideoFileUtils videoFileUtils) {
        this.videoServiceInterface = videoServiceInterface;
        this.apiService = apiService;
        this.videoFileUtils = videoFileUtils;
    }

    /**
     *
     * @param id
     * @return
     * @throws IOException
     * 파일을 어떤 경로에서 가져오느냐에 따른 차이. 현재는 s3로 가정 하고 url resource 사용. s3 util을 쓴다면 더 쉬울 것으로 예상
     */

    public File getAnalyzedVideo(Long id) throws IOException {

        //1. 요청받은 비디오 파일을 가져온다.
        AttachFile video = videoServiceInterface.getVideo(id);
        UrlResource videoFile = new UrlResource(video.getFilePath());

        //2. 비디오 파일을 나눈다. (A,B)
        File originalFile = videoFile.getFile();
        List<String> fileList = new ArrayList<>();
        splitVideoIntoTwo(fileList,originalFile,2);

        //3-1. A를 DL_SERVER_URL1으로 비동기로 보낸다. (분석이 된 영상을 응답받는다.)
        UrlResource firstFilResource = new UrlResource(fileList.get(0));
        File firstFile = firstFilResource.getFile();
        Response firstAnalyzedFile = apiService.post(DL_SERVER_URL1,HttpHeaders.EMPTY,firstFile, Response.class).getBody();

        //3-2. B를 DL_SERVER_URL2으로 비동기로 보낸다. (분석이 된 영상을 응답받는다.)
        UrlResource secondFileResource = new UrlResource(fileList.get(1));
        File secondFile = secondFileResource.getFile();
        Response secondAnalyzedFile = apiService.post(DL_SERVER_URL2, HttpHeaders.EMPTY, secondFile, Response.class).getBody();

        //4. 3단계가 모두 완료가 된다면, 비디오 파일을 다시 순서에 맞게 합친다.
        File analyzedFile;

        while(true) {
            if(firstAnalyzedFile != null && secondAnalyzedFile != null) {
                analyzedFile = new UrlResource(integrateFile(firstAnalyzedFile,secondAnalyzedFile)).getFile();
                break;
            }
        }
        //5. 합쳐진 비디오 파일을 돌려준다.
        return analyzedFile;
    }

    public void splitVideoIntoTwo(List<String> fileList,File originalFile, Integer chunkNumber) throws IOException {


        String videoFileName = originalFile.getName().substring(0, originalFile.getName().lastIndexOf(".")); //확장자를 제외한 Original file의 이름.

        File splitFile = new File(System.getProperty("user.dir") + "/files/" + videoFileName);

        if (!splitFile.exists()) {
            splitFile.mkdirs();
            System.out.println("Directory Created -> "+ splitFile.getAbsolutePath());
        }

        int i = 01;

        InputStream inputStream = new FileInputStream(originalFile);

        String videoFile = splitFile.getAbsolutePath() + "/"+String.format("%02d", i) +"_"+ originalFile.getName();

        OutputStream outputStream = new FileOutputStream(videoFile);

        System.out.println("file created location : " + videoFile);

        int splitSize = inputStream.available() / chunkNumber ;
        int streamSize = 0;
        int read = 0;

        System.out.println("================");
        System.out.println(">>> Start File Split");
        System.out.println("file size : "+ inputStream.available());
        System.out.println("each file size : " + splitSize);

        while((read = inputStream.read())!= -1) {

            if(splitSize == streamSize) {
                if (i <= chunkNumber) {
                    System.out.println("================");
                    System.out.println(">>> File Split completed");
                    System.out.println("split size : " + splitSize);
                    System.out.println("stream size : " + streamSize);

                    i++;

                    String fileCount = String.format("%02d",i);

                    videoFile = splitFile.getAbsolutePath()+"/" +fileCount + "_"+originalFile.getName();
                    outputStream = new FileOutputStream(videoFile);

                    System.out.println("File Created Location : " + videoFile);
                    streamSize = 0;

                    fileList.add(videoFile);
                }
            }
            outputStream.write(read);
            streamSize++;
        }

        inputStream.close();
        outputStream.close();

        System.out.println("=========");
        System.out.println("completed");
    }


    public String integrateFile(Response firstAnalyzedFile, Response secondAnalyzedFile) throws IOException {

        File[] files = {firstAnalyzedFile.getFile(), secondAnalyzedFile.getFile()};

        //TODO : s3 로직으로 바뀌어야 함
        String savePath = System.getProperty("user.dir") + "/files";
        String joinFileName = Arrays.asList(files).get(0).getName();

        String fileName = joinFileName.substring(0,joinFileName.lastIndexOf(".")) + "integrated";
        File fileJoinPath = new File(savePath + "/" + fileName);

        if (!fileJoinPath.exists()) {
            fileJoinPath.mkdirs();
            System.out.println("Created Directory -> "+ fileJoinPath.getAbsolutePath());
        }

        OutputStream outputStream = new FileOutputStream(fileJoinPath.getAbsolutePath() + "/" + joinFileName);

        for (File file : files) {
            System.out.println("Reading the file -> "+ file.getName());
            InputStream inputStream = new FileInputStream(file);

            int readByte = 0;
            while((readByte = inputStream.read()) != -1) {
                outputStream.write(readByte);
            }
            inputStream.close();
        }

        System.out.println("Join file saved at -> "+ fileJoinPath.getAbsolutePath() +"/"+ joinFileName);
        outputStream.close();

        return fileJoinPath.getAbsolutePath() +"/"+ joinFileName;
    }


}
