package de.ducane.roguelike.level;

import static de.androbin.gfx.util.GraphicsUtil.*;
import de.androbin.rpg.event.*;
import de.androbin.rpg.tile.*;
import de.ducane.roguelike.dark.*;
import java.awt.*;
import java.awt.geom.*;

public final class Downstairs extends RogueTile {
  public Downstairs( final TileData data, final MovingDark dark ) {
    super( data, dark );
    setEvent( Events.parse( "nextFloor" ) );
  }
  
  @ Override
  public void render( final Graphics2D g, final Point pos, final float scale ) {
    final Point2D.Float center = new Point2D.Float( pos.x, pos.y );
    
    if ( dark.contains( center ) ) {
      super.render( g, pos, scale );
    } else {
      final float px = pos.x * scale;
      final float py = pos.y * scale;
      
      drawImage( g, Tiles.getData( "granite" ).image, px, py, scale, scale );
    }
  }
}