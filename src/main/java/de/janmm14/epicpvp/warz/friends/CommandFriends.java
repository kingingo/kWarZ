package de.janmm14.epicpvp.warz.friends;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import de.janmm14.epicpvp.warz.hooks.UserDataConverter;
import eu.epicpvp.datenserver.definitions.dataserver.player.LanguageType;
import eu.epicpvp.kcore.Command.CommandHandler.Sender;
import eu.epicpvp.kcore.Translation.TranslationHandler;
import eu.epicpvp.kcore.kConfig.kConfig;
import gnu.trove.TIntCollection;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.set.TIntSet;
import lombok.NonNull;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import static de.janmm14.epicpvp.warz.util.GnuTroveJavaAdapter.stream;
import static de.janmm14.epicpvp.warz.util.MiscUtil.not;

public class CommandFriends implements TabExecutor {

	private static final int FRIEND_LIST_PAGE_SIZE = 10;
	private static final Pattern MINUS_PATTERN = Pattern.compile( "-", Pattern.LITERAL );

	@NonNull
	private final FriendModule module;
	private final FriendInfoManager manager;
	private final UserDataConverter userDataConverter;

	private final Server server;

	private final Multimap<String, String> subCommands;
	private final List<String> subCommandKeys;

	public CommandFriends(@NonNull FriendModule module) {
		this.module = module;
		manager = module.getFriendInfoManager();
		userDataConverter = module.getPlugin().getUserDataConverter();

		server = module.getPlugin().getServer();

		ImmutableMultimap.Builder<String, String> builder = ImmutableMultimap.builder();
		builder.put( "list", "list" );
		builder.put( "status", "status" );
		builder.put( "annehmen", "accept" );

		builder.put( "beenden", "entfernen" );
		builder.put( "beenden", "stop" );
		builder.put( "beenden", "remove" );
		builder.put( "ablehnen", "deny" );
		builder.put( "zurückrufen", "revoke" );

		builder.put( "anfragen", "add" );
		builder.put( "anfragen", "request" );
		builder.put( "hilfe", "help" );
		subCommands = builder.build();

		subCommandKeys = ImmutableList.copyOf( new HashSet<>( subCommands.keys() ) );
	}

	private static boolean msg(@NonNull CommandSender sender, @NonNull String message) {
		sender.sendMessage( message );
		return true;
	}

	private static boolean sendHelp(@NonNull Player plr) {
		if ( TranslationHandler.getLanguage( plr ) == LanguageType.GERMAN ) {
			msg( plr, "§7▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬§6 Friend-Menü§7 ▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬" );
			msg( plr, "§6 /friend list §8- §6Listet alle Freunde auf" );
			msg( plr, "§6 /friend status <name> §8 - §6Zeigt dir deinen Freundschaft Status" );
			msg( plr, "§6 /friend beenden <name> §8- §6Zum entfernen von freunden" );
			msg( plr, "§6 /friend annehmen <name> §8- §6Zum akzeptieren von anfragen" );
			msg( plr, "§6 /friend ablehnen <name> §8- §6Zum ablehnen von anfragen" );
			msg( plr, "§6 /friend zurückrufen <name>§8 - §6Zum zurückziehen von anfragen" );
			msg( plr, "§6 /friend anfragen <name> §8- §6Um eine anfrage zusenden" );
			msg( plr, "§7▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬§6 Friend-Menü§7 ▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬" );
		} else {
			msg( plr, "§7▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬§6 Friend-Menu§7 ▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬" );
			msg( plr, "§6 /friend list §8- §6Show all your friends " );
			msg( plr, "§6 /friend status <name> §8 -§6 Shows you if somebody is friend with you" );
			msg( plr, "§6 /friend remove <name> §8-§6 Remove somebody from your friendlist" );
			msg( plr, "§6 /friend accept <name> §8- §6Accept a friend request" );
			msg( plr, "§6 /friend deny <name> §8- §6Deny a friend request" );
			msg( plr, "§6 /friend revoke <name>§8 - §6Withdraw your friendrequest to somebody" );
			msg( plr, "§6 /friend request <name> §8- §6Send a friendrequest to somebody" );
			msg( plr, "§7▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬§6 Friend-Menu§7 ▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬" );
		}
		return true;
	}

