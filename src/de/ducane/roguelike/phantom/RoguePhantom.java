package de.ducane.roguelike.phantom;

import de.androbin.rpg.entity.*;
import de.ducane.roguelike.*;

public abstract class RoguePhantom extends Entity {
  protected RoguePhantom( final EntityData data, final int id ) {
    super( data, id );
  }
  
  public abstract void onPlayerEntered( RogueMaster master );
  
  public interface Builder {
    RoguePhantom build( EntityData data, int id );
  }
}