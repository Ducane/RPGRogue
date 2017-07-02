package de.ducane.roguelike.menu;

import de.androbin.thread.*;
import de.ducane.roguelike.entity.*;
import de.ducane.roguelike.item.*;
import java.awt.*;
import java.awt.geom.*;

public final class MainMenu extends Menu {
  private final String[] labels = { "Inventory", "Character", "Exit" };
  
  public MainMenu() {
    super( null );
  }
  
  @ Override
  protected String getLabel( final int index, final LockedList<Item> inventory ) {
    return labels[ index ];
  }
  
  @ Override
  public void onResized( final int width, final int height ) {
    stroke = new BasicStroke( 0.005f * width );
    
    bounds = new Rectangle2D.Float( 0f, 0.3f * height, 0.2f * width, 0.4f * height );
    
    buttonBounds = new Rectangle2D.Float[ labels.length ];
    
    final Rectangle2D.Float singleButton = new Rectangle2D.Float(
        0.025f * width, 0.35f * height, 0.15f * width, 0.05f * height );
    
    resizeButtons( singleButton, 0.075f * height );
  }
  
  @ Override
  public Menu runCommand( final Player player ) {
    switch ( selection ) {
      case 0:
        return new InventoryMenu( this );
      case 1:
        return new StatsMenu( this );
      case 2:
        return parent;
    }
    
    return this;
  }
}