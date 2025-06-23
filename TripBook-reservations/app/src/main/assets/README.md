# Travel Illustrations for TripBook

## Instructions for Adding Travel Illustrations

1. Visit [Freepik.com](https://www.freepik.com/)
2. Search for "travel illustrations"
3. Download illustrations that match the TripBook app style
4. Convert the illustrations to WebP format for optimal performance
5. Place the WebP files in the drawable-xxhdpi directory (app/src/main/res/drawable-xxhdpi/)
6. Use descriptive names for the files, such as:
   - travel_illustration_1.webp
   - travel_illustration_2.webp
   - etc.

## Recommended Illustrations

Look for illustrations that feature:
- Travel destinations (beaches, mountains, cities)
- People traveling or enjoying vacations
- Transportation (planes, trains, cars)
- Accommodation (hotels, resorts)
- Maps and navigation
- Booking and reservation concepts

## Usage in the App

These illustrations will be used in various parts of the Reservations Dashboard:
- Empty states
- Background elements
- Headers and banners
- Reservation cards

## Attribution

Remember to properly attribute Freepik and the illustrators as required by their license terms.


Sure! Here are the answers to the questions based on the provided content:

### Question 1: Android Activity Lifecycle (5 Marks)

The Android Activity Lifecycle refers to the various states an activity can be in during its existence. The key states include:

1. **onCreate()**: Called when the activity is first created. This is where you initialize your activity.
2. **onStart()**: Called when the activity becomes visible to the user.
3. **onResume()**: Called when the activity starts interacting with the user.
4. **onPause()**: Called when the system is about to start resuming another activity. Use this to pause ongoing tasks.
5. **onStop()**: Called when the activity is no longer visible to the user. This is where you can release resources.
6. **onDestroy()**: Called before the activity is destroyed. Use this to clean up resources.

### Question 2: Android Application Permissions

In Android development, permissions are essential for accessing sensitive data and features. The permissions can be categorized into:

1. **Normal Permissions**: These do not pose a risk to user privacy (e.g., accessing the Internet). These are automatically granted.
2. **Dangerous Permissions**: These could potentially affect user privacy or device functionality (e.g., accessing the camera, location). The user must grant these at runtime.

For example:
- **Bluetooth**: Requires permission to access Bluetooth features.
- **Camera**: Requires permission to access the camera for taking photos.
- **SD Card**: Requires permission to read/write data on external storage.

### Question 3: Android Application Resources

In an Android application project, resources are used to define various elements such as layouts, strings, and images. Types of resources include:

1. **Drawable Resources**: Images and graphics (e.g., `res/drawable`).
2. **Layout Resources**: XML files defining user interface layouts (e.g., `res/layout`).
3. **String Resources**: Text strings used in the app (e.g., `res/values/strings.xml`).
4. **Color Resources**: Color definitions (e.g., `res/values/colors.xml`).

You can add resources to enhance the application, like adding icons, background images, or custom styles.

### Question 4: Kotlin Object-Oriented Programming (10 Marks)

#### a) Design and Implementation

```kotlin
class Student(val name: String) {
    var grades: MutableList<Int> = mutableListOf()

    fun addGrade(grade: Int) {
        grades.add(grade)
    }

    fun calculateAverageGrade(): Double {
        return if (grades.isNotEmpty()) grades.average() else 0.0
    }

    fun getHighestGrade(): Int? {
        return grades.maxOrNull()
    }

    fun getLowestGrade(): Int? {
        return grades.minOrNull()
    }
}

fun main() {
    val student = Student("John Doe")
    student.addGrade(85)
    student.addGrade(90)
    student.addGrade(78)

    println("Average Grade: ${student.calculateAverageGrade()}")
    println("Highest Grade: ${student.getHighestGrade()}")
    println("Lowest Grade: ${student.getLowestGrade()}")
}
```

#### b) Multiple Courses Implementation

```kotlin
class Course(val name: String) {
    val students: MutableList<Student> = mutableListOf()

    fun addStudent(student: Student) {
        students.add(student)
    }

    fun getAverageGrade(): Double {
        val totalGrades = students.flatMap { it.grades }
        return if (totalGrades.isNotEmpty()) totalGrades.average() else 0.0
    }
}

fun main() {
    val course = Course("Mathematics")
    val student1 = Student("Alice")
    student1.addGrade(88)
    student1.addGrade(92)

    val student2 = Student("Bob")
    student2.addGrade(76)
    student2.addGrade(85)

    course.addStudent(student1)
    course.addStudent(student2)

    println("Course Average Grade: ${course.getAverageGrade()}")
}
```

Feel free to ask if you have any questions or need further assistance!

Here are the answers to the questions based on the provided content:

### Question 5: Demo Application - Multiplication of Floating Numbers (2 x 10 Marks)

#### XML Layout Code (activity_main.xml)

```xml
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp">

    <EditText
        android:id="@+id/mantissa1"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:hint="Enter first number" />

    <EditText
        android:id="@+id/mantissa2"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:hint="Enter second number" />

    <EditText
        android:id="@+id/exponent"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:hint="Enter exponent (0-2 digits)" />

    <Button
        android:id="@+id/calculateButton"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Calculate" />

    <TextView
        android:id="@+id/resultTextView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Result: " />

</LinearLayout>
```

#### Kotlin Code (MainActivity.kt)

```kotlin
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mantissa1 = findViewById<EditText>(R.id.mantissa1)
        val mantissa2 = findViewById<EditText>(R.id.mantissa2)
        val exponent = findViewById<EditText>(R.id.exponent)
        val calculateButton = findViewById<Button>(R.id.calculateButton)
        val resultTextView = findViewById<TextView>(R.id.resultTextView)

        calculateButton.setOnClickListener {
            val num1 = mantissa1.text.toString().toFloatOrNull()
            val num2 = mantissa2.text.toString().toFloatOrNull()
            val exp = exponent.text.toString().toIntOrNull()

            if (num1 != null && num2 != null && exp != null && exp in 0..99) {
                val result = (num1 * num2) * Math.pow(10.0, exp.toDouble())
                resultTextView.text = "Result: $result"
            } else {
                resultTextView.text = "Invalid input"
            }
        }
    }
}
```

### Question 6: Demo Application - Alarm Every Monday at 08:00 AM (2 x 10 Marks)

#### XML Layout Code (activity_main.xml)

```xml
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Alarm Set for Every Monday at 08:00 AM" />

    <Button
        android:id="@+id/setAlarmButton"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Set Alarm" />

</LinearLayout>
```

#### Kotlin Code (MainActivity.kt)

```kotlin
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val setAlarmButton = findViewById<Button>(R.id.setAlarmButton)

        setAlarmButton.setOnClickListener {
            setAlarm()
        }
    }

    private fun setAlarm() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)

        val calendar = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            set(Calendar.HOUR_OF_DAY, 8)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)

            // If the time has already passed today, set it for next week
            if (timeInMillis < System.currentTimeMillis()) {
                add(Calendar.WEEK_OF_YEAR, 1)
            }
        }

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY * 7, pendingIntent)
    }
}
```

#### AlarmReceiver Class

You will also need a BroadcastReceiver to handle the alarm:

```kotlin
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.media.Ringtone

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Code to ring the alarm
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        val ringtone: Ringtone = RingtoneManager.getRingtone(context, alarmSound)
        ringtone.play()
    }
}
```

### Conclusion

These implementations provide a basic structure for the two applications described in the questions. Adjust the code further based on your specific requirements or additional functionalities. If you have any more questions or need further assistance, feel free to ask!

Voici un exemple de code Kotlin pour une calculatrice simple qui effectue des opérations de base (addition, soustraction, multiplication et division) :

### Kotlin Code for a Basic Calculator

```kotlin
import java.util.Scanner

fun main() {
    val scanner = Scanner(System.`in`)

    println("Welcome to the Basic Calculator!")
    println("Enter first number:")
    val firstNumber = scanner.nextDouble()

    println("Enter second number:")
    val secondNumber = scanner.nextDouble()

    println("Choose an operation (+, -, *, /):")
    val operation = scanner.next()

    val result = when (operation) {
        "+" -> firstNumber + secondNumber
        "-" -> firstNumber - secondNumber
        "*" -> firstNumber * secondNumber
        "/" -> {
            if (secondNumber != 0.0) {
                firstNumber / secondNumber
            } else {
                "Error: Division by zero"
            }
        }
        else -> "Error: Invalid operation"
    }

    println("Result: $result")
}
```

### Explanation

1. **Import Statement**: 
   - `import java.util.Scanner` is used to read user input.

2. **Main Function**: 
   - The `main` function is the entry point of the program.

3. **User Input**:
   - The program prompts the user for two numbers and an operation. It uses `Scanner` to read inputs.

4. **Operation Selection**:
   - A `when` expression is used to determine which operation to perform based on user input.

5. **Error Handling**:
   - The code checks for division by zero and invalid operations and returns appropriate error messages.

6. **Output**:
   - The result of the operation is printed to the console.

### Running the Program
- To run this code, simply copy it into a Kotlin-compatible IDE (like IntelliJ IDEA or Android Studio) and execute the `main` function. The program will prompt you for input in the console. 

Feel free to modify or expand the functionality as needed! If you have further questions, just let me know!

### Brief Guide to Coding in Kotlin

Kotlin is designed to be expressive, concise, and safe. Here’s a brief overview of how to get started with Kotlin and some general rules to follow:

#### Getting Started

1. **Setup Environment**:
   - Install **IntelliJ IDEA** or **Android Studio**, which have built-in support for Kotlin.
   - Create a new project and select Kotlin as the programming language.

2. **Basic Syntax**:
   - **Variables**: Use `val` for immutable variables and `var` for mutable ones.
     ```kotlin
     val name: String = "John"
     var age: Int = 30
     ```

   - **Functions**: Define functions using the `fun` keyword.
     ```kotlin
     fun greet(name: String): String {
         return "Hello, $name!"
     }
     ```

   - **Control Structures**: Use standard control flow statements like `if`, `when`, and loops (`for`, `while`).
     ```kotlin
     if (age > 18) {
         println("Adult")
     }
     ```

   - **Classes and Objects**: Define classes with properties and methods.
     ```kotlin
     class Person(val name: String, var age: Int)
     ```

3. **Null Safety**:
   - Use nullable types and safe calls to prevent null pointer exceptions.
   ```kotlin
   var email: String? = null
   email?.let { println(it) }
   ```

4. **Collections**:
   - Use lists, sets, and maps. Kotlin provides powerful collection operations.
   ```kotlin
   val numbers = listOf(1, 2, 3)
   val filtered = numbers.filter { it > 1 }
   ```

#### General Coding Rules

1. **Follow Naming Conventions**:
   - Use **camelCase** for variable and function names (e.g., `myVariable`, `calculateSum`).
   - Use **PascalCase** for class names (e.g., `Person`, `Car`).

2. **Keep Code Concise**:
   - Avoid unnecessary boilerplate code. Use features like extension functions and higher-order functions.

3. **Comment Your Code**:
   - Use comments to explain complex logic but avoid over-commenting.
   ```kotlin
   // Calculate the sum of two numbers
   fun sum(a: Int, b: Int): Int {
       return a + b
   }
   ```

4. **Use Data Classes for Models**:
   - For classes that primarily hold data, use `data class` to automatically generate useful methods.
   ```kotlin
   data class User(val name: String, val age: Int)
   ```

5. **Leverage Kotlin Features**:
   - Use features like coroutines for asynchronous programming and sealed classes for representing restricted class hierarchies.

6. **Test Your Code**:
   - Write unit tests to ensure your code works as expected. Use frameworks like JUnit for testing.

7. **Keep Learning**:
   - Stay updated with Kotlin's evolving features and best practices through official documentation and community resources.

By following these guidelines, you can write clean, efficient, and maintainable Kotlin code. If you have further questions or need specific examples, feel free to ask!

### Summary of Kotlin Programming

Kotlin is a modern, statically typed programming language that runs on the Java Virtual Machine (JVM) and is officially supported by Google for Android development. It is designed to be fully interoperable with Java, making it easy for Java developers to transition to Kotlin.

#### Key Features:

1. **Concise Syntax**:
   - Kotlin reduces boilerplate code, allowing developers to write more expressive and readable code.

2. **Null Safety**:
   - Kotlin's type system distinguishes between nullable and non-nullable types, helping to eliminate null pointer exceptions.

3. **Interoperability**:
   - Kotlin can seamlessly call Java code and vice versa, allowing developers to use existing Java libraries and frameworks.

4. **Higher-Order Functions**:
   - Supports functional programming paradigms, enabling functions to be passed as parameters and returned from other functions.

5. **Coroutines**:
   - Provides built-in support for asynchronous programming through coroutines, making it easier to handle long-running tasks without blocking the main thread.

6. **Extension Functions**:
   - Allows developers to extend existing classes with new functionality without modifying their source code.

7. **Data Classes**:
   - Simplifies the creation of classes that are primarily used to hold data, automatically generating methods like `equals()`, `hashCode()`, and `toString()`.

8. **Smart Casts**:
   - Kotlin automatically casts types after checking them, reducing the need for explicit casting.

#### Use Cases:

- **Android Development**: Kotlin is the preferred language for Android app development due to its concise syntax and modern features.
- **Server-Side Development**: It can be used for backend development with frameworks like Ktor and Spring.
- **Web Development**: Kotlin can also be used for frontend development with Kotlin/JS and frameworks like React.

#### Conclusion:

Kotlin offers a robust and flexible alternative to Java, with features that enhance productivity and code safety. Its growing popularity and official support from Google make it a compelling choice for developers across various domains.