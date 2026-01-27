package com.exercise.mongo.service;

import com.exercise.mongo.model.StudentAverageScoreByAddress;
import com.exercise.mongo.model.StudentCountByAddress;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentAggregationService {
    private final MongoTemplate mongoTemplate;
    private final MongoOperations mongoOperations;
    public List<StudentCountByAddress> countByAddress;

    public List<StudentCountByAddress> countAllByAddress() {
        Aggregation aggregation = Aggregation.newAggregation(
                // grup student by address
                Aggregation.group("address")
                        .count().as("totalStudents"),
                // then count total student every address
                Aggregation.project("totalStudents")
                        .and("_id").as("address") // _id rename to address
        );

        return mongoOperations.aggregate(
                aggregation, "students", StudentCountByAddress.class
        ).getMappedResults();
    }

    public StudentCountByAddress countByAddress(String address) {
        Aggregation aggregation = Aggregation.newAggregation(
                // grup student by address
                Aggregation.match(
                        Criteria.where("address").is(address)
                ),
                Aggregation.group("address")
                        .count().as("totalStudents"),
                // then count total student every address
                Aggregation.project("totalStudents")
                        .and("_id").as("address") // _id rename to address
        );

        return mongoOperations.aggregate(
                aggregation, "students", StudentCountByAddress.class
        ).getUniqueMappedResult();
    }

    public List<StudentAverageScoreByAddress> averageScoreByAddresses(){
        // match → group → project
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(
                        Criteria.where("scores.math").exists(true)
                ),
                Aggregation.group("address")
                        .avg("scores.math").as("avgMathScore"),
                // rapihkan output
                Aggregation.project("avgMathScore")
                        .and("_id").as("address")
        );

        return mongoOperations.aggregate(
                aggregation,
                "students",
                StudentAverageScoreByAddress.class
        ).getMappedResults();
    }

    public List<StudentAverageScoreByAddress> averageScoreByAddresses(
        int minAge, int maxAge, int page, int size) {
        int skip = (page - 1 ) * size;
            Aggregation aggregation = Aggregation.newAggregation(
                    // filter
                    Aggregation.match(
                        Criteria.where("age")
                                .gte(minAge).lte(maxAge)
                                .and("scores.math").exists(true)
                    ),
                    // group + avg
                    Aggregation.group("address")
                            .avg("scores.math").as("avgMathScore"),
                    // sort
                    Aggregation.sort(Sort.Direction.DESC, "avgMathScore"),
                    // project
                    Aggregation.project("avgMathScore")
                            .and("_id").as("address"),
                    // pagination
                    Aggregation.skip(skip),
                    Aggregation.limit(size)
            );

            return mongoOperations.aggregate(
                    aggregation,
                    "students",
                    StudentAverageScoreByAddress.class
            ).getMappedResults();
    }

}

