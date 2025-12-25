package com.smartcode.analyzer.repository;

import com.smartcode.analyzer.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    List<Report> findByFileNameContainingIgnoreCase(String query);
}
