package de.ducane.roguelike.menu;

import de.androbin.thread.*;
import de.ducane.roguelike.entity.*;
import de.ducane.roguelike.item.*;
import java.awt.*;
import java.awt.geom.*;

public final class ItemSelectMenu extends Menu {
  public ItemSelectMenu( final InventoryMenu parent ) {
    super( parent );
  }
  
  @ Override
  protected String getLabel( final int index, final LockedList<Item> inventory ) {
    final int itemIndex = parent.getDataIndex();
    final Item item = inventory.tryGet( itemIndex );
    
    if ( item == null ) {
      return null;
    }
    
    switch ( index ) {
      case 0:
        return item instanceof Food ? "Eat" : "Equip";
      case 1:
        return "Throw";
      case 2:
        return "Description";
    }
    
    return null;
  }
  
  @ Override
  public Point2D.Float getOffset() {
    final int selection = parent.selection;
    return new Point2D.Float( 0f, ( selection > 3 ? -1.4f : 1f ) * bounds.height );
  }
  
  @ Override
  public void onResized( final int width, final int height ) {
    stroke = new BasicStroke( 0.005f * width );
    
    bounds = new Rectangle2D.Float( 0.55f * width, 0f, 0.2f * width, 0.25f * height );
    
    buttonBounds = new Rectangle2D.Float[ 3 ];
    
    final Rectangle2D.Float singleButton = new Rectangle2D.Float(
        0.575f * width, 0.025f * height, 0.15f * width, 0.05f * height );
    
    resizeButtons( singleButton, 0.075f * height );
  }
  
  @ Override
  public Menu runCommand( final Player player ) {
    final LockedList<Item> inventory = player.inventory;
    final int itemIndex = parent.getDataIndex();
    final Item item = inventory.get( itemIndex );
    
    switch ( selection ) {
      case 0:
        player.equip( itemIndex );
        return parent;
      case 1:
        inventory.remove( item );
        return parent;
      case 2:
        return new DescriptionMenu( this );
    }
    
    return this;
  }
}