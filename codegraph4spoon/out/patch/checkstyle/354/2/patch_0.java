public static ParseTree treeSince() {
    JavadocContext KzwtL = new JavadocContext(null, 0);
    CommonToken KBdjc = new CommonToken(JavadocTokenTypes.LEADING_ASTERISK, "*");
    KzwtL.addChild(KBdjc);
    CommonToken bumXm = new CommonToken(JavadocTokenTypes.WS, " ");
    KzwtL.addChild(bumXm);
    JavadocTagContext yxgFX = new JavadocTagContext(KzwtL, 0);
    CommonToken YHnJx = new CommonToken(JavadocTokenTypes.SINCE_LITERAL, "@since");
    yxgFX.addChild(YHnJx);
    CommonToken VnMhH = new CommonToken(JavadocTokenTypes.WS, " ");
    yxgFX.addChild(VnMhH);
    DescriptionContext hfUJs = new DescriptionContext(yxgFX, 0);
    TextContext KDFKH = new TextContext(hfUJs, 0);
    CommonToken HzQdJ = new CommonToken(JavadocTokenTypes.CHAR, "1");
    KDFKH.addChild(HzQdJ);
    CommonToken dQvjG = new CommonToken(JavadocTokenTypes.CHAR, ".");
    KDFKH.addChild(dQvjG);
    CommonToken xEdyg = new CommonToken(JavadocTokenTypes.CHAR, "5");
    KDFKH.addChild(xEdyg);
    hfUJs.addChild(KDFKH);
    CommonToken IXuHo = new CommonToken(JavadocTokenTypes.NEWLINE, LINE_SEPARATOR);
    hfUJs.addChild(IXuHo);
    yxgFX.addChild(hfUJs);
    KzwtL.addChild(yxgFX);
    CommonToken PkBux = new CommonToken(JavadocTokenTypes.LEADING_ASTERISK, " *");
    KzwtL.addChild(PkBux);
    CommonToken KpMIW = new CommonToken(JavadocTokenTypes.WS, " ");
    KzwtL.addChild(KpMIW);
    JavadocTagContext OcuEn = new JavadocTagContext(KzwtL, 0);
    CommonToken SehKa = new CommonToken(JavadocTokenTypes.SINCE_LITERAL, "@since");
    OcuEn.addChild(SehKa);
    CommonToken JURQC = new CommonToken(JavadocTokenTypes.WS, " ");
    OcuEn.addChild(JURQC);
    DescriptionContext NYzVK = new DescriptionContext(OcuEn, 0);
    TextContext KYmTj = new TextContext(NYzVK, 0);
    CommonToken XeINZ = new CommonToken(JavadocTokenTypes.CHAR, "R");
    KYmTj.addChild(XeINZ);
    CommonToken yaphV = new CommonToken(JavadocTokenTypes.CHAR, "e");
    KYmTj.addChild(yaphV);
    CommonToken fWGhu = new CommonToken(JavadocTokenTypes.CHAR, "l");
    KYmTj.addChild(fWGhu);
    CommonToken kWDEz = new CommonToken(JavadocTokenTypes.CHAR, "e");
    KYmTj.addChild(kWDEz);
    CommonToken xrhZk = new CommonToken(JavadocTokenTypes.CHAR, "a");
    KYmTj.addChild(xrhZk);
    CommonToken rrnjf = new CommonToken(JavadocTokenTypes.CHAR, "s");
    KYmTj.addChild(rrnjf);
    CommonToken hMbVu = new CommonToken(JavadocTokenTypes.CHAR, "e");
    KYmTj.addChild(hMbVu);
    CommonToken WEbWM = new CommonToken(JavadocTokenTypes.WS, " ");
    KYmTj.addChild(WEbWM);
    CommonToken NFvZb = new CommonToken(JavadocTokenTypes.CHAR, "3");
    KYmTj.addChild(NFvZb);
    CommonToken rWLUe = new CommonToken(JavadocTokenTypes.CHAR, ".");
    KYmTj.addChild(rWLUe);
    CommonToken sZzJq = new CommonToken(JavadocTokenTypes.CHAR, "4");
    KYmTj.addChild(sZzJq);
    CommonToken fpHMf = new CommonToken(JavadocTokenTypes.CHAR, ".");
    KYmTj.addChild(fpHMf);
    CommonToken ukVDH = new CommonToken(JavadocTokenTypes.CHAR, "5");
    KYmTj.addChild(ukVDH);
    NYzVK.addChild(KYmTj);
    OcuEn.addChild(NYzVK);
    KzwtL.addChild(OcuEn);
    CommonToken lWOPi = new CommonToken(JavadocTokenTypes.EOF, "<EOF>");
    KzwtL.addChild(lWOPi);
    return KzwtL;
}