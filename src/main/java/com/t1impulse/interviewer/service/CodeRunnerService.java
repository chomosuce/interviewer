package com.t1impulse.interviewer.service;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import jakarta.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.t1impulse.interviewer.config.LangConfig;
import com.t1impulse.interviewer.config.LangConfigs;
import com.t1impulse.interviewer.dto.ExecResult;
import com.t1impulse.interviewer.dto.RunRequest;
import com.t1impulse.interviewer.dto.RunResponse;
import com.t1impulse.interviewer.dto.TestDto;
import com.t1impulse.interviewer.dto.TestResultDto;


@Service
public class CodeRunnerService {

    @PostConstruct
    public void warmupImages() {
        for (LangConfig cfg : LangConfigs.CONFIGS.values()) {
            try {
                ProcessBuilder pb = new ProcessBuilder("docker", "pull", cfg.image());
                pb.redirectErrorStream(true);
                Process process = pb.start();
                // Ждём, но не навечно, чтобы не вешать приложение
                process.waitFor(300_000, TimeUnit.MILLISECONDS);
                System.out.println("Image " + cfg.image() + " was pulled");
            } catch (Exception ignored) {
                System.out.println("Не получилось подтянуть образ " + cfg.image());
            }
        }
    }

    public RunResponse run(RunRequest req) {
        LangConfig cfg = LangConfigs.CONFIGS.get(req.language());
        if (cfg == null) {
            throw new IllegalArgumentException("Unsupported language: " + req.language());
        }

        try {
            Path tmpDir = Files.createTempDirectory("job-");
            Path srcFile = tmpDir.resolve(cfg.sourceFile());
            Files.writeString(srcFile, req.source(), StandardCharsets.UTF_8);

            // 1. Компиляция (если нужна)
            String compileError = null;
            if (!cfg.compileCmd().isEmpty()) {
                ExecResult compileRes = dockerExec(
                        cfg.image(),
                        tmpDir,
                        cfg.compileCmd(),
                        null,
                        req.timeLimitMs(),
                        req.memoryLimitMb());
                if (compileRes.exitCode() != 0) {
                    compileError = compileRes.stderr();
                    return new RunResponse(
                            "COMPILATION_ERROR",
                            compileError,
                            null,
                            List.of());
                }
            }

            // 2. Прогон тестов
            List<TestResultDto> results = new ArrayList<>();
            boolean allOk = true;
            String runtimeError = null;

            for (int i = 0; i < req.tests().size(); i++) {
                TestDto t = req.tests().get(i);

                ExecResult runRes = dockerExec(
                        cfg.image(),
                        tmpDir,
                        cfg.runCmd(),
                        t.input(),
                        req.timeLimitMs(),
                        req.memoryLimitMb());

                String status;
                String got = runRes.stdout().trim();
                String expected = t.expectedOutput().trim();

                if (runRes.timeout()) {
                    status = "TLE";
                    allOk = false;
                } else if (runRes.exitCode() != 0) {
                    status = "RUNTIME_ERROR";
                    allOk = false;
                    runtimeError = runRes.stderr();
                } else if (got.equals(expected)) {
                    status = "OK";
                } else {
                    status = "WRONG_ANSWER";
                    allOk = false;
                }

                results.add(new TestResultDto(
                        i,
                        status,
                        expected,
                        got,
                        runRes.stderr()));
            }

            return new RunResponse(
                    allOk ? "OK" : "PARTIAL",
                    null,
                    runtimeError,
                    results);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private ExecResult dockerExec(
        String image,
        Path workDir,
        List<String> cmd,
        String stdin,
        int timeLimitMs,
        int memoryLimitMb
) {
    try {
        List<String> fullCmd = new ArrayList<>();
        fullCmd.add("docker");
        fullCmd.add("run");
        fullCmd.add("-i"); // keep stdin open so we can pass test input
        fullCmd.add("--rm");
        fullCmd.add("--network=none");
        fullCmd.add("--cpus=1");
        fullCmd.add("-m");
        fullCmd.add(memoryLimitMb + "m");
        fullCmd.add("--pids-limit=64");
        fullCmd.add("-v");
        fullCmd.add(workDir.toAbsolutePath() + ":/code");
        fullCmd.add("-w");
        fullCmd.add("/code");
        fullCmd.add(image);
        fullCmd.addAll(cmd);

        ProcessBuilder pb = new ProcessBuilder(fullCmd);
        Process process = pb.start();

        if (stdin != null) {
            try (OutputStream os = process.getOutputStream()) {
                os.write(stdin.getBytes(StandardCharsets.UTF_8));
                os.flush();
            }
        }

        boolean finished = process.waitFor(timeLimitMs, TimeUnit.MILLISECONDS);

        if (!finished) {
            process.destroyForcibly();
            return new ExecResult(-1, "", "TIMEOUT", true);
        }

        String stdout = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        String stderr = new String(process.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);

        return new ExecResult(process.exitValue(), stdout, stderr, false);
    } catch (Exception e) {
        return new ExecResult(-1, "", e.getMessage(), false);
    }
}
}
