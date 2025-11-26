package com.t1impulse.interviewer.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TestTopic {
    MACHINE_LEARNING("Машинное обучение", "Классическое ML: регрессия, классификация, кластеризация, метрики качества, основные алгоритмы"),
    JAVA("Java и Spring", "Backend-разработка на Java: Spring Core, Spring MVC, JPA/Hibernate, REST API"),
    FRONTEND("Frontend", "Веб-разработка: HTML, CSS, JavaScript, TypeScript, React"),
    ALGORITHMS("Алгоритмы", "Структуры данных и алгоритмы: сортировки, поиск, графы, сложность O(n)"),
    DATA_SCIENCE("Data Science", "Анализ данных: статистика, SQL, pandas, визуализация"),
    DEVOPS("DevOps", "Инфраструктура: Linux, Docker, CI/CD, мониторинг"),
    OOP("ООП и паттерны", "Объектно-ориентированное программирование, SOLID, паттерны проектирования"),
    SQL("SQL и базы данных", "Реляционные БД: запросы, JOIN, индексы, нормализация"),
    NETWORKS("Компьютерные сети", "Сети и протоколы: TCP/IP, HTTP, DNS, модель OSI"),
    INFOSEC("Информационная безопасность", "Основы ИБ: аутентификация, шифрование, типы атак"),
    CPP("C++", "Программирование на C++: память, указатели, STL, ООП");

    private final String displayName;
    private final String description;
}
