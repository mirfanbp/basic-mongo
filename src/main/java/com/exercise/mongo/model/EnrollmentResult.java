package com.exercise.mongo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnrollmentResult {
    private String student;
    private String course;
    private String semester;
    private Double score;
}
