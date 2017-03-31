package ui;

public class Location implements Comparable<Location>{

  public int row;
  public int col;
  
  public Location(int row, int col)
  {
    this.row = row;
    this.col = col;
  }

  @Override
  public int compareTo(Location location) {
    
    if (this.col < location.col)
    {
      return -1;
    }
    else if (this.col > location.col)
    {
      return 1;
    }
    else
    {
      if (this.row < location.row)
      {
        return -1;
      }
      else if (this.row > location.row)
      {
        return 1;
      }
      else return 0;
    }
  }
  
}
