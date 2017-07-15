package de.ducane.roguelike.level;

import static de.androbin.collection.util.ObjectCollectionUtil.*;
import de.androbin.rpg.*;
import de.androbin.rpg.tile.*;
import de.androbin.util.*;
import de.ducane.roguelike.dark.*;
import de.ducane.roguelike.entity.*;
import de.ducane.roguelike.item.*;
import de.ducane.roguelike.screen.*;
import de.ducane.util.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import org.json.simple.*;

public final class LevelGenerator {
  private TileType[][] types;
  private List<Rectangle> rooms;
  private Level level;
  
  private void addIfAdjacentWall( final List<Point> neighbours, final Point pos ) {
    if ( checkBounds( pos ) && types[ pos.y ][ pos.x ] == TileType.WALL ) {
      neighbours.add( pos );
    }
  }
  
  private boolean checkBounds( final Point pos ) {
    return pos.x >= 0 && pos.x < types[ 0 ].length
        && pos.y >= 0 && pos.y < types.length;
  }
  
  private Point entryPoint() {
    for ( int y = 0; y < types.length; y += 2 ) {
      for ( int x = 0; x < types[ 0 ].length; x += 2 ) {
        if ( types[ y ][ x ] == TileType.WALL ) {
          return new Point( x, y );
        }
      }
    }
    
    return null;
  }
  
  @ SuppressWarnings( "unchecked" )
  public Level generate( final Identifier id, final JSONObject data, final PlayScreen screen ) {
    final Random random = ThreadLocalRandom.current();
    
    final JSONObject droprateData = (JSONObject) data.get( "droprate" );
    final String[] monsters = JSONUtil.toStringArray( data.get( "monsters" ) );
    
    final Map<String, Float> droprates = new HashMap<>();
    
    droprateData.forEach( ( type, rate ) -> droprates.put(
        (String) type, ( (Number) rate ).floatValue() ) );
    
    final JSONObject mapData = (JSONObject) data.get( "mapdata" );
    
    final int minWidth = ( (Number) mapData.get( "minWidth" ) ).intValue();
    final int maxWidth = ( (Number) mapData.get( "maxWidth" ) ).intValue();
    final int minHeight = ( (Number) mapData.get( "minHeight" ) ).intValue();
    final int maxHeight = ( (Number) mapData.get( "maxHeight" ) ).intValue();
    
    final int width = minWidth + random.nextInt( maxWidth - minWidth + 1 );
    final int height = minHeight + random.nextInt( maxHeight - minHeight + 1 );
    
    types = new TileType[ height * 2 - 1 ][ width * 2 - 1 ];
    rooms = generateRooms( mapData, width, height );
    generateBlueprint( width, height );
    
    final Dimension size = new Dimension( types[ 0 ].length, types.length );
    level = new Level( id, size, screen, rooms );
    
    translate();
    placeItems( droprates );
    placeStairs();
    spawnMobs( monsters, screen.dark );
    
    return level;
  }
  
  private void generateBlueprint( final int width, final int height ) {
    final Random random = ThreadLocalRandom.current();
    
    for ( int y = 0; y < types.length; y++ ) {
      for ( int x = 0; x < types[ 0 ].length; x++ ) {
        if ( types[ y ][ x ] == null ) {
          types[ y ][ x ] = TileType.WALL;
        }
      }
    }
    
    generatePath();
    
    for ( final Rectangle room : rooms ) {
      final int x;
      final int y;
      
      final boolean sign = random.nextBoolean();
      
      if ( random.nextBoolean() ) {
        x = room.x != 0 && ( room.x + room.width == width || sign )
            ? room.x * 2 - 1
            : ( room.x + room.width - 1 ) * 2 + 1;
        y = ( room.y + random.nextInt( room.height ) ) * 2;
      } else {
        y = room.y != 0 && ( room.y + room.height == height || sign )
            ? room.y * 2 - 1
            : ( room.y + room.height - 1 ) * 2 + 1;
        x = ( room.x + random.nextInt( room.width ) ) * 2;
      }
      
      types[ y ][ x ] = TileType.DOOR;
    }
  }
  
