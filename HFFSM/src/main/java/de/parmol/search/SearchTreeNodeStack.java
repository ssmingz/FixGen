/*
 * Created on 20.04.2005
 * 
 * Copyright 2005 Thorsten Meinl
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
 *  
 */
package de.parmol.search;

import java.util.EmptyStackException;


/**
 * This class is a simple stack for search tree nodes.
 * 
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 */
class SearchTreeNodeStack {
	private SearchTreeNode[] m_stack;
	private int m_stackPointer = 0;
	
	/**
	 * Creates a new stack with the given inital size
	 * @param initialSize the initial size of the stack
	 */
	public SearchTreeNodeStack(int initialSize) {
		m_stack = new SearchTreeNode[initialSize];
	}
	
	/**
	 * Pushes a new node onto the stack
	 * @param node the node to push
	 */
	public void push(SearchTreeNode node) {
		if (m_stackPointer >= m_stack.length) resize();			
		
		m_stack[m_stackPointer++] = node;
	}
	
	/**
	 * Pops a node from the stack.
	 * @return the topmost node from the stack
	 * @throws EmptyStackException if the stack is empty
	 */
	public SearchTreeNode pop() throws EmptyStackException {
		if (m_stackPointer == 0) throw new EmptyStackException();
		SearchTreeNode node = m_stack[--m_stackPointer];
		m_stack[m_stackPointer] = null;
		return node;
	}
	
	/**
	 * Returns the number of nodes on the stack.
	 * @return the size of the stack
	 */
	public int size() { return m_stackPointer; }
	
	/**
	 * Returns the topmost element from the stack without removing it.
	 * @return the topmost element
	 * @throws EmptyStackException if the stack is empty
	 */
	public SearchTreeNode peek() throws EmptyStackException {
		if (m_stackPointer <= 0) throw new EmptyStackException();
		return m_stack[m_stackPointer - 1];			
	}
	
	/**
	 * Returns <code>true</code> if the stack is empty, <code>false</code> otherwise.
	 * @return if the stack is empty
	 */
	public boolean isEmpty() { return (m_stackPointer == 0); }
	
	/**
	 * Resizes the stack if it is too small.
	 */
	private void resize() {
        resize(m_stack.length + (m_stack.length >> 2) + 1);
	}

    /**
     * Resizes the stack if it is too small.
     * @param newSize the new size of the stack
     */
    private void resize(int newSize) {
        SearchTreeNode[] temp = new SearchTreeNode[newSize];
        System.arraycopy(m_stack, 0, temp, 0, m_stackPointer);
        m_stack = temp;                         
    }

	/**
	 * Splits the stack into two pieces. One half remains on this stack, the other elements are put into the
	 * given stack, that must be empty. 
	 * @param stack the stack which should receive half of the elements
	 */
	public void split(SearchTreeNodeStack stack) {
		if (m_stackPointer < 2) return;			
		if (! stack.isEmpty()) throw new IllegalArgumentException("The given stack must be empty");
		
		if (stack.m_stack.length < m_stack.length / 2) stack.resize(m_stack.length / 2 + 1);

		for (int i = 1; i < m_stackPointer; i += 2) {
			stack.m_stack[stack.m_stackPointer++] = m_stack[i];
		}			

		m_stackPointer -= stack.m_stackPointer;
		// fill the holes in the stack
		for (int i = 1; i < m_stackPointer; i ++) {
			m_stack[i] = m_stack[2*i];
		}			
		
		for (int i = m_stackPointer; i < m_stack.length; i++) m_stack[i] = null;
	}
}