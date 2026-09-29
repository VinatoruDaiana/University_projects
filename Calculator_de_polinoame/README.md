# Polynomial Calculator

A Java application developed as a university project for performing basic mathematical operations on polynomials.

## Description

The project implements polynomial representation and several common polynomial operations using Java.

A polynomial is stored using a `HashMap<Integer, Double>`, where:
- the key represents the exponent of a term;
- the value represents the coefficient.

For example, the polynomial:

```text
x^5 + 3x^3 + 2x - 6
```

can be represented internally as exponent-coefficient pairs:

```text
5 -> 1
3 -> 3
1 -> 2
0 -> -6
```

## Features

The application supports the following polynomial operations:

- Addition
- Subtraction
- Multiplication
- Differentiation
- Integration
- Polynomial display in mathematical form

Terms with a coefficient equal to zero are removed from the resulting polynomial.

## Technologies

- Java
- Java Collections Framework
- `HashMap`
- IntelliJ IDEA

## Project Structure

The source code is located in the `src` directory.

### `Polinom.java`

Represents a polynomial using:

```java
Map<Integer, Double>
```

It also handles the textual representation of the polynomial.

### `Operatii.java`

Contains the methods used to perform mathematical operations on polynomials:

```java
adunare()
scadere()
inmultire()
derivare()
integrare()
```

### `Main.java`

Creates sample polynomials and demonstrates how the available operations can be used.

## Example

Consider the following two polynomials:

```text
P1(x) = x^5 + 3x^3 + 2x - 6
P2(x) = 5x^4 + 7x + 2
```

They can be represented in Java as:

```java
Polinom p1 = new Polinom();

p1.polinom.put(5, 1.0);
p1.polinom.put(3, 3.0);
p1.polinom.put(1, 2.0);
p1.polinom.put(0, -6.0);

Polinom p2 = new Polinom();

p2.polinom.put(4, 5.0);
p2.polinom.put(1, 7.0);
p2.polinom.put(0, 2.0);
```

### Addition

```java
Polinom rezultat = Operatii.adunare(p1, p2);
System.out.println(rezultat);
```

For the polynomials above, the result is:

```text
x^5 + 5x^4 + 3x^3 + 9x - 4
```

## Available Operations

### Addition

```java
Operatii.adunare(p1, p2);
```

Adds the coefficients of terms with the same exponent.

### Subtraction

```java
Operatii.scadere(p1, p2);
```

Subtracts the coefficients of the second polynomial from the first polynomial.

### Multiplication

```java
Operatii.inmultire(p1, p2);
```

Multiplies every term of the first polynomial by every term of the second polynomial and combines terms with the same exponent.

### Differentiation

```java
Operatii.derivare(p1);
```

Calculates the derivative using:

```text
a * x^n -> a * n * x^(n-1)
```

### Integration

```java
Operatii.integrare(p1);
```

Calculates the indefinite integral using:

```text
a * x^n -> a / (n + 1) * x^(n + 1)
```

## How to Run

1. Clone or download the repository.
2. Open the project in IntelliJ IDEA or another Java IDE.
3. Open the `src` directory.
4. Run the `Main.java` class.
5. The results will be displayed in the console.

## Purpose

This project was developed as a university assignment to practice:

- object-oriented programming in Java;
- working with Java collections;
- representing mathematical structures in code;
- implementing algorithms for polynomial operations.
