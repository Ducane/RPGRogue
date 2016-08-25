package de.ducane.roguelike.screen;

import static de.androbin.gfx.util.GraphicsUtil.*;
import de.androbin.game.*;
import de.androbin.game.listener.*;
import de.androbin.rpg.*;
import de.androbin.util.*;
import de.ducane.roguelike.*;
import de.ducane.roguelike.entity.*;
import de.ducane.roguelike.item.*;
import de.ducane.roguelike.level.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.util.List;
import org.json.simple.*;

public final class PlayScreen extends RPGScreen
{
	private final String			name;
	
	private Level					level;
	
	private int						floor;
	
	private Blackout				blackout;
	
	private List<Rectangle>			rooms;
	private Rectangle				room;
	
	private BufferedImage			barImage;
	
	private int						barX;
	private int						barY;
	private int						barWidth;
	private int						barHeight;
	
	private State					state;
	
	private float					statX;
	private float					statY;
	private float					statWidth;
	private float					statHeight;
	private float					statImageX;
	private float					statImageY;
	private float					statImageWidth;
	private float					statImageHeight;
	
	private Stroke					menuStroke;
	private float					menuX;
	private float					menuY;
	private float					menuWidth;
	private float					menuHeight;
	private Stroke					menuButtonStroke;
	
	private static final String[]	MENU_BUTTON_LABELS		=
	{ "Inventar", "Beenden" };
	
	private Stroke					inventoryStroke;
	private float					inventoryX;
	private float					inventoryY;
	private float					inventoryWidth;
	private float					inventoryHeight;
	private Stroke					inventoryButtonStroke;
	private float					inventoryButtonDy;
	
	private Stroke					itemSelectStroke;
	private float					itemSelectX;
	private float					itemSelectY;
	private float					itemSelectWidth;
	private float					itemSelectHeight;
	
	private String[]				itemSelectButtonLabels	= new String[ 3 ];
	
	private Rectangle				pageCursorRight;
	private Rectangle				pageCursorLeft;
	private int						pageIndex;
	
	public PlayScreen( final Game game, final float scale, final String name )
	{
		super( game, scale );
		this.name = name;
		
		Tiles.tileFactory = ( x, y, data ) -> new RogueTile( this, x, y, data );
		
		generateLevel();
		updateBlackout();
	}
	
	public void generateLevel()
	{
		world = level = LevelGenerator.generate( this, (JSONObject) JSONUtil.parseJSON( "/level/level" + ( floor / 4 + 1 ) + ".json" ) );
		level.screen = this;
		
		final Point pos = level.getUpStairsPos();
		final Player player = new Player( this, level, pos.x, pos.y, name );
		level.addEntity( player );
		setPlayer( player );
		
		this.rooms = level.getRooms();
		this.room = getCurrentRoom();
		
		updateBlackout();
		floor++;
	}
	
	public Blackout getBlackout()
	{
		return blackout;
	}
	
	public float getBlackoutX()
	{
		return room == null ? ( getPlayer().getPX() + 0.5f ) * scale : room.x * scale;
	}
	
	public float getBlackoutY()
	{
		return room == null ? ( getPlayer().getPY() + 0.5f ) * scale : room.y * scale;
	}
	
	@ Override
	public KeyListener getKeyListener()
	{
		return new TeeKeyListener( new PlayKeyListener(), super.getKeyListener() );
	}
	
	@ Override
	public MouseListener getMouseListener()
	{
		return new PlayMouseListener();
	}
	
	@ Override
	public MouseMotionListener getMouseMotionListener()
	{
		return new PlayMouseMotionListener();
	}
	
	@ Override
	public Player getPlayer()
	{
		return (Player) super.getPlayer();
	}
	
