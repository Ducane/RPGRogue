package de.ducane.roguelike.menu;

import static de.androbin.gfx.util.GraphicsUtil.*;
import de.androbin.thread.*;
import de.ducane.roguelike.entity.*;
import de.ducane.roguelike.item.*;
import java.awt.*;
import java.awt.geom.*;

public final class InventoryMenu extends Menu {
  private int pageIndex;
  
  private Rectangle2D.Float statBounds;
  private Rectangle2D.Float statImageBounds;
  
  private Rectangle2D.Float pageCursorRight;
  private Rectangle2D.Float pageCursorLeft;
  
  public InventoryMenu( final MainMenu parent ) {
    super( parent );
  }
  
  @ Override
  protected int getDataIndex() {
    return selection + pageIndex * 8;
  }
  
  @ Override
  protected String getLabel( final int index, final LockedList<Item> inventory ) {
    final int itemIndex = index + pageIndex * 8;
    
    final Item item = inventory.tryGet( itemIndex );
    
    if ( item == null ) {
      return "----------";
    }
    
    final String name = item.name;
    return name.length() > 10 ? name.substring( 0, 10 ) : name;
  }
  
  @ Override
  public Point2D.Float getOffset() {
    return new Point2D.Float( 0f, buttonBounds[ selection ].y );
  }
  
  @ Override
  public Menu onClick( final Point2D.Float p, final Player player ) {
    if ( pageCursorLeft.contains( p ) ) {
      pageIndex = Math.max( 0, pageIndex - 1 );
      return this;
    } else if ( pageCursorRight.contains( p ) ) {
      final LockedList<Item> inventory = player.inventory;
      pageIndex = Math.min( pageIndex + 1, ( inventory.size() - 1 ) / 8 );
      return this;
    }
    
    return super.onClick( p, player );
  }
  
  @ Override
  public void onResized( final int width, final int height ) {
    stroke = new BasicStroke( 0.005f * width );
    
    bounds = new Rectangle2D.Float( 0.25f * width, 0f, 0.25f * width, 0.7f * height );
    
    buttonBounds = new Rectangle2D.Float[ 8 ];
    
    final Rectangle2D.Float singleButton = new Rectangle2D.Float(
        0.3f * width, 0.01f * height, 0.15f * width, 0.075f * height );
    
    resizeButtons( singleButton, 0.075f * height );
    
    statBounds = new Rectangle2D.Float( 0f, 0f, 0.2f * width, 0.25f * height );
    statImageBounds = new Rectangle2D.Float(
        0.055f * width, 0.045f * height, 0.09f * width, 0.16f * height );
    
    pageCursorLeft = new Rectangle2D.Float(
        bounds.x + ( 0.1f * bounds.width ),
        bounds.y + ( 0.95f * bounds.height ), 10f, 10f );
    pageCursorRight = new Rectangle2D.Float(
        bounds.x + ( 0.9f * bounds.width ),
        bounds.y + ( 0.95f * bounds.height ), 10f, 10f );
  }
  
  @ Override
  public void render( final Graphics2D g, final Player player ) {
    super.render( g, player );
    
    final LockedList<Item> inventory = player.inventory;
    
    final FontMetrics fm = g.getFontMetrics();
    
    final String fold = ( pageIndex + 1 ) + "/" + ( ( inventory.size() - 1 ) / 8 + 1 );
    g.setColor( Color.WHITE );
    g.drawString( fold,
        bounds.x + ( bounds.width - fm.stringWidth( fold ) ) * 0.5f,
        bounds.y + 0.95f * bounds.height );
    fillRect( g, pageCursorLeft );
    fillRect( g, pageCursorRight );
    
    final Item item = inventory.tryGet( getDataIndex() );
    
    if ( item != null ) {
      g.setColor( Color.BLACK );
      fillRect( g, statBounds );
      g.setStroke( stroke );
      g.setColor( Color.WHITE );
      drawRect( g, statBounds.x, bounds.y, statBounds.width, statBounds.height );
      drawImage( g, item.image, statImageBounds );
    }
  }
  
  @ Override
  protected Menu runCommand( final Player player ) {
    final LockedList<Item> inventory = player.inventory;
    
    if ( getDataIndex() < inventory.size() ) {
      return new ItemSelectMenu( this );
    }
    
    return this;
  }
}