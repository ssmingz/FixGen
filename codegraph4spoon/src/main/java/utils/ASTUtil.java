package utils;

import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.CtElement;
import spoon.support.reflect.declaration.CtMethodImpl;
import spoon.support.reflect.declaration.CtParameterImpl;

public class ASTUtil {
    public static String buildSignature(CtMethodImpl method) {
        StringBuilder sb = new StringBuilder();
        sb.append(method.getSimpleName() + "#?");
        for (int i = 0; i < method.getParameters().size(); i++) {
            CtParameterImpl svd = (CtParameterImpl) method.getParameters().get(i);
            sb.append(",").append(svd.getType().getSimpleName());
        }
        return sb.toString();
    }
}
