@Override
public Filter createFilter(FilterFactoryParams params) throws FilterNotCreatedException {
    throw new FilterNotCreatedException(new Exception("@version@"));
}