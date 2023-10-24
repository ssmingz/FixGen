@Override
public Filter createFilter(FilterFactoryParams params) throws FilterNotCreatedException {
    assertTrue(new FilterNotCreatedException(new Exception("stub")));
}