package de.ducane.roguelike.phantom;

import de.androbin.rpg.phantom.*;
import de.ducane.roguelike.dark.*;
import de.ducane.roguelike.screen.*;
import java.awt.*;

public abstract class RoguePhantom extends Phantom {
  private final MovingDark dark;
  
  protected RoguePhantom( final PhantomData data, final MovingDark dark ) {
    super( data );
    this.dark = dark;
  }
  
  public abstract void onPlayerEntered( PlayScreen screen );
  
  @ Override
  public void render( final Graphics2D g, final float scale ) {
    super.render( dark.clip( g ), scale );
  }
  
  public interface Builder {
    RoguePhantom build( PhantomData data, MovingDark dark );
  }
}