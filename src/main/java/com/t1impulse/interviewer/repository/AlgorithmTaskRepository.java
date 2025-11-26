package com.t1impulse.interviewer.repository;

import com.t1impulse.interviewer.entity.AlgorithmTask;
import com.t1impulse.interviewer.entity.Difficulty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlgorithmTaskRepository extends JpaRepository<AlgorithmTask, Long> {

    List<AlgorithmTask> findByDifficulty(Difficulty difficulty);

    @Query(value = """
        SELECT DISTINCT t.* FROM algorithm_tasks t 
        LEFT JOIN task_tests tt ON t.id = tt.task_id 
        WHERE t.difficulty = :difficulty 
        ORDER BY RANDOM() 
        LIMIT 1
        """, nativeQuery = true)
    Optional<AlgorithmTask> findRandomByDifficulty(@Param("difficulty") String difficulty);

    @Query(value = """
        SELECT * FROM algorithm_tasks 
        ORDER BY RANDOM() 
        LIMIT 1
        """, nativeQuery = true)
    Optional<AlgorithmTask> findRandom();

    boolean existsById(Long id);
}

