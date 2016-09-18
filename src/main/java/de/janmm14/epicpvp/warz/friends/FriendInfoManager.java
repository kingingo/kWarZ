package de.janmm14.epicpvp.warz.friends;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;

import com.google.common.base.Joiner;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import eu.epicpvp.kcore.kConfig.kConfig;
import gnu.trove.set.TIntSet;

import de.janmm14.epicpvp.warz.hooks.UserDataConverter;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.NonNull;

import static de.janmm14.epicpvp.warz.util.GnuTroveJavaAdapter.toJava;
import static de.janmm14.epicpvp.warz.util.GnuTroveJavaAdapter.toTSet;

public class FriendInfoManager {

	private static final Joiner SEMICOLON_JOINER = Joiner.on( ';' );

	@Getter
	@NonNull
	private final FriendModule module;
	private final UserDataConverter userDataConverter;
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
	private boolean disableFlush = false;

	private final LoadingCache<Integer, FriendInfo> friendInfoCache = CacheBuilder.newBuilder()
		.initialCapacity( 128 )
		.concurrencyLevel( 2 )
		.expireAfterAccess( 15, TimeUnit.MINUTES )
		.removalListener( new RemovalListener<Integer, FriendInfo>() {
			@Override
			public void onRemoval(@NotNull RemovalNotification<Integer, FriendInfo> notification) {
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
							new FlushRunnable( value ).run();
//							asyncSaverThread.execute( new FlushRunnable( value ) );
						}
						break;
					}
					default:
						break;
				}
			}
		} )
		.build( CacheLoader.from( this::fetch ) );

	public FriendInfoManager(@NonNull FriendModule module) {
		this.module = module;
		userDataConverter = module.getPlugin().getUserDataConverter();
	}

	/**
	 * Waits until all save tasks are stopped or 10 seconds elapsed
	 * (if really 10 seconds elapse, there is an error somewhere)
	 */
	void syncStop() {
		try {
			asyncSaverThread.awaitTermination( 10, TimeUnit.SECONDS );
		}
		catch ( InterruptedException ex ) {
			ex.printStackTrace();
		}
	}

	/**
	 * If you are planning to use {@link #get(int)} after this method, consider using {@link #getIfCached(int)}, as its faster
	 *
	 * @param playerId the playerId to get the {@link FriendInfo} from.
	 * @return whether the {@link FriendInfo} of the given playerId is cached currently
	 */
	public boolean isCached(int playerId) {
		return friendInfoCache.getIfPresent( playerId ) != null;
	}

	/**
	 * @param playerId the playerId to get the {@link FriendInfo} from.
	 * @return the cache {@link FriendInfo} or null if its not cached
	 */
	@Nullable
	public FriendInfo getIfCached(int playerId) {
		return friendInfoCache.getIfPresent( playerId );
	}

	/**
	 * Gets either the cached value or performes a database lookup
	 *
	 * @param playerId the uuid to get the {@link FriendInfo} from.
	 * @return the FriendInfo
	 */
	public FriendInfo get(int playerId) {
		return friendInfoCache.getUnchecked( playerId );
	}

	/**
	 * Gets either the cached value or performes a database lookup
	 *
	 * @param uuid the uuid to get the {@link FriendInfo} from.
	 * @return the FriendInfo
	 */
	public FriendInfo get(UUID uuid) {
		return friendInfoCache.getUnchecked( userDataConverter.getProfile( uuid ).getPlayerId() );
	}

	/**
	 * Gets either the cached value or performes a database lookup and then removes it from the cache.
	 *
	 * @param playerId the playerId to get the {@link FriendInfo} from.
	 * @return the FriendInfo
	 */
	public FriendInfo getAndFlush(int playerId) {
		FriendInfo friendInfo = get( playerId );
		flush( playerId );
		return friendInfo;
	}

	/**
	 * Removes the {@link FriendInfo} from the cache and saves it asynchroniously to the database
	 *
	 * @param playerId the playerId which {@link FriendInfo} should be removed
	 */
	public void flush(int playerId) {
		friendInfoCache.invalidate( playerId ); //this calls our custom removal listener which saves if the FriendInfo is modified
	}

	/**
	 * Removes the {@link FriendInfo} from the cache and does not save it
	 *
	 * @param playerId the playerId which {@link FriendInfo} should be removed
	 */
	public void discard(int playerId) {
		disableFlush = true;
		friendInfoCache.invalidate( playerId );
		disableFlush = false;
	}

	/**
	 * Clears the {@link FriendInfo} cache and saves it asynchroniously
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
	 * @param playerId the playerId to get the {@link FriendInfo} from.
	 * @return true if the info has to be loaded, false otherwise
	 */
	public boolean loadAsync(int playerId) {
		if ( !isCached( playerId ) ) {
			asyncLoaderThread.execute( () -> get( playerId ) );
			return true;
		}
		return false;
	}

	private FriendInfo fetch(int playerId) {
		kConfig cfg = getModule().getPlugin().getUserDataConfig().getConfig( playerId );

		TIntSet friendWith = toTSet( cfg.getIntegerList( "friendWith" ) );
		TIntSet requestsGot = toTSet( cfg.getIntegerList( "requestsGot" ) );
		TIntSet requestsSent = toTSet( cfg.getIntegerList( "requestsSent" ) );
		TIntSet notifyFriendshipEnded = toTSet( cfg.getIntegerList( "notifyFriendshipEnded" ) );
		TIntSet notifyRequestDenied = toTSet( cfg.getIntegerList( "notifyRequestDenied" ) );
		TIntSet notifyRequestAccepted = toTSet( cfg.getIntegerList( "notifyRequestAccepted" ) );

		return new FriendInfo( this, playerId, friendWith, requestsGot, requestsSent, notifyFriendshipEnded, notifyRequestDenied, notifyRequestAccepted, cfg );
	}

	private void save(FriendInfo friendInfo) {
		kConfig cfg = friendInfo.getConfig();

		if(cfg!=null){
			cfg.set( "friendWith", toJava( friendInfo.getFriendWith() ) );
			cfg.set( "requestsGot", toJava( friendInfo.getRequestsGot() ) );
			cfg.set( "requestsSent", toJava( friendInfo.getRequestsSent() ) );

			getModule().getPlugin().getUserDataConfig().saveConfig( friendInfo.getPlayerId() );

			friendInfo.setDirty( false );
		}else{
			System.err.println("FriendInfo Config from "+friendInfo.getPlayerId()+" is NULL!!!!!!");
		}
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
