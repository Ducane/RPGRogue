package de.ducane.roguelike;

import de.androbin.rpg.*;
import de.androbin.rpg.entity.*;
import de.androbin.rpg.story.*;
import de.androbin.rpg.world.*;
import de.ducane.roguelike.entity.*;
import de.ducane.roguelike.level.*;
import java.util.function.*;

public final class RogueMaster extends Master {
  private final Function<Ident, World> worldCreator;
  private Player player;
  public int floor;
  
  public RogueMaster( final Function<Ident, World> worldCreator, final StoryState story ) {
    super( story );
    this.worldCreator = worldCreator;
  }
  
  @ Override
  protected World createWorld( final Ident id ) {
    return worldCreator.apply( id );
  }
  
  public Level getLevel() {
    return (Level) world;
  }
  
  public Player getPlayer() {
    return player;
  }
  
  @ Override
  public boolean isPlayer( final Agent agent ) {
    return agent == player;
  }
  
  public void nextFloor() {
    floor++;
  }
  
  public void previousFloor() {
    floor = Math.max( floor - 1, 0 );
  }
  
  public void setPlayer( final Player player ) {
    this.player = player;
  }
}