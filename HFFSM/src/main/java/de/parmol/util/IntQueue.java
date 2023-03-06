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
 * This class represents a queue that holds ints. It grows if more space is needed (but does not shrink).
 * 
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 */
public class IntQueue {
  private int[] m_queue;
  private int m_head = 0, m_tail = -1;
  
  /**
   * Creates a new queue for ints.
   * @param initialSize the initial size of the queue
   */
  public IntQueue(int initialSize) {
    m_queue = new int[initialSize];
  }
  
  /**
   * Creates a new queue for ints with a default size (of currently 256).
   */
  public IntQueue() { this(256); }
  
  /**
   * Adds a new value to the queue.
   * @param value the new value.
   */
  public void enqueue(int value) {
    if (m_tail >= m_queue.length) {
      if (m_head >= m_queue.length / 4) {
        System.arraycopy(m_queue, m_head, m_queue, 0, m_tail - m_head + 1);
        m_tail -= m_head;
        m_head = 0;
      } else {
        int[] temp = new int[m_queue.length + m_queue.length/4];
        System.arraycopy(m_queue, 0, temp, 0, m_queue.length);
        m_queue = temp;
      }      
    }
    
    m_queue[++m_tail] = value;
  }

  /**
   * Returns and remove the value at the front of the queue.
   * @return the front value
   */
  public int dequeue() {
    if (m_head > m_tail) {
      throw new NoSuchElementException("Queue is empty");
    }
    
    return m_queue[m_head++];
  }
  
  /**
   * @return the value at the front of the queue
   */
  public int first() {
    if (m_head > m_tail) {
      throw new NoSuchElementException("Queue is empty");
    }
    return m_queue[m_head];
  }

  /** 
   * @return the value at the back of the queue
   */
  public int last() {
    if (m_head > m_tail) {
      throw new NoSuchElementException("Queue is empty");
    }
    return m_queue[m_tail];
  }

  /** 
   * @return <code>true</code> if the queue is empty, <code>false</code> otherwise
   */
  public boolean empty() { return (m_head > m_tail); }
}

