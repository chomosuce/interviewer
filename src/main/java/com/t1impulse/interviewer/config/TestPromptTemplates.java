package com.t1impulse.interviewer.config;

import java.util.EnumMap;
import java.util.Map;

public final class TestPromptTemplates {

    public record PromptTemplate(String model, String system, String userTemplate, double temperature, double topP,
                                 int maxTokens) {
        
        /**
         * Возвращает user prompt с указанным количеством вопросов
         */
        public String getUserPrompt(int questionCount) {
            return userTemplate.replace("{QUESTION_COUNT}", String.valueOf(questionCount));
        }
    }

    private static final String JSON_FORMAT_INSTRUCTION = """
            
            ВАЖНО: Выведи результат ТОЛЬКО в формате JSON без каких-либо дополнительных комментариев.
            Формат ответа:
            {
              "questions": [
                {
                  "text": "Текст вопроса",
                  "options": {
                    "a": "Вариант ответа A",
                    "b": "Вариант ответа B",
                    "c": "Вариант ответа C",
                    "d": "Вариант ответа D"
                  },
                  "correctAnswer": "буква правильного ответа (a, b, c или d)"
                }
              ]
            }
            Никакого текста до или после JSON. Только валидный JSON.
            """;

    private static final Map<TestTopic, PromptTemplate> TEMPLATES = new EnumMap<>(TestTopic.class);

