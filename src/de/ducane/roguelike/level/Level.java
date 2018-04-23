package de.ducane.roguelike.level;

import de.androbin.rpg.*;
import de.androbin.rpg.dir.*;
import de.androbin.rpg.entity.*;
import de.androbin.rpg.tile.*;
import de.ducane.roguelike.entity.*;
import de.ducane.roguelike.item.*;
import de.ducane.roguelike.phantom.*;
import de.ducane.roguelike.screen.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public final class Level extends World {
  private final PlayScreen screen;
  private final List<Rectangle> rooms;
  
  public final MiniMap miniMap;
  
  private Point downStairsPos;
  private Point upStairsPos;
  
  public Level( final Ident id, final Dimension size, final PlayScreen screen,
      final List<Rectangle> rooms ) {
    super( id, size );
    
    this.screen = screen;
    this.rooms = rooms;
    
    this.miniMap = new MiniMap( this );
  }
  
  public Point getDownStairsPos() {
    return downStairsPos;
  }
  
  public RoguePhantom getPhantom( final Point pos ) {
    return (RoguePhantom) super.getEntity( false, pos );
  }
  
  public RogueEntity getEntity( final Point pos ) {
    return (RogueEntity) super.getEntity( true, pos );
  }
  
  @ Override
  public RogueTile getTile( final Point pos ) {
    return (RogueTile) super.getTile( pos );
  }
  
  public List<Rectangle> getRooms() {
    return Collections.unmodifiableList( rooms );
  }
  
  public Point getUpStairsPos() {
    return upStairsPos;
  }
  
  public Item takeItem( final Point pos ) {
    final RogueTile tile = getTile( pos );
    final Item item = tile.getItem();
    
    tile.setItem( null );
    return item;
  }
  
  public void moveMobs( final Entity target ) {
    for ( final Entity entity : listEntities() ) {
      if ( entity instanceof Mob && !entity.move.hasNext() ) {
        final Mob mob = (Mob) entity;
        mob.move.makeNext( new DirectionPair( mob.aim( target, true ) ) );
      }
    }
  }
  
  public void onPlayerMoved( final Player player ) {
    screen.onPlayerMoved();
    
    final RoguePhantom phantom = getPhantom( player.pos );
    
    if ( phantom != null ) {
      phantom.onPlayerEntered( screen );
    }
  }
  
  protected void setUpStairsPos( final Point pos ) {
    this.upStairsPos = pos;
    addEntity( Entities.create( Ident.fromSerial( "phantom/upstairs" ), 0 ), pos );
  }
  
  protected void setDownStairsPos( final Point pos ) {
    this.downStairsPos = pos;
    setTile( pos, Tiles.create( Downstairs.TYPE ) );
  }
  
  public void update() {
    final List<Entity> toRemove = new ArrayList<>();
    
    for ( final Entity entity : listEntities( true ) ) {
      final RogueEntity rogueEntity = (RogueEntity) entity;
      
      if ( rogueEntity.isDead( true ) ) {
        toRemove.add( rogueEntity );
      }
    }
    
    for ( final Entity entity : toRemove ) {
      removeEntity( entity );
    }
  }
}