class PlaceHold{
public Assignments assignNext(PotentialAssignment source) {
    List<PotentialAssignment> assigned = new ArrayList<PotentialAssignment>(fAssigned);
    assigned.add(source);
    return new Assignments(assigned, fUnassigned.subList(1, fUnassigned.size()), fClass, fConstructorParameterCount, );
}
}