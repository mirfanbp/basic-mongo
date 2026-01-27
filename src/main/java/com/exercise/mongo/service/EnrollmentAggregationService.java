package com.exercise.mongo.service;

import com.exercise.mongo.model.EnrollmentResult;
import com.mongodb.client.MongoClient;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EnrollmentAggregationService {
    private final MongoTemplate mongoTemplate;
    private final MongoClient mongo;

    public List<EnrollmentResult> findEnrollments(String semester, int page, int size) {
        List<AggregationOperation> operations = new ArrayList<>();

        // lookup students
        operations.add(Aggregation.lookup("students", "studentId", "_id", "student"));

        // lookup courses
        operations.add(Aggregation.lookup("courses", "courseId", "_id", "course"));

        // unwind -> jika array -> ubah jadi object biasa / dipecah
        operations.add(Aggregation.unwind("student"));operations.add(Aggregation.unwind("course"));

        // filter semester (optional)
        if (semester != null)
            operations.add(Aggregation.match(Criteria.where("semester").is(semester)));

        // sort bu score desc
        operations.add(Aggregation.sort(Sort.by(Sort.Direction.DESC, "score")));

        // pagination
        // ???
        operations.add(Aggregation.skip((long) page * size));
        operations.add(Aggregation.limit(size));

        // project field
        operations.add(Aggregation.project()
                .and("student.name").as("student")
                .and("course.name").as("course")
                .and("semester").as("semester")
                .and("score").as("score"));

        Aggregation aggregation = Aggregation.newAggregation(operations);

        // ???
        return mongoTemplate.aggregate(aggregation, "enrollments", EnrollmentResult.class).getMappedResults();
    }
}
