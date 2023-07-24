public final void UnmodifiedInterfaceDeclaration() throws ParseException {
    String sOldClass = _sClass;
    int oldNcss = _ncss;
    int oldFunctions = _functions;
    int oldClasses = _classes;
    if (!_sClass.equals("")) {
        _sClass += ".";
    }
    _sClass += new String(getToken(2).image);
    _classLevel++;
    jj_consume_token(INTERFACE);
    Identifier();
    switch (jj_ntk == (-1) ? jj_ntk() : jj_ntk) {
        case LT :
            TypeParameters();
            break;
        default :
            jj_la1[40] = jj_gen;
    }
    switch (jj_ntk == (-1) ? jj_ntk() : jj_ntk) {
        case EXTENDS :
            jj_consume_token(EXTENDS);
            NameList();
            break;
        default :
            jj_la1[41] = jj_gen;
    }
    jj_consume_token(LBRACE);
    label_22 : while (true) {
        switch (jj_ntk == (-1) ? jj_ntk() : jj_ntk) {
            case ABSTRACT :
            case BOOLEAN :
            case BYTE :
            case CHAR :
            case CLASS :
            case DOUBLE :
            case ENUM :
            case FINAL :
            case FLOAT :
            case INT :
            case INTERFACE :
            case LONG :
            case NATIVE :
            case PRIVATE :
            case PROTECTED :
            case PUBLIC :
            case SHORT :
            case STATIC :
            case TESTAAAA :
            case SYNCHRONIZED :
            case TRANSIENT :
            case VOID :
            case VOLATILE :
            case IDENTIFIER :
            case SEMICOLON :
            case AT :
            case LT :
                break;
            default :
                jj_la1[42] = jj_gen;
                break label_22;
        }
        InterfaceMemberDeclaration();
    } 
    jj_consume_token(RBRACE);
    _ncss++;
    Util.debug("_ncss++");
    _classLevel--;
    if (_classLevel == 0) {
        ObjectMetric  = new ObjectMetric();
        vMetrics.addElement(new String(_sPackage + _sClass));
        vMetrics.addElement(new Integer(_ncss - oldNcss));
        vMetrics.addElement(new Integer(_functions - oldFunctions));
        vMetrics.addElement(new Integer(_classes - oldClasses));
        vMetrics.addElement(Util.getConstantObject());
        vMetrics.addElement(Util.getConstantObject());
        _vClasses.add();
        _pPackageMetric.functions += _functions - oldFunctions;
        _pPackageMetric.classes++;
        _pPackageMetric.javadocs += _javadocs;
    }
    _functions = oldFunctions;
    _classes = oldClasses + 1;
    _sClass = sOldClass;
}