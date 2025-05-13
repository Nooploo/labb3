import kotlinx.coroutines.*
import kotlin.math.roundToInt

class Student(
    name: String,
    age: Int,
    grades: List<Int>
) {
    var name: String = name.trim().replaceFirstChar { it.uppercase() }
        set(value) {
            field = value.trim().replaceFirstChar { it.uppercase() }
        }

    var age: Int = age
        set(value) {
            if (value >= 0) field = value
        }

    var grades: List<Int> = grades
        private set

    val isAdult: Boolean
        get() = age >= 18

    val status: String by lazy {
        if (isAdult) "Adult" else "Minor"
    }

    init {
        println("Student created: $name, age: $age")
    }

    constructor(name: String) : this(name, 0, listOf())

    fun getAverage(): Double {
        return if (grades.isNotEmpty()) grades.average() else 0.0
    }

    fun processGrades(operation: (Int) -> Int) {
        grades = grades.map { operation(it) }
    }

    fun updateGrades(newGrades: List<Int>) {
        grades = newGrades
    }

    operator fun plus(other: Student): Student {
        val newGrades = this.grades + other.grades
        return Student(name = "$name & ${other.name}", age = (this.age + other.age) / 2, grades = newGrades)
    }

    operator fun times(multiplier: Int): Student {
        val newGrades = grades.map { it * multiplier }
        return Student(name, age, newGrades)
    }

    override operator fun equals(other: Any?): Boolean {
        if (other !is Student) return false
        return this.name == other.name && this.getAverage().roundToInt() == other.getAverage().roundToInt()
    }
}

class Group(vararg students: Student) {
    private val studentList = students.toList()

    operator fun get(index: Int): Student {
        return studentList[index]
    }

    fun getTopStudent(): Student? {
        return studentList.maxByOrNull { it.getAverage() }
    }
}

suspend fun fetchGradesFromServer(): List<Int> {
    delay(2000)
    return listOf(90, 85, 78, 92, 88)
}

fun main() = runBlocking {
    val student1 = Student("   anna   ")
    student1.age = 19
    student1.updateGrades(listOf(80, 75, 90))

    val student2 = Student(name = "Bogdan", age = 20, grades = listOf(60, 70, 75, 85, 90))

    println("Average grade of ${student1.name}: ${student1.getAverage()}")
    println("Average grade of ${student2.name}: ${student2.getAverage()}")

    val combined = student1 + student2
    println("Combined student: ${combined.name}, grades: ${combined.grades}")

    val multiplied = student1 * 2
    println("Grades after multiplication: ${multiplied.grades}")

    println("Are students equal? ${student1 == student2}")

    println("${student1.name} status: ${student1.status}")

    val group = Group(student1, student2, combined)
    println("Top student in group: ${group.getTopStudent()?.name}")

    println("Fetching new grades from server...")
    val newGrades = async { fetchGradesFromServer() }
    student1.updateGrades(newGrades.await())
    println("Updated grades for ${student1.name}: ${student1.grades}")
}
