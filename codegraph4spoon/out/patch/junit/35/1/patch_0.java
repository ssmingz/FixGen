public Assignments assignNext(PotentialAssignment source) {
    List<PotentialAssignment> assigned = new ArrayList<PotentialAssignment>(fAssigned);
    assigned.add(source);
    return new Assignments(fContext, assigned, fUnassigned.subList(1, fUnassigned.size()), );
}