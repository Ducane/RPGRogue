package de.ducane.roguelike;

import static de.ducane.roguelike.Configuration.window_.*;
import static de.ducane.util.FontUtil.*;
import de.androbin.gfx.*;
import de.androbin.screen.*;
import de.androbin.screen.transit.*;
import de.androbin.shell.env.*;
import de.ducane.roguelike.screen.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public final class Main {
  public static final boolean DEBUG = true;
  
  private Main() {
  }
  
  public static void main( final String[] args ) {
    final SmoothScreenManager<AWTTransition> screens = new AWTScreenManager();
    final AWTEnv env = new AWTEnv( screens, FPS );
    env.start( TITLE );
    
    final CustomPane canvas = env.canvas;
    
    SwingUtilities.invokeLater( () -> {
      installFonts();
      
      final JFrame window = new JFrame( TITLE );
      window.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
      window.setResizable( RESIZABLE );
      window.setSize( getSize() );
      window.setLocationRelativeTo( null );
      window.setContentPane( canvas );
      window.setVisible( true );
      
      if ( DEBUG ) {
        screens.call( new PlayScreen( screens, 48f, "Herbert2000" ) );
      } else {
        screens.call( new IntroScreen( screens ) );
      }
      
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