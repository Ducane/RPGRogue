package de.ducane.roguelike.level;

import static de.androbin.collection.util.ObjectCollectionUtil.*;
import static de.androbin.gfx.util.GraphicsUtil.*;
import de.androbin.rpg.*;
import de.androbin.rpg.obj.*;
import de.ducane.roguelike.dark.*;
import de.ducane.roguelike.entity.*;
import de.ducane.roguelike.item.*;
import de.ducane.roguelike.obj.*;
import de.ducane.roguelike.screen.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;

public final class Level extends World {
  public final PlayScreen screen;
  
  private final List<Point> visitedTiles = new LinkedList<>();
  private final String[] monsters;
  
  private final List<Rectangle> rooms;
  
  private Point downStairsPos;
  private Point upStairsPos;
  
  public Level( final PlayScreen screen, final String name, final Dimension size,
      final List<Rectangle> rooms, final String[] monsters ) {
    super( size, name );
    
    this.screen = screen;
    
    this.rooms = rooms;
    this.monsters = monsters;
  }
  
  public Point getDownStairsPos() {
    return upStairsPos;
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
  
  public void giveItem( final RogueEntity entity ) {
    final RogueTile field = getTile( entity.getPos() );
    final Item item = field.getItem();
    
    if ( item == null ) {
      return;
    }
    
    if ( entity instanceof Mob ) {
      final Mob mob = (Mob) entity;
      
      if ( mob.getItem() == null ) {
        mob.setItem( item );
        field.setItem( null );
      }
    } else if ( entity instanceof Player ) {
      final Player player = (Player) entity;
      player.addItem( item );
      field.setItem( null );
    }
  }
  
  public void moveMobs() {
    for ( final Entity entity : listEntities() ) {
      if ( entity instanceof Mob && entity.getMoveDir() == null ) {
        final Mob mob = (Mob) entity;
        mob.moveRequestDir = mob.aim( screen.getPlayer(), true );
      }
    }
  }
  
  public void onCollisionObject( final Player player ) {
    final RogueObject object = (RogueObject) getGameObject( player.getPos() );
    
    if ( object == null || player.running ) {
      return;
    }
    
    object.onPlayerEntered( this, player );
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
      
      onCollisionObject( player );
    }
  }
  
  public void renderMiniMap( final Graphics2D g, final Point2D.Float playerPos,
      final float scale, final int width ) {
    final float size = scale * 0.2f;
    
    for ( final Point pos : visitedTiles ) {
      final RogueTile tile = getTile( pos );
      
      Color color;
      
      switch ( tile.data.name ) {
        case "granite":
          color = new Color( 0.8f, 0.8f, 0.8f, 0.7f );
          break;
        
        case "floor":
        case "door":
          color = new Color( 0.2f, 0.8f, 1f, 0.7f );
          break;
        
        default:
          color = new Color( 0f, 0f, 0f, 0f );
          break;
      }
      
      if ( pos.equals( upStairsPos ) ) {
        color = new Color( 0.3f, 0.3f, 0.3f, 0.7f );
      } else if ( pos.equals( downStairsPos ) ) {
        color = new Color( 0.15f, 0.15f, 0.15f, 0.7f );
      }
      
      if ( tile.getItem() != null ) {
        color = new Color( 1f, 0.25f, 0.25f, 0.7f );
      }
      
      final float x = width - this.size.width * size + pos.x * size;
      final float y = pos.y * size;
      
      g.setColor( color );
      fillRect( g, x, y, size, size );
    }
    
    g.setColor( Color.GREEN );
    fillRect( g, width - ( this.size.width - playerPos.x ) * size,
        playerPos.y * size, size, size );
  }
  
  public void setUpStairsPos( final Point pos ) {
    this.upStairsPos = pos;
    addGameObject( (RogueObject) GameObjects.create( "upstairs", pos ) );
  }
  
  public void setDownStairsPos( final Point pos ) {
    this.downStairsPos = pos;
    addGameObject( (RogueObject) GameObjects.create( "downstairs", pos ) );
  }
  
  public void spawnMobs() {
    final Random random = ThreadLocalRandom.current();
    
    for ( final Rectangle room : getRooms() ) {
      final String monster = randomElement( monsters, null );
      
      final int x = ( random.nextInt( room.width ) + room.x ) * 2;
      final int y = ( random.nextInt( room.height ) + room.y ) * 2;
      
      final Mob mob = new Mob( screen, this, MobType.get( monster ), new Point( x, y ) );
      addEntity( mob );
    }
  }
  
  public void updateMiniMap( final MovingDark dark, final float scale ) {
    for ( int y = 0; y < size.height; y++ ) {
      for ( int x = 0; x < size.width; x++ ) {
        final Point p = new Point( x, y );
        final Point2D.Float pos = new Point2D.Float( ( x + 0.5f ) * scale, ( y + 0.5f ) * scale );
        
        if ( !visitedTiles.contains( p ) && dark.contains( pos ) ) {
          visitedTiles.add( p );
        }
      }
    }
  }
  
  public void update() {
    final List<Entity> toRemove = new LinkedList<>();
    
    for ( final Entity entity : listEntities() ) {
      if ( !( entity instanceof RogueEntity ) ) {
        continue;
      }
      
      if ( ( (RogueEntity) entity ).isDead() ) {
        toRemove.add( entity );
      }
    }
    
    for ( final Entity entity : toRemove ) {
      removeEntity( entity );
    }
  }
}