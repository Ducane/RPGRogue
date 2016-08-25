package de.ducane.roguelike.level;

import static de.androbin.gfx.util.GraphicsUtil.*;
import static de.androbin.collection.util.ObjectCollectionUtil.*;
import de.androbin.rpg.*;
import de.ducane.roguelike.*;
import de.ducane.roguelike.entity.*;
import de.ducane.roguelike.gameobject.*;
import de.ducane.roguelike.item.*;
import de.ducane.roguelike.screen.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;

public final class Level extends World
{
	public PlayScreen				screen;
	
	private final List<Point>		visitedTiles	= new LinkedList<Point>();
	private final String[]			monsters;
	
	private final List<Rectangle>	rooms;
	
	private Point					downStairsPos;
	private Point					upStairsPos;
	
	public Level( final PlayScreen screen, final int width, final int height, final List<Rectangle> rooms, final String[] monsters )
	{
		super( width, height );
		
		this.screen = screen;
		
		this.rooms = rooms;
		this.monsters = monsters;
	}
	
	public boolean canAttack( final RogueEntity entity, final Direction viewDir )
	{
		final int x = entity.getX() + viewDir.dx;
		final int y = entity.getY() + viewDir.dy;
		
		return getEntityAt( x, y ) != null;
	}
	
	public RogueEntity getEntityAt( final int x, final int y )
	{
		for ( final Entity entity : getEntities() )
		{
			if ( entity.getX() == x && entity.getY() == y )
			{
				return (RogueEntity) entity;
			}
		}
		
		return null;
	}
	
	public Point getDownStairsPos()
	{
		return downStairsPos;
	}
	
	@ Override
	public RogueTile getTile( final int x, final int y )
	{
		return (RogueTile) super.getTile( x, y );
	}
	
	public Point getUpStairsPos()
	{
		return upStairsPos;
	}
	
	public List<Rectangle> getRooms()
	{
		return Collections.unmodifiableList( rooms );
	}
	
	public void giveItem( final RogueEntity entity )
	{
		final RogueTile field = getTile( entity.getX(), entity.getY() );
		final Item item = field.getItem();
		
		if ( item != null )
		{
			entity.collectItem( item );
			field.setItem( null );
		}
	}
	
	public void moveMobs()
	{
		for ( final Entity entity : getEntities() )
		{
			if ( entity instanceof Mob && entity.getMoveDir() == null )
			{
				final Mob mob = (Mob) entity;
				mob.calcDirection( screen.getPlayer() );
			}
		}
	}
	
	public void next()
	{
		visitedTiles.removeAll( visitedTiles );
		screen.nextFloor();
		screen.generateLevel();
	}
	
	public void onCollisionObject( final Player player )
	{
		final RogueTile tile = getTile( player.getX(), player.getY() );
		final GameObject object = tile.getObject();
		
		if ( object != null && !player.isRunning() )
		{
			object.onPlayerEntered( this, player );
		}
	}
	
	public void onEntityMoved( final RogueEntity entity )
	{
		if ( entity instanceof Mob )
		{
			final Mob mob = (Mob) entity;
			
			if ( !mob.hasItem() )
			{
				giveItem( mob );
			}
		}
		
		if ( entity instanceof Player )
		{
			final Player player = (Player) entity;
			
			if ( !player.isRunning() )
			{
				giveItem( player );
			}
			
			onCollisionObject( player );
		}
	}
	
	public void renderMiniMap( final Graphics2D g, final float playerX, final float playerY, final float scale, final int width )
	{
		final float size = scale * 0.2f;
		
		for ( final Point p : visitedTiles )
		{
			final RogueTile tile = getTile( p.x, p.y );
			
			Color color;
			
			switch ( tile.data.name )
			{
				case "granite" :
					color = new Color( 0.8f, 0.8f, 0.8f, 0.7f );
					break;
				
				case "floor" :
				case "door" :
					color = new Color( 0.2f, 0.8f, 1f, 0.7f );
					break;
				
				default :
					color = new Color( 0f, 0f, 0f, 0f );
					break;
			}
			
			/**/ if ( getUpStairsPos().equals( p ) )
			{
				color = new Color( 0.3f, 0.3f, 0.3f, 0.7f );
			}
			else if ( getDownStairsPos().equals( p ) )
			{
				color = new Color( 0.15f, 0.15f, 0.15f, 0.7f );
			}
			
			if ( tile.getItem() != null )
			{
				color = new Color( 1f, 0.25f, 0.25f, 0.7f );
			}
			
			final int x = (int) ( width - this.width * size + p.x * size );
			final int y = (int) ( p.y * size );
			
			final int nx = (int) ( width - this.width * size + ( p.x + 1 ) * size );
			final int ny = (int) ( ( p.y + 1 ) * size );
			
			g.setColor( color );
			g.fillRect( x, y, nx - x, ny - y );
		}
		
		g.setColor( Color.GREEN );
		fillRect( g, width - this.width * size + playerX * size, playerY * size, size, size );
	}
	
	public void setUpStairsPos( final int x, final int y )
	{
		this.upStairsPos = new Point( x, y );
		getTile( x, y ).setObject( new Upstairs() );
	}
	
	public void setDownStairsPos( final int x, final int y )
	{
		this.downStairsPos = new Point( x, y );
		getTile( x, y ).setObject( new Downstairs() );
	}
	
	public void spawnMobs()
	{
		final Random random = ThreadLocalRandom.current();
		
		for ( final Rectangle room : getRooms() )
		{
			final String monster = randomElement( monsters, null );
			
			final int x = ( random.nextInt( room.width ) + room.x ) * 2;
			final int y = ( random.nextInt( room.height ) + room.y ) * 2;
			
			final Mob mob = new Mob( screen, this, MobType.get( monster ), x, y );
			addEntity( mob );
		}
	}
	
	public void updateMiniMap( final Blackout blackout, final float scale, final float cx, final float cy )
	{
		for ( int y = 0; y < height; y++ )
		{
			for ( int x = 0; x < width; x++ )
			{
				final Point p = new Point( x, y );
				
				if ( !visitedTiles.contains( p ) && blackout.contains( cx, cy, ( x + 0.5f ) * scale, ( y + 0.5f ) * scale ) )
				{
					visitedTiles.add( p );
				}
			}
		}
	}
	
	public void update()
	{
		final List<Entity> toRemove = new LinkedList<>();
		
		for ( final Entity entity : getEntities() )
		{
			if ( !( entity instanceof RogueEntity ) )
			{
				continue;
			}
			
			if ( ( (RogueEntity) entity ).isDead() )
			{
				toRemove.add( entity );
			}
		}
		
		for ( final Entity entity : toRemove )
		{
			removeEntity( entity );
		}
	}
}