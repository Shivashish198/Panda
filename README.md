🐼 Panda

Panda is a minimalistic, interpreted programming language built from scratch in Java.
It’s simple, readable, and designed for learning how languages work — from lexing to interpreting.


---

✨ Features

Variables with let

Arithmetic with correct precedence

Print statements

Parentheses for grouping

== operator for comparison


Example:

let x = 2 + 7 * 8
print(x)  // 58
if (x == 58) {
    print("Correct!")
}


---

🏗 How It Works

1. Lexer → Turns source code into tokens


2. Parser → Builds an Abstract Syntax Tree (AST)


3. Interpreter → Executes the AST




---

🚀 Run

javac -d out $(find src -name "*.java")
java -cp out Main


---

🛠 Roadmap

Loops & functions

Strings & concatenation