	@Override
	@SuppressWarnings("deprecation")
	@eu.epicpvp.kcore.Command.CommandHandler.Command(command = "friend", alias = { "freunde", "friede", "frieden", "freund" }, sender = Sender.PLAYER)
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
		if ( !( sender instanceof Player ) ) {
			msg( sender, "§cDu musst ein Spieler sein!" );
			return false;
		}
		Player plr = ( Player ) sender;
		if ( args.length == 0 ) {
			sendHelp( plr );
			return false;
		}
		String plrName = plr.getName();
		UUID initiatorUuid = plr.getUniqueId();
		switch ( args[ 0 ].toLowerCase() ) {
			case "list": {
				FriendInfo initiatorInfo = manager.get( initiatorUuid );
				if ( initiatorInfo == null ) {
					msg( plr, TranslationHandler.getPrefixAndText( plr, "WARZ_CMD_FRIEND_WORK_FAIL" ) );
					return false;
				}
				int rowstart = 0;
				int page = 1;
				if ( args.length > 2 ) {
					if ( !StringUtils.isNumeric( args[ 1 ] ) ) {
						msg( plr, TranslationHandler.getPrefixAndText( plr, "BG_INTEGER" ) );
						return false;
					}
					page = Integer.parseInt( args[ 1 ] );
					if ( page < 1 ) {// if 0, rowstart would be negative
						page = 1;
					}
					if ( page > ( initiatorInfo.getFriendWith().size() / FRIEND_LIST_PAGE_SIZE + 1 ) ) {
						msg( plr, TranslationHandler.getPrefixAndText( plr, "WARZ_CMD_FRIEND_PAGEAMOUNT_FALSE" ) );
						return false;
					}
					rowstart = ( page - 1 ) * FRIEND_LIST_PAGE_SIZE;
				}
				sendFriendList( plr, initiatorInfo.getFriendWith(), rowstart, FRIEND_LIST_PAGE_SIZE, alias, page + 1 );
				return false;
			}
			case "status": {
				if ( args.length < 2 ) {
					//TODO send help
					msg( plr, "§a/friend " + args[ 0 ] + " <name>" );
					return false;
				}
				if ( args[ 1 ].equalsIgnoreCase( plrName ) || args[ 1 ].equalsIgnoreCase( initiatorUuid.toString() ) || args[ 1 ].equalsIgnoreCase( initiatorUuid.toString().replace( "-", "" ) ) ) {
					msg( plr, module.getPrefix() + TranslationHandler.getPrefixAndText( plr, "WARZ_CMD_FRIEND_SELF_PEACE" ) );
					return false;
				}
				UserDataConverter.Profile targetProfile = userDataConverter.getProfileFromInput( args[ 1 ] );
				if ( targetProfile == null ) {
					msg( plr, module.getPrefix() + TranslationHandler.getPrefixAndText( plr, "WARZ_CMD_FRIEND_PLAYER_NOTFOUND", args[ 1 ] ) );
					return false;
				}
				FriendInfo initiator = manager.get( initiatorUuid );

				int targetPlayerId = targetProfile.getPlayerId();
				if ( PlayerFriendRelation.areFriends( manager, initiator, targetPlayerId ) ) {
					msg( plr, TranslationHandler.getPrefixAndText( plr, "WARZ_CMD_FRIEND_FRIENDSHIP", targetProfile.getName() ) );
					return false;
				}
				if ( PlayerFriendRelation.isRequestSent( manager, initiator, targetPlayerId ) ) {
					msg( plr, TranslationHandler.getPrefixAndText( plr, "WARZ_CMD_FRIEND_SEND_FRIENDSHIP", targetProfile.getName() ) );
					return false;
				}
				if ( PlayerFriendRelation.isRequestRecieved( manager, initiator, targetPlayerId ) ) {
					msg( plr, TranslationHandler.getPrefixAndText( plr, "WARZ_CMD_FRIEND_RECEIVE_FRIENDSHIP", targetProfile.getName() ) );
					return false;
				}
				msg( plr, TranslationHandler.getPrefixAndText( plr, "WARZ_CMD_FRIEND_NOT_FRIENDSHIP", targetProfile.getName() ) );
				return false;
			}
			case "hilfe":
			case "help": {
				sendHelp( plr );
				return false;
			}
			case "beenden":
			case "entfernen":
			case "stop":
			case "remove":
			case "ablehnen":
			case "deny":
			case "zurückrufen":
			case "revoke": {
				if ( args.length < 2 ) {
					msg( plr, "§a/friend " + args[ 0 ] + " <name>" );
					return false;
				}
				if ( args[ 1 ].equalsIgnoreCase( plrName )
					|| args[ 1 ].equalsIgnoreCase( initiatorUuid.toString() )
					|| args[ 1 ].equalsIgnoreCase( initiatorUuid.toString().replace( "-", "" ) ) ) {
					msg( plr, module.getPrefix() + TranslationHandler.getPrefixAndText( plr, "WARZ_CMD_FRIEND_SEND_SELF_FRIENDSHIP" ) );
					return false;
				}
				FriendInfo initiator = manager.get( initiatorUuid );
				UserDataConverter.Profile targetProfile = userDataConverter.getProfileFromInput( args[ 1 ] );
				if ( targetProfile == null ) {
					msg( plr, module.getPrefix() + TranslationHandler.getPrefixAndText( plr, "WARZ_CMD_FRIEND_PLAYER_NOTFOUND", args[ 1 ] ) );
					return false;
				}
				int targetPlayerId = targetProfile.getPlayerId();
				if ( PlayerFriendRelation.areFriends( manager, initiator, targetPlayerId ) ) {
					FriendInfo targetInfo = manager.get( targetPlayerId );

					module.getPlugin().getServer().getScheduler().runTaskAsynchronously( module.getPlugin(), new Runnable() {

						@Override
						public void run() {
							int time = 31;
							while ( true ) {
								FriendInfo initiator = manager.get( initiatorUuid );
								FriendInfo targetInfo = manager.get( targetPlayerId );
								time--;
								if ( time <= 0 ) {

									targetInfo.getFriendWith().remove( initiator.getPlayerId() );
									targetInfo.setDirty();
									initiator.getFriendWith().remove( targetPlayerId );
									initiator.setDirty();

									msg( plr, module.getPrefix() + TranslationHandler.getPrefixAndText( plr, "WARZ_CMD_FRIEND_DISSOLVE_FRIENDSHIP", targetProfile.getName() ) );
									Player targetPlr = targetInfo.getPlayer();
									if ( targetPlr != null ) {
										msg( targetPlr, module.getPrefix() + TranslationHandler.getPrefixAndText( targetPlr, "WARZ_CMD_FRIEND_DISSOLVE_FROM_FRIENDSHIP", plrName ) );
									} else {
										targetInfo.getNotifyFriendshipEnded().add( initiator.getPlayerId() );
										targetInfo.setDirty();
									}

									return;
								} else if ( time == 30 || time == 20 || time == 10 || time <= 5 ) {
									Player targetPlr = targetInfo.getPlayer();
									if ( targetPlr != null ) {
										msg( targetPlr, module.getPrefix() + TranslationHandler.getPrefixAndText( targetPlr, "WARZ_CMD_FRIEND_DISSOLVE_IN_FRIENDSHIP", plrName, time ) );
									}
									Player initiatorPlr = initiator.getPlayer();
									if ( initiatorPlr != null ) {
										msg( initiatorPlr, module.getPrefix() + TranslationHandler.getPrefixAndText( targetPlr, "WARZ_CMD_FRIEND_DISSOLVE_IN_FRIENDSHIP", targetInfo.getPlayerId(), time ) );
									}
								}
								try {
									Thread.sleep( 1000 );
								}
								catch ( InterruptedException e ) {
									e.printStackTrace();
								}
							}
						}
					} );
					return false;
				}
				if ( PlayerFriendRelation.isRequestSent( manager, initiator, targetPlayerId ) ) {
					FriendInfo targetInfo = manager.get( targetPlayerId );

					targetInfo.getRequestsGot().remove( initiator.getPlayerId() );
					targetInfo.setDirty();
					initiator.getRequestsSent().remove( targetPlayerId );
					initiator.setDirty();

					msg( plr, module.getPrefix() + TranslationHandler.getPrefixAndText( plr, "WARZ_CMD_FRIEND_CANCEL_FRIENDSHIP_REQUEST", targetProfile.getName() ) );
					Player targetPlr = server.getPlayer( targetProfile.getUuid() );
					if ( targetPlr != null ) {
						msg( targetPlr, module.getPrefix() + TranslationHandler.getPrefixAndText( targetPlr, "WARZ_CMD_FRIEND_CANCEL_FROM_FRIENDSHIP_REQUEST", plrName ) );
						return false;
					}
					return false;
				}
				if ( PlayerFriendRelation.isRequestRecieved( manager, initiator, targetPlayerId ) ) {
					FriendInfo targetInfo = manager.get( targetPlayerId );

					targetInfo.getRequestsSent().remove( initiator.getPlayerId() );
					targetInfo.setDirty();
					initiator.getRequestsGot().remove( targetPlayerId );
					initiator.setDirty();

					msg( plr, module.getPrefix() + TranslationHandler.getPrefixAndText( plr, "WARZ_CMD_FRIEND_REJECT_FRIENDSHIP_REQUEST", targetProfile.getName() ) );
					Player targetPlr = server.getPlayer( targetProfile.getUuid() );
					if ( targetPlr != null ) {
						msg( targetPlr, module.getPrefix() + TranslationHandler.getPrefixAndText( plr, "WARZ_CMD_FRIEND_REJECT_FROM_FRIENDSHIP_REQUEST", plrName ) );
						return false;
					} else {
						targetInfo.getNotifyRequestDenied().add( initiator.getPlayerId() );
						targetInfo.setDirty();
					}
					return false;
				}
				msg( plr, module.getPrefix() + TranslationHandler.getPrefixAndText( plr, "WARZ_CMD_FRIEND_FRIENDSHIP_REQUEST_NOT", targetProfile.getName() ) );
				return false;
			}
			case "accept":
			case "annehmen": {
				if ( args.length < 2 ) {
					msg( plr, "§a/friend " + args[ 0 ] + " <name>" );
					return false;
				}
				if ( args[ 1 ].equalsIgnoreCase( plrName )
					|| args[ 1 ].equalsIgnoreCase( initiatorUuid.toString() )
					|| args[ 1 ].equalsIgnoreCase( initiatorUuid.toString().replace( "-", "" ) ) ) {
					msg( plr, module.getPrefix() + TranslationHandler.getPrefixAndText( plr, "WARZ_CMD_FRIEND_ARE_FRIENDS" ) );
					return false;
				}
				FriendInfo initiatorInfo = manager.get( initiatorUuid );
				UserDataConverter.Profile targetProfile = userDataConverter.getProfileFromInput( args[ 1 ] );
				if ( targetProfile == null ) {
					msg( plr, module.getPrefix() + TranslationHandler.getPrefixAndText( plr, "WARZ_CMD_FRIEND_PLAYER_NOTFOUND", args[ 1 ] ) );
					return false;
				}

				int targetPlayerId = targetProfile.getPlayerId();
				FriendInfo targetInfo = manager.get( targetPlayerId );

				if ( PlayerFriendRelation.areFriends( manager, initiatorInfo, targetProfile.getPlayerId() ) ) {
					msg( plr, TranslationHandler.getPrefixAndText( plr, "WARZ_CMD_FRIEND_FRIENDSHIP", targetProfile.getName() ) );
					return false;
				}
				if ( !PlayerFriendRelation.isRequestRecieved( manager, initiatorInfo, targetProfile.getPlayerId() ) ) {
					msg( plr, TranslationHandler.getPrefixAndText( plr, "WARZ_CMD_FRIEND_FRIENDSHIP_REQUEST_NOT_EXIST", targetProfile.getName() ) );
					return false;
				}

				initiatorInfo.getRequestsGot().remove( targetPlayerId );
				initiatorInfo.getRequestsSent().remove( targetPlayerId );
				initiatorInfo.getFriendWith().add( targetPlayerId );
				initiatorInfo.setDirty();

				targetInfo.getRequestsSent().remove( initiatorInfo.getPlayerId() );
				targetInfo.getRequestsGot().remove( initiatorInfo.getPlayerId() );
				targetInfo.getFriendWith().add( initiatorInfo.getPlayerId() );
				targetInfo.setDirty();

				msg( plr, module.getPrefix() + TranslationHandler.getPrefixAndText( plr, "WARZ_CMD_FRIEND_ACCEPT_FRIENDSHIP", targetProfile.getName() ) );

				Player targetPlr = targetInfo.getPlayer();
				if ( targetPlr != null ) {
					msg( targetPlr, module.getPrefix() + TranslationHandler.getPrefixAndText( targetPlr, "WARZ_CMD_FRIEND_ACCEPT_FROM_FRIENDSHIP", plrName ) );
					return false;
				} else {
					targetInfo.getNotifyRequestAccepted().add( initiatorInfo.getPlayerId() );
					targetInfo.setDirty();
				}
				return false;
			}
			case "add":
			case "anfragen":
			case "request": {
				if ( args.length < 2 ) {
					msg( plr, "§a/friend " + args[ 0 ] + " [Player]" );
					return false;
				}
				if ( args[ 1 ].equalsIgnoreCase( plrName )
					|| args[ 1 ].equalsIgnoreCase( initiatorUuid.toString() )
					|| args[ 1 ].equalsIgnoreCase( initiatorUuid.toString().replace( "-", "" ) ) ) {
					msg( plr, module.getPrefix() + TranslationHandler.getPrefixAndText( plr, "WARZ_CMD_FRIEND_SEND_SELF_FRIENDSHIP" ) );
					return false;
				}
				FriendInfo initiatorInfo = manager.get( initiatorUuid );
				UserDataConverter.Profile targetProfile = userDataConverter.getProfileFromInput( args[ 1 ] );
				if ( targetProfile == null ) {
					msg( plr, module.getPrefix() + TranslationHandler.getPrefixAndText( plr, "WARZ_CMD_FRIEND_PLAYER_NOTFOUND", args[ 1 ] ) );
					return false;
				}

				if ( PlayerFriendRelation.areFriends( manager, initiatorInfo, targetProfile.getPlayerId() ) ) {
					msg( plr, TranslationHandler.getPrefixAndText( plr, "WARZ_CMD_FRIEND_FRIENDSHIP", targetProfile.getName() ) );
					return false;
				}
				if ( PlayerFriendRelation.isRequestSent( manager, initiatorInfo, targetProfile.getPlayerId() ) ) {
					msg( plr, TranslationHandler.getPrefixAndText( plr, "WARZ_CMD_FRIEND_HAVE_SENT_FRIENDSHIP", targetProfile.getName() ) );
					return false;
				}
				if ( PlayerFriendRelation.isRequestRecieved( manager, initiatorInfo, targetProfile.getPlayerId() ) ) {
					msg( plr, TranslationHandler.getPrefixAndText( plr, "WARZ_CMD_FRIEND_HAVE_GOT_FRIENDSHIP", targetProfile.getName(), alias ) );
					return false;
				}

				int targetPlayerId = targetProfile.getPlayerId();
				FriendInfo targetInfo = manager.get( targetPlayerId );
				initiatorInfo.getRequestsSent().add( targetPlayerId );
				initiatorInfo.setDirty();
				targetInfo.getRequestsGot().add( initiatorInfo.getPlayerId() );
				targetInfo.setDirty();

				msg( plr, TranslationHandler.getPrefixAndText( plr, "WARZ_CMD_FRIEND_SEND_FRIENDSHIP", targetProfile.getName() ) );

				Player targetPlr = targetInfo.getPlayer();
				if ( targetPlr != null ) {
					msg( targetPlr, TranslationHandler.getPrefixAndText( targetPlr, "WARZ_CMD_FRIEND_RECEIVE_FRIENDSHIP", plrName ) );
				}
				return false;
			}
			case "clearallrequests":
				if ( !sender.isOp() ) {
					msg( plr, TranslationHandler.getPrefixAndText( plr, "WARZ_CMD_UNKNOWN", args[ 0 ] ) );
					sendHelp( plr );
					return false;
				}
				module.getPlugin().getServer().getScheduler().runTaskAsynchronously( module.getPlugin(), () -> {
					File[] userdatas = new File( module.getPlugin().getDataFolder(), "userdata" ).listFiles();
					if ( userdatas == null ) {
						sender.sendMessage( "Could not list files in dir." );
						return;
					}
					int amount = userdatas.length;
					sender.sendMessage( "Found " + amount + " userdata configs. Start clearing all friend requests" );
					for ( File userdata : userdatas ) {
						kConfig config = new kConfig( userdata );
						config.set( "shop.delivery.requestsGot", null );
						config.set( "shop.delivery.requestsSent", null );
						config.save();
					}
					sender.sendMessage( "Done!" );
				} );
				return false;
			default: {
				msg( plr, TranslationHandler.getPrefixAndText( plr, "WARZ_CMD_UNKNOWN", args[ 0 ] ) );
				sendHelp( plr );
				return false;
			}
		}
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
		if (true)
			return null;
		if ( !( sender instanceof Player ) ) {
			return ImmutableList.of();
		}
		Player plr = ( Player ) sender;
		if ( args.length == 0 ) { //should not happen; api behaviour not documented enough to remove
			return ImmutableList.of();
		}
		UserDataConverter.Profile profile = userDataConverter.getProfile( plr );
		int playerId = profile.getPlayerId();
		switch ( args[ 0 ].toLowerCase().trim() ) {
			case "": {
				return subCommandKeys;
			}
			case "accept":
			case "annehmnen": {
				return getTabCompleteMatchesAndGetFriendInfoSet( playerId, args, 1, FriendInfo::getRequestsGot );
			}
			case "status": {
				return getTabCompleteMatchesAndGetFriendInfo( playerId, args, 1, friendInfo -> {
					return IntStream.concat(
						IntStream.concat(
							Bukkit.getOnlinePlayers().stream().mapToInt( p -> userDataConverter.getProfile( p ).getPlayerId() ),
							stream( friendInfo.getFriendWith() ) ),
						IntStream.concat(
							stream( friendInfo.getRequestsGot() ),
							stream( friendInfo.getRequestsSent() ) ) );
				} );
			}
			case "beenden":
			case "entfernen":
			case "remove": {
				return getTabCompleteMatchesAndGetFriendInfoSet( playerId, args, 1, FriendInfo::getFriendWith );
			}
			case "stop": {
				return getTabCompleteMatchesAndGetFriendInfo( playerId, args, 1, friendInfo -> {
					return IntStream.concat(
						stream( friendInfo.getFriendWith() ),
						stream( friendInfo.getRequestsSent() ) );
				} );
			}
			case "ablehnen":
			case "deny": {
				return getTabCompleteMatchesAndGetFriendInfoSet( playerId, args, 1, FriendInfo::getRequestsGot );
			}
			case "zurückrufen":
			case "revoke": {
				return getTabCompleteMatchesAndGetFriendInfoSet( playerId, args, 1, FriendInfo::getRequestsSent );
			}
			case "request":
			case "anfragen": {
				return getTabCompleteMatchesAndGetFriendInfo( playerId, args, 1, friendInfo -> {
					return Bukkit.getOnlinePlayers().stream()
						.mapToInt( p -> userDataConverter.getProfile( p ).getPlayerId() )
						.filter( not( friendInfo.getFriendWith()::contains ) );
				} );
			}
			default: {
				if ( args.length == 1 ) {
					String startedSubCmd = args[ 0 ].toLowerCase();

					//look for base subcommand matches; if there is no match for a base subcommand, checking its aliases
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

	private List<String> getTabCompleteMatchesAndGetFriendInfo(int uuid, String[] args, int namePos, Function<FriendInfo, IntStream> playerIdProvider) {
		FriendInfo friendInfo = manager.get( uuid );
		if ( friendInfo == null ) {
			return null;
		}
		return getTabCompleteMatches( args, namePos, getNamesFromUuid( playerIdProvider.apply( friendInfo ) ) );
	}

	private List<String> getTabCompleteMatchesAndGetFriendInfoSet(int uuid, String[] args, int namePos, Function<FriendInfo, TIntSet> playerIdProvider) {
		FriendInfo friendInfo = manager.get( uuid );
		if ( friendInfo == null ) {
			return null;
		}
		return getTabCompleteMatches( args, namePos, getNamesFromUuid( playerIdProvider.apply( friendInfo ) ) );
	}

	private Stream<String> getNamesFromUuidStream(TIntCollection uuids) {
		return mapStreamUuidsToNames( stream( uuids ) );
	}

	private Stream<String> mapStreamUuidsToNames(IntStream uuidStream) {
		return uuidStream.mapToObj( userDataConverter::getProfile ).map( UserDataConverter.Profile::getName );
	}

	private List<String> getNamesFromUuid(TIntCollection uuids) {
		return getNamesFromUuidStream( uuids )
			.collect( Collectors.toList() );
	}

	private List<String> getNamesFromUuid(IntStream uuids) {
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

	private String getPlayerStringColoredByOnlineState(int playerId) {
		UserDataConverter.Profile profile = userDataConverter.getProfile( playerId );
		String name = profile.getName();
		if ( profile.isOnline() ) {
			return "§4" + name;
		} else {
			return "§a" + name;
		}
	}

	private boolean sendFriendList(Player sender, TIntSet uuids, int rowStart, int perPage, String alias, int nextPage) {
		String toSend = "";
		int max = rowStart + perPage;
		TIntList uuidsList = new TIntArrayList( uuids );
		for ( int i = rowStart; i < max && uuidsList.size() > i; i++ ) {
			toSend += " > " + getPlayerStringColoredByOnlineState( uuidsList.get( i ) ) + "\n";
		}
		if ( toSend.isEmpty() ) {
			msg( sender, TranslationHandler.getPrefixAndText( sender, "WARZ_CMD_FRIEND_PAGE_EMPTY" ) );
		}
		if ( max <= uuids.size() ) {
			msg( sender, TranslationHandler.getPrefixAndText( sender, "WARZ_CMD_FRIEND_PAGE_NEXT" ) );
		}
		return msg( sender, toSend );
	}
}
