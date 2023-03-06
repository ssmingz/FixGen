/*
 * Created on Jan 14, 2005
 * 
 * This file is part of ParMol. ParMol is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 * 
 * ParMol is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * ParMol; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 * 
 */
package de.parmol.graph;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Maps the int labels used inside the classes that implement Graph to Strings and vice versa. Strings are assigned a
 * unique int when they are first used for a query.
 * 
 * @author Sebastian Seifert <sebastian@kontextfrei.de>
 */
public class LabelRegistry {

	private ArrayList nodeLabels = new ArrayList();
	private ArrayList edgeLabels = new ArrayList();

	private HashMap nodeLabelStr2Int = new HashMap();
	private HashMap edgeLabelStr2Int = new HashMap();


	/**
	 * Create a new, empty LabelRegistry.
	 */
	public LabelRegistry() {
		this(null, null);
	}


	/**
	 * Create a preinitialized LabelRegistry.
	 * 
	 * @param nodeLabels An array of node label Strings, or null.
	 * @param edgeLabels An array of edge label Strings, or null.
	 */
	public LabelRegistry(String[] nodeLabels, String[] edgeLabels) {
		if (nodeLabels != null) {
			this.nodeLabels = new ArrayList(nodeLabels.length);
			for (int i = 0; i < nodeLabels.length; i++) {
				this.nodeLabels.add(nodeLabels[i]);
				nodeLabelStr2Int.put(nodeLabels[i], new Integer(i));
			}
		}

		if (edgeLabels != null) {
			this.edgeLabels = new ArrayList(edgeLabels.length);
			for (int i = 0; i < edgeLabels.length; i++) {
				this.edgeLabels.add(edgeLabels[i]);
				edgeLabelStr2Int.put(edgeLabels[i], new Integer(i));
			}
		}
	}


	/**
	 * Returns an int for a node label String. If the String is passed for the first time, it is registered and a new
	 * unique int is returned. Every subsequent time, the same int will be returned.
	 * 
	 * @param labelStr A node label.
	 * @return The int value registered for this String, or a heretofore unused int.
	 */
	public int nodeLabelInt(String labelStr) {
		if (nodeLabelStr2Int.containsKey(labelStr)) {
			return ((Integer) nodeLabelStr2Int.get(labelStr)).intValue();
		} else {
			int nodeLabelInt = nodeLabels.size();
			nodeLabelStr2Int.put(labelStr, new Integer(nodeLabelInt));
			nodeLabels.add(labelStr);
			return nodeLabelInt;
		}
	}


	/**
	 * Returns an int for an edge label String. If the String is passed for the first time, it is registered and a new
	 * unique int is returned. Every subsequent time, the same int will be returned.
	 * 
	 * @param labelStr An edge label.
	 * @return The int value registered for this String, or a heretofore unused int.
	 */
	public int edgeLabelInt(String labelStr) {
		if (edgeLabelStr2Int.containsKey(labelStr)) {
			return ((Integer) edgeLabelStr2Int.get(labelStr)).intValue();
		} else {
			int edgeLabelInt = edgeLabels.size();
			edgeLabelStr2Int.put(labelStr, new Integer(edgeLabelInt));
			edgeLabels.add(labelStr);
			return edgeLabelInt;
		}
	}


	/**
	 * Returns a previously registered String that belongs to the int, or throws NoSuchElementException.
	 * 
	 * @param labelInt An int that has been returned by nodeLabelInt.
	 * @return The String that belongs to labelInt.
	 */
	public String nodeLabelStr(int labelInt) {
		return (String) nodeLabels.get(labelInt);
	}


	/**
	 * Returns a previously registered String that belongs to the int, or throws NoSuchElementException.
	 * 
	 * @param labelInt An int that has been returned by edgeLabelInt.
	 * @return The String that belongs to labelInt.
	 */
	public String edgeLabelStr(int labelInt) {
		return (String) edgeLabels.get(labelInt);
	}


	/**
	 * Checks if the given integer node label is registered.
	 * 
	 * @param label a node label
	 * @return <code>true</code> if the label is registered, <code>false</code> otherwise
	 */
	public boolean existsNodeLabel(int label) {
		return label < nodeLabels.size();
	}


	/**
	 * Checks if the given string node label is registered.
	 * 
	 * @param label a node label
	 * @return <code>true</code> if the label is registered, <code>false</code> otherwise
	 */
	public boolean existsNodeLabel(String label) {
		return nodeLabelStr2Int.containsKey(label);
	}


	/**
	 * Checks if the given integer edge label is registered.
	 * 
	 * @param label an edge label
	 * @return <code>true</code> if the label is registered, <code>false</code> otherwise
	 */
	public boolean existsEdgeLabel(int label) {
		return label < edgeLabels.size();
	}


	/**
	 * Checks if the given string edge label is registered.
	 * 
	 * @param label an edge label
	 * @return <code>true</code> if the label is registered, <code>false</code> otherwise
	 */
	public boolean existsEdgeLabel(String label) {
		return edgeLabelStr2Int.containsKey(label);
	}
}