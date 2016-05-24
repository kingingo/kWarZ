package de.janmm14.epicpvp.warz.friends;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import de.janmm14.epicpvp.warz.hooks.UuidNameConverter;
import lombok.NonNull;

public class CommandFriends implements TabExecutor {

	private static final int FRIEND_LIST_PAGE_SIZE = 10;
	private static final Pattern MINUS_PATTERN = Pattern.compile( "-", Pattern.LITERAL );

	@NonNull
	private final FriendModule module;
	private final FriendInfoManager manager;
	private final UuidNameConverter uuidNameConverter; //TODO wait for answer
	private final String prefix = "";

	private final Server server;

	private final Multimap<String, String> subCommands;
	private final List<String> subCommandKeys;

	public CommandFriends(@NonNull FriendModule module) {
		this.module = module;
		manager = module.getFriendInfoManager();
		uuidNameConverter = module.getPlugin().getUuidNameConverter();

		server = module.getPlugin().getServer();

		ImmutableMultimap.Builder<String, String> builder = ImmutableMultimap.builder();
		builder.put( "list", "list" );
		builder.put( "status", "status" );
		builder.put( "annehmen", "accept" );
		builder.put( "ablehnen", "zurückrufen" );
		builder.put( "ablehnen", "revoke" );
		builder.put( "anfragen", "request" );
		builder.put( "hilfe", "help" );
		subCommands = builder.build();

		subCommandKeys = ImmutableList.copyOf( new HashSet<>( subCommands.keys() ) );
	}

	private boolean msg(@NonNull String message, @NonNull Player plr) {
		plr.sendMessage( message );
		return true;
	}

