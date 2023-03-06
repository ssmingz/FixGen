/*
 * Created on May 17, 2004
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
package de.parmol.graph;

import java.util.Iterator;


/**
 * This list just stores graph embeddings and offers some basic functionality to join two embedding lists
 * and iterate over them.
 * 
 * @author Thorsten Meinl <Thorsten@meinl.bnv-bamberg.de>
 *
 */
public class EmbeddingList {
  /**
   * Adds an embedding to this list.
   * @param ge a new embedding
   */  
	public void addEmbedding(GraphEmbedding ge) {
        //TODO add implementation 
    }
    
    /**
     * @return an Iterator over all embeddings
     */
    public Iterator iterator() {
        //TODO add implementation
        return null;
    }
    
    /**
     * Merges this list with the given embeddings list 
     * @param listB the list to merge with
     * @return the new merged embedding list
     */
    public EmbeddingList mergeEmbeddings(EmbeddingList listB) {
        //TODO add implementation
        return null;
    }
}
