package utils;

import spoon.reflect.declaration.CtField;
import spoon.support.reflect.code.*;
import spoon.support.reflect.reference.CtCatchVariableReferenceImpl;
import spoon.support.reflect.reference.CtFieldReferenceImpl;
import spoon.support.reflect.reference.CtLocalVariableReferenceImpl;
import spoon.support.reflect.reference.CtParameterReferenceImpl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SyntaxUtil {
    // See isKeyword for more information on keywords
    private static final Collection<String> baseKeywords = fillWithBaseKeywords();
    private static final Collection<String> java2Keywords = Collections.singleton("strictfp");
    private static final Collection<String> java4Keywords = Collections.singleton("assert");
    private static final Collection<String> java5Keywords = Collections.singleton("enum");
    private static final Collection<String> java9Keywords = Collections.singleton("_");

    private static final Collection<Class> variableTypes = fillWithVarTypes();

    /*
     * This method validates the simplename.
     * spoon needs to allow more names that are allowed by the JLS, as
     * - array references have a name, e.g. int[], where [] would not be allowed normally
     * - ? is used as name for intersection types
     * - <init>, <clinit>, <nulltype> are used to represent initializers and the null reference
     * - anonymous/local classes start with numbers in spoon
     * - simple names of packages are just their names, but they may contain '.'
     * - the name can contain generics, e.g. List<String>[]
     */
    public static boolean checkIdentifierForJLSCorrectness(String simplename, int complianceLevel) {
        if (isSpecialType(simplename)) {
            return true;
        }
        return checkAll(simplename, complianceLevel);
        //JLSViolation.throwIfSyntaxErrorsAreNotIgnored(this, "Not allowed javaletter or keyword in identifier found. See JLS for correct identifier. Identifier: " + simplename);
    }

    /*
     * returns true if is a variable type.
     */
    public static boolean isVariableType(Object ctType) {
        return variableTypes.contains(ctType);
    }

    public static boolean isSpecialType(String identifier) {
        return identifier.isEmpty()
                || "?".equals(identifier) // is wildcard, used for intersection types
                || (identifier.startsWith("<") && identifier.endsWith(">"));
    }

    /*
     * returns true if the name is valid.
     */
    public static boolean checkAll(String name, int complianceLevel) {
        int i = 0;
        // leading digits come from anonymous/local classes. Skip them
        while (i < name.length() && Character.isDigit(name.charAt(i))) {
            i++;
        }
        int start = i; // used to mark the beginning of a part
        final char anything = 0;
        char expectNext = anything;
        for (; i < name.length(); i++) {
            if (expectNext != anything) {
                if (name.charAt(i) != expectNext) {
                    return false;
                } else if (name.charAt(i) == expectNext) {
                    expectNext = anything; // reset
                    continue; // skip it, no further checks required
                }
            }
            switch (name.charAt(i)) {
                case '.':
                case '<':
                case '>':
                    // we scanned a word of valid java identifiers (see default case) until one
                    // of the special delimiting chars that are allowed in spoon
                    // now we just need to make sure it is not a keyword
                    if (isKeyword(name.substring(start, i), complianceLevel)) {
                        return false; // keyword -> not allowed
                    }
                    start = i + 1; // skip this special char
                    break;
                case '[':
                    expectNext = ']'; // next char *must* close
                    break;
                default: // if we come across an illegal java identifier char here, it's not valid at all
                    if (start == i && !Character.isJavaIdentifierStart(name.charAt(i))
                            || !Character.isJavaIdentifierPart(name.charAt(i))) {
                        return false;
                    }
                    break;
            }
        }
        // make sure the end state is correct too
        if (expectNext != anything) {
            return false; // expected something that didn't appear anymore
        }
        // e.g. a name that only contains valid java identifiers will end up here (start will never be updated)
        // and we still need to make sure it is not a keyword.
        // as updating start uses i + 1, it might be out of bounds, so avoid SIOOBEs here
        if (start < name.length()) {
            return !isKeyword(name.substring(start), complianceLevel);
        }
        return true;
    }

    /**
     * Keywords list and history selected according to:
     * https://docs.oracle.com/en/java/javase/15/docs/specs/sealed-classes-jls.html#jls-3.9
     * https://en.wikipedia.org/wiki/List_of_Java_keywords (contains history of revisions)
     * and https://docs.oracle.com/javase/tutorial/java/nutsandbolts/_keywords.html (history up to java 8)
     *
     * @param simplename
     * @return true if simplename is a keyword in the current setting (compliance level), false if not
     */
    public static boolean isKeyword(String simplename, int complianceLevel) {
        return (baseKeywords.contains(simplename)
                || (complianceLevel >= 2 && java2Keywords.contains(simplename))
                || (complianceLevel >= 4 && java4Keywords.contains(simplename))
                || (complianceLevel >= 5 && java5Keywords.contains(simplename))
                || (complianceLevel >= 9 && java9Keywords.contains(simplename)));
    }

    private static Collection<String> fillWithBaseKeywords() {
        // removed types because needed as ref: "int","short", "char", "void", "byte","float", "true","false","boolean","double","long","class", "null"
        // in the method isKeyword, more keywords are added to the checks based on the compliance level
        return Stream.of("abstract", "continue", "for", "new", "switch", "default", "if", "package", "synchronized",  "do", "goto", "private",
                        "this", "break",  "implements", "protected", "throw", "else", "import", "public", "throws", "case", "instanceof", "return",
                        "transient", "catch", "extends", "try", "final", "interface", "static", "finally", "volatile",
                        "const",  "native", "super", "while")
                .collect(Collectors.toCollection(HashSet::new));
    }

    private static Collection<Class> fillWithVarTypes() {
        return Stream.of(
                        CtFieldReadImpl.class, CtFieldWriteImpl.class, CtArrayReadImpl.class, CtArrayWriteImpl.class,
                        CtVariableReadImpl.class, CtVariableWriteImpl.class,
                        CtThisAccessImpl.class,
                        CtCatchVariableReferenceImpl.class, CtFieldReferenceImpl.class, CtLocalVariableReferenceImpl.class, CtParameterReferenceImpl.class)
                .collect(Collectors.toCollection(HashSet::new));
    }
}