	private boolean sendHelp(@NonNull Player plr) {
		//TODO send help message
		return true;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) //TODO make command handling asnyc
	{
		if ( !( sender instanceof Player ) ) {
			//TODO msg
			return true;
		}
		if ( args.length == 0 ) {
			//TODO send help
			return true;
		}
		Player plr = ( Player ) sender;
		String plrName = plr.getName();
		UUID initiatorUuid = plr.getUniqueId();
		switch ( args[ 0 ].toLowerCase() ) {
			case "list": {
				FriendInfo friendInfo = manager.get( initiatorUuid );
				if ( friendInfo == null ) {
					return msg( "§cFehler bei der Verarbeitung.", plr );
				}
				int rowstart = 0;
				int page = 1;
				if ( args.length > 2 ) {
					if ( !StringUtils.isNumeric( args[ 1 ] ) ) {
						return msg( "§cDas ist keine gültige Zahl!", plr );
					}
					page = Integer.parseInt( args[ 1 ] );
					if ( page < 1 ) {// if 0, rowstart would be negative
						page = 1;
					}
					if ( page > ( friendInfo.getFriendWith().size() / FRIEND_LIST_PAGE_SIZE + 1 ) ) {
						return msg( "§cDas ist keine gültige Seitenzahl!", plr );
					}
					rowstart = ( page - 1 ) * FRIEND_LIST_PAGE_SIZE;
				}
				return sendFriendList( plr, friendInfo.getFriendWith(), rowstart, FRIEND_LIST_PAGE_SIZE, alias, page + 1 );
			}
			case "status": {
				if ( args.length < 2 ) {
					//TODO send help
				}
				if ( args[ 1 ].equalsIgnoreCase( plrName ) ) {
					return msg( prefix + "Ich nehme an, dass du mit dir selbst Frieden hast.", plr );
				}
				UuidNameConverter.Profile targetProfile = uuidNameConverter.getProfileFromInput( args[ 1 ] );
				if ( targetProfile == null ) {
					return msg( prefix + "§cDer Spieler §6" + args[ 1 ] + "§c wurde nicht gefunden.", plr );
				}
				FriendInfo initiator = manager.get( initiatorUuid );

				UUID targetUuid = targetProfile.getUuid();
				if ( PlayerFriendRelation.areFriends( manager, initiator, targetUuid ) ) {
					return msg( "", plr );
				}
				if ( PlayerFriendRelation.isRequestSent( manager, initiator, targetUuid ) ) {
					return msg( "", plr );
				}
				if ( PlayerFriendRelation.isRequestRecieved( manager, initiator, targetUuid ) ) {
					return msg( "", plr );
				}
				return msg( "", plr );
			}
			case "hilfe":
			case "help": {
				return msg( "", plr );
			}
			case "revoke":
			case "ablehnen":
			case "zurückrufen": {
				if ( args.length < 2 ) {
					//TODO send help or error
				}
				if ( args[ 1 ].equalsIgnoreCase( plrName ) ||
					args[ 1 ].equalsIgnoreCase( plr.getUniqueId().toString() ) ||
					args[ 1 ].equalsIgnoreCase( plr.getUniqueId().toString().replace( "-", "" ) ) ) {
					return msg( prefix + "Du hast dir selbst keine Friedensanfrage gesendet.", plr );
				}
				FriendInfo initiator = manager.get( initiatorUuid );
				UuidNameConverter.Profile targetProfile = uuidNameConverter.getProfileFromInput( args[ 1 ] );
				if ( targetProfile == null ) {
					return msg( prefix + "§cDer Spieler §6" + args[ 1 ] + "§c wurde nicht gefunden.", plr );
				}
				UUID targetUuid = targetProfile.getUuid();
				if ( PlayerFriendRelation.isRequestSent( manager, initiator, targetUuid ) ) {
					FriendInfo targetPi = manager.get( targetUuid );

					targetPi.getRequestsGot().remove( initiatorUuid );
					targetPi.setDirty();
					initiator.getRequestsSent().remove( targetUuid );
					initiator.setDirty();

					//TODO send message to plr : eigene friedensanfrage zurückgezogen
					Player targetPlr = server.getPlayer( targetUuid );
					if ( targetPlr != null ) {
						targetPlr.sendMessage( prefix + plrName + "§c hat seine Friedensanfrage zurückgezogen." );
					} else {
						//TODO save message for target player?
					}
					return true;
				}
				if ( PlayerFriendRelation.isRequestRecieved( manager, initiator, targetUuid ) ) {
					FriendInfo targetPi = manager.get( targetUuid );

					targetPi.getRequestsSent().remove( initiatorUuid );
					targetPi.setDirty();
					initiator.getRequestsGot().remove( targetUuid );
					initiator.setDirty();

					//preqrevoked, plr, args[1] //TODO send message
					//preqrevokedbyother, targetProfile.getName(), plrName //TODO send message
				}
				if ( PlayerFriendRelation.areFriends( manager, initiator, targetUuid ) ) {
					FriendInfo targetPi = manager.get( targetUuid );

					targetPi.getFriendWith().remove( initiatorUuid );
					targetPi.setDirty();
					initiator.getFriendWith().remove( targetUuid );
					initiator.setDirty();

					//prevoked, plr, args[1]);
					//prevokedother, targetProfile.getName(), plrName
				}
				plr.sendMessage( prefix + " §cDu bist mit " + targetProfile.getName() + " nicht befreundet und weder er noch du haben eine Freundschaftsanfrage geschickt." );
				return true;
			}
			case "accept":
			case "annehmen": {//TODO implement
				if ( args.length < 2 ) {
					//TODO send help
				}
				if ( args[ 1 ].equalsIgnoreCase( plrName ) ||
					args[ 1 ].equalsIgnoreCase( plr.getUniqueId().toString() ) ||
					args[ 1 ].equalsIgnoreCase( MINUS_PATTERN.matcher( plr.getUniqueId().toString() ).replaceAll( Matcher.quoteReplacement( "" ) ) ) ) {
					return msg( prefix + "Du kannst dir selber keine Friedensanfrage schicken.", plr );
				}
				FriendInfo initiator = manager.get( initiatorUuid );
				UuidNameConverter.Profile targetProfile = uuidNameConverter.getProfileFromInput( args[ 1 ] );
				if ( targetProfile == null ) {
					return msg( prefix + "§cDer Spieler §6" + args[ 1 ] + "§c wurde nicht gefunden.", plr );
				}
				UUID targetUuid = targetProfile.getUuid();
				//TODO go on implement
				return true;
			}
			case "anfragen":
			case "request": {
				//TODO implement
				return true;
			}
			default: {
				//TODO send help
				return true;
			}
		}
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
		if ( !( sender instanceof Player ) ) {
			return ImmutableList.of();
		}
		Player plr = ( Player ) sender;
		if ( args.length == 0 ) { //should not happen; api behaviour not documented enough to remove
			return ImmutableList.of();
		}
		UUID uuid = plr.getUniqueId();
		switch ( args[ 0 ].toLowerCase().trim() ) {
			case "": {
				return subCommandKeys;
			}
			case "accept":
			case "annehmnen": {
				return getTabCompleteMatchesAndGetFriendInfoList( uuid, args, 1, FriendInfo::getRequestsGot );
			}
			case "status": {
				return getTabCompleteMatchesAndGetFriendInfo( uuid, args, 1, friendInfo ->
					Stream.concat(
						Stream.concat(
							Bukkit.getOnlinePlayers().stream().map( Player::getUniqueId ),
							friendInfo.getFriendWith().stream() ),
						Stream.concat(
							friendInfo.getRequestsGot().stream(),
							friendInfo.getRequestsSent().stream() ) ) );
			}
			case "revoke":
			case "ablehnen":
			case "zurückrufen": {
				return getTabCompleteMatchesAndGetFriendInfo( uuid, args, 1, friendInfo ->
					Stream.concat( Stream.concat(
						friendInfo.getFriendWith().stream(),
						friendInfo.getRequestsSent().stream() ),
						friendInfo.getRequestsGot().stream() ) );
			}
			case "request":
			case "anfragen": {
				return getTabCompleteMatchesAndGetFriendInfo( uuid, args, 1, friendInfo ->
					Bukkit.getOnlinePlayers().stream()
						.map( Player::getUniqueId )
						.filter( ( ( Predicate<UUID> ) friendInfo.getFriendWith()::contains ).negate() ) );
			}
			default: {
				if ( args.length == 1 ) {
					String startedSubCmd = args[ 0 ].toLowerCase();

					//look for base subcommand matches; if there is no match for a base subcommand, checking its
					Map<String, Collection<String>> entries = subCommands.asMap();
					List<String> result = new ArrayList<>();

					for ( Map.Entry<String, Collection<String>> entry : entries.entrySet() ) {
						if ( entry.getKey().startsWith( startedSubCmd ) ) { // base subcommand match first
							result.add( entry.getKey() );
						} else {
							//find first matching alias
							Optional<String> first = entry.getValue().stream()
								.filter( subCmdAlias -> subCmdAlias.startsWith( startedSubCmd ) )
								.findFirst();
							if ( first.isPresent() ) {
								result.add( first.get() );
							}
						}
					}
					if ( result.isEmpty() ) { // if nothing matches, just return all base subcommands
						return subCommandKeys;
					}
				}
				return ImmutableList.of();
			}
		}
	}

