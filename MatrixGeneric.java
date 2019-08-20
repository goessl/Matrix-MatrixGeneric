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
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;



/**
 * Generic matrix class used to store and operate on generic matricies.
 * The indices of the elements are zero indexed.
 * 
 * @author Sebastian Gössl
 * @version 1.0 20.8.2019
 */
public class MatrixGeneric<E> implements Iterable<E> {
    
    /**
     * Dimensions of the matrix.
     * Height: Number of rows
     * Width: Number of columns
     */
    private final int height, width;
    /**
     * Elements of the matrix.
     */
    private final Object[][] data;
    
    
    
    /**
     * Constructs a copy of the given matrix.
     * 
     * @param other matrix to copy
     */
    public MatrixGeneric(MatrixGeneric<E> other) {
        this(other.getHeight(), other.getWidth());
        final Iterator<E> iterator = other.iterator();
        set(() -> (iterator.next()));
    }
    
    /**
     * Constructs a new matrix with the content of the given array.
     * 
     * @param array data to be stored into the matrix
     */
    public MatrixGeneric(E[][] array) {
        this(array.length, array[0].length, (y, x) -> (array[y][x]));
    }
    
    /**
     * Constructs a new matrix with <code>height</code> rows and
     * <code>width</code> columns and fills it with the given value.
     * 
     * @param height number of rows
     * @param width number of columns
     * @param value value to fill the matrix with
     */
    public MatrixGeneric(int height, int width, E value) {
        this(height, width, () -> (value));
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
    public MatrixGeneric(int height, int width, Supplier<E> supplier) {
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
    public MatrixGeneric(int height, int width,
            BiFunction<Integer, Integer, E> function) {
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
    public MatrixGeneric(int height, int width) {
      this.height = height;
      this.width = width;
      
      data = new Object[height][width];
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
    public E get(int row, int column) {
        return (E)data[row][column];
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
    public E set(int row, int column, E value) {
      final E oldElement = (E)data[row][column];
      data[row][column] = value;
      return oldElement;
    }
    
    /**
     * Replaces all elements of the matrix with the specified elements.
     * 
     * @param value element to replace all elements
     */
    public void set(E value) {
        set(() -> (value));
    }
    
    /**
     * Replaces all elements with the values returned from the given supplier.
     * 
     * @param supplier supplier to supply new values for all elements
     */
    public void set(Supplier<E> supplier) {
        for(int j=0; j<getHeight(); j++) {
            for(int i=0; i<getWidth(); i++) {
                set(j, i, supplier.get());
            }
        }
    }
    
    /**
     * Replaces all elements with the values returned from the given function.
     * It recieves the position (row and column indices) of the element to
     * replace as arguments.
     * 
     * @param function function to calculate new values for all elements
     */
    public void set(BiFunction<Integer, Integer, E> function) {
        for(int j=0; j<getHeight(); j++) {
            for(int i=0; i<getWidth(); i++) {
                set(j, i, function.apply(j, i));
            }
        }
    }
    
    
    
    /**
     * Returns the transpose of this matrix.
     * 
     * @return Transpose of this matrix.
     */
    public MatrixGeneric<E> transpose() {
        final MatrixGeneric<E> result = new MatrixGeneric<>(
                getWidth(), getHeight(),
                (y, x) -> (get(x, y)));
        
        return result;
    }
    
    
    /**
     * Applies the given operator on every element of this matrix.
     * 
     * @param operator operator to apply on every element of this matrix
     */
    public void forEach(UnaryOperator<E> operator) {
        final Iterator<E> iterator = iterator();
        set(() -> (operator.apply(iterator.next())));
    }
    
    /**
     * Applies the given operator elementwise on every element of this matrix
     * and the given one.
     * 
     * @param operand second operand
     * @param operator operator to apply on every element of this matrix
     */
    public void forEach(MatrixGeneric<E> operand, BinaryOperator<E> operator) {
        final Iterator<E> i1 = iterator();
        final Iterator<E> i2 = operand.iterator();
        set(() -> (operator.apply(i1.next(), i2.next())));
    }
    
    /**
     * Applies the given operator on every element of this matrix and returns
     * the result.
     * 
     * @param operator operator to apply on every element of this matrix
     * @return result of the operation
     */
    public MatrixGeneric<E> apply(UnaryOperator<E> operator) {
        final Iterator<E> iterator = iterator();
        final MatrixGeneric<E> result = new MatrixGeneric<>(
                getHeight(), getWidth(),
                () -> (operator.apply(iterator.next())));
        
        return result;
    }
    
    /**
     * Applies the given operator elementwise on every element of this matrix
     * and the given one and returns the result.
     * 
     * @param operand second operand
     * @param operator operator to apply on every element of the matricies
     * @return result of the operation
     */
    public MatrixGeneric<E> apply(MatrixGeneric<E> operand,
            BinaryOperator<E> operator) {
        
        final Iterator<E> i1 = iterator();
        final Iterator<E> i2 = operand.iterator();
        final MatrixGeneric<E> result = new MatrixGeneric<>(
                getHeight(), getWidth(),
                () -> (operator.apply(i1.next(), i2.next())));
        
        return result;
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
    public MatrixGeneric<E> applyDifSize(MatrixGeneric<E> operand,
            BinaryOperator<E> operator) {
        
        final MatrixGeneric<E> result = new MatrixGeneric<>(
                Math.max(getHeight(), operand.getHeight()),
                Math.max(getWidth(), operand.getWidth()),
                (y, x) -> {
                    final E value1 = get(y % getHeight(), x % getWidth());
                    final E value2 = operand.get(
                            y % operand.getHeight(), x % operand.getWidth());
                    return operator.apply(value1, value2);
                });
        
        return result;
    }
    
    
    
    /**
     * Iterator which iterates over all elements column-row wise.
     */
    private class MatrixGenericIterator implements Iterator<E> {
        /**
         * Current position of the iterator.
         */
        private int i=0, j=0;
        
        @Override
        public boolean hasNext() {
            return j<getHeight() && i<getWidth();
        }
        
        @Override
        public E next() {
            if(!hasNext()) {
                throw new NoSuchElementException();
            }
            
            
            final E element = get(j, i);
            
            if(++i >= getWidth()) {
                i = 0;
                j++;
            }
            
            return element;
        }
    };
    
    /**
     * Returns and iterator which iterates over all elements column-row wise.
     * 
     * @return iterator which iterates over all elements column-row wise
     */
    @Override
    public Iterator<E> iterator() {
        return new MatrixGenericIterator();
    }
    
    /**
     * Returns a copy of this matrix in 2 dimensional array form
     * 
     * @return copy of this matrix in 2 dimensional array form
     */
    public Object[][] toArray() {
        final Object[][] array = new Object[height][width];
        
        for(int i=0; i<array.length; i++) {
            System.arraycopy(data[i], 0, array[i], 0, array[i].length);
        }
        
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
        
        for(Object[] array : data) {
            builder.append(Arrays.toString(array)).append("\n");
        }
        
        return builder.toString();
    }
}
