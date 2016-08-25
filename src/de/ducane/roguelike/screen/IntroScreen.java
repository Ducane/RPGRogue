package de.ducane.roguelike.screen;

import static de.ducane.roguelike.Configuration.gui_.intro_.*;
import de.androbin.game.*;
import de.androbin.gfx.transition.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class IntroScreen extends Screen
{
	private Font		font;
	
	private int			dx;
	
	private int			pageIndex;
	private float		charProgress;
	private float		imageProgress;
	
	private final float	charSpeed;
	private final float	imageSpeed;
	
	private boolean		stepwiseOutput;
	private boolean		imageShown;
	
	public IntroScreen( final Game game )
	{
		super( game );
		
		charSpeed = TEXT_SPEED;
		imageSpeed = IMAGE_SPEED;
		stepwiseOutput = true;
	}
	
	@ Override
	public KeyListener getKeyListener()
	{
		return new InterludeKeyListener();
	}
	
	@ Override
	public void onResumed()
	{
		super.onResumed();
		
		pageIndex = 0;
		charProgress = 0f;
		imageProgress = 0f;
		stepwiseOutput = true;
		imageShown = false;
	}
	
	@ Override
	public void onResized( final int width, final int height )
	{
		dx = (int) ( TEXT_DX * width );
		
		font = new Font( TEXT_FONT_NAME, TEXT_FONT_STYLE, (int) ( TEXT_FONT_SIZE * height ) );
	}
	
	@ Override
	public void render( final Graphics2D g )
	{
		g.setColor( BACKGROUND_COLOR );
		g.fillRect( 0, 0, getWidth(), getHeight() );
		
		g.setColor( FONT_COLOR );
		g.setFont( font );
		
		final FontMetrics fm = g.getFontMetrics();
		final List<String> list = new LinkedList<String>();
		
		final String page = TEXT[ pageIndex ];
		final String[] lines = page.split( "\n" );
		
		for ( int i = 0; i < lines.length; i++ )
		{
			list.addAll( wrapLines( lines[ i ], fm, getWidth() - 2 * dx ) );
		}
		
		int y = getHeight() - fm.getHeight() * list.size();
		
		int charsLeft = (int) charProgress;
		
		for ( int i = 0; i < list.size(); i++ )
		{
			final String line = list.get( i );
			
			if ( stepwiseOutput )
			{
				if ( charsLeft >= line.length() )
				{
					g.drawString( line, dx, y );
					charsLeft -= line.length();
					
					if ( i == list.size() - 1 )
					{
						stepwiseOutput = false;
					}
				}
				else
				{
					g.drawString( line.substring( 0, charsLeft ), dx, y );
					break;
				}
			}
			else
			{
				charProgress = TEXT[ pageIndex ].length();
				g.drawString( line, dx, y );
			}
			
			y += fm.getHeight();
		}
		
		if ( imageShown )
		{
			// TODO whole image
		}
		else
		{
			// TODO imageProgress
		}
	}
	
	public static int trimIndex( final String l, final FontMetrics fm, final int width )
	{
		int pw = 0;
		
		for ( int i = 0; i < l.length(); i++ )
		{
			pw += fm.charWidth( l.charAt( i ) );
			
			if ( pw > width )
			{
				return l.lastIndexOf( ' ', i );
			}
		}
		
		return -1;
	}
	
	@ Override
	public void update( final float delta )
	{
		charProgress += delta * charSpeed;
		imageProgress += delta * imageSpeed;
		
		if ( imageProgress >= 1f )
		{
			imageShown = true;
		}
	}
	
	public static List<String> wrapLines( final String label, final FontMetrics fm, final int width )
	{
		final List<String> lines = new LinkedList<String>();
		String l = label;
		
		while ( fm.stringWidth( l ) > width )
		{
			final int i = trimIndex( l, fm, width );
			lines.add( l.substring( 0, i ) );
			l = l.substring( i + 1 );
		}
		
		lines.add( l );
		return lines;
	}
	
	private class InterludeKeyListener extends KeyAdapter
	{
		@ Override
		public void keyReleased( final KeyEvent event )
		{
			switch ( event.getKeyCode() )
			{
				case KeyEvent.VK_SPACE :
				case KeyEvent.VK_ENTER :
				{
					stepwiseOutput ^= true;
					
					if ( stepwiseOutput )
					{
						if ( pageIndex < TEXT.length - 1 )
						{
							pageIndex++;
							charProgress = 0f;
							imageProgress = 0f;
							imageShown = false;
						}
						else
						{
							game.gsm.crossfadeCall( new MenuScreen( game ), ColorCrossfade.BLACK, 1f );
						}
					}
					else
					{
						imageShown = true;
					}
					
					break;
				}
			}
		}
	}
}