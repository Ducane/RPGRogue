package de.ducane.roguelike.obj;

import de.androbin.rpg.obj.*;
import de.ducane.roguelike.dark.*;
import de.ducane.roguelike.entity.*;
import de.ducane.roguelike.level.*;
import java.awt.*;
import java.awt.geom.*;

public abstract class RogueObject extends GameObject {
  private final MovingDark dark;
  
  protected RogueObject( final GameObjectData data, final Point pos, final MovingDark dark ) {
    super( data, pos );
    this.dark = dark;
  }
  
  public abstract void onPlayerEntered( Level level, Player player );
  
  @ Override
  public void render( final Graphics2D g, final float scale ) {
    final Point2D.Float center = new Point2D.Float(
        pos.x + data.size.width * 0.5f,
        pos.y + data.size.height * 0.5f );
    
    if ( !dark.contains( center ) ) {
      return;
    }
    
    super.render( g, scale );
  }
  
  public interface Builder {
    RogueObject build( GameObjectData data, Point pos, MovingDark dark );
  }
}