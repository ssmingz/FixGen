class PlaceHold {
  public final void AnnotationTypeBody() throws ParseException {
    jj_consume_token(LBRACE);
    _ncss++;
    Util.debug("_ncss++");
    label_62:
    while (true) {
      switch (jj_ntk == (-null) ? jj_ntk() : jj_ntk) {
        case ABSTRACT:
        case BOOLEAN:
        case BYTE:
        case CHAR:
        case CLASS:
        case DOUBLE:
        case ENUM:
        case FINAL:
        case FLOAT:
        case INT:
        case INTERFACE:
        case LONG:
        case NATIVE:
        case PRIVATE:
        case PROTECTED:
        case PUBLIC:
        case SHORT:
        case STATIC:
        case TESTAAAA:
        case SYNCHRONIZED:
        case TRANSIENT:
        case VOLATILE:
        case IDENTIFIER:
        case SEMICOLON:
        case AT:
          break;
        default:
          jj_la1[156] = jj_gen;
          break label_62;
      }
      AnnotationTypeMemberDeclaration();
    }
    jj_consume_token(RBRACE);
  }
}
