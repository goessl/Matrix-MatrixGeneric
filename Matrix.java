/*
 * MIT License
 * 
 * Copyright (c) 2019 Sebastian Gössl
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */



package matrix;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.PrimitiveIterator;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleSupplier;
import java.util.function.DoubleUnaryOperator;
import java.util.function.ToDoubleBiFunction;
import java.util.stream.IntStream;



/**
 * Matrix class used to store and operate on matricies.
 * The indices of the elements are zero indexed like if it would be a two
 * dimensional array (what it actually is internally).
 * The first index is always the row index counted from top to bottom and the
 * second index is always the column index counted from left to right.
 * e.g. a 2x3 matrix
 *  [[0,0 0,1 0,2],
 *   [1,0 1,1 1,2]]
 * 
 * All operations are applied, if possible, in parallel, otherwise
 * column-row vise.
 * 
 * 
 * @author Sebastian Gössl
 * @version 1.4 3.9.2019
 */
public class Matrix implements Iterable<Double> {
    
    /**
     * Dimensions of the matrix.
     * Height: Number of rows
     * Width: Number of columns
     */
    private final int height, width;
    /**
     * Elements of the matrix.
     */
    private final double[][] data;
    
    
    
    /**
     * Constructs a copy of the given matrix.
     * 
     * @param other matrix to copy
     */
    public Matrix(Matrix other) {
        this(other.getHeight(), other.getWidth(), (j, i) -> other.get(j, i));
    }
    
    /**
     * Constructs a new matrix with the content of the given array.
     * 
     * @param array data to be stored into the matrix
     */
    public Matrix(double[][] array) {
        this(array.length, array[0].length, (j, i) -> array[j][i]);
    }
    
    /**
     * Constructs a new matrix with <code>height</code> rows and
     * <code>width</code> columns and fills it with the given value.
     * 
     * @param height number of rows
     * @param width number of columns
     * @param value value to fill the matrix with
     */
    public Matrix(int height, int width, double value) {
        this(height, width);
        set(value);
    }
    
    /**
     * Constructs a new matrix with <code>height</code> rows and
     * <code>width</code> columns and fills it with the elements returned from
     * the given supplier.
     * 
     * @param height number of rows
     * @param width number of columns
     * @param supplier supplier to fill the matrix with values
     */
    public Matrix(int height, int width, DoubleSupplier supplier) {
        this(height, width);
        set(supplier);
    }
    
    /**
     * Constructs a new matrix with <code>height</code> rows and
     * <code>width</code> columns and fills the elements with the given
     * function.
     * The function receives the row and column indices of the current element
     * to calculate.
     * 
     * @param height number of rows
     * @param width number of columns
     * @param function function that recieves the indices of the element it
     * shall calculate
     */
    public Matrix(int height, int width,
            ToDoubleBiFunction<Integer, Integer> function) {
        this(height, width);
        set(function);
    }
    
    /**
     * Constructs a new matrix with <code>height</code> rows and
     * <code>width</code> columns.
     * All elements are initialized to zero.
     * 
     * @param height number of rows
     * @param width number of columns
     */
    public Matrix(int height, int width) {
        this.height = height;
        this.width = width;
        
        data = new double[height][width];
    }
    
    
    
    /**
     * Returns the number of rows.
     * 
     * @return number of rows
     */
    public int getHeight() {
        return height;
    }
    
    /**
     * Returns the number of columns.
     * 
     * @return number of columns
     */
    public int getWidth() {
        return width;
    }
    
    /**
     * Returns the element at the specified position.
     * 
     * @param row row of the element to return
     * @param column column of the element to return
     * @return the element at the specified position
     */
    public double get(int row, int column) {
        return data[row][column];
    }
    
    /**
     * Replaces the element at the specified position with the specified
     * element.
     * 
     * @param row row of the element to set
     * @param column column of the element to set
     * @param value element to be stored at the specified position
     * @return element previously at the specified position
     */
    public double set(int row, int column, double value) {
        final double oldElement = data[row][column];
        data[row][column] = value;
        return oldElement;
    }
    
