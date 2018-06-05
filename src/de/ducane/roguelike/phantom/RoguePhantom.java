package de.ducane.roguelike.phantom;

import de.androbin.rpg.entity.*;
import de.ducane.roguelike.*;

public abstract class RoguePhantom extends Entity {
  private final EntityData data;
  
  protected RoguePhantom( final EntityData data, final int id ) {
    super( id );
    this.data = data;
  }
  
  @ Override
  public EntityData getData() {
    return data;
  }
  
  public abstract void onPlayerEntered( RogueMaster master );
  
  public interface Builder {
    RoguePhantom build( EntityData data, int id );
  }
}