/*
 * Created on 28.03.2005
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
package de.parmol.FFSM;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.parmol.graph.Graph;


/**
 * This class is the core of the FFSM algorithm, it joins end extends the matrices. Optionally it can be given an array
 * of all frequent node labels. This information is then used in the extension step so that only frequent nodes are
 * added. This may improve the performance a little bit if many unfrequent node labels exist.
 * 
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 */
public class JoinerAndExtender {
	private final GraphEdge[] m_frequentEdges;
	private final GraphEdge m_edge = new GraphEdge(-1, -1, -1);
	
	/**
	 * Creates a new joiner and extender.
	 */
	public JoinerAndExtender() {
		m_frequentEdges = null;
	}


	/**
	 * Creates a new joiner and extender with the given frequent node labels. This array is then used by during the
	 * extension process to filter out infrequent matrices before calculating the embedding lists.
	 * 
	 * @param frequentEdges an array of all frequent graph edges
	 */
	public JoinerAndExtender(GraphEdge[] frequentEdges) {
		m_frequentEdges = new GraphEdge[frequentEdges.length];
		System.arraycopy(frequentEdges, 0, m_frequentEdges, 0, frequentEdges.length);
		Arrays.sort(m_frequentEdges);
	}


	/**
	 * Joins the two given matrices and puts all resulting matrices in the given collection. The number of created
	 * matrices is returned. Please keep in mind, that the order of the matrices is important for join case 3b.
	 * 
	 * @param matrixA the first matrix
	 * @param matrixB the second matrix
	 * @param newMatrices a collection into which the new matrices should be put
	 * @return the number of created matrices (1 or 2)
	 */
	public int join(Matrix matrixA, Matrix matrixB, Collection newMatrices) {
		if ((matrixA.getEdgeCount() == 0) || (matrixB.getEdgeCount() == 0)) return 0;

		assert (matrixA.compareMaximalProperSubmatrix(matrixB) == 0);
		assert (matrixA.getEmbeddings().size() * matrixB.getEmbeddings().size() != 0);

		final boolean innerA = matrixA.isInnerMatrix();
		final boolean innerB = matrixB.isInnerMatrix();

		final int lastEdgeA = matrixA.getLastEdge();
		final int lastEdgeB = matrixB.getLastEdge();

		if ((lastEdgeA == Graph.NO_EDGE) || (lastEdgeB == Graph.NO_EDGE)) return 0;

		if (innerA && innerB) { // join case 1
			if (matrixA.getNodeCount() != matrixB.getNodeCount()) return 0;
			if (matrixA.compareTo(matrixB) < 0) return 0;

			if (lastEdgeA != lastEdgeB) {
				if (m_frequentEdges != null) {
					m_edge.nodeALabel = matrixB.getNodeLabel(matrixB.getNodeA(lastEdgeB));
					m_edge.nodeBLabel = matrixB.getNodeLabel(matrixB.getNodeB(lastEdgeB));
					m_edge.edgeLabel = matrixB.getEdgeLabel(lastEdgeB);
	
					if (Arrays.binarySearch(m_frequentEdges, m_edge) < 0) {
						return 0;
					}
				}
				
				// here we make an assumption about the encoding of the edges which is not very clean from the
				// software engineering point of view...
				final Matrix newMatrix = new Matrix(matrixA, EmbeddingList.intersect(matrixA.getEmbeddings(), matrixB
						.getEmbeddings()), 0);
				newMatrix.addEdge(newMatrix.getNode(matrixB.getNodeIndex(matrixB.getNodeA(lastEdgeB))), newMatrix
						.getNode(matrixB.getNodeIndex(matrixB.getNodeB(lastEdgeB))), matrixB.getEdgeLabel(lastEdgeB));
				newMatrices.add(newMatrix);

				return 1;
			} else {
				return 0;
			}
		} else if (innerA && !innerB) { // join case 2
			if (matrixA.getNodeCount() + 1 != matrixB.getNodeCount()) return 0;
			if (matrixA.compareTo(matrixB) < 0) return 0;

			if (m_frequentEdges != null) {
				m_edge.nodeALabel = matrixA.getNodeLabel(matrixA.getNodeA(lastEdgeA));
				m_edge.nodeBLabel = matrixA.getNodeLabel(matrixA.getNodeB(lastEdgeA));
				m_edge.edgeLabel = matrixA.getEdgeLabel(lastEdgeA);

				if (Arrays.binarySearch(m_frequentEdges, m_edge) < 0) {
					return 0;
				}
			}

			
			final Matrix newMatrix = new Matrix(matrixB, EmbeddingList.joinCase2Intersection(matrixA.getEmbeddings(), matrixB
					.getEmbeddings()), 0);
			newMatrix.addEdge(newMatrix.getNode(matrixA.getNodeIndex(matrixA.getNodeA(lastEdgeA))), newMatrix.getNode(matrixA
					.getNodeIndex(matrixA.getNodeB(lastEdgeA))), matrixA.getEdgeLabel(lastEdgeA));
			newMatrices.add(newMatrix);

			return 1;
		} else if (!innerA && !innerB) { // join case 3
			if (matrixA.getNodeCount() != matrixB.getNodeCount()) return 0;

			final int nA = matrixB.getNodeA(lastEdgeB);
			final int nB = matrixB.getNodeB(lastEdgeB);
			
			boolean ok = true;
			if (m_frequentEdges != null) {
				m_edge.nodeALabel = matrixB.getNodeLabel(matrixB.getNodeCount() - 1);
				m_edge.nodeBLabel = matrixB.getNodeLabel((nA > nB) ? nB : nA);
				m_edge.edgeLabel = matrixB.getEdgeLabel(lastEdgeB);

				if (Arrays.binarySearch(m_frequentEdges, m_edge) < 0) {
					ok = false;
				}
			}
			
			// join case 3b
			if (ok) {
				Matrix newMatrix = new Matrix(matrixA, EmbeddingList.joinCase3BIntersection(matrixA.getEmbeddings(), matrixB
						.getEmbeddings()), 1);
				final int newNode = newMatrix.addNode(matrixB.getNodeLabel(matrixB.getNodeCount() - 1));
	
				newMatrix.addEdge(newNode, (nA > nB) ? nB : nA, matrixB.getEdgeLabel(lastEdgeB));
				newMatrices.add(newMatrix);
			}

			// join case 3a
			if ((lastEdgeA != lastEdgeB)
					&& (matrixA.getNodeLabel(matrixA.getNodeCount() - 1) == matrixB.getNodeLabel(matrixB.getNodeCount() - 1))) {
				if (matrixA.compareTo(matrixB) < 0) return 1;

				if (m_frequentEdges != null) {
					m_edge.nodeALabel = matrixB.getNodeLabel(nA);
					m_edge.nodeBLabel = matrixB.getNodeLabel(nB);
					m_edge.edgeLabel = matrixB.getEdgeLabel(lastEdgeB);

					if (Arrays.binarySearch(m_frequentEdges, m_edge) < 0) {
						return 1;
					}
				}				
				
				Matrix newMatrix = new Matrix(matrixA, EmbeddingList.intersect(matrixA.getEmbeddings(), matrixB.getEmbeddings()), 0);
				newMatrix.addEdge(nA, nB, matrixB.getEdgeLabel(lastEdgeB));
				newMatrices.add(newMatrix);

				return 2;
			} else {
				return 1;
			}
		}

		return 0;
	}


