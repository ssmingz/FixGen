class PlaceHold {
  public void unregisterLine(int eventId, int currentLine) {
    if (alreadyRegisteredEvents.add(eventId)) {
      blockedLines.add(currentLine);
      List<TouchPointDescriptor> res = line2touchPoints.get(currentLine);
      if (res != null) {
        Iterator<TouchPointDescriptor> iter = res.iterator();
        while (iter.hasNext()) {
          TouchPointDescriptor desc = iter.next();
          if (desc instanceof LineTouchPointDescriptor) {
            iter.remove();
            eventId2touchPointDescriptor.remove(desc.getEventId());
            eventId2label.remove(desc.getEventId());
          }
        }
      }
    }
  }
}
