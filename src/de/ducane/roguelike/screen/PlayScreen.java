package de.ducane.roguelike.screen;

import static de.androbin.gfx.util.GraphicsUtil.*;
import de.androbin.game.*;
import de.androbin.game.listener.*;
import de.androbin.rpg.*;
import de.androbin.rpg.event.*;
import de.androbin.rpg.event.Event;
import de.androbin.rpg.gfx.*;
import de.androbin.rpg.obj.*;
import de.androbin.rpg.tile.*;
import de.androbin.thread.*;
import de.androbin.util.*;
import de.ducane.roguelike.dark.*;
import de.ducane.roguelike.entity.*;
import de.ducane.roguelike.level.*;
import de.ducane.roguelike.obj.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.List;
import org.json.simple.*;

public final class PlayScreen extends RPGScreen {
  private int floor;
  private int requestedFloor;
  
  public final MovingDark dark;
  
  private List<Rectangle> rooms;
  private Rectangle room;
  
  private boolean attack;
  
  private Rectangle2D.Float barBounds;
  
  private final LockedList<Menu> menus;
  private Point2D.Float menuOffset;
  
  public PlayScreen( final Game game, final float scale, final String name ) {
    super( game, scale );
    
    menus = new LockedList<>();
    
    dark = new MovingDark( new Color( 0f, 0f, 0f, 0.8f ), scale );
    dark.pos = this::getDarkPos;
    dark.width = getWidth();
    dark.height = getHeight();
    
    Tiles.builder = data -> RogueTiles.create( data, dark );
    GameObjects.builder = ( data, pos ) -> RogueObjects.create( data, pos, dark );
    
    Events.BUILDERS.put( "downstairs", args0 -> Event.func( "downstairs", ( master, args1 ) -> {
      final Entity entity = (Entity) args1.get( "entity" );
      final Player player = getPlayer();
      
      if ( entity == player && !player.running ) {
        requestNextFloor();
      }
    } ) );
    
    player = new Player( RogueEntites.getData( "player/Player" ), name );
    camera.setFocus( Camera.focus( player ) );
    
    updateFloor();
  }
  
  @ Override
  public Level createWorld( final String name ) {
    final int floor = Integer.parseInt( name.split( "-" )[ 1 ] );
    final int index = Math.min( ( floor / 4 + 1 ), 5 );
    
    final String path = "level/level" + index + ".json";
    final JSONObject data = (JSONObject) JSONUtil.parseJSON( path ).get();
    
    final LevelGenerator generator = new LevelGenerator();
    return generator.generate( this, name, data );
  }
  
  private Rectangle currentRoom() {
    Rectangle tileRoom = null;
    
    for ( final Rectangle room : rooms ) {
      final int x = room.x * 2 - 1;
      final int y = room.y * 2 - 1;
      final int width = room.width * 2 + 1;
      final int height = room.height * 2 + 1;
      
      tileRoom = new Rectangle( x, y, width, height );
      
      if ( tileRoom.contains( player.getPos() ) ) {
        return tileRoom;
      }
    }
    
    return null;
  }
  
  private Point2D.Float getDarkPos() {
    final Point2D.Float pos = getPlayer().getFloatPos();
    return room == null
        ? new Point2D.Float( pos.x + 0.5f, pos.y + 0.5f )
        : new Point2D.Float( room.x, room.y );
  }
  
  @ Override
  public KeyListener getKeyListener() {
    return new TeeKeyListener( new KeyInput(), super.getKeyListener() );
  }
  
  private Level getLevel() {
    return (Level) world;
  }
  
  @ Override
  public MouseListener getMouseListener() {
    return new MouseInput();
  }
  
  @ Override
  public MouseMotionListener getMouseMotionListener() {
    return new MouseMotionInput();
  }
  
  public Player getPlayer() {
    return (Player) player;
  }
  
  public void onPlayerMoved() {
    this.room = currentRoom();
    updateDark();
    
    getLevel().moveMobs( getPlayer() );
  }
  
  @ Override
  public void onResized( final int width, final int height ) {
    if ( dark != null ) {
      dark.width = width;
      dark.height = height;
    }
    
    barBounds = new Rectangle2D.Float(
        0.2f * width, 0.075f * height, 0.4f * width, 0.025f * height );
    
    for ( final Menu menu : Menu.values() ) {
      menu.onResized( width, height );
    }
    
    menuOffset = new Point2D.Float( 0.05f * getWidth(), 0.2f * getHeight() );
  }
  
  @ Override
  public void render( final Graphics2D g ) {
    super.render( g );
    dark.darken( g, trans );
    
    getLevel().miniMap.render( g, getPlayer().getFloatPos(), scale, getWidth() );
    
    renderStats( g );
    renderMenus( g );
  }
  
  private void renderMenus( final Graphics2D g ) {
    final Point2D.Float pos = new Point2D.Float();
    g.translate( menuOffset.x, menuOffset.y );
    pos.x += menuOffset.x;
    pos.y += menuOffset.y;
    
    menus.forEach( menu -> {
      menu.render( g, getPlayer() );
      
      final Point2D.Float offset = menu.getOffset();
      g.translate( offset.x, offset.y );
      pos.x += offset.x;
      pos.y += offset.y;
    } );
    
    g.translate( -pos.x, -pos.y );
  }
  
