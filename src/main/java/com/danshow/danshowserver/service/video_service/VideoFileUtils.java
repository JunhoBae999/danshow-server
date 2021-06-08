package com.danshow.danshowserver.service.video_service;

import com.danshow.danshowserver.aspect.TimeCheck;
import lombok.extern.slf4j.Slf4j;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import org.modelmapper.internal.util.Assert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class VideoFileUtils {

    @Value("${local.ffmpeg}")
    private String ffmpegPath;
    @Value("${local.ffprobe}")
    private String ffprobePath;

    private FFmpeg fFmpeg;

    private FFprobe fFprobe;

    @PostConstruct
    public void init() {
        try {
            fFmpeg = new FFmpeg(ffmpegPath);
            Assert.isTrue(fFmpeg.isFFmpeg());

            fFprobe = new FFprobe(ffprobePath);
            Assert.isTrue(fFprobe.isFFprobe());

            log.debug("VideoFIleUtils init complete");

        }catch (Exception e) {
            log.error("VideoFileUtils init fail", e);
        }
    }

    @TimeCheck
    public List<String> splitFile(String inputPath, String originalFileName, String outputPath , Integer chunkNumber) throws IOException {
        FFmpegProbeResult probeResult = fFprobe.probe(inputPath);

        Double totalDuration = probeResult.getFormat().duration;
        Double streamSize = totalDuration / chunkNumber;

        String originalFileNameWithoutExtension = originalFileName.substring(0,originalFileName.indexOf("."));
        String originalFileNameWithoutExtensionWithUUID = UUID.randomUUID().toString() + "-" + originalFileNameWithoutExtension;

        List<String> splitFileList = new ArrayList<String>();
        int startPoint = 0;

        createDirectory(outputPath);

        for(int i = 1; i<=chunkNumber; i++) {

            String totalPath = outputPath + "/"+originalFileNameWithoutExtensionWithUUID+"_"+i+".mp4";

            FFmpegBuilder builder = new FFmpegBuilder()
                    .overrideOutputFiles(true)
                    .addInput(inputPath)
                    .addExtraArgs("-ss", String.valueOf(startPoint))
                    .addExtraArgs("-t", String.valueOf(streamSize))
                    .addOutput(totalPath)
                    .done();

            FFmpegExecutor executor = new FFmpegExecutor(fFmpeg, fFprobe);
            executor.createJob(builder, p -> {
                if(p.isEnd()) {
                    log.info("split completed processed" );
                }
            }).run();

            log.info("split done");

            //createTxt(totalPath, outputPath, originalFileNameWithoutExtensionWithUUID);

            splitFileList.add(totalPath);
            startPoint += streamSize;
        }
        return splitFileList;
    }

    /**
     * @param inputPath split과 다르게 inputpath에 만듭니다.
     * @param originalFileName
     * @throws IOException
     */
    @TimeCheck
    public String integrateFiles(String inputPath,String originalFileName) throws IOException {

        String fileList = inputPath + "/" + originalFileName+".txt";

        String outputPath = inputPath + "/" + originalFileName + "_final_ver.mp4";

        FFmpegBuilder builder = new FFmpegBuilder()
                .overrideOutputFiles(true)
                .addInput(fileList)
                .addExtraArgs("-f","concat")
                .addExtraArgs("-safe", "0")
                .addOutput(outputPath)
                .done();

        FFmpegExecutor executor = new FFmpegExecutor(fFmpeg, fFprobe);
        executor.createJob(builder).run();

        log.info("final integrate phase complete, output path  : " + outputPath);
        return outputPath;

    }

    //로컬에 있는 파일로부터 썸네일 생성
    public String extractThumbnail(String inputPath,String originalFileName, String outputPath) throws IOException {

        String originalFileNameWithoutExtension = originalFileName.substring(0,originalFileName.indexOf("."));
        outputPath = outputPath + "/" + originalFileNameWithoutExtension + "_thumbnail.gif";

        log.info("new ouput path : " + outputPath);


        FFmpegBuilder builder = new FFmpegBuilder()
                .overrideOutputFiles(true)
                .setInput(inputPath)
                .addExtraArgs("-ss", "00:00:01")
                .addOutput(outputPath)
                .setFrames(1)
                .done();

        FFmpegExecutor executor = new FFmpegExecutor(fFmpeg, fFprobe);		// FFmpeg 명령어 실행을 위한 FFmpegExecutor 객체 생성
        executor.createJob(builder).run();
        return outputPath;
    }
    
    // 멀티파일을 받아서 로컬에 저장하고 썸네일 생성
    public String extractThumbnail(MultipartFile video) throws IOException {
        String originalFileName = video.getOriginalFilename();
        String outputPath = System.getProperty("user.dir") + "/files";
        String inputPath = System.getProperty("user.dir") + "/files/"
                +originalFileName.substring(0,originalFileName.indexOf("."))+"/"+originalFileName;
        video.transferTo(new File(inputPath));
        return extractThumbnail(inputPath, originalFileName, outputPath);
    }

    //멀티파일과 저장할 파일 이름을 받아서 로컬에 저장하고 썸네일 생성
    public String extractThumbnail(MultipartFile video, String originalFileName) throws IOException {
        String outputPath = System.getProperty("user.dir") + "/files";

        log.info("output-path : "+outputPath);

        String inputPath = System.getProperty("user.dir") + "/files/"+originalFileName;

        log.info("inputp-path :" + inputPath);

        video.transferTo(new File(inputPath));
        return extractThumbnail(inputPath, originalFileName, outputPath);
    }

    public void createTxt(String totalFilePath, String fileJoinPath, String originalFileNameWithoutExtension) {

        String fileName = fileJoinPath + "/" + originalFileNameWithoutExtension + ".txt";

        try{
            File file = new File(fileName);

            FileWriter fw = new FileWriter(file, true);

            fw.write("file " + "'" + totalFilePath + "'\n");
            fw.flush();
            fw.close();
        }catch (Exception e) {
            log.error("txt file creation fail : " + e);
        }
    }

    public void deleteFile(String filePath) {
        File deleteFile = new File(filePath);
        if(deleteFile.exists()) {
            deleteFile.delete();
        }
    }

    public String integrateFileSideBySide(String firstVideoPath, String secondVideoPath, String outputPath) {

        FFmpegBuilder builder = new FFmpegBuilder()
                .overrideOutputFiles(true)
                .addInput(firstVideoPath)
                .addInput(secondVideoPath)
                .addOutput(outputPath)
                .addExtraArgs("-preset", "ultrafast")
                .addExtraArgs("-filter_complex", "[0:v]setpts=PTS-STARTPTS, pad=iw*2+5:ih[bg]; [1:v]setpts=PTS-STARTPTS[fg]; [bg][fg]overlay=w+10")
                .done();
        
        FFmpegExecutor executor = new FFmpegExecutor(fFmpeg, fFprobe);
        executor.createJob(builder).run();

        return outputPath;

    }

    public String extractAudio(String inputPath,String originalFileName, String outputPath) throws IOException {
        String originalFileNameWithoutExtension = originalFileName.substring(0,originalFileName.indexOf("."));
        outputPath = outputPath + "/" +originalFileNameWithoutExtension + "_audio.mp3";

        FFmpegBuilder builder = new FFmpegBuilder()
                .overrideOutputFiles(true)
                .setInput(inputPath)
                .addOutput(outputPath)
                .addExtraArgs("-ab","128k")
                .addExtraArgs("-vn")                    //비디오 추출 안함
                //.addExtraArgs("-acodec","libmp3lame")   //오디오 코덱 지정, mp3
                .addExtraArgs("-ar","44.1k")            //sampling rate
                .addExtraArgs("-ac","2")                //오디오 2채널
                .addExtraArgs("-f","mp3")
                .done();

        FFmpegExecutor executor = new FFmpegExecutor(fFmpeg, fFprobe);		// FFmpeg 명령어 실행을 위한 FFmpegExecutor 객체 생성
        executor.createJob(builder).run();
        return outputPath;
    }

    //멀티파일과 저장할 파일 이름을 받아서 로컬에 저장하고 오디오 생성
    public String extractAudio(MultipartFile video, String originalFileName) throws IOException {
        String outputPath = System.getProperty("user.dir") + "/files";
        String inputPath = System.getProperty("user.dir") + "/files/"+originalFileName;
        video.transferTo(new File(inputPath));
        return extractAudio(inputPath, originalFileName, outputPath);
    }

    public void createDirectory(String path) {
        File fileJoinPath = new File(path);

        if (!fileJoinPath.exists()) {
            fileJoinPath.mkdirs();
        }
    }

    @TimeCheck
    public void writeToFile(String filename, byte[] pData) {

        if(pData == null){
            return;
        }
        int lByteArraySize = pData.length;
        try{
            File lOutFile = new File(filename);
            FileOutputStream lFileOutputStream = new FileOutputStream(lOutFile);
            lFileOutputStream.write(pData);
            lFileOutputStream.close();
        }catch(Throwable e){
            e.printStackTrace(System.out);
        }
    }
}
