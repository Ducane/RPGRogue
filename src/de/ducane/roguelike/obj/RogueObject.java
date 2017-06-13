package de.ducane.roguelike.obj;

import de.androbin.rpg.obj.*;
import de.ducane.roguelike.*;
import de.ducane.roguelike.entity.*;
import de.ducane.roguelike.level.*;
import de.ducane.roguelike.screen.*;
import java.awt.*;
import java.awt.geom.*;

public abstract class RogueObject extends GameObject {
  private final PlayScreen screen;
  
  protected RogueObject( final PlayScreen screen, final GameObjectData data, final Point pos ) {
    super( data, pos );
    this.screen = screen;
  }
  
  public abstract void onPlayerEntered( Level level, Player player );
  
  @ Override
  public void render( final Graphics2D g, final float scale ) {
    final Blackout blackout = screen.getBlackout();
    final Point2D.Float c = screen.getBlackoutPos();
    
    final Point2D.Float center = new Point2D.Float(
        ( pos.x + 0.5f * data.size.width ) * scale, ( pos.y + 0.5f * data.size.height ) * scale );
    
    if ( !blackout.contains( c, center ) ) {
      return;
    }
    
    super.render( g, scale );
  }
  
  public interface Builder {
    RogueObject build( PlayScreen screen, GameObjectData data, final Point pos );
  }
}