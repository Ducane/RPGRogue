package de.ducane.roguelike.level;

import de.androbin.rpg.*;
import de.androbin.rpg.obj.*;
import de.androbin.rpg.tile.*;
import de.ducane.roguelike.entity.*;
import de.ducane.roguelike.item.*;
import de.ducane.roguelike.obj.*;
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
  
  public Level( final PlayScreen screen, final String name, final Dimension size,
      final List<Rectangle> rooms ) {
    super( size, name );
    
    this.screen = screen;
    this.rooms = rooms;
    
    this.miniMap = new MiniMap( this );
  }
  
  public Point getDownStairsPos() {
    return downStairsPos;
  }
  
  @ Override
  public RogueObject getGameObject( final Point pos ) {
    return (RogueObject) super.getGameObject( pos );
  }
  
  @ Override
  public RogueEntity getEntity( final Point pos ) {
    return (RogueEntity) super.getEntity( pos );
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
  
  private void giveItem( final RogueEntity entity ) {
    final RogueTile tile = getTile( entity.getPos() );
    final Item item = tile.getItem();
    
    if ( item == null ) {
      return;
    }
    
    if ( giveItem( entity, item ) ) {
      tile.setItem( null );
    }
  }
  
  private static boolean giveItem( final RogueEntity entity, final Item item ) {
    if ( entity instanceof Mob ) {
      final Mob mob = (Mob) entity;
      
      if ( mob.getItem() == null ) {
        mob.setItem( item );
        return true;
      }
    } else if ( entity instanceof Player ) {
      final Player player = (Player) entity;
      player.inventory.add( item );
      return true;
    }
    
    return false;
  }
  
  public void moveMobs( final Entity target ) {
    for ( final Entity entity : listEntities() ) {
      if ( entity instanceof Mob && !entity.move.hasRequested() ) {
        final Mob mob = (Mob) entity;
        mob.move.request( mob.aim( target, true ) );
      }
    }
  }
  
  public void onEntityMoved( final RogueEntity entity ) {
    if ( entity instanceof Mob ) {
      final Mob mob = (Mob) entity;
      
      if ( mob.getItem() == null ) {
        giveItem( mob );
      }
    }
    
    if ( entity instanceof Player ) {
      screen.onPlayerMoved();
      
      final Player player = (Player) entity;
      
      if ( !player.running ) {
        giveItem( player );
      }
      
      final RogueObject object = getGameObject( player.getPos() );
      
      if ( object != null ) {
        object.onPlayerEntered( screen );
      }
    }
  }
  
  protected void setUpStairsPos( final Point pos ) {
    this.upStairsPos = pos;
    addGameObject( GameObjects.create( "upstairs", pos ) );
  }
  
  protected void setDownStairsPos( final Point pos ) {
    this.downStairsPos = pos;
    setTile( pos, Tiles.create( "downstairs" ) );
  }
  
  public void update() {
    final List<Entity> toRemove = new ArrayList<>();
    
    for ( final Entity entity : listEntities() ) {
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