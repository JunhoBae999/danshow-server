package com.danshow.danshowserver.service.video_service;

import com.danshow.danshowserver.aspect.TimeCheck;
import com.danshow.danshowserver.web.dto.response.Response;
import com.danshow.danshowserver.web.uploader.S3Uploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyzeService {

    private static final String DL_SERVER_URL1 = "http://localhost:8080/mirror";

    private static final String DL_SERVER_URL2 = "http://localhost:8080/mirror";

    private final VideoServiceInterface videoServiceInterface;

    private final VideoFileUtils videoFileUtils;

    private final ApiService<Response> apiService;

    private final S3Uploader s3Uploader;

    ExchangeStrategies exchangeStrategies =
            ExchangeStrategies.builder()
                    .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(-1))
        .build();// to unlimited memory size .build();

    WebClient webClient = WebClient.builder()
            .exchangeStrategies(exchangeStrategies)
            .build();

    WebClient secondWebClient = WebClient.builder()
            .exchangeStrategies(exchangeStrategies)
            .build();

    @Autowired
    public AnalyzeService(ApiService<Response> apiService, VideoServiceInterface videoServiceInterface, VideoFileUtils videoFileUtils, S3Uploader s3Uploader) {
        this.videoServiceInterface = videoServiceInterface;
        this.apiService = apiService;
        this.videoFileUtils = videoFileUtils;
        this.s3Uploader = s3Uploader;
    }

    /**
     *
     * @param id
     * @return
     * @throws IOException
     * 파일을 어떤 경로에서 가져오느냐에 따른 차이. 현재는 s3로 가정 하고 url resource 사용. s3 util을 쓴다면 더 쉬울 것으로 예상
     */

    @TimeCheck
    public String getAnalyzedVideo(MultipartFile memberTestVideo, Long id,String token) throws IOException {

        //1. 요청받은 비디오 파일을 요청받은 Id의 파일과 함께 합친 후 s3에 업로드한다.
        File savedIntegratedFile = videoServiceInterface.uploadMemberTestVideo(memberTestVideo, id);

        //2. 비디오 파일을 나눈다. (A,B)
        String localSavePath = System.getProperty("user.dir")+"/tmp";
        List<String> fileList =
                videoFileUtils.splitFile(savedIntegratedFile.getAbsolutePath(),
                        savedIntegratedFile.getName(),
                        localSavePath,
                        2);

        //3-1. A,B를 DL_SERVER_URL1으로 비동기로 보낸다. (분석이 된 영상을 응답받는다.)
        File firstFils = new File(fileList.get(0));
        File secondFile = new File(fileList.get(1));

        String firstFilePath = localSavePath+"/01_"+memberTestVideo.getOriginalFilename();
        String secondFilePath = localSavePath+"/02_"+memberTestVideo.getOriginalFilename();

        String originalFileNameWithoutExtension =
                memberTestVideo.getOriginalFilename().substring(0,memberTestVideo.getOriginalFilename().indexOf("."));

        Tuple2<byte[], byte[]> fetchVideos = fetchVideos(Files.readAllBytes(firstFils.toPath()),
                Files.readAllBytes(secondFile.toPath()), token);

        videoFileUtils.writeToFile(firstFilePath, fetchVideos.getT1());
        videoFileUtils.writeToFile(secondFilePath, fetchVideos.getT2());

        //4. 3단계가 모두 완료가 된다면, 비디오 파일을 다시 순서에 맞게 합친다.
        videoFileUtils.createTxt(firstFilePath,localSavePath,originalFileNameWithoutExtension);
        videoFileUtils.createTxt(secondFilePath,localSavePath,originalFileNameWithoutExtension);

        firstFils.delete();
        secondFile.delete();

        File analyzedFile = new File(videoFileUtils.integrateFiles(localSavePath,originalFileNameWithoutExtension));

        log.info("final file before s3 path : " + analyzedFile.getAbsolutePath());
        log.info("final file before s3 filename : " + analyzedFile.getName());

        String video = s3Uploader.upload(analyzedFile.getAbsolutePath(),analyzedFile.getName(),"video");

        log.info("video path : " + video);
        //5. 합쳐진 비디오 파일을 돌려준다.
        return video;
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

        fileList.add(videoFile);

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

    public Mono<byte[]> getFirstFile(File file) throws IOException {

        byte[] bytes = Files.readAllBytes(file.toPath());

        return webClient
                .post()
                .uri(DL_SERVER_URL1)
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .retrieve()
                .bodyToMono(byte[].class);
    }

    public Mono<byte[]> getFirstFile(byte[] bytes, String token) throws IOException {

        return webClient
                .post()
                .uri(DL_SERVER_URL1)
                .header("X-AUTH-TOKEN",token)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .bodyValue(bytes)
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .retrieve()
                .bodyToMono(byte[].class)
                .subscribeOn(Schedulers.parallel())
                .map(x -> {
                    log.info("async method call : getFirst method called");
                    return x;
                });
    }

    public Mono<byte[]> getSecondFile(File file) throws IOException {

        byte[] bytes = Files.readAllBytes(file.toPath());

        return secondWebClient
                .post()
                .uri(DL_SERVER_URL2)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .retrieve()
                .bodyToMono(byte[].class)
                .subscribeOn(Schedulers.parallel());
    }

    public Mono<byte[]> getSecondFile(byte[] bytes, String token) throws IOException {


        return webClient
                .post()
                .uri(DL_SERVER_URL2)
                .header("X-AUTH-TOKEN",token)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .bodyValue(bytes)
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .retrieve()
                .bodyToMono(byte[].class)
                .subscribeOn(Schedulers.parallel())
                .map(x -> {
                    log.info("async method call : getSecondFile method called");
                    return x;
                });
    }

    public Tuple2<byte[], byte[]> fetchVideos(byte[] firstFile, byte[] secondFile, String token) throws IOException {
        return Mono.zip(getFirstFile(firstFile,token), getSecondFile(secondFile,token))
                .block();
    }
}
