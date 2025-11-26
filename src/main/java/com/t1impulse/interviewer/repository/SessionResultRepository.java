package com.t1impulse.interviewer.repository;

import com.t1impulse.interviewer.entity.SessionResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SessionResultRepository extends JpaRepository<SessionResult, Long> {

    List<SessionResult> findByCandidateId(Long candidateId);

    List<SessionResult> findByCandidate_SessionId(UUID sessionId);

    List<SessionResult> findByCandidate_AccessToken(String accessToken);
}

