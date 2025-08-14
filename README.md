# Panda Interpreter — Resolved Issues Summary

This document explains the issues identified and resolved in the Panda project during the current session, along with a brief overview of the changes and examples.

## Overview
Panda is a tiny expression language with variables, arithmetic operations, and print statements. The project includes:
- Lexer: tokenizes input
- Parser: builds an AST from tokens
- AST classes: Expressions and Statements
- Interpreter: evaluates statements and expressions
- Main: entry point to read, parse, and execute a single line of Panda code

## Issues Resolved

1. Binary expression evaluation in Interpreter
   - Problem: The Interpreter attempted arithmetic directly on AST node objects (left/right) and cast them to doubles. This would lead to ClassCastException at runtime and incorrect behavior because operands weren’t recursively evaluated.
   - Fix: Recursively evaluate both sides of a binary expression and coerce to numbers before applying the operator.
   - Key change:
     - In `Interpreter.evaluate(Expressions exp)`, for `Expressions.Binary`, call `evaluate` on `left` and `rig`, then convert to `double` via a helper `toNumber` method, and perform arithmetic on the numeric values.

2. Undefined variable handling
   - Problem: Accessing a variable that was never defined silently returned null, potentially propagating confusing errors later.
   - Fix: When evaluating an `Expressions.Variable`, check if the name exists in the environment; if absent, throw a clear `RuntimeException("Undefined variable: <name>")`.

3. Missing modulo operator in Parser
   - Problem: The Lexer and TokenType already supported the modulo `%` operator and the Interpreter handled it, but the Parser did not parse `%` in the multiplication-precedence rule. As a result, expressions using `%` failed to parse.
   - Fix: Include `TokenType.MODULO` in the `parseMultiplication()` loop so `%` is parsed with `*` and `/` precedence.

4. Variable state loss between statements in Main
   - Problem: `Main` created a new `Interpreter` for each statement; variable bindings did not persist across statements, making code like `let a = 1 print(a)` fail.
   - Fix: Create a single `Interpreter` instance and execute all parsed statements with it, preserving the environment across statements.

## Files Changed
- `src/Interpreter/Interpreter.java`
  - Evaluate operands recursively for binary expressions, coerce to numbers with `toNumber`, and perform arithmetic safely.
  - Validate variables exist; throw a clear error if undefined.
- `src/Parser/Parser.java`
  - Add `TokenType.MODULO` handling inside `parseMultiplication()` so `%` is parsed correctly.
- `src/Main.java`
  - Instantiate one `Interpreter` and execute all statements with it to preserve variables.

## Examples

- Recursive binary evaluation
  - Input: `print(1 + 2 * 3)`
  - Result: `7.0` (correct precedence and evaluation)

- Modulo support
  - Input: `print(10 % 3)`
  - Result: `1.0`

- Variable persistence
  - Input: `let a = 5 print(a + 2)`
  - Result: `7.0`

- Undefined variable error
  - Input: `print(b)`
  - Result: `RuntimeException: Undefined variable: b`

## How to Run
1. Build the project (e.g., via your IDE or `javac`).
2. Run `Main` and enter a single line of Panda code, such as:
   - `let x = 10 print(x % 4 + 1)`

The program will execute the statements and also print their AST representations afterward.

## Notes
- Numbers are currently represented as doubles; arithmetic results are printed as double values.
- Grammar supports parentheses and standard operator precedence: `*`, `/`, `%` bind tighter than `+`, `-`.
