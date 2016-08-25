package de.ducane.roguelike.entity;

import static de.androbin.gfx.util.GraphicsUtil.*;
import static de.ducane.util.AWTUtil.*;
import de.androbin.rpg.*;
import de.ducane.roguelike.*;
import java.awt.*;
import java.awt.image.*;

public final class MobRenderer extends Renderer
{
	private final Mob mob;
	
	public MobRenderer( final Mob mob )
	{
		this.mob = mob;
	}
	
	@ Override
	public void render( final Graphics2D g )
	{
		final Blackout blackout = mob.screen.getBlackout();
		
		final float cx = mob.screen.getBlackoutX();
		final float cy = mob.screen.getBlackoutY();
		
		final float entityX = ( mob.getPX() + 0.5f ) * bounds.width;
		final float entityY = ( mob.getPY() + 0.5f ) * bounds.height;
		
		if ( !blackout.contains( cx, cy, entityX, entityY ) )
		{
			return;
		}
		
		final BufferedImage[][] moveAnimation = mob.type.moveAnimation;
		final int i = mob.getViewDir().ordinal();
		
		if ( mob.attacking )
		{
			final Direction dir = mob.getViewDir();
			
			final float d = (float) Math.sin( mob.attackProgress * Math.PI ) * 0.35f;
			
			final float dx = d * dir.dx * bounds.x;
			final float dy = d * dir.dy * bounds.y;
			
			g.translate( dx, dy );
			drawImage( g, moveAnimation[ i ][ 0 ], bounds );
			g.translate( -dx, -dy );
		}
		else
		{
			final int j = (int) ( mob.getMoveProgress() * moveAnimation[ 0 ].length );
			drawImage( g, moveAnimation[ i ][ j ], bounds );
		}
		
		if ( mob.damaging )
		{
			g.setFont( new Font( "Determination Mono", 0, 30 ) );
			
			final String damage = String.valueOf( mob.damage );
			
			final FontMetrics fm = g.getFontMetrics();
			
			final float x = bounds.x + ( bounds.width - fm.stringWidth( damage ) ) * 0.5f;
			final float y = bounds.y + ( bounds.height - fm.stringWidth( damage ) ) * 0.5f;
			
			final float maxY = y + 0.5f * bounds.height;
			
			drawBorderedString( g, String.valueOf( damage ), x, (int) ( y - Math.sin( mob.damageProgress * Math.PI ) * ( maxY - y ) ), 3, Color.BLACK, Color.WHITE );
		}
	}
}