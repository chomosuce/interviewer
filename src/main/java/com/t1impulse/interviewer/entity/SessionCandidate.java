package com.t1impulse.interviewer.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "session_candidates")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionCandidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private InterviewSession session;

    @Column(nullable = false, unique = true)
    private String accessToken;

    @Column(nullable = false)
    private String candidateName;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    @Builder.Default
    private Boolean started = false;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (started == null) {
            started = false;
        }
    }
}

