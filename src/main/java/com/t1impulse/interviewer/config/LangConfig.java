package com.t1impulse.interviewer.config;

import java.util.List;

public record LangConfig(
        String image,
        String sourceFile,
        List<String> compileCmd,
        List<String> runCmd) {
}