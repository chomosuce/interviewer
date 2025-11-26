package com.t1impulse.interviewer.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "algorithm_tasks")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlgorithmTask {

    @Id
    private Long id;

    @Column(nullable = false)
    private String titleRu;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descriptionRu;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Difficulty difficulty;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TaskTest> tests;
}

