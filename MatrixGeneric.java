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
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;



/**
 * Generic matrix class used to store and operate on generic matricies.
 * The indices of the elements are zero indexed like if it would be a two
 * dimensional array (what it actually is internally).
 * The first index is always the row index counted from top to bottom and the
 * second index is always the column index counted from left to right.
 * e.g. a 2x3 matrix
 *  [[0,0 0,1 0,2],
 *   [1,0 1,1 1,2]]
 * 
 * All operations that apply to more than one single element (set, iterator,
 * forEach, ...) traverse the matrix column-row vise.
 * e.g. the same 2x3 matrix will be operated in this order
 *  [[0 1 2],
 *   [3 4 5]]
 * 
 * 
 * @author Sebastian Gössl
 * @version 1.2 1.9.2019
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
     * The elements of the matrix are filled into the matrix column-row vise.
     * 
     * @param supplier supplier to supply new values for all elements
     */
    public void set(Supplier<E> supplier) {
        forEachIndices((y, x) -> set(y, x, supplier.get()));
    }
    
    /**
     * Replaces all elements with the values returned from the given function.
     * It recieves the position (row and column indices) of the element to
     * replace as arguments.
     * 
     * @param function function to calculate new values for all elements
     */
    public void set(BiFunction<Integer, Integer, E> function) {
        forEachIndices((y, x) -> set(y, x, function.apply(y, x)));
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
    public void apply(UnaryOperator<E> operator) {
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
    public void apply(MatrixGeneric<E> operand, BinaryOperator<E> operator) {
        final Iterator<E> i1 = iterator();
        final Iterator<E> i2 = operand.iterator();
        set(() -> (operator.apply(i1.next(), i2.next())));
    }
    
    /**
     * Applies the given operator on every element of this matrix and the given
     * matrix elementwise wrapping around.
     * 
     * @param operand second operand
     * @param operator operator to apply on every element of the matrix
     */
    public void applyDifSize(MatrixGeneric<E> operand,
            BinaryOperator<E> operator) {
        set((y, x) -> {
            final E value1 = get(y, x);
            final E value2 = operand.get(y % getHeight(), x % getWidth());
            return operator.apply(value1, value2);
        });
    }
    
    /**
     * Applies the given operator on every element of this matrix and returns
     * the result.
     * 
     * @param operator operator to apply on every element of this matrix
     * @return result of the operation
     */
    public MatrixGeneric<E> applyNew(UnaryOperator<E> operator) {
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
    public MatrixGeneric<E> applyNew(MatrixGeneric<E> operand,
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
    public MatrixGeneric<E> applyNewDifSize(MatrixGeneric<E> operand,
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
        public E next() {
            if(!hasNext()) {
                throw new NoSuchElementException();
            }
            
            
            final E element = get(row, column);
            
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
    public Iterator<E> iterator() {
        return new MatrixGenericIterator();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void forEach(Consumer<? super E> action) {
        forEachIndices((y, x) -> action.accept(get(y, x)));
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
     * Spliterator which iterates over all elements column-row wise.
     * Works similarly as the MatrixIterator. If an split occours the remaining
     * elements are split in half and the super spliterator continous to up to
     * the split and the new spliterator begins at the split up to the old
     * fence of the super spliterator.
     */
    private static class MatrixGenericSpliterator<E> implements Spliterator<E> {
        /**
         * Matrix this spliterator traverses.
         */
        private final MatrixGeneric<E> matrix;
        /**
         * Indices of the next element.
         */
        private int row, column;
        /**
         * Indices of the first element not beeing iterated over.
         */
        private int fenceRow, fenceColumn;
        
        
        
        /**
         * Constructs a new MatrixSpliterator that iterates over the given
         * matrix.
         * It starts at indices 0,0 and continous up to position
         * (height-1),(width-1).
         * 
         * @param matrix matrix this spliterator iterates over
         */
        public MatrixGenericSpliterator(MatrixGeneric<E> matrix) {
            this(matrix, 0, 0, matrix.getHeight(), 0);
        }
        
        /**
         * Constructs a new MatrixSpliterator that iterates over the given
         * matrix, starting at the given indices continuing up to position
         * fenceRow,fenceColumn.
         * 
         * @param matrix matrix this spliterator iterates over
         * @param row row index of the element on which this spliterator starts
         * @param column column index of the element on which this spliterator
         * starts
         * @param fenceRow row index of the last element this spliterator
         * iterates over
         * @param fenceColumn column index of the last element this spliterator
         * iterates over
         */
        public MatrixGenericSpliterator(MatrixGeneric<E> matrix,
                int row, int column, int fenceRow, int fenceColumn) {
            
            this.matrix = matrix;
            
            this.row = row;
            this.column = column;
            
            this.fenceRow = fenceRow;
            this.fenceColumn = fenceColumn;
        }
        
        
        
        /**
         * {@inheritDoc}
         */
        @Override
        public int characteristics() {
            return Spliterator.ORDERED | Spliterator.SIZED
                    | Spliterator.NONNULL | Spliterator.CONCURRENT
                    | Spliterator.SUBSIZED;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public long estimateSize() {
            /*return (fenceRow - row) * matrix.getWidth()
                    - column + fenceColumn;*/
            return linearIndex(fenceRow, fenceColumn)
                    - linearIndex(row, column);
        }
        
        
        /**
         * Returns a linear index equivalent the the given indices. The linear
         * index representation equals the ordering of this spliterator.
         * 
         * @param row row index of the position
         * @param column column index of the position
         * @return linear equvalent of the given position
         */
        private long linearIndex(int row, int column) {
            return row * matrix.getWidth() + column;
        }
        
        /**
         * Returns the row index of the given linear index representation.
         * 
         * @param index linear index representation
         * @return row index of the given linear index representation
         */
        private int indexToRow(long index) {
            return (int)(index / matrix.getWidth());
        }
        
        /**
         * Returns the column index of the given linear index representation.
         * 
         * @param index linear index representation
         * @return column index of the given linear index representation
         */
        private int indexToColumn(long index) {
            return (int)(index % matrix.getWidth());
        }
        
        
        /**
         * Returns if the next tryAdvance call will succeed.
         * 
         * @return if the next tryAdvance call will succeed
         */
        public boolean hasNext() {
            return row < fenceRow
                    || (row == fenceRow && column < fenceColumn);
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public boolean tryAdvance(Consumer<? super E> action) {
            if(!hasNext()) {
                return false;
            }
            
            
            final E element = matrix.get(row, column);
            
            if(++column >= matrix.getWidth()) {
                column = 0;
                row++;
            }
            
            action.accept(element);
            
            return true;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public Spliterator<E> trySplit() {
            final long fence = linearIndex(fenceRow, fenceColumn);
            final long index = linearIndex(row, column);
            final long mid = (index + fence) >>> 1;
            
            
            if(index < mid && mid < fence) {
                
                final MatrixGenericSpliterator<E> newSpliterator =
                        new MatrixGenericSpliterator<>(matrix,
                                indexToRow(mid), indexToColumn(mid),
                                fenceRow, fenceColumn);
                
                fenceRow = indexToRow(mid);
                fenceColumn = indexToColumn(mid);
                
                return newSpliterator;
                
            } else {
                return null;
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Spliterator<E> spliterator() {
        return new MatrixGenericSpliterator<>(this);
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
