package de.ducane.roguelike.entity;

import static de.androbin.gfx.util.GraphicsUtil.*;
import static de.ducane.util.AWTUtil.*;
import de.androbin.rpg.*;
import java.awt.*;
import java.awt.image.*;

public final class PlayerRenderer extends Renderer
{
	private final Player player;
	
	public PlayerRenderer( final Player player )
	{
		this.player = player;
	}
	
	@ Override
	public void render( final Graphics2D g )
	{
		final BufferedImage[][] moveAnimation = player.moveAnimation;
		final int i = player.getViewDir().ordinal();
		final int j = (int) ( player.getMoveProgress() * moveAnimation[ 0 ].length );
		drawImage( g, moveAnimation[ i ][ j ], bounds );
		
		if ( player.damaging )
		{
			g.setFont( new Font( "Determination Mono", 0, 30 ) );
			
			final String damage = String.valueOf( player.damage );
			
			final FontMetrics fm = g.getFontMetrics();
			
			final float x = bounds.x + ( bounds.width - fm.stringWidth( damage ) ) * 0.5f;
			final float y = bounds.y + ( bounds.height - fm.stringWidth( damage ) ) * 0.5f;
			
			final float maxY = y + 0.5f * bounds.height;
			
			drawBorderedString( g, String.valueOf( damage ), x, (float) ( y - Math.sin( player.damageProgress * Math.PI ) * ( maxY - y ) ), 3, Color.BLACK, Color.WHITE );
		}
	}
}