	@ Override
	public void onResized( final int width, final int height )
	{
		barX = (int) ( 0.2f * width );
		barY = (int) ( 0.05f * height );
		barWidth = (int) ( 0.4f * width );
		barHeight = (int) ( 0.025f * height );
		
		statX = (int) ( 0.1f * width );
		statY = (int) ( 0.15f * height );
		statWidth = (int) ( 0.2f * width );
		statHeight = (int) ( 0.25f * height );
		statImageX = (int) ( 0.15f * width );
		statImageY = (int) ( 0.2f * height );
		statImageWidth = (int) ( 0.1f * width );
		statImageHeight = (int) ( 0.15f * height );
		
		menuX = (int) ( 0.1f * width );
		menuY = (int) ( 0.45f * height );
		menuWidth = (int) ( 0.2f * width );
		menuHeight = (int) ( 0.4f * height );
		menuStroke = new BasicStroke( 0.005f * getWidth() );
		menuButtonStroke = new BasicStroke( 0f );
		
		final float menuButtonX = 0.125f * width;
		final float menuButtonY = 0.475f * height;
		final float menuButtonWidth = 0.15f * width;
		final float menuButtonHeight = 0.05f * height;
		final float menuButtonDy = 0.075f * height;
		
		State.Menu.buttonBounds = new Rectangle2D.Float[ MENU_BUTTON_LABELS.length ];
		
		for ( int i = 0; i < State.Menu.buttonBounds.length; i++ )
		{
			State.Menu.buttonBounds[ i ] = new Rectangle2D.Float( menuButtonX, menuButtonY + menuButtonDy * i, menuButtonWidth, menuButtonHeight );
		}
		
		inventoryStroke = new BasicStroke( 0.005f * getWidth() );
		inventoryX = (int) ( 0.35f * width );
		inventoryY = (int) ( 0.15f * height );
		inventoryWidth = (int) ( 0.25f * width );
		inventoryHeight = (int) ( 0.7f * height );
		inventoryButtonStroke = new BasicStroke( 0f );
		
		final float inventoryButtonX = 0.4f * width;
		final float inventoryButtonY = 0.175f * height;
		final float inventoryButtonWidth = 0.15f * width;
		final float inventoryButtonHeight = 0.05f * height;
		
		inventoryButtonDy = (int) ( 0.075f * height );
		State.Inventory.buttonBounds = new Rectangle2D.Float[ 8 ];
		
		for ( int i = 0; i < State.Inventory.buttonBounds.length; i++ )
		{
			State.Inventory.buttonBounds[ i ] = new Rectangle2D.Float( inventoryButtonX, inventoryButtonY + inventoryButtonDy * i, inventoryButtonWidth, inventoryButtonHeight );
		}
		
		pageCursorLeft = new Rectangle( (int) ( inventoryX + ( 0.1f * inventoryWidth ) ), (int) ( inventoryY + ( 0.95f * inventoryHeight ) ), 10, 10 );
		pageCursorRight = new Rectangle( (int) ( inventoryX + ( 0.9f * inventoryWidth ) ), (int) ( inventoryY + ( 0.95f * inventoryHeight ) ), 10, 10 );
		
		itemSelectStroke = new BasicStroke( 0.005f * getWidth() );
		itemSelectX = (int) ( 0.65f * width );
		itemSelectY = (int) ( 0.15f * height );
		itemSelectWidth = (int) ( 0.2f * width );
		itemSelectHeight = (int) ( 0.25f * height );
		
		final float itemSelectButtonX = 0.675f * width;
		final float itemSelectButtonY = 0.175f * height;
		final float itemSelectButtonWidth = 0.15f * width;
		final float itemSelectButtonHeight = 0.05f * height;
		final float itemSelectButtonDy = 0.075f * height;
		
		State.ItemSelect.buttonBounds = new Rectangle2D.Float[ 3 ];
		
		for ( int i = 0; i < State.ItemSelect.buttonBounds.length; i++ )
		{
			State.ItemSelect.buttonBounds[ i ] = new Rectangle2D.Float( itemSelectButtonX, itemSelectButtonY + itemSelectButtonDy * i, itemSelectButtonWidth, itemSelectButtonHeight );
		}
	}
	
	private void updateBlackout()
	{
		final Color color = new Color( 0f, 0f, 0f, 0.8f );
		
		if ( room == null )
		{
			blackout = new CircularBlackout( color, scale * 1.5f );
		}
		else
		{
			blackout = new RectangularBlackout( color, scale * room.width, scale * room.height );
		}
		
		level.updateMiniMap( blackout, scale, getBlackoutX(), getBlackoutY() );
	}
	
