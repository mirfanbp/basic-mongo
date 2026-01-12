package com.exercise.mongo.service;

import com.exercise.mongo.domain.Student;
import com.exercise.mongo.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;

    public Student create(Student s) {
        return studentRepository.save(s);
    }
    public List<Student> findAll() {
        return studentRepository.findAll();
    }
    public Optional<Student> findById(String id) {
        return studentRepository.findById(id);
    }
    public Student update(String id, Student s) {
        s.setId(id);
        return studentRepository.save(s);
    }
    public void delete(String id) {
        studentRepository.deleteById(id);
    }


//    @Transactional
//    public void createStudentLAndLog(Student s, LogEntry log) {
//        studentRepository.save(s);
//    }
}
