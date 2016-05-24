package de.janmm14.epicpvp.warz.friends;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.base.Joiner;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import lombok.Getter;
import lombok.NonNull;

public class FriendInfoManager {

	private static final int MAXIMUM_CACHE_SIZE = 300;
	private static final Joiner SEMICOLON_JOINER = Joiner.on( ';' );

	@Getter
	@NonNull
	private final FriendModule module;
	private final ExecutorService asyncSaverThread = Executors
		.newSingleThreadScheduledExecutor(
			new ThreadFactoryBuilder()
				.setNameFormat( "FriendInfo Save Thread" ) //append " #%d" when multiple threads
				.build() );
	private final ExecutorService asyncLoaderThread = Executors
		.newSingleThreadScheduledExecutor(
			new ThreadFactoryBuilder()
				.setNameFormat( "FriendInfo Load Thread" ) //append " #%d" when multiple threads
				.build() );
	private PreparedStatement fetchStmt;
	private PreparedStatement saveStmt;
	private boolean disableFlush = false;

	private final LoadingCache<UUID, FriendInfo> friendInfoCache = CacheBuilder.newBuilder()
		.initialCapacity( 128 )
		.maximumSize( MAXIMUM_CACHE_SIZE )
		.removalListener( new RemovalListener<UUID, FriendInfo>() {
			@Override
			@ParametersAreNonnullByDefault
			public void onRemoval(RemovalNotification<UUID, FriendInfo> notification) {
				if ( disableFlush ) {
					return;
				}
				switch ( notification.getCause() ) {
					case EXPIRED:
					case COLLECTED: //<- should not happen, but to be sure
					case EXPLICIT:
					case SIZE: {
						FriendInfo value = notification.getValue();
						if ( value != null && value.isDirty() ) {
							asyncSaverThread.execute( new FlushRunnable( value ) );
						}
						break;
					}
					default:
						break;
				}
			}
		} )
		.build( CacheLoader.from( this::fetch ) );

	public FriendInfoManager(@NonNull FriendModule module) throws SQLException {
		this.module = module;
		createFetchStmt();
		createSaveStmt();
	}

	private void createFetchStmt() throws SQLException {
		//fetchStmt = module.getPlugin().getSql().prepareStatement("SELECT * FROM `mt_main`.`module_friendinfo` WHERE `uuid`=?");
	}

	private void createSaveStmt() throws SQLException {
		/*saveStmt = module.getPlugin().getSql().prepareStatement("INSERT INTO `mt_main`.`module_friendinfo` (`uuid`,`friendWith`,`requestsGot`,`requestsSent`) VALUES (?,?,?,?) " +
			"ON DUPLICATE KEY UPDATE SET `friendWith`=?,`requestsGot`=?,`requestsSent`=? WHERE `uuid`=?");*/
	}

	/**
	 * Waits until all save tasks are stopped or 10 seconds elapsed
	 * (if really 10 seconds elapse, there is an error somewhere)
	 */
	void syncStop() {
		try {
			fetchStmt.close();
		}
		catch ( SQLException e ) {
			e.printStackTrace();
		}
		try {
			asyncSaverThread.awaitTermination( 10, TimeUnit.SECONDS );
		}
		catch ( InterruptedException ex ) {
			ex.printStackTrace();
		}
	}

	/**
	 * If you are planning to use {@link #get(UUID)} after this method, consider using {@link #getIfCached(UUID)}, as its faster
	 *
	 * @param uuid the uuid to get the {@link FriendInfo} from.
	 * @return whether the {@link FriendInfo} of the given uuid is cached currently
	 */
	public boolean isCached(UUID uuid) {
		return friendInfoCache.getIfPresent( uuid ) != null;
	}

	/**
	 * @param uuid the uuid to get the {@link FriendInfo} from.
	 * @return the cache {@link FriendInfo} or null if its not cached
	 */
	@Nullable
	public FriendInfo getIfCached(UUID uuid) {
		return friendInfoCache.getIfPresent( uuid );
	}

	/**
	 * Gets either the cached value or performes a database lookup
	 *
	 * @param uuid the uuid to get the {@link FriendInfo} from.
	 * @return the FriendInfo
	 */
	public FriendInfo get(UUID uuid) {
		return friendInfoCache.getUnchecked( uuid );
	}

	/**
	 * Gets either the cached value or performes a database lookup and then removes it from the cache.
	 *
	 * @param uuid the uuid to get the {@link FriendInfo} from.
	 * @return the FriendInfo
	 */
	public FriendInfo getAndFlush(UUID uuid) {
		FriendInfo friendInfo = get( uuid );
		flush( uuid );
		return friendInfo;
	}

	/**
	 * Removes the {@link FriendInfo} from the cache and saves it asynchroniously to the database
	 *
	 * @param uuid the uuid which {@link FriendInfo} should be removed
	 */
	public void flush(UUID uuid) {
		friendInfoCache.invalidate( uuid ); //this calls our custom removal listener which saves if the FriendInfo is modified
	}