	@ Override
	public void render( final Graphics2D g )
	{
		super.render( g );
		
		{
			final float dx = getTranslationX();
			final float dy = getTranslationY();
			
			blackout.darken( g, dx + getBlackoutX(), dy + getBlackoutY(), getWidth(), getHeight() );
		}
		
		level.renderMiniMap( g, getPlayer().getPX(), getPlayer().getPY(), scale, getWidth() );
		
		g.setColor( Color.WHITE );
		
		debug( g );
		renderHPBar( g );
		
		g.setColor( Color.WHITE );
		
		final Player player = getPlayer();
		g.setFont( new Font( "Determination Mono", 0, (int) ( 0.04 * getHeight() ) ) );
		g.drawString( "Lv " + player.getStage(), barX - (int) ( 0.1f * getWidth() ), barY );
		g.drawString( "E" + floor, barX - 0.175f * getWidth(), barY );
		g.drawString( "HP " + player.getHp() + "/" + player.getMaxHp(), barX, barY - 0.01f * getHeight() );
		
		final FontMetrics fm = g.getFontMetrics();
		
		if ( state != null )
		{
			g.setColor( Color.BLACK );
			fillRect( g, menuX, menuY, menuWidth, menuHeight );
			g.setStroke( menuStroke );
			g.setColor( Color.WHITE );
			drawRect( g, menuX, menuY, menuWidth, menuHeight );
			
			g.setStroke( menuButtonStroke );
			
			for ( int i = 0; i < State.Menu.buttonBounds.length; i++ )
			{
				final Rectangle2D.Float rect = State.Menu.buttonBounds[ i ];
				g.setColor( state == State.Menu && i == state.selection ? Color.YELLOW : Color.WHITE );
				g.drawString( MENU_BUTTON_LABELS[ i ], rect.x + ( rect.width - fm.stringWidth( MENU_BUTTON_LABELS[ i ] ) ) / 2, rect.y + ( rect.height - fm.getHeight() ) / 2 + fm.getAscent() );
			}
		}
		
		if ( state == State.Inventory || state == State.ItemSelect )
		{
			g.setColor( Color.BLACK );
			fillRect( g, inventoryX, inventoryY, inventoryWidth, inventoryHeight );
			g.setStroke( inventoryStroke );
			g.setColor( Color.WHITE );
			drawRect( g, inventoryX, inventoryY, inventoryWidth, inventoryHeight );
			
			final List<Item> inventory = player.getInventory();
			g.setStroke( inventoryButtonStroke );
			
			for ( int i = pageIndex * 8; i < ( pageIndex + 1 ) * 8; i++ )
			{
				final Rectangle2D.Float rect = State.Inventory.buttonBounds[ i % 8 ];
				
				if ( inventory.size() > i )
				{
					final String itemName = inventory.get( i ).name;
					final String subItemName = itemName.length() > 10 ? itemName.substring( 0, 10 ) : itemName;
					g.setColor( state == State.Inventory && i % 8 == state.selection ? Color.YELLOW : Color.WHITE );
					g.drawString( subItemName, rect.x + ( rect.width - fm.stringWidth( subItemName ) ) / 2, rect.y + ( rect.height - fm.getHeight() ) / 2 + fm.getAscent() );
				}
				else
				{
					final String nothing = "----------";
					g.setColor( Color.WHITE );
					g.drawString( nothing, rect.x + ( rect.width - fm.stringWidth( nothing ) ) / 2, rect.y + ( rect.height - fm.getHeight() ) / 2 + fm.getAscent() );
				}
			}
			
			final String size = inventory.size() + "/" + ( pageIndex + 1 );
			g.setColor( Color.WHITE );
			g.drawString( size, inventoryX + ( inventoryWidth - fm.stringWidth( size ) ) / 2, inventoryY + ( 0.95f * inventoryHeight ) );
			g.fillRect( pageCursorRight.x, pageCursorRight.y, pageCursorRight.width, pageCursorRight.height );
			g.fillRect( pageCursorLeft.x, pageCursorLeft.y, pageCursorLeft.width, pageCursorLeft.height );
			
			final int index = State.Inventory.selection + pageIndex * 8;
			
			if ( inventory.size() > index )
			{
				g.setColor( Color.BLACK );
				fillRect( g, statX, statY, statWidth, statHeight );
				g.setStroke( inventoryStroke );
				g.setColor( Color.WHITE );
				drawRect( g, statX, inventoryY, statWidth, statHeight );
			}
			
			if ( !inventory.isEmpty() && inventory.size() > index )
			{
				drawImage( g, inventory.get( pageIndex > 0 ? index : State.Inventory.selection ).getImage(), statImageX, statImageY, statImageWidth, statImageHeight );
			}
		}
		
		if ( state == State.ItemSelect )
		{
			g.setColor( Color.BLACK );
			fillRect( g, itemSelectX, itemSelectY + State.Inventory.selection * inventoryButtonDy, itemSelectWidth, itemSelectHeight );
			g.setStroke( itemSelectStroke );
			g.setColor( Color.WHITE );
			drawRect( g, itemSelectX, itemSelectY + State.Inventory.selection * inventoryButtonDy, itemSelectWidth, itemSelectHeight );
			
			for ( int i = 0; i < State.ItemSelect.buttonBounds.length; i++ )
			{
				final float dy = State.Inventory.selection * inventoryButtonDy;
				g.translate( 0, dy );
				
				final Rectangle2D.Float rect = State.ItemSelect.buttonBounds[ i ];
				g.setColor( i == state.selection ? Color.YELLOW : Color.WHITE );
				g.drawString( itemSelectButtonLabels[ i ], rect.x + ( rect.width - fm.stringWidth( itemSelectButtonLabels[ i ] ) ) / 2, rect.y + ( rect.height - fm.getHeight() ) / 2 + fm.getAscent() );
				g.translate( 0, -dy );
			}
		}
	}
	
