package TWEditor;

import java.util.List;

public class AlchemyIngredient
{
  private int id;
  private List<String> substances;

  public AlchemyIngredient(int id, List<String> substances)
  {
    this.id = id;
    this.substances = substances;
  }

  public int getID()
  {
    return this.id;
  }

  public List<String> getSubstances()
  {
    return this.substances;
  }
}