    /**
     * Replaces all elements of the matrix with the specified elements.
     * 
     * @param value element to replace all elements
     */
    public void set(double value) {
        forEachIndicesParallel((j, i) -> set(j, i, value));
    }
    
    /**
     * Replaces all elements with the values returned from the given supplier.
     * The elements of the matrix are filled into the matrix column-row vise.
     * 
     * @param supplier supplier to supply new values for all elements
     */
    public void set(DoubleSupplier supplier) {
        forEachIndices((j, i) -> set(j, i, supplier.getAsDouble()));
    }
    
    /**
     * Replaces all elements with the values returned from the given function.
     * It recieves the position (row and column indices) of the element to
     * replace as arguments.
     * 
     * @param function function to calculate new values for all elements
     */
    public void set(ToDoubleBiFunction<Integer, Integer> function) {
        forEachIndicesParallel(
                (j, i) -> set(j, i, function.applyAsDouble(j, i)));
    }
    
    
    
    /**
     * Adds the given matrix to this matrix elementwise and returns the result.
     * 
     * @param operand other summand
     * @return sum
     */
    public Matrix add(Matrix operand) {
        return applyNew(operand, (x, y) -> x + y);
    }
    
    /**
     * Subtracts the given matrix from this matrix elementwise and returns the
     * result.
     * 
     * @param operand subtrahend
     * @return difference
     */
    public Matrix subtract(Matrix operand) {
        return applyNew(operand, (x, y) -> x - y);
    }
    
    /**
     * Multiplies every element of this matrix with the given value and returns
     * the result.
     * Scalar multiplication.
     * 
     * @param factor scalar factor
     * @return product
     */
    public Matrix multiply(double factor) {
        return applyNew((x) -> factor * x);
    }
    
    /**
     * Matrix multiplies this matrix with the given matrix and returns the
     * result.
     * 
     * @param operand second factor
     * @return product
     */
    public Matrix multiply(Matrix operand) {
        return new Matrix(getHeight(), operand.getWidth(), (j, i) -> {
            double sum = 0;
            for(int k=0; k<getWidth() && k<operand.getHeight(); k++) {
                sum += get(j, k) * operand.get(k, i);
            }
            return sum;
        });
    }
    
    /**
     * Multiplies this matrix with the given matrix elementwise and returns the
     * result.
     * 
     * @param operand factor
     * @return product
     */
    public Matrix multiplyElementwise(Matrix operand) {
        return applyNew(operand, (x, y) -> x * y);
    }
    
    /**
     * Divides this matrix by the given matrix elementwise and returns the
     * result.
     * 
     * @param operand divisor
     * @return quotient
     */
    public Matrix divideElementwise(Matrix operand) {
        return applyNew(operand, (x, y) -> x / y);
    }
    
    /**
     * Returns the transpose of this matrix.
     * 
     * @return Transpose of this matrix.
     */
    public Matrix transpose() {
        return new Matrix(getWidth(), getHeight(), (j, i) -> get(i, j));
    }
    
    
    
    /**
     * Applies the given operator on every element of this matrix.
     * 
     * @param operator operator to apply on every element of this matrix
     */
    public void apply(DoubleUnaryOperator operator) {
        set((j, i) -> operator.applyAsDouble(get(j, i)));
    }
    
    /**
     * Applies the given operator elementwise on every element of this matrix
     * and the given one.
     * 
     * @param operand second operand
     * @param operator operator to apply on every element of this matrix
     */
    public void apply(Matrix operand, DoubleBinaryOperator operator) {
        set((j, i) -> operator.applyAsDouble(get(j, i), operand.get(j, i)));
    }
    
    /**
     * Applies the given operator on every element of this matrix and the given
     * matrix elementwise wrapping around.
     * 
     * @param operand second operand
     * @param operator operator to apply on every element of the matrix
     */
    public void applyDifSize(Matrix operand, DoubleBinaryOperator operator) {
        set((j, i) -> operator.applyAsDouble(
                get(j, i),
                operand.get(j % getHeight(), i % getWidth())));
    }
    
    /**
     * Applies the given operator on every element of this matrix and returns
     * the result.
     * 
     * @param operator operator to apply on every element of this matrix
     * @return result of the operation
     */
    public Matrix applyNew(DoubleUnaryOperator operator) {
        return new Matrix(getHeight(), getWidth(),
                (j, i) -> operator.applyAsDouble(get(j, i)));
    }
    
