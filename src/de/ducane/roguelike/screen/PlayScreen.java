package de.ducane.roguelike.screen;

import static de.androbin.gfx.util.GraphicsUtil.*;
import de.androbin.json.*;
import de.androbin.rpg.*;
import de.androbin.rpg.entity.*;
import de.androbin.rpg.gfx.*;
import de.androbin.rpg.overlay.*;
import de.androbin.rpg.story.*;
import de.androbin.shell.*;
import de.androbin.shell.input.*;
import de.androbin.thread.*;
import de.ducane.roguelike.*;
import de.ducane.roguelike.entity.*;
import de.ducane.roguelike.gfx.*;
import de.ducane.roguelike.gfx.dark.*;
import de.ducane.roguelike.level.*;
import de.ducane.roguelike.menu.*;
import de.ducane.roguelike.menu.Menu;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.List;

public final class PlayScreen extends RPGScreen<RogueMaster> {
  private int floor;
  
  public final MovingDark dark;
  
  private List<Rectangle> rooms;
  private Rectangle room;
  
  private boolean attack;
  
  private Rectangle2D.Float barBounds;
  
  private final LockedList<Menu> menus;
  private Point2D.Float menuOffset;
  
  public PlayScreen( final String name ) {
    master = new RogueMaster( this::createWorld, new StoryState( new StoryGraph() ) );
    master.addOverlay( new MoveInputOverlay( master::getPlayer ) );
    master.addOverlay( new RogueOverlay() );
    
    menus = new LockedList<>();
    
    scale = 48f;
    
    dark = new MovingDark( new Color( 0f, 0f, 0f, 0.8f ), scale );
    dark.pos = this::getDarkPos;
    dark.width = getWidth();
    dark.height = getHeight();
    
    Renderers.registerEntity( "rogue", new RogueEntityRenderer( dark ) );
    Renderers.registerTile( "rogue", new RogueTileRenderer( dark ) );
    
    master.setPlayer( new Player( Entities.getData( Ident.parse( "rogue/player/Player" ) ),
        name ) );
    master.camera.setFocus( () -> master.getPlayer().getFloatBounds().center() );
    
    updateFloor();
  }
  
  private Level createWorld( final Ident id ) {
    final int floor = Integer.parseInt( id.lastElement().split( "-" )[ 1 ] );
    final int index = Math.min( ( floor / 4 + 1 ), 5 );
    
    final String path = "level/level" + index + ".json";
    final XObject data = XUtil.readJSON( path ).get().asObject();
    
    final LevelGenerator generator = new LevelGenerator();
    return generator.generate( id, data, this );
  }
  
  private Rectangle currentRoom() {
    final Point pos = master.getPlayer().getSpot().getPos();
    Rectangle tileRoom = null;
    
    for ( final Rectangle room : rooms ) {
      final int x = room.x * 2 - 1;
      final int y = room.y * 2 - 1;
      final int width = room.width * 2 + 1;
      final int height = room.height * 2 + 1;
      
      tileRoom = new Rectangle( x, y, width, height );
      
      if ( tileRoom.contains( pos ) ) {
        return tileRoom;
      }
    }
    
    return null;
  }
  
  private Point2D.Float getDarkPos() {
    final Point2D.Float pos = master.getPlayer().getFloatBounds().center();
    return room == null
        ? new Point2D.Float( pos.x, pos.y )
        : new Point2D.Float( room.x, room.y );
  }
  
  public void onPlayerMoved() {
    this.room = currentRoom();
    updateDark();
    
    master.getLevel().moveMobs( master.getPlayer() );
  }
  
  @ Override
  protected void onResized( final int width, final int height ) {
    dark.width = width;
    dark.height = height;
    
    barBounds = new Rectangle2D.Float(
        0.2f * width, 0.075f * height, 0.4f * width, 0.025f * height );
    
    menus.forEach( menu -> menu.onResized( width, height ) );
    
    menuOffset = new Point2D.Float( 0.05f * getWidth(), 0.2f * getHeight() );
  }
  
  @ Override
  public void render( final Graphics2D g ) {
    super.render( g );
    
    dark.darken( g, trans );
    dark.disposeAll();
    
    master.getLevel().miniMap.render( g, master.getPlayer().getFloatPos(), scale, getWidth() );
    
    renderStats( g );
    renderMenus( g );
  }
  
