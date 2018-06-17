package de.ducane.roguelike;

import static de.ducane.roguelike.Configuration.window_.*;
import static de.ducane.util.FontUtil.*;
import de.androbin.gfx.*;
import de.androbin.rpg.*;
import de.androbin.rpg.entity.*;
import de.androbin.rpg.event.*;
import de.androbin.rpg.gfx.sheet.*;
import de.androbin.rpg.tile.*;
import de.androbin.screen.*;
import de.androbin.screen.transit.*;
import de.androbin.shell.env.*;
import de.ducane.roguelike.entity.*;
import de.ducane.roguelike.event.*;
import de.ducane.roguelike.event.handler.*;
import de.ducane.roguelike.level.*;
import de.ducane.roguelike.phantom.*;
import de.ducane.roguelike.screen.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

public final class Rogue {
  public static boolean debug = false;
  
  private Rogue() {
  }
  
  public static void init() {
    Globals.init( "globals.json" );
    
    Tiles.register( "rogue", RogueTile::new );
    
    Entities.register( "phantom/upstairs", Upstairs::new );
    Entities.register( "rogue/mob", Mob::new );
    
    Entities.registerData( "rogue", RogueEntityData::new );
    
    Sheets.registerEntity( "rogue/mob", new SimpleAgentLayout() );
    Sheets.registerEntity( "rogue/player", new BetterAgentLayout() );
    
    Events.putBuilder( "downstairs", DownstairsEvent.BUILDER );
    
    Events.putHandler( DownstairsEvent.class, new DownstairsEventHandler() );
  }
  
  public static void main( final String[] args )
      throws InvocationTargetException, InterruptedException {
    final List<String> argsList = Arrays.asList( args );
    debug = argsList.contains( "debug" );
    
    final SmoothScreenManager<AWTTransition> screens = new AWTScreenManager();
    final AWTEnv env = new AWTEnv( screens, FPS );
    env.start( TITLE );
    
    init();
    
    final CustomPane canvas = env.canvas;
    
    SwingUtilities.invokeAndWait( () -> {
      installFonts();
      
      final JFrame window = new JFrame( TITLE );
      window.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
      window.setResizable( RESIZABLE );
      window.setSize( getSize() );
      window.setLocationRelativeTo( null );
      window.setContentPane( canvas );
      window.setVisible( true );
      
      window.addWindowListener( new WindowAdapter() {
        @ Override
        public void windowClosing( final WindowEvent event ) {
          screens.setRunning( false );
        }
      } );
      
      canvas.addKeyListener( new KeyAdapter() {
        @ Override
        public void keyReleased( final KeyEvent event ) {
          if ( event.getKeyCode() != KeyEvent.VK_F11 ) {
            return;
          }
          
          final GraphicsDevice graphicsDevice = GraphicsEnvironment
              .getLocalGraphicsEnvironment()
              .getDefaultScreenDevice();
          final boolean fullscreen = graphicsDevice.getFullScreenWindow() == window;
          
          window.dispose();
          graphicsDevice.setFullScreenWindow( fullscreen ? null : window );
          window.setVisible( true );
          
          canvas.requestFocusInWindow();
        }
      } );
    } );
    
    SwingUtilities.invokeAndWait( () -> {
      if ( debug ) {
        screens.call( new PlayScreen( "Kevin" ) );
      } else {
        screens.call( new IntroScreen( screens ) );
      }
    } );
  }
  
  private static Dimension getSize() {
    final Dimension desktopSize = SystemGraphics.getDesktopSize();
    
    final int width = (int) ( desktopSize.getWidth() * SCALE );
    final int height = (int) ( desktopSize.getHeight() * SCALE );
    
    return new Dimension( width, height );
  }
  
  private static void installFonts() {
    final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    
    installFont( ge, "DTM.otf" );
    installFont( ge, "RPG.ttf" );
  }
}