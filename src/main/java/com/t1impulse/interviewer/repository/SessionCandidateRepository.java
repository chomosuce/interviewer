package com.t1impulse.interviewer.repository;

import com.t1impulse.interviewer.entity.SessionCandidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SessionCandidateRepository extends JpaRepository<SessionCandidate, Long> {

    Optional<SessionCandidate> findByAccessToken(String accessToken);

    List<SessionCandidate> findBySessionId(UUID sessionId);
}