  private void renderMenus( final Graphics2D g ) {
    final AffineTransform savedTransform = g.getTransform();
    g.translate( menuOffset.x, menuOffset.y );
    
    menus.forEach( menu -> {
      menu.render( g, master.getPlayer() );
      
      final Point2D.Float offset = menu.getOffset();
      g.translate( offset.x, offset.y );
    } );
    
    g.setTransform( savedTransform );
  }
  
  private void renderStats( final Graphics2D g ) {
    g.setColor( Color.BLACK );
    fillRect( g, barBounds );
    
    final Player player = master.getPlayer();
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
  
  @ Override
  public void update( final float delta ) {
    if ( !menus.isEmpty() || master.getPlayer().isDead( true ) ) {
      return;
    }
    
    if ( attack ) {
      master.getPlayer().attack.request( true );
      attack = false;
    }
    
    super.update( delta );
    master.getLevel().update();
    
    if ( floor != master.floor ) {
      floor = master.floor;
      updateFloor();
    }
  }
  
  private void updateDark() {
    if ( room == null ) {
      dark.dark = new CircleDark( scale * 1.5f );
    } else {
      dark.dark = new RectDark( scale * room.width, scale * room.height );
    }
    
    master.getLevel().miniMap.update( dark, scale );
  }
  
  public void updateFloor() {
    final Ident id = Ident.parse( "floor-" + floor );
    final Level level = (Level) master.getWorld( id );
    final Player player = master.getPlayer();
    
    if ( master.world != null ) {
      master.world.entities.remove( player );
    }
    
    master.world = level;
    master.world.entities.add( player, level.getUpStairsPos() );
    
    rooms = level.getRooms();
    room = currentRoom();
    updateDark();
  }
  
  private final class RogueOverlay extends AbstractShell
      implements KeyInput, MouseInput, MouseMotionInput, Overlay {
    public RogueOverlay() {
      final Inputs inputs = getInputs();
      inputs.keyboard = this;
      inputs.mouse = this;
      inputs.mouseMotion = this;
    }
    
    @ Override
    public void keyPressed( final int keycode ) {
      if ( keycode == KeyEvent.VK_SHIFT ) {
        master.getPlayer().setRunning( true );
      }
    }
    
    @ Override
    public void keyReleased( final int keycode ) {
      switch ( keycode ) {
        case KeyEvent.VK_SHIFT:
          master.getPlayer().setRunning( false );
          break;
        case KeyEvent.VK_SPACE:
          attack = true;
          break;
        case KeyEvent.VK_M:
          if ( menus.isEmpty() ) {
            final Menu menu = new MainMenu();
            menu.onResized( getWidth(), getHeight() );
            menus.add( menu );
          } else {
            menus.clear();
          }
          
          break;
      }
    }
    
    @ Override
    public void mousePressed( final int x, final int y, final int button ) {
      if ( menus.isEmpty() ) {
        return;
      }
      
      if ( button == MouseEvent.BUTTON1 ) {
        final int index = menus.size() - 1;
        
        final Point2D.Float p = new Point2D.Float( x, y );
        p.x -= menuOffset.x;
        p.y -= menuOffset.y;
        
        for ( int i = 0; i < index; i++ ) {
          final Point2D.Float offset = menus.get( i ).getOffset();
          p.x -= offset.x;
          p.y -= offset.y;
        }
        
        final Menu menu = menus.get( index );
        final Menu next = menu.onClick( p, master.getPlayer() );
        
        if ( next == menu.parent ) {
          menus.remove( menus.size() - 1 );
        } else if ( next != menu ) {
          next.onResized( getWidth(), getHeight() );
          menus.add( next );
        }
      } else if ( button == MouseEvent.BUTTON3 ) {
        menus.remove( menus.size() - 1 );
      }
    }
    
    @ Override
    public void mouseMoved( final int x, final int y ) {
      if ( menus.isEmpty() ) {
        return;
      }
      
      final int index = menus.size() - 1;
      
      final Point2D.Float p = new Point2D.Float( x, y );
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
    
    @ Override
    protected void onResized( final int width, final int height ) {
    }
  }
}