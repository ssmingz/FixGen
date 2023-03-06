/*
 * Created on Feb 15, 2005
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
import java.io.*;

/**
 * This class is for managing debug messages.
 *
 * @author Marc Woerlein <Marc.Woerlein@informatik.uni-erlangen.de>
 */
public class Debug{
	/** the curretn debug level */
    public static int dlevel;
    /** the print stream used for debug (default: System.out) */ 
    public static PrintStream out=System.out;
    
    /**
     * prints the given text to the debug stream
     * @param text
     */
    public final static void print(String text){ print(0,text); }
    /**
     * prints the given text with a leading line break to the debug stream
     * @param text
     */
    public final static void println(String text){ println(0,text); }
    
    /**
     * prints the given text to the debug stream, 
     * if the given level isn't greater than the current debug level 
     * @param level
     * @param text
     */
    public final static void print(int level,String text){
        if (level<=dlevel) out.print(text);
    }
    /**
     * prints the given text with a leading line break to the debug stream, 
     * if the given level isn't greater than the current debug level 
     * @param level
     * @param text
     */
    public final static void println(int level,String text){
        if (level<=dlevel) out.println(text);
    }

}
