public class Label {
  private String name;
  private Integer address;
  private boolean defined;

  public Label(String name) {
      this.name = name;
      this.address = null;
      this.defined = false;
  }

  public String getName() {
      return name;
  }

  public void setAddress(int address) {
      this.address = address;
      this.defined = true;
  }

  public Integer getAddress() {
      return address;
  }

  public boolean isDefined() {
      return defined;
  }

  @Override
  public String toString() {
      return "Label{" + "name='" + name + '\'' + ", address=" + address + ", defined=" + defined + '}';
  }
}