	private void renderHPBar( final Graphics2D g )
	{
		final Player player = getPlayer();
		final float progress = (float) player.getHp() / player.getMaxHp();
		final int barProgressWidth = (int) ( barWidth * progress );
		
		if ( barProgressWidth > 0 )
		{
			barImage = new BufferedImage( barProgressWidth, barHeight, BufferedImage.TYPE_INT_ARGB );
		}
		
		final Rectangle barRectangle = new Rectangle( barX, barY, barProgressWidth, barHeight );
		
		final int barRgb = new Color(
				progress < 0.5F ? 0.5F : ( 1F - progress ) * 2F / ( 1 / 0.5F ),
				progress > 0.5F ? 0.5F : progress * 2F / ( 1 / 0.5F ),
				0F ).getRGB();
		
		for ( int y = 0; y < barHeight; y++ )
		{
			for ( int x = 0; x < barProgressWidth; x++ )
			{
				if ( barRectangle.contains( barX + x, barY + y ) )
				{
					barImage.setRGB( x, y, barRgb );
				}
			}
		}
		
		g.setColor( Color.BLACK );
		g.fillRect( barX, barY, barWidth, barHeight );
		
		if ( barProgressWidth > 0 )
		{
			g.drawImage( barImage, barX, barY, null );
		}
		
		g.setStroke( new BasicStroke( 0.0003f * getHeight() ) );
		
		final Color backgroundBarRgb = new Color(
				progress < 0.5f ? 0.3f : ( 1f - progress ) * 0.6f,
				progress > 0.5f ? 0.3f : progress * 0.6f,
				0f );
		
		g.setColor( backgroundBarRgb );
		g.drawRect( barX, barY, barWidth, barHeight );
	}
	
	private void debug( final Graphics2D g )
	{
		final FontMetrics fm = g.getFontMetrics();
		final Player player = getPlayer();
		
		final String x = String.valueOf( player.getPX() );
		final String y = String.valueOf( player.getPY() );
		
		final String viewDir = "viewDir: " + player.getViewDir();
		final String moveDir = "moveDir: " + player.getMoveDir();
		
		final String name = "name: " + player.name;
		
		g.drawString( x, getWidth() - fm.stringWidth( x ), getHeight() - fm.getHeight() * 5 );
		g.drawString( y, getWidth() - fm.stringWidth( y ), getHeight() - fm.getHeight() * 4 );
		
		g.drawString( viewDir, getWidth() - fm.stringWidth( viewDir ), getHeight() - fm.getHeight() * 3 );
		g.drawString( moveDir, getWidth() - fm.stringWidth( moveDir ), getHeight() - fm.getHeight() * 2 );
		
		g.drawString( name, getWidth() - fm.stringWidth( name ), getHeight() - fm.getHeight() * 1 );
	}
	
	@ Override
	protected void onPlayerMoved()
	{
		super.onPlayerMoved();
		
		this.room = getCurrentRoom();
		updateBlackout();
		
		level.moveMobs();
	}
	
	private Rectangle getCurrentRoom()
	{
		Rectangle tileRoom = null;
		
		for ( final Rectangle room : rooms )
		{
			final int x = room.x * 2 - 1;
			final int y = room.y * 2 - 1;
			final int width = room.width * 2 + 1;
			final int height = room.height * 2 + 1;
			
			tileRoom = new Rectangle( x, y, width, height );
			
			if ( tileRoom.contains( getPlayer().getX(), getPlayer().getY() ) )
			{
				return tileRoom;
			}
		}
		
		return null;
	}
	
	private void runItemSelectCommand( final int selection )
	{
		final Player player = getPlayer();
		final List<Item> inventory = player.getInventory();
		final int index = State.Inventory.selection + pageIndex * 8;
		
		final Item item = inventory.get( index );
		
		switch ( selection )
		{
			case 0 :
				euip( player, inventory, index, item );
				state = State.Inventory;
				break;
			case 1 :
				// TODO display item info
				break;
			case 2 :
				inventory.remove( item );
				break;
		}
	}
	
