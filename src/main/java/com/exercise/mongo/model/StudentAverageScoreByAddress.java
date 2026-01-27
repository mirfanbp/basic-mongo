package com.exercise.mongo.model;

import lombok.Data;

@Data
public class StudentAverageScoreByAddress {
    private String address;
    private Double avgMathScore;
}

