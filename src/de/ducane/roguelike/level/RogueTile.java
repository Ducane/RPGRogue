package de.ducane.roguelike.level;

import static de.androbin.gfx.util.GraphicsUtil.*;
import de.androbin.rpg.*;
import de.ducane.roguelike.*;
import de.ducane.roguelike.gameobject.*;
import de.ducane.roguelike.item.*;
import de.ducane.roguelike.screen.*;
import java.awt.*;

public final class RogueTile extends Tile
{
	private final PlayScreen	screen;
	
	private Item				item;
	private GameObject			object;
	
	public RogueTile( final PlayScreen screen, final int x, final int y, final TileData data )
	{
		super( x, y, data );
		this.screen = screen;
	}
	
	public void setObject( final GameObject object )
	{
		this.object = object;
	}
	
	public void setItem( final Item item )
	{
		this.item = item;
	}
	
	@ Override
	public void render( final Graphics2D g, final float scale )
	{
		super.render( g, scale );
		
		final Blackout blackout = screen.getBlackout();
		
		final float cx = screen.getBlackoutX();
		final float cy = screen.getBlackoutY();
		
		final Item item = getItem();
		
		if ( item != null )
		{
			final float itemX = x * scale;
			final float itemY = y * scale;
			
			final float d = 0.5f * scale;
			
			final boolean itemVisible = blackout.contains( cx, cy, itemX + d, itemY + d );
			
			if ( itemVisible )
			{
				drawImage( g, item.getImage(), itemX, itemY, scale, scale );
			}
		}
		
		final GameObject object = getObject();
		
		if ( object != null )
		{
			final float objectX = x * scale;
			final float objectY = y * scale;
			
			final float d = 0.5f * scale;
			
			final boolean stairsVisible = blackout.contains( cx, cy, objectX + d, objectY + d );
			
			if ( stairsVisible )
			{
				drawImage( g, object.getImage(), objectX, objectY, scale, scale );
			}
		}
	}
	
	public Item getItem()
	{
		return item;
	}
	
	public GameObject getObject()
	{
		return object;
	}
}