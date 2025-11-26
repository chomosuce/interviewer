package com.t1impulse.interviewer.repository;

import com.t1impulse.interviewer.config.TestTopic;
import com.t1impulse.interviewer.entity.GeneratedTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GeneratedTestRepository extends JpaRepository<GeneratedTest, Long> {
    
    List<GeneratedTest> findByTopic(TestTopic topic);
    
    List<GeneratedTest> findByTopicOrderByCreatedAtDesc(TestTopic topic);
}

