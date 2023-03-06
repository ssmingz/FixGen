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

import java.util.*;

import de.parmol.graph.Graph;
import de.parmol.graph.GraphEmbedding;

/**
 * This class ...
 * @author Marc Woerlein (marc.woerlein@gmx.de)
 *
 */
public class EmbeddingSet implements Collection {

	private HashMap map=new HashMap();
	private int size=0;

	/* (non-Javadoc)
	 * @see java.util.Collection#size()
	 */
	public int size() {
		return size;
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#isEmpty()
	 */
	public boolean isEmpty() {
		return map.isEmpty();
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#contains(java.lang.Object)
	 */
	public boolean contains(Object arg0) {
		GraphEmbedding emb=(GraphEmbedding) arg0;
		Graph sup=emb.getSuperGraph();
		Set s=(Set) map.get(sup);
		if (s==null) return false;
		else return (s.contains(arg0));
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#iterator()
	 */
	public Iterator iterator() {
		/* TODO: l�sst sich bestimmt noch optimieren */
		return new Iterator(){
			private Iterator ackit=null;
			private Iterator setit=map.values().iterator();
			public final boolean hasNext(){
				if (ackit==null || !ackit.hasNext()){
					if (!setit.hasNext()) return false; 
					else ackit=((Set)setit.next()).iterator();
				}
				return ackit.hasNext();
			}
			public final Object next(){
				if (hasNext()) return ackit.next();
				else throw new NoSuchElementException("No more elements");
			}
            public final void remove(){ throw new UnsupportedOperationException(); }
		};
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#toArray()
	 */
	public Object[] toArray() {
		return toArray(new Object[size]);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#toArray(java.lang.Object[])
	 */
	public Object[] toArray(Object[] arg0) {
		int i=0;
		for (Iterator it=iterator();it.hasNext();){
			arg0[i++]=it.next();
		}
		return arg0;
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#add(java.lang.Object)
	 */
	public boolean add(Object arg0) {
		GraphEmbedding emb=(GraphEmbedding) arg0;
		Graph sup=emb.getSuperGraph();
		Set s=(Set)map.get(sup);
		if (s==null) {
			s=new HashSet();
			map.put(sup,s);
		}
		boolean ret=s.add(emb);
		if (ret) ++size;
		return ret;
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#remove(java.lang.Object)
	 */
	public boolean remove(Object arg0) {
		GraphEmbedding emb=(GraphEmbedding) arg0;
		Graph sup=emb.getSuperGraph();
		Set s=(Set)map.get(sup);
		if (s==null) return false;
		boolean ret=s.remove(emb);
		if (s.isEmpty()) map.remove(sup);
		if (ret) --size;
		return ret;
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#containsAll(java.util.Collection)
	 */
	public boolean containsAll(Collection arg0) {
		boolean ret=true;
		for (Iterator it=arg0.iterator();it.hasNext();)
			if (!contains(it.next())) ret=false;
		return ret;
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#addAll(java.util.Collection)
	 */
	public boolean addAll(Collection arg0) {
		boolean ret=true;
		for (Iterator it=arg0.iterator();it.hasNext();)
			if (!add(it.next())) ret=false;
		return ret;
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#removeAll(java.util.Collection)
	 */
	public boolean removeAll(Collection arg0) {
		boolean ret=true;
		for (Iterator it=arg0.iterator();it.hasNext();)
			if (!remove(it.next())) ret=false;
		return ret;
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#retainAll(java.util.Collection)
	 */
	public boolean retainAll(Collection arg0) {
		throw new UnsupportedOperationException("retainAll is not suported for EmbeddingSet");
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#clear()
	 */
	public void clear() {
		map.clear();
		size=0;
	}

	/**
	 * @param supergraph
	 * @return a colections of GraphEmbeddings coresponding to the given supergraph
	 */
	public Collection getEmbeddings(Graph supergraph){
		return (Set) map.get(supergraph);
	}
	
	/**
	 * @return a Set of all supergraph coresponding in this EmbeddingSet2
	 */
	public Set superGraphs(){ return map.keySet(); }
	
}
