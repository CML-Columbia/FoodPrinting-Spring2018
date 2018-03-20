/* Creative Machines Lab|FoodPrinting.Software Spring 2018
 * Editors: Sarah Yuan + Tutch Winyarat
 * Unmodfied legacy code/
 * FoodFilter.java class "filters" .food file
 * A .food file is a text file containing configurations 
 * that can be loaded onto the GUI.
 * An instance of the class is created in PrintOptionWindow.actionPerformed()
 */
 
 /// hi hi gitHub tests

//hello world
import java.io.File;
import javax.swing.filechooser.FileFilter;

public class FoodFilter extends FileFilter
{
  public FoodFilter() {}
  
  public boolean accept(File f)
  {
    if (f.getName().endsWith(".food")) return true;
    return false;
  }
  
  public String getDescription()
  {
    return "Food parameter files (.food)";
  }
}
