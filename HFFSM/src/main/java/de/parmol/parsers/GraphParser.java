/*
 * Created on Jun 14, 2004
 *
 * Copyright 2004 Thorsten Meinl
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
package de.parmol.parsers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;

import de.parmol.graph.Graph;
import de.parmol.graph.GraphFactory;


/**
 * This interface describes a parser for graphs.
 * 
 * @author Thorsten.Meinl@informatik.uni-erlangen.de
 *
 */
public interface GraphParser {
	/** */
	public final static int MATRIX_GRAPH = 1;
	/** */
	public final static int LIST_GRAPH = 2;
    
    /**
     * Creates a new graph by parsing the given string.
     * @param text the string representation of a graph
     * @param factory a factory for creating new graphs
     * @return the parsed graph
     * @throws ParseException if the given string was not a valid graph 
     */
    public Graph parse(String text, GraphFactory factory) throws ParseException;
    
    /**  
     * @param g the graph that shall be serialized
     * @return a string represenation of the given graph 
     */
    public String serialize(Graph g);
    
    /**
     * Serializes all graphs in the given array into the given OutputStream
     * @param graphs an array of graphs
     * @param out an OutputStream
     * @throws IOException if an error occurs while writing to the given OutputStream
     */
    public void serialize(Graph[] graphs, OutputStream out) throws IOException;
    
    
    /**
     * Creates new graphs by parsing data from the given input stream (which should have been created by serialize before)
     * @param in an InputStream
     * @param factory a factory for creating new graphs
     * @return an array of parsed graphs
     * @throws ParseException if the given InputStream contains errors
     * @throws IOException if an error occurs while reading from the given InputStream
     */
    public Graph[] parse(InputStream in, GraphFactory factory) throws IOException, ParseException;
    
    
    /**
     * Returns the properties that a graph factory for this parser must have. The value is a bitwise or of the properties listed in de.parmol.graph.GraphFactory .
     * @return the desired properties
     */
    public int getDesiredGraphFactoryProperties();
    
    /**
     * Returns a String representation of the given integer node label
     * @param nodeLabel the integer node label
     * @return the node label as string
     */
    public String getNodeLabel(int nodeLabel);
    
    /**
     * @return if the last parsed graph was directed or not
     */
    public boolean directed();
}