  private List<Rectangle> generateRooms( final JSONObject data,
      final int width, final int height ) {
    final Random random = ThreadLocalRandom.current();
    
    final int minRooms = ( (Number) data.get( "minRooms" ) ).intValue();
    final int maxRooms = ( (Number) data.get( "maxRooms" ) ).intValue();
    final int minRoomWidth = ( (Number) data.get( "minRoomWidth" ) ).intValue();
    final int maxRoomWidth = ( (Number) data.get( "maxRoomWidth" ) ).intValue();
    final int minRoomHeight = ( (Number) data.get( "minRoomHeight" ) ).intValue();
    final int maxRoomHeight = ( (Number) data.get( "maxRoomHeight" ) ).intValue();
    
    final int nRooms = minRooms + random.nextInt( maxRooms - minRooms + 1 );
    
    final List<Rectangle> rooms = new ArrayList<>( nRooms );
    
    for ( int i = 0; i < nRooms; i++ ) {
      for ( int t = 0; t < 100; t++ ) {
        final int roomWidth = minRoomWidth + random.nextInt( maxRoomWidth - minRoomWidth + 1 );
        final int roomHeight = minRoomHeight + random.nextInt( maxRoomHeight - minRoomHeight + 1 );
        
        final int roomX = random.nextInt( width - roomWidth + 1 );
        final int roomY = random.nextInt( height - roomHeight + 1 );
        
        final Rectangle room = new Rectangle( roomX, roomY, roomWidth, roomHeight );
        
        boolean intersects = false;
        
        for ( int j = 0; j < rooms.size(); j++ ) {
          if ( MathUtil.intersects( room, rooms.get( j ), 1 ) ) {
            intersects = true;
            break;
          }
        }
        
        if ( !intersects ) {
          final int startY = roomY * 2;
          final int endY = ( roomY + roomHeight - 1 ) * 2;
          final int startX = roomX * 2;
          final int endX = ( roomX + roomWidth - 1 ) * 2;
          
          for ( int y = startY; y <= endY; y++ ) {
            for ( int x = startX; x <= endX; x++ ) {
              types[ y ][ x ] = TileType.GRANITE;
            }
          }
          
          rooms.add( room );
          break;
        }
      }
    }
    
    return rooms;
  }
  
  private void generatePath() {
    final Random random = ThreadLocalRandom.current();
    final Deque<Point> deque = new ArrayDeque<>();
    
    Point lastDirection = null;
    
    Point current = entryPoint();
    types[ current.y ][ current.x ] = TileType.FLOOR;
    deque.add( current );
    
    while ( !deque.isEmpty() ) {
      final List<Point> neighbours = new ArrayList<>();
      
      addIfAdjacentWall( neighbours, new Point( current.x + 2, current.y ) );
      addIfAdjacentWall( neighbours, new Point( current.x - 2, current.y ) );
      addIfAdjacentWall( neighbours, new Point( current.x, current.y + 2 ) );
      addIfAdjacentWall( neighbours, new Point( current.x, current.y - 2 ) );
      
      if ( hasUnvisitedNeighbours( neighbours, types ) ) {
        final Point neighbour;
        
        final float chance = (float) Math.random();
        final Point sameDirectionPoint = lastDirection == null ? null
            : new Point( current.x + lastDirection.x, current.y + lastDirection.y );
        
        if ( sameDirectionPoint == null || !neighbours.contains( sameDirectionPoint )
            || chance < 0.25f ) {
          neighbour = neighbours.get( random.nextInt( neighbours.size() ) );
        } else {
          neighbour = sameDirectionPoint;
        }
        
        deque.push( current );
        
        final Point between = new Point(
            ( current.x + neighbour.x ) / 2,
            ( current.y + neighbour.y ) / 2 );
        types[ between.y ][ between.x ] = TileType.FLOOR;
        lastDirection = new Point( neighbour.x - current.x, neighbour.y - current.y );
        current = neighbour;
        types[ neighbour.y ][ neighbour.x ] = TileType.FLOOR;
      } else if ( !deque.isEmpty() ) {
        current = deque.pop();
      }
    }
  }
  
