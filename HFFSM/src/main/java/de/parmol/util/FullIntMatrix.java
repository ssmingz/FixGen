/*
 * Created on 12.12.2004
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

import java.text.DecimalFormat;

import de.parmol.Util;

/**
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 *
 */
public final class FullIntMatrix implements IntMatrix {
	/** the array holding the complete matrix */
	private int[] m_matrix;
	
	private int m_size, m_initialValue;

	/**
	 * Creates a new FullIntMatrix with the given size and initial values
	 * @param initialSize the size of the matrix in rows (or columns)
	 * @param initialValue the initial value of each matrix element
	 */
	public FullIntMatrix(int initialSize, int initialValue) {
		m_matrix = new int[initialSize * initialSize];		
		for (int i = 0; i < m_matrix.length; i++) m_matrix[i] = initialValue;
		m_size = initialSize;
		m_initialValue = initialValue;
	}
	
	/**
	 * Creates a new FullIntMatrix that is an exact copy of the given template
	 * @param template a FullIntMatrix that should be copied
	 */
	public FullIntMatrix(FullIntMatrix template) {
		m_matrix = new int[template.m_matrix.length];
		System.arraycopy(template.m_matrix, 0, m_matrix, 0, m_matrix.length);
		m_size = template.m_size;
		m_initialValue = template.m_initialValue;
	}


	/* (non-Javadoc)
	 * @see de.parmol.util.IntMatrix#getValue(int, int)
	 */
	public int getValue(int row, int col) {
		if ((row > m_size) || (col > m_size)) throw new IllegalArgumentException("row or column index too big: matrix has only " + m_size + " rows/cols");
		
		return m_matrix[row * m_size + col];
	}


	/* (non-Javadoc)
	 * @see de.parmol.util.IntMatrix#setValue(int, int, int)
	 */
	public void setValue(int row, int col, int value) {
		if ((row > m_size) || (col > m_size)) throw new IllegalArgumentException("row or column index too big: matrix has only " + m_size + " rows/cols");
		
		m_matrix[row * m_size + col] = value;
	}


	/* (non-Javadoc)
	 * @see de.parmol.util.IntMatrix#getSize()
	 */
	public int getSize() { return m_size; }

	/* (non-Javadoc)
	 * @see de.parmol.util.IntMatrix#resize(int)
	 */
	public void resize(int newSize) {
		int[] temp = new int[newSize * newSize];

		for (int i = 0; i < temp.length; i++) temp[i] = m_initialValue;		
		for (int i = 0; i < m_size; i++) {
			System.arraycopy(m_matrix, i * m_size, temp, i * newSize, m_size);
		}
		m_matrix = temp;
		m_size = newSize;
	}


	/* (non-Javadoc)
	 * @see de.parmol.util.IntMatrix#deleteRowAndCol(int)
	 */
	public void deleteRowAndCol(int row) {
		int shift = 0;
		
		m_size--;
		for (int r = 0; r < m_size; r++) {
			if (r == row) shift += m_size + 1;
			for (int c = 0; c < m_size; c++) {
				if (c == row) shift++;
				
				m_matrix[r * m_size + c] = m_matrix[r * m_size + c + shift]; 				
			}
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		int maxLength = 1;
		for (int i = 0; i < m_matrix.length; i++) {
			maxLength = java.lang.Math.max(maxLength, Util.getDigits(m_matrix[i]));			
		}
		
		DecimalFormat format = new DecimalFormat("000000000000000000000000000".substring(0, maxLength));
		
		StringBuffer buf = new StringBuffer((m_matrix.length + 1) * maxLength + 16);
		for (int row = 0; row < m_size; row++) {
			for (int col = 0; col < m_size; col++) {
				buf.append(format.format(getValue(row, col)));
			}
			buf.append('\n');
		}
		
		return buf.toString();
	}

	/* (non-Javadoc)
	 * @see de.parmol.util.IntMatrix#exchangeRows(int, int)
	 */
	public void exchangeRows(int rowA, int rowB) {
		if (rowA == rowB) return;
		
		if (rowA > rowB) {
			int t = rowA;
			rowA = rowB;
			rowB = t;
		}
		
		for (int i = 0; i < m_size; i++) {
			int tA = m_matrix[rowA * m_size + i];
			int tB = m_matrix[rowB * m_size + i];
			
			m_matrix[rowB * m_size + i] = tA;
			m_matrix[rowA * m_size + i] = tB;
		}

		
		for (int i = 0; i < m_size; i++) {
			int tA = m_matrix[i * m_size + rowA];
			int tB = m_matrix[i * m_size + rowB];
			
			m_matrix[i * m_size + rowB] = tA;
			m_matrix[i * m_size + rowA] = tB;
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {
		FullIntMatrix m = (FullIntMatrix) o;
		

		int max = java.lang.Math.max(this.m_size*this.m_size + this.m_size, m.m_size*m.m_size + m.m_size);
		for (int i = 0; i < max; i++) {
			if (this.m_matrix[i] != m.m_matrix[i]) {
				return this.m_matrix[i] - m.m_matrix[i];
			}
		}
		
		return this.m_size - m.m_size;
	}	
}
