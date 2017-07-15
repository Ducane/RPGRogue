package de.ducane.roguelike.phantom;

import de.androbin.rpg.phantom.*;
import de.ducane.roguelike.dark.*;
import de.ducane.roguelike.screen.*;
import java.awt.*;
import java.awt.geom.*;

public abstract class RoguePhantom extends Phantom {
  private final MovingDark dark;
  
  protected RoguePhantom( final PhantomData data, final Point pos, final MovingDark dark ) {
    super( data, pos );
    this.dark = dark;
  }
  
  public abstract void onPlayerEntered( PlayScreen screen );
  
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
    RoguePhantom build( PhantomData data, Point pos, MovingDark dark );
  }
}