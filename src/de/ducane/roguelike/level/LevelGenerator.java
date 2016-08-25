package de.ducane.roguelike.level;

import de.androbin.rpg.*;
import de.androbin.util.*;
import de.ducane.roguelike.item.*;
import de.ducane.roguelike.screen.*;
import de.ducane.util.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import org.json.simple.*;

public final class LevelGenerator
{
	private LevelGenerator()
	{
	}
	
	@ SuppressWarnings( "unchecked" )
	public static Level generate( final PlayScreen screen, final JSONObject data )
	{
		final JSONObject droprateData = (JSONObject) data.get( "droprate" );
		final String[] monsters = JSONUtil.toStringArray( (JSONArray) data.get( "monsters" ) );
		
		final Map<String, Float> droprates = new HashMap<String, Float>();
		
		droprateData.forEach( ( type, rate ) ->
		{
			droprates.put( (String) type, ( (Number) rate ).floatValue() );
		} );
		
		final JSONObject mapData = (JSONObject) data.get( "mapdata" );
		
		final int minRooms = ( (Number) mapData.get( "minRooms" ) ).intValue();
		final int maxRooms = ( (Number) mapData.get( "maxRooms" ) ).intValue();
		final int minRoomWidth = ( (Number) mapData.get( "minRoomWidth" ) ).intValue();
		final int maxRoomWidth = ( (Number) mapData.get( "maxRoomWidth" ) ).intValue();
		final int minRoomHeight = ( (Number) mapData.get( "minRoomHeight" ) ).intValue();
		final int maxRoomHeight = ( (Number) mapData.get( "maxRoomHeight" ) ).intValue();
		final int minWidth = ( (Number) mapData.get( "minWidth" ) ).intValue();
		final int maxWidth = ( (Number) mapData.get( "maxWidth" ) ).intValue();
		final int minHeight = ( (Number) mapData.get( "minHeight" ) ).intValue();
		final int maxHeight = ( (Number) mapData.get( "maxHeight" ) ).intValue();
		
		final Random random = ThreadLocalRandom.current();
		
		final int nRooms = minRooms + random.nextInt( maxRooms - minRooms + 1 );
		final int width = minWidth + random.nextInt( maxWidth - minWidth + 1 );
		final int height = minHeight + random.nextInt( maxHeight - minHeight + 1 );
		
		final TileType[][] types = new TileType[ height * 2 - 1 ][ width * 2 - 1 ];
		final List<Rectangle> rooms = generateRooms( minRoomWidth, maxRoomWidth, minRoomHeight, maxRoomHeight, random, nRooms, width, height, types );
		generateBlueprint( types, width, height, rooms, random );
		final Level level = new Level( screen, types[ 0 ].length, types.length, rooms, monsters );
		translate( level, types );
		placeItems( level, droprates );
		placeStairs( level );
		level.spawnMobs();
		return level;
	}
	
	private static void generateBlueprint( final TileType[][] tileTypes, final int width, final int height, final List<Rectangle> rooms, final Random random )
	{
		for ( int y = 0; y < tileTypes.length; y++ )
		{
			for ( int x = 0; x < tileTypes[ 0 ].length; x++ )
			{
				if ( tileTypes[ y ][ x ] == null )
				{
					tileTypes[ y ][ x ] = TileType.WALL;
				}
			}
		}
		
		boolean pathFinished = false;
		
		for ( int y = 0; y < tileTypes.length && !pathFinished; y += 2 )
		{
			for ( int x = 0; x < tileTypes[ 0 ].length && !pathFinished; x += 2 )
			{
				if ( tileTypes[ y ][ x ] == TileType.WALL )
				{
					generatePath( tileTypes, x, y, width, height );
					pathFinished = true;
				}
			}
		}
		
		for ( final Rectangle room : rooms )
		{
			int x;
			int y;
			
			final boolean sign = random.nextBoolean();
			
			if ( random.nextBoolean() )
			{
				x = room.x != 0 && ( room.x + room.width == width || sign ) ? room.x * 2 - 1 : ( room.x + room.width - 1 ) * 2 + 1;
				y = ( room.y + random.nextInt( room.height ) ) * 2;
			}
			else
			{
				y = room.y != 0 && ( room.y + room.height == height || sign ) ? room.y * 2 - 1 : ( room.y + room.height - 1 ) * 2 + 1;
				x = ( room.x + random.nextInt( room.width ) ) * 2;
				
			}
			
			tileTypes[ y ][ x ] = TileType.DOOR;
		}
	}
	
