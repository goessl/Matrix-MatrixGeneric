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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.function.Function;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;



/**
 * JPanel extension to visually output a matrix.
 * 
 * @author Sebastian Gössl
 * @version 1.0 17.9.2019
 */
public class JMatrixPanel<E> extends JPanel {
    
    /**
     * Matrix to be displayed.
     */
    private final MatrixGeneric<E> matrix;
    /**
     * Function that maps the values of the matrix to colours on the screen.
     */
    private final Function<E, Color> colorMap;
    
    
    
    /**
     * Constructs a new JMatrixPanel with the given matrix & colorMap.
     * 
     * @param matrix matrix to be displayed
     * @param colorMap function that maps values of the matrix to colours
     */
    public JMatrixPanel(MatrixGeneric<E> matrix, Function<E, Color> colorMap) {
        super();
        this.matrix = matrix;
        this.colorMap = colorMap;
    }
    
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(matrix.getWidth(), matrix.getHeight());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void paintComponent(Graphics g) {
        
        final Graphics2D g2 = (Graphics2D)g;
        
        final Rectangle clip = getBounds();
        final double ky = (double)clip.height / matrix.getHeight();
        final double kx = (double)clip.width / matrix.getWidth();
        
        matrix.forEachIndices((j, i) -> {
            g2.setColor(colorMap.apply(matrix.get(j, i)));
            g2.fillRect((int)(kx*i), (int)(ky*j),
                    (int)Math.ceil(kx), (int)Math.ceil(ky));
        });
    }
    
    
    
    
    public static void main(String[] args) {
        
        final MatrixGeneric<Double> matrix =
                new MatrixGeneric<>(100, 100,
                        (j, i) -> (double)(j+i) / (100+100));
        final Function<Double, Color> colorMap =
                (x) -> Color.getHSBColor(
                        -2f/3f * x.floatValue() + 2f/3f, 1f, 1f);
        
        EventQueue.invokeLater(() -> {
            final JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(
                    WindowConstants.DISPOSE_ON_CLOSE);
            frame.getContentPane().add(new JMatrixPanel(matrix, colorMap));
            frame.pack();
            frame.setVisible(true);
        });
    }
}
