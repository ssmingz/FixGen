/*
 * Created on 24.04.2005
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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 *
 */
public class DefaultGraphClassifier implements GraphClassifier {
	private final HashMap m_frequencies = new HashMap();
	
	/**
	 * Creates a new default graph classifier. The class information is read from the given file.
	 * 
	 * @param file the name of a file with class information 
	 * @throws IOException if the file cannot be read 
	 */
	public DefaultGraphClassifier(String file) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(file));
		
		String line;
		while ((line = in.readLine()) != null) {
			String[] temp = line.split(" => ");
			
			String[] freqs = temp[1].split(",");
			float[] frequencies = new float[freqs.length];
			for (int i = 0; i < freqs.length; i++) {
				frequencies[i] = Float.parseFloat(freqs[i]);
			}
			
			m_frequencies.put(temp[0], frequencies);
		}
	}

	/* (non-Javadoc)
	 * @see de.parmol.graph.GraphClassifier#getClassFrequencies(java.lang.String)
	 */
	public float[] getClassFrequencies(String graphID) {
		return (float[]) m_frequencies.get(graphID);
	}

}
