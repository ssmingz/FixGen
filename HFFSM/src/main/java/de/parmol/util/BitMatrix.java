/*
 * Created on Aug 4, 2004
 *
 * This file is part of ParMol.
 * ParMol is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * ParMol is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with ParMol; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package de.parmol.util;

/**
 * This class represents a quadratic matrix that only stores bit values. Internally the matrix is represented
 * as an int[]-array so that the memory consumption of the whole matrix is <code>ceil(size*size/32)</code> bits.
 * 
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 */
public final class BitMatrix {
	protected int[] m_matrix;
	protected int m_size;
	
	/**
	 * Creates a new BitMarix with an initial size.
	 * @param initialSize the initial size of the matrix (number of rows/columns)
	 */
	public BitMatrix(int initialSize) {
		m_size = initialSize;		
		m_matrix = new int[(int) java.lang.Math.ceil(m_size*m_size / 32)];
	}

	/**
	 * Creates a new BitMatrix that is a copy of the given matrix.
	 * @param template the matrix that should be copied
	 */
	public BitMatrix(BitMatrix template) {
		m_matrix = new int[template.m_matrix.length];
		System.arraycopy(template.m_matrix, 0, m_matrix, 0, m_matrix.length);
		m_size = template.m_size;
	}

	/**
	 * Sets the bit in the specified row and column of the matrix.
	 * @param row the row index of the bit, a value between 0 and <code>getSize() - 1</code>
	 * @param column the the column index of the bit, a value between 0 and <code>getSize() - 1</code>
	 */
	public void setBit(int row, int column) {
		m_matrix[(row * m_size + column) / 32] |= 1 << ((row * m_size + column) % 32); 		
	}

	/**
	 * Clears the bit in the specified row and column of the matrix.
	 * @param row the row index of the bit, a value between 0 and <code>getSize() - 1</code>
	 * @param column the the column index of the bit, a value between 0 and <code>getSize() - 1</code>
	 */
	public void clearBit(int row, int column) {
		m_matrix[(row * m_size + column) / 32] &= ~(1 << ((row * m_size + column) % 32)); 		
	}
	
	/**
	 * Returns the bit in the specified row and column of the matrix.
	 * @param row the row index of the bit, a value between 0 and <code>getSize() - 1</code>
	 * @param column the the column index of the bit, a value between 0 and <code>getSize() - 1</code>
	 * @return <code>true</code> if the bit is set, <code>false</code> otherwise
	 */	
	public boolean getBit(int row, int column) {
		return ((m_matrix[(row * m_size + column) / 32] & (1 << ((row * m_size + column) % 32))) != 0);
	}
	
	/**
	 * Returns the number of rows/columns in the matrix.
	 * @return the size of the matrix
	 */
	public int getSize() { return m_size; }
	
	/**
	 * Resizes the matrix. If the new size is smaller than the current size, entries are deleted.
	 * @param newSize the new number of rows/columns
	 */
	public void resize(int newSize) {			
		int[] temp = new int[(int) java.lang.Math.ceil(newSize*newSize / 32)];
		
		for (int row = 0; row < newSize; row++) {
			for (int col = 0; col < newSize; col++) {
				if ((m_matrix[(row * m_size + col) / 32] & (1 << ((row * m_size + col) % 32))) != 0) {
					temp[(row * newSize + col) / 32] |= 1 << ((row * newSize + col) % 32);
				}
			}
		}
		m_size = newSize;
		m_matrix = temp;
	}	
}
