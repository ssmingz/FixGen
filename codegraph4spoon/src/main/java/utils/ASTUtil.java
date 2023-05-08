package utils;

import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.CtElement;
import spoon.support.reflect.declaration.CtMethodImpl;

public class ASTUtil {
    public static String buildSignature(CtMethodImpl method) {
        StringBuilder sb = new StringBuilder();
        sb.append(method.getSimpleName() + "#?");
        for (int i = 0; i < method.getParameters().size(); i++) {
            CtExpression svd = (CtExpression) method.getParameters().get(i);
            sb.append("," + svd.getType().getSimpleName());
        }
        return sb.toString();
    }
}
