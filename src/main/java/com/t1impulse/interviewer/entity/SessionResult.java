package com.t1impulse.interviewer.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "session_results")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    private SessionCandidate candidate;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String testResults;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String algorithmResults;

    @Column(nullable = false)
    @Builder.Default
    private Boolean violationDetected = false;

    @Column(nullable = false)
    private LocalDateTime submittedAt;

    @PrePersist
    protected void onCreate() {
        submittedAt = LocalDateTime.now();
        if (violationDetected == null) {
            violationDetected = false;
        }
    }
}

