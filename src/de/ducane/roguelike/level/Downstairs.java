package de.ducane.roguelike.level;

import static de.androbin.gfx.util.GraphicsUtil.*;
import de.androbin.rpg.tile.*;
import de.ducane.roguelike.dark.*;
import java.awt.*;

public final class Downstairs extends RogueTile {
  public Downstairs( final TileData data, final MovingDark dark ) {
    super( data, dark );
  }
  
  @ Override
  public void render( final Graphics2D g, final Point pos, final float scale ) {
    final float px = pos.x * scale;
    final float py = pos.y * scale;
    
    drawImage( g, Tiles.getData( "granite" ).image, px, py, scale, scale );
    
    super.render( dark.clip( g ), pos, scale );
  }
}