	private static List<Rectangle> generateRooms( final int minRoomWidth, final int maxRoomWidth, final int minRoomHeight, final int maxRoomHeight, final Random random, final int nRooms, final int width, final int height, final TileType[][] tileTypes )
	{
		final List<Rectangle> rooms = new LinkedList<Rectangle>();
		
		for ( int i = 0; i < nRooms; i++ )
		{
			for ( int t = 0; t < 100; t++ )
			{
				final int roomWidth = minRoomWidth + random.nextInt( maxRoomWidth - minRoomWidth + 1 );
				final int roomHeight = minRoomHeight + random.nextInt( maxRoomHeight - minRoomHeight + 1 );
				
				final int roomX = random.nextInt( width - roomWidth + 1 );
				final int roomY = random.nextInt( height - roomHeight + 1 );
				
				final Rectangle room = new Rectangle( roomX, roomY, roomWidth, roomHeight );
				
				boolean intersects = false;
				
				for ( int j = 0; j < rooms.size(); j++ )
				{
					if ( MathUtil.intersects( room, rooms.get( j ), 1 ) )
					{
						intersects = true;
						break;
					}
				}
				
				if ( !intersects )
				{
					final int startY = roomY * 2;
					final int endY = ( roomY + roomHeight - 1 ) * 2;
					final int startX = roomX * 2;
					final int endX = ( roomX + roomWidth - 1 ) * 2;
					
					for ( int y = startY; y <= endY; y++ )
					{
						for ( int x = startX; x <= endX; x++ )
						{
							tileTypes[ y ][ x ] = TileType.GRANITE;
						}
					}
					
					rooms.add( room );
					break;
				}
			}
		}
		
		return rooms;
	}
	
	public static void generatePath( final TileType[][] types, final int x, final int y, final int width, final int height )
	{
		final Deque<Point> deque = new ArrayDeque<Point>();
		
		final Random random = ThreadLocalRandom.current();
		
		Point lastDirection = null;
		
		Point current = new Point( x, y );
		types[ current.y ][ current.x ] = TileType.FLOOR;
		deque.add( current );
		
		while ( !deque.isEmpty() )
		{
			final List<Point> neighbours = new LinkedList<Point>();
			final Point neighbourRight = new Point( current.x + 2, current.y );
			if ( neighbourRight.x >= 0 && neighbourRight.x < types[ 0 ].length && types[ neighbourRight.y ][ neighbourRight.x ] == TileType.WALL )
			{
				neighbours.add( neighbourRight );
			}
			final Point neighbourLeft = new Point( current.x - 2, current.y );
			if ( neighbourLeft.x >= 0 && neighbourLeft.x < types[ 0 ].length && types[ neighbourLeft.y ][ neighbourLeft.x ] == TileType.WALL )
			{
				neighbours.add( neighbourLeft );
			}
			final Point neighbourDown = new Point( current.x, current.y + 2 );
			if ( neighbourDown.y >= 0 && neighbourDown.y < types.length && types[ neighbourDown.y ][ neighbourDown.x ] == TileType.WALL )
			{
				neighbours.add( neighbourDown );
			}
			final Point neighbourUp = new Point( current.x, current.y - 2 );
			if ( neighbourUp.y >= 0 && neighbourUp.y < types.length && types[ neighbourUp.y ][ neighbourUp.x ] == TileType.WALL )
			{
				neighbours.add( neighbourUp );
			}
			
			if ( hasNotVisitedNeighbours( neighbours, types ) )
			{
				Point unvisitedNeighbour = null;
				
				final float randomFloat = (float) Math.random();
				final Point sameDirectionPoint = lastDirection == null ? null : new Point( current.x + lastDirection.x, current.y + lastDirection.y );
				
				if ( sameDirectionPoint == null || !neighbours.contains( sameDirectionPoint ) || randomFloat < 0.25f )
				{
					unvisitedNeighbour = neighbours.get( random.nextInt( neighbours.size() ) );
				}
				else
				{
					unvisitedNeighbour = sameDirectionPoint;
				}
				
				deque.push( current );
				final Point betweenCurrentAndNeighbour = new Point( ( unvisitedNeighbour.x - current.x ) / 2 + current.x, ( unvisitedNeighbour.y - current.y ) / 2 + current.y );
				types[ betweenCurrentAndNeighbour.y ][ betweenCurrentAndNeighbour.x ] = TileType.FLOOR;
				lastDirection = new Point( unvisitedNeighbour.x - current.x, unvisitedNeighbour.y - current.y );
				current = unvisitedNeighbour;
				types[ unvisitedNeighbour.y ][ unvisitedNeighbour.x ] = TileType.FLOOR;
			}
			else if ( !deque.isEmpty() )
			{
				current = deque.pop();
			}
		}
	}
	
