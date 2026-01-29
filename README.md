# Aggregation Practice (MongoDB)

Basic mongoDB practice to understand:
- Aggregation Pipeline
- Embedded Document
- Referenced each collection

---

## Aggregation

### Definition
> Processing data in the database before sending it to the application  
> _(Mengolah data langsung di database sebelum dikirim ke aplikasi)_

---

## Schema

### Students
```mongodb-json
{
    "_id": ObjectId,
    "name": String,
    "address": String,
    "age": Number,
    "phone": String
}
```

### Courses
```mongodb-json
{
    "_id": ObjectId,
    "name": String,
    "credit": Number
}
```
### Enrolments
```mongodb-json
{
    "_id": ObjectId,
    "studentId": ObjectId,
    "courseId": ObjectId,
    "semester": String,
    "score": Number
}
```
---

## Sample Data
### Insert student
```mongodb-json
db.students.insertMany([
  { name: "Andi", address: "Bandung", age: 21, phone: "0811" },
  { name: "Budi", address: "Bandung", age: 22, phone: "0812" },
  { name: "Citra", address: "Jakarta", age: 20, phone: "0813" },
  { name: "Dewi", address: "Jakarta", age: 23, phone: "0814" },
  { name: "Eko", address: "Surabaya", age: 24, phone: "0815" },
  { name: "Fajar", address: "Surabaya", age: 22, phone: "0816" },
  { name: "Gilang", address: "Bandung", age: 21, phone: "0817" },
  { name: "Hana", address: "Bandung", age: 25, phone: "0818" },
  { name: "Indra", address: "Jakarta", age: 26, phone: "0819" },
  { name: "Joko", address: "Jakarta", age: 22, phone: "0820" }
])
```

### Insert student for embedded
```mongodb-json
db.students_scores.insertMany([
  { name: "Andi", address: "Bandung", age: 21, scores: { math: 80, english: 75, science: 78 } },
  { name: "Budi", address: "Bandung", age: 22, scores: { math: 85, english: 70, science: 80 } },
  { name: "Citra", address: "Jakarta", age: 20, scores: { math: 78, english: 82, science: 75 } },
  { name: "Dewi", address: "Jakarta", age: 23, scores: { math: 90, english: 88, science: 92 } },
  { name: "Eko", address: "Surabaya", age: 24, scores: { math: 70, english: 72, science: 68 } },
  { name: "Fajar", address: "Surabaya", age: 22, scores: { math: 75, english: 74, science: 73 } }
])
```

---
# Practice
## Basic Aggregation
### Count Students Grouped by Address
```mongodb-json
db.students.aggregate([
    {
        $group: {
            _id: "$address",   // group by address field
            count: { $sum: 1 } // count students per address
        }
    }
])
```
### Filter & Count (Match + Group)
```mongodb-json
// Flow: filter -> group -> count
db.students.aggregate([
    {
        $match: {
            address: "Bandung" // filter by specific address
        }
    },
    {
        $group: {
            _id: "$address",
            count: { $sum: 1 }
        }
    }
])
```

## Aggregation with Embedded Data
### Average Score with Age Filter & Pagination
```mongodb-json
db.students.aggregate([
    {
        $match: {
            age: { $gte: 20, $lte: 25 },      // age range filter (20-25)
            "scores.math": { $exists: true }  // ensure  math field exist
        }
    },
    {
        $group: {
            _id: "$address",
            avgMathScore: { $avg: "$scores.math" }
        }
    },
    { $sort: { avgMathScore: -1 } },          // 1: asc, -1: desc
    {
        $project: {
            _id: 0,                           // hide internal _id
            address: "$_id",                  // change _id to address
            avgMathScore: 1                   // show score
        }
    },
    { $skip: 0 },                             // pagination: (page * size)
    { $limit: 3 }                             // pagination: page size
])
```

## Aggregation with Referenced Data (Join Collections)
### Basic Lookup (Join 3 Collections)
```mongodb-json
db.enrollments.aggregate([
    {
        $lookup: {
            from: "students",
            localField: "studentId",
            foreignField: "_id",
            as: "student"
        }
    },
    {
        $lookup: {
            from: "courses",
            localField: "courseId",
            foreignField: "_id",
            as: "course"
        }
    },
    { $unwind: "$student" }, // change array to object
    { $unwind: "$course" },  // change array to object
    {
        $project: {
            _id: 0,
            student: "$student.name",
            course: "$course.name",
            semester: 1,
            score: 1
        }
    }
])
```

### Optimal Filtering (Match Before Lookup)
```mongodb-json
db.enrollments.aggregate([
    {
        $match: {
            studentId: ObjectId("696a21ee95a60d15c5063afa") // filter ID first
        }
    },
    { $lookup: { from: "students", localField: "studentId", foreignField: "_id", as: "student" } },
    { $lookup: { from: "courses", localField: "courseId", foreignField: "_id", as: "course" } },
    { $unwind: "$student" },
    { $unwind: "$course" },
    {
        $project: {
            _id: 0,
            student: "$student.name",
            course: "$course.name",
            semester: 1,
            score: 1
        }
    }
])
```

### Average Score per Student (Analytic case)
```mongodb-json
db.enrollments.aggregate([
    {
        $group: {
            _id: "$studentId",
            avgScore: { $avg: "$score" },
            totalCourses: { $sum: 1 }
        }
    },
    {
        $lookup: {
            from: "students",
            localField: "_id",
            foreignField: "_id",
            as: "student"
        }
    },
    { $unwind: "$student" },
    {
        $project: {
            _id: 0,
            student: "$student.name",
            avgScore: 1,
            totalCourses: 1
        }
    }
])
```