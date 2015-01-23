package hw9;
import hw8.CampusParser;
import hw8.MainDriver;
import hw8.Pair;
import hw8.CampusParser.MalformedDataException;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;

/**
 * The main class of the path-finding program. Responsible for putting all of the pieces 
 * together, and running the program.
 * 
 * The user has two levels of zoom that are represented by a "+" and "-" button, and the user
 * can move the map by holding down a mouse button then dragging. 
 * 
 * The buildings are selected by scrolling through and selecting a starting building from the upper 
 * list and an ending building from the lower list. The user can then click on the "find path" button
 * to update the map with the results of the best possible path. The locations of the buildings are 
 * displayed on the map as soon as the user selects them.
 * 
 * This program was tested on a Windows 7 machine with the following specs:
 * 
 * AMD Phenom II X4 840T Processor 2.90 GHz
 * 8GB RAM
 * 64-bit Operating System 
 * AMD Radeon HD 6670 1GB video memory
 * 
 * Author: Jonathan Shustov
 */

public class CPM {

  public static void main(String[] args) {
      
    //Get building data, model, and more:
    ArrayList<Pair<String, String>> buildingNames = new ArrayList<Pair<String, String>>();
    Map<String, String> nameToLocation = new HashMap<String, String>();
    //Parse map data
    try {
        CampusParser.parseViewData("campus_buildings.dat", buildingNames, nameToLocation);
    } catch (MalformedDataException e) {
        System.err.println("Malformed data exception in main of Controller" + e);
    }  
    MainDriver driver = new MainDriver("campus_paths.dat");  
    
    //Create main frame and set termination conditions
    JFrame mainFrame = new JFrame("Campus Paths");
    mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    
    //Main map and shortest path view item. This will be referenced in another object to 
    //allow the user to click and drag to scroll.
    MapWindow m = new MapWindow(driver, nameToLocation);
    HoldToScrollListener interactableMap = new HoldToScrollListener(m);
    JScrollPane scroller = new JScrollPane(m);
    scroller.getViewport().addMouseMotionListener(interactableMap);
    scroller.getViewport().addMouseListener(interactableMap);
       
    //Set up the program's control buttons. These buttons require references to other parts
    //of the program.
    BuildingOptionList pathButtons = new BuildingOptionList(buildingNames, m, scroller);
    pathButtons.setPreferredSize(new Dimension(150,600)); 

    //Add buttons to frame   
    mainFrame.add(scroller, BorderLayout.CENTER);
    mainFrame.add(pathButtons, BorderLayout.EAST);
  
    //The minimum size is 1024 by 768, meeting the required specification.
    //The preferred size is larger 
    mainFrame.setMinimumSize(new Dimension(1024, 768));
    mainFrame.setPreferredSize(new Dimension(1400, 850));
    
    mainFrame.pack();
    mainFrame.setVisible(true);
  }
}