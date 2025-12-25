package com.smartcode.analyzer.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;

    @Column(columnDefinition = "TEXT")
    private String metricsJson;     // store Metrics as JSON string

    @Column(columnDefinition = "TEXT")
    private String flowJson;        // store ProgramFlow as JSON string

    private double score;           // quality score

    private String pdfPath;         // path to generated PDF

    private LocalDateTime createdAt;
}
