package de.ducane.roguelike.menu;

import static de.ducane.util.AWTUtil.*;
import de.androbin.thread.*;
import de.ducane.roguelike.entity.*;
import de.ducane.roguelike.item.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.List;

public final class DescriptionMenu extends Menu {
  public DescriptionMenu( final ItemSelectMenu parent ) {
    super( parent );
  }
  
  @ Override
  protected String getLabel( final int index, final LockedList<Item> inventory ) {
    return null;
  }
  
  @ Override
  public void onResized( final int width, final int height ) {
    stroke = new BasicStroke( 0.005f * width );
    
    bounds = new Rectangle2D.Float(
        0.55f * width, 0.05f * height, 0.35f * width, 0.25f * height );
    
    buttonBounds = new Rectangle2D.Float[ 0 ];
  }
  
  @ Override
  public void render( final Graphics2D g, final Player player ) {
    super.render( g, player );
    
    final LockedList<Item> inventory = player.inventory;
    final Item item = inventory.get( parent.selection );
    
    final FontMetrics fm = g.getFontMetrics();
    
    g.setColor( Color.RED );
    g.drawString( item.name,
        bounds.x + bounds.width * 0.05f,
        bounds.y + bounds.height * 0.075f + fm.getAscent() );
    
    final List<String> lines = wrapLines( item.description, fm,
        (int) ( bounds.width - stroke.getLineWidth() ) );
    
    g.setColor( Color.WHITE );
    
    for ( int i = 0; i < lines.size(); i++ ) {
      g.drawString( lines.get( i ),
          bounds.x + bounds.width * 0.05f,
          bounds.y + bounds.height * 0.45f + fm.getAscent() * i );
    }
  }
}