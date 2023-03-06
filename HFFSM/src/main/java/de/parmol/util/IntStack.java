/*
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

import java.util.NoSuchElementException;

/**
 * This class represents a stack that holds ints. It grows if more space is needed (but does not shrink).
 * 
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 */
public final class IntStack {
  private int[] m_stack;
  private int m_pointer = -1; 
  
  /**
   * Creates a new stack for ints.
   * @param initialSize the initial size of the stack
   */
  public IntStack(int initialSize) {
    m_stack = new int[initialSize];
  }
  
  /**
   * Creates a new stack for ints with a default size (of currently 256).
   */
  public IntStack() { this(256); }

  
  /**
   * Pushes a new new value onto the stack.
   * @param value the new value
   */
  public void push(int value) {
    if (m_pointer >= m_stack.length) {
      int[] temp = new int[m_stack.length + (m_stack.length / 4)];
      System.arraycopy(m_stack, 0, temp, 0, m_stack.length);
      m_stack = temp;
    }
    
    m_stack[++m_pointer] = value;
  }
  
  /**
   * Pops a value from the stack.
   * @return the popped value
   * @throws NoSuchElementException if the stack is empty
   */
  public int pop() {
    if (m_pointer < 0) throw new NoSuchElementException("Stack is empty");
    
    return m_stack[m_pointer--];
  }
  
  /**
   * Returns the element on top of the stack without popping it.
   * @return the value on top of the stack
   * @throws NoSuchElementException if the stack is empty 
   */
  public int getTop() {
    if (m_pointer < 0) throw new NoSuchElementException("Stack is empty");
    
    return m_stack[m_pointer];
  }
  
  /** 
   * @return <code>true</code> if the stack is empty, <code>false</code> otherwise
   */
  public boolean empty() { return m_pointer == -1; }
}

