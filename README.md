# Java OOPs Calculator

A beginner-friendly calculator project using Java for calculation logic and HTML/CSS for the interface.

## Requirements
- JDK 17 or newer
- VS Code or another code editor

## Project structure
- `src/Calculator.java` — arithmetic logic
- `src/CalculatorServer.java` — local HTTP server and API
- `public/index.html` — calculator interface and browser-side JavaScript
- `public/style.css` — styling

## Run on Windows / VS Code terminal

Run these commands from the `JavaCalculator` root folder.

Compile:
```bash
javac --add-modules jdk.httpserver src/Calculator.java src/CalculatorServer.java
```

Start:
```bash
java --add-modules jdk.httpserver -cp src CalculatorServer
```

Open http://localhost:8080 in your browser. Keep the terminal open while using the calculator.

## Features
- Addition, subtraction, multiplication, and division
- Clear, delete, decimal, and sign toggle
- Division-by-zero error handling
- Responsive dark interface

## OOP concepts
- Encapsulation: calculation logic is contained in the `Calculator` class.
- Abstraction: callers use `calculate()` without needing to know its internal implementation.
- Extend the project with an `Operation` interface and separate operation classes to practise inheritance and polymorphism.
