package com.t1impulse.interviewer.config;

import java.util.List;
import java.util.Map;

public class LangConfigs {
    public static final Map<Language, LangConfig> CONFIGS = Map.of(
            Language.PYTHON, new LangConfig(
                    "python:3.11-alpine",
                    "main.py",
                    List.of(),
                    List.of("python", "main.py")),
            Language.JAVA, new LangConfig(
                    "eclipse-temurin:21-jdk-alpine",
                    "Main.java",
                    List.of("javac", "Main.java"),
                    List.of("java", "Main")),
            Language.CPP, new LangConfig(
                    "gcc:13.2.0",
                    "main.cpp",
                    List.of("bash", "-lc", "g++ main.cpp -O2 -std=c++17 -o main"),
                    List.of("./main")),
            Language.JS, new LangConfig(
                    "node:22-alpine",
                    "main.js",
                    List.of(),
                    List.of("node", "main.js")));
}