/*
 * Created on 18.12.2004
 *
 */
package de.parmol.parsers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import junit.framework.TestCase;
import de.parmol.parsers.antlr.DotLexer;
import de.parmol.parsers.antlr.DotParser;

/**
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 *
 */
public class DotParserTest extends TestCase {
	public DotParserTest() { super();	}

	public DotParserTest(String name) { super(name); }


	public static void main(String[] args) throws RecognitionException, TokenStreamException, FileNotFoundException {
    FileInputStream in = new FileInputStream("data/hello-diet.odd.dot");		
		DotLexer lexer = new DotLexer(in);
    DotParser parser = new DotParser(lexer);
    parser.graph();
	}
}
