class PlaceHold{
@Override
protected Checker createChecker(Configuration aFilterConfig) throws CheckstyleException {
    final DefaultConfiguration checkerConfig = new DefaultConfiguration("configuration");
    final DefaultConfiguration checksConfig = createCheckConfig(TreeWalker.class);
    checksConfig.addChild(createCheckConfig(FileContentsHolder.class));
    checksConfig.addChild(createCheckConfig(MemberNameCheck.class));
    checksConfig.addChild(createCheckConfig(ConstantNameCheck.class));
    checksConfig.addChild(createCheckConfig(IllegalCatchCheck.class));
    checkerConfig.addChild(checksConfig);
    if (aFilterConfig != null) {
        checkerConfig.addChild(aFilterConfig);
    }
    final Checker checker = new Checker();
    final Locale locale = Locale.ENGLISH;
    checker.setLocaleCountry(locale.getCountry());
    checker.setLocaleLanguage(locale.getLanguage());
    checker.configure(checkerConfig);
    checker.addListener(new BriefLogger(mStream));
    .setModuleClassLoader(Thread.currentThread().getContextClassLoader());
    return checker;
}
}