	private static boolean hasNotVisitedNeighbours( final List<Point> neighbours, final TileType[][] types )
	{
		for ( int i = 0; i < neighbours.size(); i++ )
		{
			final Point neighbour = neighbours.get( i );
			
			if ( types[ neighbour.y ][ neighbour.x ] == TileType.WALL )
			{
				return true;
			}
		}
		return false;
	}
	
	public static void placeItems( final Level level, final Map<String, Float> droprates )
	{
		final Random random = ThreadLocalRandom.current();
		
		for ( int itemCount = 0; itemCount < 100; itemCount++ )
		{
			int x;
			int y;
			
			RogueTile tile;
			
			do
			{
				x = random.nextInt( level.width );
				y = random.nextInt( level.height );
				
				tile = level.getTile( x, y );
			}
			while ( "wall".equals( tile.data.name ) || "door".equals( tile.data.name ) );
			
			final Set<String> keySet = droprates.keySet();
			final String[] keyArray = keySet.toArray( new String[ keySet.size() ] );
			
			float maxProb = 0;
			
			for ( int i = 0; i < keyArray.length; i++ )
			{
				maxProb += droprates.get( keyArray[ i ] );
			}
			
			final float itemRandom = random.nextFloat() * maxProb;
			
			float prob = 0f;
			
			for ( int i = 0; i < keyArray.length; i++ )
			{
				prob += droprates.get( keyArray[ i ] );
				
				if ( itemRandom < prob )
				{
					tile.setItem( Item.getItem( keyArray[ i ] ) );
					break;
				}
			}
		}
	}
	
	public static void placeStairs( final Level level )
	{
		final Random random = ThreadLocalRandom.current();
		
		int upX;
		int upY;
		
		do
		{
			upX = random.nextInt( level.width );
			upY = random.nextInt( level.height );
		}
		while ( !level.getTile( upX, upY ).data.name.equals( "granite" ) || level.getTile( upX, upY ).getItem() != null );
		
		level.setUpStairsPos( upX, upY );
		
		int downX;
		int downY;
		
		do
		{
			downX = random.nextInt( level.width );
			downY = random.nextInt( level.height );
		}
		while ( !level.getTile( downX, downY ).data.name.equals( "granite" ) || level.getTile( downX, downY ).getItem() != null || downX == upX && downY == upY );
		
		level.setDownStairsPos( downX, downY );
	}
	
	private static void translate( final Level level, final TileType[][] tileTypes )
	{
		for ( int y = 0; y < tileTypes.length; y++ )
		{
			for ( int x = 0; x < tileTypes[ y ].length; x++ )
			{
				switch ( tileTypes[ y ][ x ] )
				{
					case WALL :
						level.setTile( x, y, Tiles.getTile( "wall", x, y ) );
						break;
					case FLOOR :
						level.setTile( x, y, Tiles.getTile( "floor", x, y ) );
						break;
					case GRANITE :
						level.setTile( x, y, Tiles.getTile( "granite", x, y ) );
						break;
					case DOOR :
						level.setTile( x, y, Tiles.getTile( "door", x, y ) );
				}
			}
		}
	}
	
	private enum TileType
	{
		WALL,
		GRANITE,
		FLOOR,
		DOOR;
	}
}