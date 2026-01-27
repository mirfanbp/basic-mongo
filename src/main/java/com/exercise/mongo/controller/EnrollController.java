package com.exercise.mongo.controller;

import com.exercise.mongo.model.EnrollmentResult;
import com.exercise.mongo.service.EnrollmentAggregationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/enrollments")
public class EnrollController {
    private final EnrollmentAggregationService enrollmentAggregationService;

    @GetMapping
    public List<EnrollmentResult> getEnrollments(
            @RequestParam(required = false) String semester,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return enrollmentAggregationService.findEnrollments(semester, page, size);
    }
}
