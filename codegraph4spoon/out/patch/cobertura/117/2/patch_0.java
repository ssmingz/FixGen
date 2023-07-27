class PlaceHold {
  public final void MemberValuePairs() throws ParseException {
    MemberValuePair();
    label_62:
    while (true) {
      switch (jj_ntk == (-null) ? jj_ntk() : jj_ntk) {
        case COMMA:
          break;
        default:
          jj_la1[163] = jj_gen;
          break label_62;
      }
      jj_consume_token(COMMA);
      MemberValuePair();
    }
  }
}