  private boolean hasUnvisitedNeighbours( final List<Point> neighbours, final TileType[][] types ) {
    for ( int i = 0; i < neighbours.size(); i++ ) {
      final Point pos = neighbours.get( i );
      
      if ( types[ pos.y ][ pos.x ] == TileType.WALL ) {
        return true;
      }
    }
    
    return false;
  }
  
  private void placeItems( final Map<String, Float> droprates ) {
    final Random random = ThreadLocalRandom.current();
    
    final int minItems = 4;
    final int maxItems = 9;
    
    final int items = minItems + random.nextInt( maxItems - minItems );
    
    for ( int itemCount = 0; itemCount < items; itemCount++ ) {
      RogueTile tile;
      
      do {
        final int x = random.nextInt( level.size.width );
        final int y = random.nextInt( level.size.height );
        
        tile = level.getTile( new Point( x, y ) );
      } while ( tile.data.event == null );
      
      final Set<String> keySet = droprates.keySet();
      final String[] keyArray = keySet.toArray( new String[ keySet.size() ] );
      
      float maxProb = 0;
      
      for ( int i = 0; i < keyArray.length; i++ ) {
        maxProb += droprates.get( keyArray[ i ] );
      }
      
      final float itemRandom = random.nextFloat() * maxProb;
      
      float prob = 0f;
      
      for ( int i = 0; i < keyArray.length; i++ ) {
        prob += droprates.get( keyArray[ i ] );
        
        if ( prob > itemRandom ) {
          tile.setItem( Items.get( keyArray[ i ] ) );
          break;
        }
      }
    }
  }
  
  private void placeStairs() {
    final Random random = ThreadLocalRandom.current();
    
    Point up;
    
    do {
      final int x = random.nextInt( level.size.width );
      final int y = random.nextInt( level.size.height );
      up = new Point( x, y );
    } while ( !"granite".equals( level.getTile( up ).data.name )
        || level.getTile( up ).getItem() != null );
    
    level.setUpStairsPos( up );
    
    Point down;
    
    do {
      final int x = random.nextInt( level.size.width );
      final int y = random.nextInt( level.size.height );
      down = new Point( x, y );
    } while ( !"granite".equals( level.getTile( down ).data.name )
        || level.getTile( down ).getItem() != null || down.equals( up ) );
    
    level.setDownStairsPos( down );
  }
  
  private void spawnMobs( final String[] monsters, final MovingDark dark ) {
    final Random random = ThreadLocalRandom.current();
    
    for ( final Rectangle room : rooms ) {
      final String monster = randomElement( monsters, null );
      
      Point pos;
      boolean success;
      
      do {
        final int x = ( random.nextInt( room.width ) + room.x ) * 2;
        final int y = ( random.nextInt( room.height ) + room.y ) * 2;
        pos = new Point( x, y );
        
        success = level.getEntity( pos ) == null && level.getThing( pos ) == null;
      } while ( !success );
      
      final Mob mob = new Mob( RogueEntites.getData( "mob/" + monster ), dark );
      level.addEntity( mob, pos );
    }
  }
  
  private void translate() {
    for ( int y = 0; y < types.length; y++ ) {
      for ( int x = 0; x < types[ y ].length; x++ ) {
        final Point pos = new Point( x, y );
        final String name = types[ y ][ x ].name;
        level.setTile( pos, Tiles.create( name ) );
      }
    }
  }
  
  private enum TileType {
    WALL( "wall" ),
    GRANITE( "granite" ),
    FLOOR( "floor" ),
    DOOR( "door" );
    
    public final String name;
    
    TileType( final String name ) {
      this.name = name;
    }
  }
}