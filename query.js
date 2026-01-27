// ======================================================
// BASIC AGGREGATION
// ======================================================

// Count student where address = "Bandung"
// Flow: filter -> group -> count
db.students.aggregate([
    {
        $match: {
            address: "Bandung" // filter student by specific address
        }
    },
    {
        $group: {
            _id: "$address",   // group by address
            count: { $sum: 1 } // count total students
        }
    }
])

// Count students grouped by address
// Use case: statistik jumlah student per kota
db.students.aggregate([
    {
        $group: {
            _id: "$address",   // group by address
            count: { $sum: 1 } // count students per address
        }
    }
])


// ======================================================
// AGGREGATION WITH EMBEDDED DATA
// ======================================================

// Count average math score per address
// Only include students that have math score
db.students.aggregate([
    {
        $match: {
            "scores.math": { $exists: true } // ensure math score exists
        }
    },
    {
        $group: {
            _id: "$address",                  // group by address
            avgMathScore: { $avg: "$scores.math" } // calculate average
        }
    }
])


// Get average math score per address
// with age filter, sorting, and pagination
// Notes:
// - gte = greater than or equal
// - lte = less than or equal
// - sort: -1 desc, 1 asc
db.students.aggregate([
    {
        $match: {
            age: { $gte: 20, $lte: 25 },      // filter by age range
            "scores.math": { $exists: true }  // ensure math score exists
        }
    },
    {
        $group: {
            _id: "$address",
            avgMathScore: { $avg: "$scores.math" }
        }
    },
    {
        $sort: {
            avgMathScore: -1                  // sort by highest average score
        }
    },
    {
        $project: {
            _id: 0,                           // hide internal _id
            address: "$_id",                  // rename group key
            avgMathScore: 1                   // include avgMathScore
        }
    },
    { $skip: 0 },                             // pagination: page * size
    { $limit: 3 }                             // pagination: page size
])


// ======================================================
// REFERENCING (NORMALIZATION)
// ======================================================

// Update all students: add age field
db.students.updateMany(
    {},
    {
        $set: { age: 27 }                     // add / overwrite age field
    }
)


// Insert master data for courses collection
db.courses.insertMany([
    { _id: ObjectId("64b000000000000000000001"), name: "Math", credit: 3 },
    { _id: ObjectId("64b000000000000000000002"), name: "Physics", credit: 4 },
    { _id: ObjectId("64b000000000000000000003"), name: "English", credit: 2 }
])


// Insert enrollment data (relation student ↔ course)
db.enrollments.insertMany([
    {
        studentId: ObjectId("6976ed26bc63a2cc96a883df"), // reference student (Andi)
        courseId: ObjectId("64b000000000000000000001"), // reference course (Math)
        semester: "2024-1",
        score: 85
    },
    {
        studentId: ObjectId("6976ed26bc63a2cc96a883df"), // Andi
        courseId: ObjectId("64b000000000000000000003"), // English
        semester: "2024-1",
        score: 90
    },
    {
        studentId: ObjectId("6976ed26bc63a2cc96a883e0"), // Budi
        courseId: ObjectId("64b000000000000000000002"), // Physics
        semester: "2024-1",
        score: 78
    }
])


// ======================================================
// AGGREGATION WITH LOOKUP (JOIN)
// ======================================================

// Join enrollment with student and course
// $lookup  : similar to SQL JOIN
// $unwind  : convert array result into single object
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
    { $unwind: "$student" }, // flatten student array
    { $unwind: "$course" },  // flatten course array
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


// Join + filter by student name and course name
db.enrollments.aggregate([
    { $lookup: { from: "students", localField: "studentId", foreignField: "_id", as: "student" } },
    { $lookup: { from: "courses", localField: "courseId", foreignField: "_id", as: "course" } },
    { $unwind: "$student" },
    { $unwind: "$course" },
    {
        $match: {
            "student.name": "Budi",   // filter by student name
            "course.name": "Physics"  // filter by course name
        }
    },
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


// More optimal filtering: match first by studentId
// Best practice: filter early before lookup
db.enrollments.aggregate([
    {
        $match: {
            studentId: ObjectId("696a21ee95a60d15c5063afa")
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


// Filter enrollments by semester
// Flow: lookup → unwind → match → project
db.enrollments.aggregate([
    { $lookup: { from: "students", localField: "studentId", foreignField: "_id", as: "student" } },
    { $lookup: { from: "courses", localField: "courseId", foreignField: "_id", as: "course" } },
    { $unwind: "$student" },
    { $unwind: "$course" },
    {
        $match: {
            semester: "2024-1" // filter by semester
        }
    },
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


// ======================================================
// ANALYTICS
// ======================================================

// Average score per student
// Also count total courses taken
db.enrollments.aggregate([
    {
        $group: {
            _id: "$studentId",        // group by student
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


// Average score per course
db.enrollments.aggregate([
    {
        $group: {
            _id: "$courseId",         // group by course
            avgScore: { $avg: "$score" },
            totalStudents: { $sum: 1 }
        }
    },
    {
        $lookup: {
            from: "courses",
            localField: "_id",
            foreignField: "_id",
            as: "course"
        }
    },
    { $unwind: "$course" },
    {
        $project: {
            _id: 0,
            course: "$course.name",
            avgScore: 1,
            totalStudents: 1
        }
    }
])


// ======================================================
// PAGINATION PATTERN
// ======================================================

// Pagination in aggregation
// Best order: match → sort → skip → limit → project
db.enrollments.aggregate([
    { $lookup: { from: "students", localField: "studentId", foreignField: "_id", as: "student" } },
    { $lookup: { from: "courses", localField: "courseId", foreignField: "_id", as: "course" } },
    { $unwind: "$student" },
    { $unwind: "$course" },
    { $match: { semester: "2024-1" } },
    { $sort: { score: -1 } }, // highest score first
    { $skip: 0 },             // page * size
    { $limit: 5 },            // page size
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