package com.danshow.danshowserver.service.video_service;

import lombok.extern.slf4j.Slf4j;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import org.modelmapper.internal.util.Assert;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Slf4j
@Component
public class VideoFileUtils {

    private String ffmpegPath = "/usr/local/bin/ffmpeg";

    private String ffprobePath = "/usr/local/bin/ffprobe";

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

    public void splitFile(String inputPath,String originalFileName, String outputPath ,Integer chunkNumber) throws IOException {

        FFmpegProbeResult probeResult = fFprobe.probe(inputPath);

        Double totalDuration = probeResult.getFormat().duration;
        Double streamSize = totalDuration / chunkNumber;

        String originalFileNameWithoutExtension = originalFileName.substring(0,originalFileName.indexOf("."));

        int startPoint = 0;

        File fileJoinPath = new File(outputPath + "/" +originalFileNameWithoutExtension);

        if (!fileJoinPath.exists()) {
            fileJoinPath.mkdirs();
            log.info("Created Directory -> "+ fileJoinPath.getAbsolutePath());
        }

        for(int i = 1; i<=chunkNumber; i++) {

            log.info("output path : " + fileJoinPath);

            String totalPath = fileJoinPath + "/"+originalFileNameWithoutExtension+"_"+i+".mp4";

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

            createTxt(totalPath, fileJoinPath.getAbsolutePath(), originalFileNameWithoutExtension);


            startPoint += streamSize;

        }

    }

    /**
     * @param inputPath split과 다르게 inputpath에 만듭니다.
     * @param originalFileName
     * @throws IOException
     */
    public void integrateFiles(String inputPath,String originalFileName) throws IOException {

        String fileList = inputPath + "/" + originalFileName+".txt";

        FFmpegBuilder builder = new FFmpegBuilder()
                .overrideOutputFiles(true)
                .addInput(fileList)
                .addExtraArgs("-f","concat")
                .addExtraArgs("-safe", "0")
                .addOutput(inputPath + "/" + originalFileName + "_merged.mp4")
                .done();

        FFmpegExecutor executor = new FFmpegExecutor(fFmpeg, fFprobe);
        executor.createJob(builder).run();
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

}
