package de.janmm14.epicpvp.warz.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import eu.epicpvp.kcore.Util.UtilTime;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ConfigLocationAdapter {

	private final FileConfiguration config;

	private Location stringToLocation(String loc) {
		String[] s = loc.split( ";" );
		Location l = new Location( Bukkit.getWorld( s[ 5 ] ), Double.valueOf( s[ 0 ] ), Double.valueOf( s[ 1 ] ), Double.valueOf( s[ 2 ] ) );
		l.setYaw( Float.valueOf( s[ 3 ] ) );
		l.setPitch( Float.valueOf( s[ 4 ] ) );
		return l;
	}

	private String locationToString(Location loc) {
		return loc.getX() + ";" + loc.getY() + ";" + loc.getZ() + ";" + loc.getYaw() + ";" + loc.getPitch() + ";" + loc.getWorld().getName();
	}

	public List<Location> getLocationList(String path) {
		List<String> locStrings = config.getStringList( path );
		ArrayList<Location> locs = new ArrayList<>( locStrings.size() );
		for ( String locStr : locStrings ) {
			locs.add( stringToLocation( locStr ) );
		}
		return locs;
	}

	public void setLocationList(String path, ArrayList<Location> locs) {
		ArrayList<String> s = new ArrayList<>();

		for ( Location loc : locs ) {
			s.add( locationToString( loc ) );
		}
		config.set( path, s );
	}

	public void setLocationList(String path, Location[] locs) {
		ArrayList<String> s = new ArrayList<>();

		for ( Location loc : locs ) {
			s.add( locationToString( loc ) );
		}
		config.set( path, s );
	}

	public void setLocation(String path, Location location) {
		config.set( path + ".world", location.getWorld().getName() );
		config.set( path + ".x", location.getX() );
		config.set( path + ".y", location.getY() );
		config.set( path + ".z", location.getZ() );
		config.set( path + ".pitch", location.getPitch() );
		config.set( path + ".yaw", location.getYaw() );
		config.set( path + ".Date", UtilTime.now() );
	}

	public Location getLocation(String path) {
		String world = config.getString( path + ".world" );
		if ( Bukkit.getWorld( world ) != null ) {
			Location loc = new Location( Bukkit.getWorld( world ), config.getDouble( path + ".x" ), config.getDouble( path + ".y" ), config.getDouble( path + ".z" ) );
			loc.setYaw( Float.parseFloat( config.getString( path + ".yaw" ) ) );
			loc.setPitch( Float.parseFloat( config.getString( path + ".pitch" ) ) );
			return loc;
		}
		return null;
	}
}
