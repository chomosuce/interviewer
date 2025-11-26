package com.t1impulse.interviewer.repository;

import com.t1impulse.interviewer.entity.TaskTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskTestRepository extends JpaRepository<TaskTest, Long> {

    List<TaskTest> findByTaskId(Long taskId);
}

