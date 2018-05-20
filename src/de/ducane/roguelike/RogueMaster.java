package de.ducane.roguelike;

import de.androbin.rpg.*;
import de.androbin.rpg.story.*;
import de.androbin.rpg.world.*;
import de.ducane.roguelike.entity.*;
import de.ducane.roguelike.level.*;
import java.util.function.*;

public final class RogueMaster extends Master {
  public int floor;
  
  public RogueMaster( final Function<Ident, World> worldCreator, final StoryState story ) {
    super( worldCreator, story );
  }
  
  public Level getLevel() {
    return (Level) world;
  }
  
  public Player getPlayer() {
    return (Player) player;
  }
  
  public void nextFloor() {
    floor++;
  }
  
  public void previousFloor() {
    floor = Math.max( floor - 1, 0 );
  }
}