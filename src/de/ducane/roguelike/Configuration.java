package de.ducane.roguelike;

import static de.androbin.util.JSONUtil.*;
import java.awt.*;
import org.json.simple.*;

public class Configuration
{
	private static final JSONObject CONFIG = (JSONObject) parseJSON( "config.json" );
	
	public static final class window_
	{
		private static final JSONObject	CONFIG_WINDOW	= (JSONObject) CONFIG.get( "window" );
		
		public static final float		SCALE			= ( (Number) CONFIG_WINDOW.get( "scale" ) ).floatValue();
		public static final boolean		RESIZABLE		= (boolean) CONFIG_WINDOW.get( "resizable" );
		public static final int			FPS				= ( (Number) CONFIG_WINDOW.get( "fps" ) ).intValue();
		public static final String		TITLE			= (String) CONFIG_WINDOW.get( "title" );
	}
	
	public static final class gui_
	{
		private static final JSONObject	CONFIG_GUI			= (JSONObject) CONFIG.get( "gui" );
		
		public static final float		CROSSFADE_SWITCH	= ( (Number) CONFIG_GUI.get( "crossfade_switch" ) ).floatValue();
		
		public static final class menu_
		{
			private static final JSONObject	CONFIG_MENU							= (JSONObject) CONFIG_GUI.get( "menu" );
			
			/*
			 * Variablen f�r das Rendering des Backgrounds.
			 */
			public static final Color		BACKGROUND_COLOR					= Color.decode( (String) CONFIG_MENU.get( "background_color" ) );
			
			public static final Color		BACKGROUND_DECO_COLOR				= Color.decode( (String) CONFIG_MENU.get( "background_deco_color" ) );
			public static final float		BACKGROUND_DECO_Y_OFFSET			= ( (Number) CONFIG_MENU.get( "background_deco_y_offset" ) ).floatValue();
			
			/*
			 * Variablen f�r das Rendering der Buttons.
			 */
			public static final String[]	BUTTON_LABELS						= toStringArray( CONFIG_MENU.get( "button_labels" ) );
			public static final Color		BUTTON_COLOR_LABEL					= Color.decode( ( (String) CONFIG_MENU.get( "button_color_label" ) ) );
			
			public static final Color		BUTTON_COLOR_BORDER					= Color.decode( (String) CONFIG_MENU.get( "button_color_border" ) );
			
			public static final float		BUTTON_X							= ( (Number) CONFIG_MENU.get( "button_x" ) ).floatValue();
			public static final float		BUTTON_Y							= ( (Number) CONFIG_MENU.get( "button_y" ) ).floatValue();
			public static final float		BUTTON_DY							= ( (Number) CONFIG_MENU.get( "button_dy" ) ).floatValue();
			public static final float		BUTTON_WIDTH						= ( (Number) CONFIG_MENU.get( "button_width" ) ).floatValue();
			public static final float		BUTTON_HEIGHT						= ( (Number) CONFIG_MENU.get( "button_height" ) ).floatValue();
			
			public static final String		BUTTON_FONT_NAME					= (String) CONFIG_MENU.get( "button_font_name" );
			public static final int			BUTTON_FONT_STYLE					= ( (Number) CONFIG_MENU.get( "button_font_style" ) ).intValue();
			public static final float		BUTTON_FONT_SIZE					= ( (Number) CONFIG_MENU.get( "button_font_size" ) ).floatValue();
			
			public static final float		BUTTON_BORDER_THICKNESS				= ( (Number) CONFIG_MENU.get( "button_border_thickness" ) ).floatValue();
			public static final float		BUTTON_BORDER_THICKNESS_SELECTED	= ( (Number) CONFIG_MENU.get( "button_border_thickness_selected" ) ).floatValue();
			
			/*
			 * Variablen f�r das Rendering des Cursors.
			 */
			
			public static final float		CURSOR_SPEED						= ( (Number) CONFIG_MENU.get( "cursor_speed" ) ).floatValue();
			
			public static final Color		CURSOR_COLOR						= Color.decode( (String) CONFIG_MENU.get( "cursor_color" ) );
			public static final Color		CURSOR_COLOR_BORDER					= Color.decode( (String) CONFIG_MENU.get( "cursor_color_border" ) );
			
			public static final float		CURSOR_BORDER_THICKNESS				= ( (Number) CONFIG_MENU.get( "cursor_border_thickness" ) ).floatValue();
			
			/*
			 * Zeit, nach welcher zum IntroScreen gewechselt wird.
			 */
			public static final int			RETURN_TIME							= ( (Number) CONFIG_MENU.get( "return_time" ) ).intValue();
			
			/*
			 * Variablen f�r das Rendering des Titels.
			 */
			public static final float		TITLE_X								= ( (Number) CONFIG_MENU.get( "title_x" ) ).floatValue();
			public static final float		TITLE_Y								= ( (Number) CONFIG_MENU.get( "title_y" ) ).floatValue();
			public static final float		TITLE_WIDTH							= ( (Number) CONFIG_MENU.get( "title_width" ) ).floatValue();
			public static final float		TITLE_HEIGHT						= ( (Number) CONFIG_MENU.get( "title_height" ) ).floatValue();
		}
		
		public static final class intro_
		{
			private static final JSONObject	CONFIG_INTRO		= (JSONObject) CONFIG_GUI.get( "intro" );
			
			public static final Color		BACKGROUND_COLOR	= Color.decode( (String) CONFIG_INTRO.get( "background_color" ) );
			public static final Color		FONT_COLOR			= Color.decode( (String) CONFIG_INTRO.get( "font_color" ) );
			
			@ SuppressWarnings( "unchecked" )
			public static final String[]	TEXT				= (String[]) ( (JSONArray) CONFIG_INTRO.get( "text" ) ).toArray( new String[ 0 ] );
			
			public static final float		TEXT_SPEED			= ( (Number) CONFIG_INTRO.get( "char_speed" ) ).floatValue();
			public static final float		IMAGE_SPEED			= ( (Number) CONFIG_INTRO.get( "image_speed" ) ).floatValue();
			
			public static final float		TEXT_DX				= ( (Number) CONFIG_INTRO.get( "text_dx" ) ).floatValue();
			
			public static final String		TEXT_FONT_NAME		= (String) CONFIG_INTRO.get( "text_font_name" );
			public static final int			TEXT_FONT_STYLE		= ( (Number) CONFIG_INTRO.get( "text_font_style" ) ).intValue();
			public static final float		TEXT_FONT_SIZE		= ( (Number) CONFIG_INTRO.get( "text_font_size" ) ).floatValue();
		}
	}
	
	public static final class level_
	{
		private static final JSONObject CONFIG_LEVEL = (JSONObject) CONFIG.get( "level" );
		
		public static final class tile_
		{
			private static final JSONObject	CONFIG_TILE	= (JSONObject) CONFIG_LEVEL.get( "tile" );
			
			public static final int			TILE_SIZE	= ( (Number) CONFIG_TILE.get( "tile_size" ) ).intValue();
		}
	}
}