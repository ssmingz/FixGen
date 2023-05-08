class PlaceHold {
  protected VAJUtil getUtil() {
    if (util == null) {
      if (remoteServer == null) {
        util = new VAJLocalToolUtil();
      } else {
        util = new VAJRemoteUtil(this, remoteServer);
      }
    }
    return util;
  }
}
