package com.t1impulse.interviewer.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "task_tests")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private AlgorithmTask task;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String testInput;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String testOutput;
}

