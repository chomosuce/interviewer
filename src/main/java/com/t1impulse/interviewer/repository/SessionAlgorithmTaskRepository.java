package com.t1impulse.interviewer.repository;

import com.t1impulse.interviewer.entity.SessionAlgorithmTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SessionAlgorithmTaskRepository extends JpaRepository<SessionAlgorithmTask, Long> {

    List<SessionAlgorithmTask> findBySessionId(UUID sessionId);
}