	/**
	 * Extends the given matrix in all possible ways and puts the resulting matrices into the given collection.
	 * 
	 * @param matrix the matrix
	 * @param newMatrices a collection into which the new matrices should be put
	 * @return the number of created matrices
	 */
	public int extend(Matrix matrix, Collection newMatrices) {
		int count = 0;
		if (!matrix.isInnerMatrix()) {
			final Map extensions = new HashMap();
			final EmbeddingList embeddings = matrix.getEmbeddings();

			for (int j = 0; j < embeddings.size(); j++) {
				final Embedding emb = embeddings.get(j);
				final int lastEmbeddingNode = emb.getNode();

				for (int i = emb.getSuperGraph().getDegree(lastEmbeddingNode) - 1; i >= 0; i--) {
					final int edge = emb.getSuperGraph().getNodeEdge(lastEmbeddingNode, i);
					final int otherNode = emb.getSuperGraph().getOtherNode(edge, lastEmbeddingNode);

					final int otherNodeLabel = emb.getSuperGraph().getNodeLabel(otherNode);
					
					// do a quick check for unfrequent node labels if an array with the frequent node labels has been given
					// in the constructor
					m_edge.nodeALabel = emb.getSuperGraph().getNodeLabel(lastEmbeddingNode);
					m_edge.nodeBLabel = otherNodeLabel;
					m_edge.edgeLabel = emb.getSuperGraph().getEdgeLabel(edge);

					if ((m_frequentEdges != null) && (Arrays.binarySearch(m_frequentEdges, m_edge) < 0)) {
						continue;
					}


					if (!emb.containsNode(otherNode)) { // make sure that no node is used twice in the embedding						
						Matrix newMatrix = (Matrix) extensions.get(m_edge);

						if (newMatrix == null) {
							newMatrix = new Matrix(matrix, new EmbeddingList(embeddings.size()), 1);
							newMatrix.addNodeAndEdge(matrix.getNode(matrix.getNodeCount() - 1), emb.getSuperGraph().getNodeLabel(
									otherNode), emb.getSuperGraph().getEdgeLabel(edge));
							newMatrices.add(newMatrix);
							extensions.put(new GraphEdge(m_edge), newMatrix);
							count++;
						}

						newMatrix.getEmbeddings().addEmbeddingSorted(new Embedding(emb, otherNode));
					}
				}
			}
		}

		return count;
	}
}