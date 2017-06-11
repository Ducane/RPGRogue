package de.ducane.roguelike;

import static de.ducane.roguelike.Configuration.window_.*;
import static de.ducane.util.FontUtil.*;
import de.androbin.game.*;
import de.androbin.gfx.*;
import de.ducane.roguelike.screen.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public final class Main {
  public static final boolean DEBUG = false;
  
  private Main() {
  }
  
  public static void main( final String[] args ) {
    SwingUtilities.invokeLater( () -> {
      installFonts();
      
      final Game game = new Game();
      
      final JFrame window = new JFrame( TITLE );
      window.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
      window.setResizable( RESIZABLE );
      window.setSize( getSize() );
      window.setLocationRelativeTo( null );
      window.setContentPane( game );
      
      window.addWindowListener( new WindowAdapter() {
        @ Override
        public void windowClosing( final WindowEvent event ) {
          game.stop();
        }
      } );
      
      window.setVisible( true );
      
      game.start();
      game.gsm.call( DEBUG ? new PlayScreen( game, 48f, "Herbert2000" ) : new IntroScreen( game ) );
      
      game.addKeyListener( new KeyAdapter() {
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
          
          game.requestFocusInWindow();
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