	private void euip( final Player player, final List<Item> inventory, final int index, final Item item )
	{
		/**/ if ( item instanceof Food )
		{
			if ( player.getHp() != player.getMaxHp() )
			{
				player.eat( (Food) item );
				inventory.remove( index );
			}
		}
		else if ( item instanceof Weapon )
		{
			if ( player.getWeapon() == null )
			{
				inventory.remove( index );
			}
			else
			{
				inventory.set( index, player.dequipWeapon() );
			}
			
			player.equipWeapon( (Weapon) item );
		}
		else if ( item instanceof Accessoire )
		{
			if ( player.getAccessoire() == null )
			{
				inventory.remove( index );
			}
			else
			{
				inventory.set( index, player.dequipWeapon() );
			}
			
			player.equipAccessoire( (Accessoire) item );
		}
		else if ( item instanceof Armor )
		{
			if ( player.getArmor() == null )
			{
				inventory.remove( index );
			}
			else
			{
				inventory.set( index, player.dequipWeapon() );
			}
			
			player.equipArmour( (Armor) item );
		}
	}
	
	private void runInventoryCommand( final int selection )
	{
		final Player player = getPlayer();
		final List<Item> inventory = player.getInventory();
		final int index = selection + pageIndex * 8;
		
		if ( inventory.size() > index )
		{
			state = State.ItemSelect;
			
			final Item item = inventory.get( index );
			
			if ( item instanceof Food )
			{
				itemSelectButtonLabels[ 0 ] = "Eat";
			}
			else if ( item instanceof Weapon || item instanceof Armor || item instanceof Accessoire )
			{
				itemSelectButtonLabels[ 0 ] = "Equip";
			}
			
			itemSelectButtonLabels[ 1 ] = "Info";
			itemSelectButtonLabels[ 2 ] = "Throw";
		}
	}
	
	private void runMenuCommand( final int selection )
	{
		switch ( selection )
		{
			case 0 :
				state = State.Inventory;
				break;
			case 1 :
				game.gsm.close();
				break;
		}
	}
	
	@ Override
	public void update( final float delta )
	{
		super.update( delta );
		
		for ( final Entity entity : level.getEntities() )
		{
			if ( entity instanceof Mob && getPlayer().isDamaging() )
			{
				( (Mob) entity ).requestAttack();
			}
		}
		
		level.update();
	}
	
	private class PlayKeyListener extends KeyAdapter
	{
		@ Override
		public void keyPressed( final KeyEvent event )
		{
			final Player player = getPlayer();
			
			if ( event.isShiftDown() )
			{
				player.setRunning( true );
			}
		}
		
		@ Override
		public void keyReleased( final KeyEvent event )
		{
			final Player player = getPlayer();
			
			if ( !event.isShiftDown() )
			{
				player.setRunning( false );
			}
			
			switch ( event.getKeyCode() )
			{
				case KeyEvent.VK_SPACE :
					if ( !player.isDamaging() )
					{
						player.requestAttack();
					}
					break;
				case KeyEvent.VK_M :
					state = state == null ? State.Menu : null;
					break;
			}
		}
	}
	
	private class PlayMouseListener extends MouseAdapter
	{
		@ Override
		public void mousePressed( final MouseEvent event )
		{
			if ( event.getButton() == MouseEvent.BUTTON1 )
			{
				switch ( state )
				{
					case Menu :
						runMenuCommand( state.selection );
						break;
					
					case Inventory :
						for ( int i = 0; i < State.Inventory.buttonBounds.length; i++ )
						{
							if ( State.Inventory.buttonBounds[ i ].contains( event.getPoint() ) )
							{
								runInventoryCommand( state.selection );
							}
						}
						
						if ( pageCursorLeft.contains( event.getPoint() ) )
						{
							pageIndex = Math.max( 0, pageIndex - 1 );
						}
						
						if ( pageCursorRight.contains( event.getPoint() ) && getPlayer().getInventory().size() > ( pageIndex + 1 ) * 8 )
						{
							pageIndex++;
						}
						break;
					
					case ItemSelect :
						runItemSelectCommand( state.selection );
						break;
				}
			}
			else if ( event.getButton() == MouseEvent.BUTTON3 )
			{
				state = state.previous();
			}
		}
	}
	
	private class PlayMouseMotionListener extends MouseAdapter
	{
		@ Override
		public void mouseMoved( final MouseEvent event )
		{
			if ( state == null )
			{
				return;
			}
			
			for ( int i = 0; i < state.buttonBounds.length; i++ )
			{
				if ( state.buttonBounds[ i ].contains( event.getPoint() ) )
				{
					state.selection = i;
				}
			}
		}
	}
	
	public void nextFloor()
	{
		floor++;
	}
}