    static {
        TEMPLATES.put(TestTopic.MACHINE_LEARNING, new PromptTemplate(
                "qwen3-32b-awq",
                "/no_think Ты — строгий и аккуратный преподаватель по машинному обучению. Твоя задача — генерировать тесты по классическому машинному обучению. Отвечай ТОЛЬКО валидным JSON.",
                """
                        Сгенерируй тест из {QUESTION_COUNT} вопросов по классическому машинному обучению.
                        Темы: supervised/unsupervised/reinforcement learning, регрессия, классификация, переобучение, регуляризация, кросс-валидация, основные алгоритмы (линейная/логистическая регрессия, k-NN, деревья решений, Random Forest, SVM, k-means), метрики качества (accuracy, precision, recall, F1, ROC-AUC).
                        Уровень сложности: средний.
                        Пиши на русском языке.
                        """ + JSON_FORMAT_INSTRUCTION,
                0.4, 0.9, 4000));

        TEMPLATES.put(TestTopic.BACKEND, new PromptTemplate(
                "qwen3-32b-awq",
                "/no_think Ты — строгий преподаватель по backend-разработке на Java/Spring. Отвечай ТОЛЬКО валидным JSON.",
                """
                        Сгенерируй тест из {QUESTION_COUNT} вопросов по backend-разработке на Java и Spring.
                        Темы: основы Java (классы, интерфейсы, коллекции), HTTP и REST, Spring Core (DI, бины), Spring MVC, JPA/Hibernate, базовая безопасность.
                        Уровень сложности: средний.
                        Пиши на русском языке.
                        """ + JSON_FORMAT_INSTRUCTION,
                0.4, 0.9, 4000));

        TEMPLATES.put(TestTopic.FRONTEND, new PromptTemplate(
                "qwen3-32b-awq",
                "/no_think Ты — строгий преподаватель по frontend-разработке. Отвечай ТОЛЬКО валидным JSON.",
                """
                        Сгенерируй тест из {QUESTION_COUNT} вопросов по frontend-разработке.
                        Темы: HTML, CSS (селекторы, box-model, flex, grid), JavaScript (типы, функции, промисы, async/await), TypeScript, React (компоненты, пропсы, state, useEffect), SPA, работа с API.
                        Уровень сложности: средний.
                        Пиши на русском языке.
                        """ + JSON_FORMAT_INSTRUCTION,
                0.4, 0.9, 4000));

        TEMPLATES.put(TestTopic.ALGORITHMS, new PromptTemplate(
                "qwen3-32b-awq",
                "/no_think Ты — строгий преподаватель по алгоритмам и структурам данных. Отвечай ТОЛЬКО валидным JSON.",
                """
                        Сгенерируй тест из {QUESTION_COUNT} вопросов по алгоритмам и структурам данных.
                        Темы: массив, список, стек, очередь, хэш-таблица, дерево, Big-O, сортировки, бинарный поиск, графы (BFS, DFS).
                        Уровень сложности: средний.
                        Пиши на русском языке.
                        """ + JSON_FORMAT_INSTRUCTION,
                0.4, 0.9, 4000));

        TEMPLATES.put(TestTopic.DATA_SCIENCE, new PromptTemplate(
                "qwen3-32b-awq",
                "/no_think Ты — строгий преподаватель по Data Science и аналитике данных. Отвечай ТОЛЬКО валидным JSON.",
                """
                        Сгенерируй тест из {QUESTION_COUNT} вопросов по Data Science и аналитике.
                        Темы: описательная статистика, вероятность, SQL (SELECT, JOIN, GROUP BY), pandas, визуализация данных.
                        Уровень сложности: средний.
                        Пиши на русском языке.
                        """ + JSON_FORMAT_INSTRUCTION,
                0.4, 0.9, 4000));

        TEMPLATES.put(TestTopic.DEVOPS, new PromptTemplate(
                "qwen3-32b-awq",
                "/no_think Ты — строгий преподаватель по DevOps и инфраструктуре. Отвечай ТОЛЬКО валидным JSON.",
                """
                        Сгенерируй тест из {QUESTION_COUNT} вопросов по DevOps.
                        Темы: Linux, Docker, docker-compose, CI/CD (pipeline, build, deploy), мониторинг и логирование, staging vs production.
                        Уровень сложности: средний.
                        Пиши на русском языке.
                        """ + JSON_FORMAT_INSTRUCTION,
                0.4, 0.9, 4000));

        TEMPLATES.put(TestTopic.OOP, new PromptTemplate(
                "qwen3-32b-awq",
                "/no_think Ты — строгий преподаватель по ООП и паттернам проектирования. Отвечай ТОЛЬКО валидным JSON.",
                """
                        Сгенерируй тест из {QUESTION_COUNT} вопросов по ООП и паттернам проектирования.
                        Темы: инкапсуляция, наследование, полиморфизм, абстракция, интерфейсы, паттерны (Singleton, Factory, Strategy, Observer), SOLID.
                        Уровень сложности: средний.
                        Пиши на русском языке.
                        """ + JSON_FORMAT_INSTRUCTION,
                0.4, 0.9, 4000));

        TEMPLATES.put(TestTopic.SQL, new PromptTemplate(
                "qwen3-32b-awq",
                "/no_think Ты — строгий преподаватель по реляционным базам данных и SQL. Отвечай ТОЛЬКО валидным JSON.",
                """
                        Сгенерируй тест из {QUESTION_COUNT} вопросов по SQL и базам данных.
                        Темы: таблицы, ключи, нормализация, SELECT/INSERT/UPDATE/DELETE, WHERE, ORDER BY, JOIN, агрегатные функции, GROUP BY, HAVING.
                        Уровень сложности: средний.
                        Пиши на русском языке.
                        """ + JSON_FORMAT_INSTRUCTION,
                0.4, 0.9, 4000));

        TEMPLATES.put(TestTopic.NETWORKS, new PromptTemplate(
                "qwen3-32b-awq",
                "/no_think Ты — строгий преподаватель по компьютерным сетям. Отвечай ТОЛЬКО валидным JSON.",
                """
                        Сгенерируй тест из {QUESTION_COUNT} вопросов по компьютерным сетям.
                        Темы: модель OSI, TCP vs UDP, IP-адресация, HTTP, DNS, DHCP, switch vs router, LAN vs WAN.
                        Уровень сложности: средний.
                        Пиши на русском языке.
                        """ + JSON_FORMAT_INSTRUCTION,
                0.4, 0.9, 4000));

        TEMPLATES.put(TestTopic.INFOSEC, new PromptTemplate(
                "qwen3-32b-awq",
                "/no_think Ты — строгий преподаватель по информационной безопасности. Отвечай ТОЛЬКО валидным JSON.",
                """
                        Сгенерируй тест из {QUESTION_COUNT} вопросов по информационной безопасности.
                        Темы: конфиденциальность, целостность, доступность, аутентификация vs авторизация, типы атак (phishing, brute force, SQL injection), шифрование, безопасность паролей.
                        Уровень сложности: средний.
                        Пиши на русском языке.
                        """ + JSON_FORMAT_INSTRUCTION,
                0.4, 0.9, 4000));

        TEMPLATES.put(TestTopic.CPP, new PromptTemplate(
                "qwen3-32b-awq",
                "/no_think Ты — строгий преподаватель по C++. Отвечай ТОЛЬКО валидным JSON.",
                """
                        Сгенерируй тест из {QUESTION_COUNT} вопросов по C++.
                        Темы: типы данных, функции, указатели и ссылки, работа с памятью (стек/куча, new/delete), ООП в C++, STL (vector, string, алгоритмы).
                        Уровень сложности: средний.
                        Пиши на русском языке.
                        """ + JSON_FORMAT_INSTRUCTION,
                0.4, 0.9, 4000));
    }

    private TestPromptTemplates() {
    }

    public static PromptTemplate get(TestTopic topic) {
        return TEMPLATES.get(topic);
    }
}
