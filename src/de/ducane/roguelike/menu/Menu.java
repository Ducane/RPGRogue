package de.ducane.roguelike.menu;

import static de.androbin.gfx.util.GraphicsUtil.*;
import de.androbin.thread.*;
import de.ducane.roguelike.entity.*;
import de.ducane.roguelike.item.*;
import java.awt.*;
import java.awt.geom.*;

public abstract class Menu {
  public final Menu parent;
  
  protected int selection;
  protected Rectangle2D.Float[] buttonBounds;
  
  protected BasicStroke stroke;
  protected Rectangle2D.Float bounds;
  
  public Menu( final Menu parent ) {
    this.parent = parent;
  }
  
  protected int getDataIndex() {
    return -1;
  }
  
  protected abstract String getLabel( int index, LockedList<Item> inventory );
  
  public Point2D.Float getOffset() {
    return new Point2D.Float();
  }
  
  public Menu onClick( final Point2D.Float p, final Player player ) {
    for ( final Rectangle2D.Float bounds : buttonBounds ) {
      if ( bounds.contains( p ) ) {
        return runCommand( player );
      }
    }
    
    return this;
  }
  
  public final void onHover( final Point2D.Float p ) {
    for ( int i = 0; i < buttonBounds.length; i++ ) {
      if ( buttonBounds[ i ].contains( p ) ) {
        selection = i;
      }
    }
  }
  
  public abstract void onResized( int width, int height );
  
  public void render( final Graphics2D g, final Player player ) {
    g.setColor( Color.BLACK );
    fillRect( g, bounds );
    g.setStroke( stroke );
    g.setColor( Color.WHITE );
    drawRect( g, bounds );
    
    final LockedList<Item> inventory = player.inventory;
    
    final FontMetrics fm = g.getFontMetrics();
    
    for ( int i = 0; i < buttonBounds.length; i++ ) {
      final String label = getLabel( i, inventory );
      
      if ( label == null ) {
        continue;
      }
      
      final Rectangle2D.Float rect = buttonBounds[ i ];
      g.setColor( i == selection ? Color.YELLOW : Color.WHITE );
      g.drawString( label,
          rect.x + ( rect.width - fm.stringWidth( label ) ) * 0.5f,
          rect.y + ( rect.height - fm.getHeight() ) * 0.5f + fm.getAscent() );
    }
  }
  
  protected final void resizeButtons( final Rectangle2D.Float single, final float dy ) {
    for ( int i = 0; i < buttonBounds.length; i++ ) {
      buttonBounds[ i ] = new Rectangle2D.Float(
          single.x, single.y + dy * i, single.width, single.height );
    }
  }
  
  protected Menu runCommand( final Player player ) {
    return this;
  }
}