  private void renderStats( final Graphics2D g ) {
    g.setColor( Color.BLACK );
    fillRect( g, barBounds );
    
    final Player player = getPlayer();
    final Stats stats = player.getStats();
    
    final float health = (float) stats.hp / stats.maxHp;
    
    final Color color;
    final Color border;
    
    if ( health <= 0.5f ) {
      color = new Color( 0.5f, health, 0f );
      border = new Color( 0.3f, health * 0.6f, 0f );
    } else {
      color = new Color( 1f - health, 0.5f, 0f );
      border = new Color( ( 1f - health ) * 0.6f, 0.3f, 0f );
    }
    
    g.setColor( color );
    fillRect( g, barBounds.x, barBounds.y, barBounds.width * health, barBounds.height );
    
    g.setColor( border );
    g.setStroke( new BasicStroke( 0.002f * getHeight() ) );
    drawRect( g, barBounds );
    
    g.setColor( Color.WHITE );
    g.setFont( new Font( "Determination Mono", 0, (int) ( 0.04f * getHeight() ) ) );
    
    g.drawString( "Lv " + stats.level(), barBounds.x - 0.1f * getWidth(), barBounds.y );
    g.drawString( "E" + floor, barBounds.x - 0.175f * getWidth(), barBounds.y );
    g.drawString( "HP " + stats.hp + "/" + stats.maxHp,
        barBounds.x, barBounds.y - 0.01f * getHeight() );
  }
  
  public void requestNextFloor() {
    requestedFloor = floor + 1;
  }
  
  public void requestPreviousFloor() {
    requestedFloor = Math.max( floor - 1, 0 );
  }
  
  @ Override
  public void update( final float delta ) {
    if ( !menus.isEmpty() ) {
      return;
    }
    
    if ( attack ) {
      getPlayer().attack.request( true );
      attack = false;
    }
    
    super.update( delta );
    getLevel().update();
    
    if ( floor != requestedFloor ) {
      floor = requestedFloor;
      updateFloor();
    }
  }
  
  private void updateDark() {
    if ( room == null ) {
      dark.dark = new CircleDark( scale * 1.5f );
    } else {
      dark.dark = new RectDark( scale * room.width, scale * room.height );
    }
    
    getLevel().miniMap.update( dark, scale );
  }
  
  public void updateFloor() {
    final String name = "floor-" + floor;
    final Level level = (Level) getWorld( name );
    switchWorld( name, level.getUpStairsPos() );
    rooms = level.getRooms();
    room = currentRoom();
    updateDark();
  }
  
  private final class KeyInput extends KeyAdapter {
    @ Override
    public void keyPressed( final KeyEvent event ) {
      final Player player = getPlayer();
      
      if ( event.isShiftDown() ) {
        player.running = true;
      }
    }
    
    @ Override
    public void keyReleased( final KeyEvent event ) {
      final Player player = getPlayer();
      
      if ( !event.isShiftDown() ) {
        player.running = false;
      }
      
      switch ( event.getKeyCode() ) {
        case KeyEvent.VK_SPACE:
          attack = true;
          break;
        case KeyEvent.VK_M:
          if ( menus.isEmpty() ) {
            menus.add( Menu.Main );
          } else {
            menus.clear();
          }
          
          break;
      }
    }
  }
  
  private final class MouseInput extends MouseAdapter {
    @ Override
    public void mousePressed( final MouseEvent event ) {
      if ( menus.isEmpty() ) {
        return;
      }
      
      if ( event.getButton() == MouseEvent.BUTTON1 ) {
        final int index = menus.size() - 1;
        
        final Point2D.Float p = new Point2D.Float( event.getX(), event.getY() );
        p.x -= menuOffset.x;
        p.y -= menuOffset.y;
        
        for ( int i = 0; i < index; i++ ) {
          final Point2D.Float offset = menus.get( i ).getOffset();
          p.x -= offset.x;
          p.y -= offset.y;
        }
        
        final Menu menu = menus.get( index );
        final int code = menu.onClick( p, getPlayer() );
        
        switch ( code ) {
          case -2:
            game.gsm.close();
            break;
          case -1:
            menus.remove( menus.size() - 1 );
            break;
          case 0:
            break;
          default:
            menus.add( Menu.values()[ code - 1 ] );
            break;
        }
      } else if ( event.getButton() == MouseEvent.BUTTON3 ) {
        menus.remove( menus.size() - 1 );
      }
    }
  }
  
  private final class MouseMotionInput extends MouseAdapter {
    @ Override
    public void mouseMoved( final MouseEvent event ) {
      if ( menus.isEmpty() ) {
        return;
      }
      
      final int index = menus.size() - 1;
      
      final Point2D.Float p = new Point2D.Float( event.getX(), event.getY() );
      p.x -= menuOffset.x;
      p.y -= menuOffset.y;
      
      for ( int i = 0; i < index; i++ ) {
        final Point2D.Float offset = menus.get( i ).getOffset();
        p.x -= offset.x;
        p.y -= offset.y;
      }
      
      final Menu menu = menus.get( index );
      menu.onHover( p );
    }
  }
}