    /**
     * Applies the given operator elementwise on every element of this matrix
     * and the given one and returns the result.
     * 
     * @param operand second operand
     * @param operator operator to apply on every element of the matricies
     * @return result of the operation
     */
    public Matrix applyNew(Matrix operand, DoubleBinaryOperator operator) {
        return new Matrix(getHeight(), getWidth(), (j, i) ->
                operator.applyAsDouble(get(j, i), operand.get(j, i)));
    }
    
    /**
     * Applies the given operator on every element of this matrix and the given
     * matrix elementwise wrapping around and returns the result.
     * The result has as many rows and the matrix with more rows and as many
     * columns as the matrix with more columns.
     * 
     * @param operand second operand
     * @param operator operator to apply on every element of the matricies
     * @return result of the operation
     */
    public Matrix applyNewDifSize(Matrix operand, DoubleBinaryOperator operator) {
        return new Matrix(
                Math.max(getHeight(), operand.getHeight()),
                Math.max(getWidth(), operand.getWidth()),
                (j, i) -> {
                    final double value1 = get(j % getHeight(), i % getWidth());
                    final double value2 = operand.get(
                            j % operand.getHeight(), i % operand.getWidth());
                    return operator.applyAsDouble(value1, value2);
                });
    }
    
    
    
    /**
     * Iterator which iterates over all elements column-row wise.
     */
    private class MatrixIterator implements PrimitiveIterator.OfDouble {
        /**
         * Indices of the next element.
         */
        private int row = 0, column = 0;
        
        
        
        /**
         * {@inheritDoc}
         */
        @Override
        public boolean hasNext() {
            return row < getHeight() && column < getWidth();
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public double nextDouble() {
            if(!hasNext()) {
                throw new NoSuchElementException();
            }
            
            
            final double element = get(row, column);
            
            if(++column >= getWidth()) {
                column = 0;
                row++;
            }
            
            return element;
        }
    }
    
    /**
     * Returns and iterator which iterates over all elements column-row wise.
     * 
     * @return iterator which iterates over all elements column-row wise
     */
    @Override
    public PrimitiveIterator.OfDouble iterator() {
        return new MatrixIterator();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void forEach(Consumer<? super Double> action) {
        forEachIndices((j, i) -> action.accept(get(j, i)));
    }
    
    /**
     * Same as forEach but the elements are processed in parallel.
     * 
     * @param action action to be performed for each element
     */
    public void forEachParallel(Consumer<? super Double> action) {
        forEachIndicesParallel((j, i) -> action.accept(get(j, i)));
    }
    
    /**
     * Applies the given consumer to all available indices in this matrix in
     * the same order as the iterator traverses them.
     * 
     * @param consumer consumer to be applied on all available indices
     */
    public void forEachIndices(BiConsumer<Integer, Integer> consumer) {
        for(int j=0; j<getHeight(); j++) {
            for(int i=0; i<getWidth(); i++) {
                consumer.accept(j, i);
            }
        }
    }
    
    /**
     * Applies the given consumer to all available indices in this matrix in
     * parallel.
     * 
     * @param consumer consumer to be applied on all available indices
     */
    public void forEachIndicesParallel(BiConsumer<Integer, Integer> consumer) {
        IntStream.range(0, getHeight()).parallel().forEach((j) ->
                IntStream.range(0, getWidth()).parallel().forEach((i) ->
                        consumer.accept(j, i)));
    }
    
    
    /**
     * Returns a copy of this matrix in 2 dimensional array form
     * 
     * @return copy of this matrix in 2 dimensional array form
     */
    public double[][] toArray() {
        final double[][] array = new double[getHeight()][getWidth()];
        
        forEachIndices((j, i) -> array[j][i] = get(j, i));
        
        return array;
    }
    
    /**
     * Returns a string representation of the contents of this matrix.
     * 
     * @return string representation of the contents of this matrix
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        
        for(double[] array : toArray()) {
            builder.append(Arrays.toString(array)).append('\n');
        }
        
        return builder.toString();
    }
}