	/**
	 * Removes the {@link FriendInfo} from the cache and does not save it
	 *
	 * @param uuid the uuid which {@link FriendInfo} should be removed
	 */
	public void discard(UUID uuid) {
		disableFlush = true;
		friendInfoCache.invalidate( uuid );
		disableFlush = false;
	}

	/**
	 * Clears the {@link FriendInfo} cache and saves it asynchroniously to the database
	 */
	public void flushAll() {
		friendInfoCache.invalidateAll(); //also saves if needed
	}

	/**
	 * Clears the {@link FriendInfo} cache and does not save it
	 */
	public void discardAll() {
		disableFlush = true;
		friendInfoCache.invalidateAll();
		disableFlush = false;
	}

	/**
	 * Loads the info for the uuid asynchroniously.
	 *
	 * @param uuid the uuid to get the {@link FriendInfo} from.
	 * @return true if the info has to be loaded, false otherwise
	 */
	public boolean loadAsync(UUID uuid) {
		if ( !isCached( uuid ) ) {
			asyncLoaderThread.execute( () -> get( uuid ) );
			return true;
		}
		return false;
	}

	private FriendInfo fetch(UUID uuid) { //TODO implement
		ArrayList<UUID> friendWith = new ArrayList<>(), requestsGot = new ArrayList<>(), requestsSent = new ArrayList<>();
		try {
			if ( fetchStmt == null ) {
				createFetchStmt();
			}
			fetchStmt.clearParameters();
			fetchStmt.setString( 1, uuid.toString() );
			try ( ResultSet rs = fetchStmt.executeQuery() ) {
				if ( rs.next() ) {
					String[] friendWithArray = rs.getString( "friendWith" ).split( ";" );
					convertUuidAndAdd( friendWithArray, friendWith );

					String[] requestsGotArray = rs.getString( "requestsGot" ).split( ";" );
					convertUuidAndAdd( requestsGotArray, requestsGot );

					String[] requestsSentArray = rs.getString( "requestsSent" ).split( ";" );
					convertUuidAndAdd( requestsSentArray, requestsSent );
					return new FriendInfo( uuid, friendWith, requestsGot, requestsSent );
				} else {
					FriendInfo friendInfo = createNew( uuid );
					friendInfo.setDirty();
					asyncSaverThread.execute( new FlushRunnable( friendInfo ) ); //TODO should we save empty friend data in sql?
					return friendInfo;
				}
			}
			catch ( SQLException ex ) {
				module.getPlugin().getLogger().severe( "[FriendModule] Error while fetching friend data for " + uuid );
				ex.printStackTrace();
			}
		}
		catch ( SQLException ex ) {
			module.getPlugin().getLogger().severe( "[FriendModule] Error while fetching friend data for " + uuid );
			ex.printStackTrace();
		}
		return null;
	}

	private void save(FriendInfo pi) {
		UUID uuid = pi.getUuid();

		List<UUID> friendWith = pi.getFriendWith();
		String friendWithStr = SEMICOLON_JOINER.join( friendWith );

		List<UUID> requestsGot = pi.getRequestsGot();
		String requestsGotStr = SEMICOLON_JOINER.join( requestsGot );

		List<UUID> requestsSent = pi.getRequestsSent();
		String requestsSentStr = SEMICOLON_JOINER.join( requestsSent );

		try {
			saveStmt.setString( 1, uuid.toString() );
			saveStmt.setString( 2, friendWithStr );
			saveStmt.setString( 3, requestsGotStr );
			saveStmt.setString( 4, requestsSentStr );
			saveStmt.setString( 5, friendWithStr );
			saveStmt.setString( 6, requestsGotStr );
			saveStmt.setString( 7, requestsSentStr );
			saveStmt.setString( 8, uuid.toString() );
			int affectedRows = saveStmt.executeUpdate();
			if ( affectedRows != 0 && affectedRows != 1 ) {
				module.getPlugin().getLogger().severe( "[FriendModule] Error while saving friend data for " + uuid + " : affected rows is not 1 or 0 but " + affectedRows );
			}
			pi.setDirty( false );
		}
		catch ( SQLException ex ) {
			module.getPlugin().getLogger().severe( "[FriendModule] Error while saving friend data for " + uuid + " :" );
			ex.printStackTrace();
		}
	}

	private static void convertUuidAndAdd(String[] toAdd, ArrayList<UUID> list) {
		list.ensureCapacity( toAdd.length );
		for ( String uuidStr : toAdd ) {
			list.add( UUID.fromString( uuidStr ) );
		}
	}

	private static FriendInfo createNew(UUID uuid) {
		return new FriendInfo( uuid, new ArrayList<>(), new ArrayList<>(), new ArrayList<>() );
	}

	private final class FlushRunnable implements Runnable {

		@NonNull
		private final FriendInfo friendInfo;

		private FlushRunnable(@NonNull FriendInfo friendInfo) {
			this.friendInfo = friendInfo;
		}

		@Override
		public void run() {
			if ( !friendInfo.isDirty() ) {
				return;
			}
			save( friendInfo );
		}
	}
}
