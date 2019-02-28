# Matrix

Matrix class used to store and operate on matricies.
The indices of the elements are zero indexed.

## Usage

The dimensions of the matrix are imutable and must be known when calling any
of the constructors.
All basic getters & setters are implemented.
Many methods (and constructors) use the Java functional interfaces for simple
ways to initialize, set or modify the elements of the matrix.
This class already implements basic mathematical operations:
 - Addition
 - Subtraction
 - Scalar multiplication
 - Matrix multiplication
 - Elementwise multiplication (Hadamard product)
 - Elementwise division
 - Transposition
Other operations can be implemented easily with the foreach & apply methods.
The foreach methods modify the elements of the matrix itself and
the apply methods return their result as a new matrix.

## Getting Started

Simply download this repository and add it to your project! Done!

## TODO

 - [ ] Argument validation
 - [x] Documentation

## License (MIT)

MIT License

Copyright (c) 2019 Sebastian G�ssl

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
