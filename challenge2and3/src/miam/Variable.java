package miam;

class Variable {
  public String name;
  public Integer data;

  public Variable(String Name) {
    name = Name;
  }

  void checkInitialise() throws BareBonesException {
    if (data == null) {
      throw new BareBonesException("Variable " + name + " was used before it has been cleared.");
    }
  }
}
