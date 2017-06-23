package de.ducane.roguelike.level;

import static de.androbin.gfx.util.GraphicsUtil.*;
import de.ducane.roguelike.dark.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;

public final class MiniMap {
  private final Level level;
  
  private final List<Point> visited = new ArrayList<>();
  
  public MiniMap( final Level level ) {
    this.level = level;
  }
  
  public void render( final Graphics2D g, final Point2D.Float playerPos,
      final float scale, final int width ) {
    final float size = scale * 0.2f;
    
    for ( final Point pos : visited ) {
      final RogueTile tile = level.getTile( pos );
      
      Color color;
      
      switch ( tile.data.name ) {
        case "granite":
          color = new Color( 0.8f, 0.8f, 0.8f, 0.7f );
          break;
        
        case "floor":
        case "door":
          color = new Color( 0.2f, 0.8f, 1f, 0.7f );
          break;
        
        default:
          color = new Color( 0f, 0f, 0f, 0f );
          break;
      }
      
      if ( pos.equals( level.getUpStairsPos() ) ) {
        color = new Color( 0.3f, 0.3f, 0.3f, 0.7f );
      } else if ( pos.equals( level.getDownStairsPos() ) ) {
        color = new Color( 0.15f, 0.15f, 0.15f, 0.7f );
      }
      
      if ( tile.getItem() != null ) {
        color = new Color( 1f, 0.25f, 0.25f, 0.7f );
      }
      
      final float x = width + ( pos.x - level.size.width ) * size;
      final float y = pos.y * size;
      
      g.setColor( color );
      fillRect( g, x, y, size, size );
    }
    
    g.setColor( Color.GREEN );
    fillRect( g, width + ( playerPos.x - level.size.width ) * size,
        playerPos.y * size, size, size );
  }
  
  public void update( final MovingDark dark, final float scale ) {
    for ( int y = 0; y < level.size.height; y++ ) {
      for ( int x = 0; x < level.size.width; x++ ) {
        final Point p = new Point( x, y );
        final Point2D.Float center = new Point2D.Float( x + 0.5f, y + 0.5f );
        
        if ( !visited.contains( p ) && dark.contains( center ) ) {
          visited.add( p );
        }
      }
    }
  }
}