	private List<String> getTabCompleteMatchesAndGetFriendInfo(UUID uuid, String[] args, int namePos, Function<FriendInfo, Stream<UUID>> uuidProvider) {
		FriendInfo friendInfo = manager.get( uuid );
		if ( friendInfo == null ) {
			return null;
		}
		return getTabCompleteMatches( args, namePos, getNamesFromUuid( uuidProvider.apply( friendInfo ) ) );
	}

	private List<String> getTabCompleteMatchesAndGetFriendInfoList(UUID uuid, String[] args, int namePos, Function<FriendInfo, List<UUID>> uuidProvider) {
		FriendInfo friendInfo = manager.get( uuid );
		if ( friendInfo == null ) {
			return null;
		}
		return getTabCompleteMatches( args, namePos, getNamesFromUuid( uuidProvider.apply( friendInfo ) ) );
	}

	private Stream<String> getNamesFromUuidStream(List<UUID> uuids) {
		return mapStreamUuidsToNames( uuids.stream() );
	}

	private Stream<String> mapStreamUuidsToNames(Stream<UUID> uuidStream) {
		return uuidStream.map( uuidNameConverter::getProfile ).map( UuidNameConverter.Profile::getName );
	}

	private List<String> getNamesFromUuid(List<UUID> uuids) {
		return getNamesFromUuidStream( uuids )
			.collect( Collectors.toList() );
	}

	private List<String> getNamesFromUuid(Stream<UUID> uuids) {
		return mapStreamUuidsToNames( uuids )
			.collect( Collectors.toList() );
	}

	private List<String> getTabCompleteMatches(String[] args, int namePos, Stream<String> names) {
		if ( args.length <= namePos + 1 ) {
			String startedPlayerName = args[ namePos ].toLowerCase();

			return names
				.filter( s -> s.toLowerCase().startsWith( startedPlayerName ) )
				.collect( Collectors.toList() );
		}
		return ImmutableList.of();
	}

	private List<String> getTabCompleteMatches(String[] args, int namePos, List<String> names) {
		return getTabCompleteMatches( args, namePos, names.stream() );
	}

	private String getPlayerStringColoredByOnlineState(UUID uuid) {
		String plrName = uuidNameConverter.getProfile( uuid ).getName();
		if ( Bukkit.getPlayer( uuid ) == null ) {
			return "§4" + plrName;
		}
		return "§a" + plrName;
	}

	private boolean sendFriendList(Player sender, List<UUID> uuids, int rowStart, int perPage, String alias, int nextPage) {
		String toSend = "";
		int max = rowStart + perPage;
		for ( int i = 0; i < max && uuids.size() > i; i++ ) {
			toSend += " > " + getPlayerStringColoredByOnlineState( uuids.get( i ) ) + "\n";
		}
		if ( toSend.isEmpty() ) {
			sender.sendMessage( "§cDiese Seite ist leer" );
		}
		if ( max <= uuids.size() ) {
			sender.sendMessage( "§6Zur nächsten Seite" );//TODO better message
		}
		return msg( toSend, sender );
	}
}
