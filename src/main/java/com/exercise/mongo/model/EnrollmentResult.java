package com.exercise.mongo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnrollmentResult implements Serializable {
    // Object yang masuk Redis wajib implements Serializable

    private static final long serialVersionUID = 1L;
    private String student;
    private String course;
    private String semester;
    private Double score;
}
