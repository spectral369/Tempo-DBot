package com.tempodbot.main;

import java.util.EnumSet;

import com.tempodbot.listeners.MessageListener;
import com.tempodbot.statics.StaticInfo;

import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

/**
 * 
 * @author spectral369 main class with runner and configuration for this
 *         project!!!
 *
 */
public class Main {

	public static void main(String[] args) {
		try {
			final int cores = Runtime.getRuntime().availableProcessors();
			if (cores <= 1) {
				System.out.println("Available Cores \"" + cores + "\", setting Parallelism Flag");
				System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "1");
			}

			DefaultShardManagerBuilder builder = DefaultShardManagerBuilder
					.createDefault(StaticInfo.DISCORD_TOKEN.getVal());

			/** intents are the privileges of the bot. SAm za miki ud star bisnov **/
			builder.setEnabledIntents(GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_MESSAGES,
					GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.GUILD_EMOJIS);
			builder.setCompression(Compression.NONE);
			builder.setBulkDeleteSplittingEnabled(false);
			builder.disableCache(
					EnumSet.of(CacheFlag.ACTIVITY, CacheFlag.EMOTE, CacheFlag.CLIENT_STATUS, CacheFlag.ONLINE_STATUS));
			builder.enableCache(CacheFlag.VOICE_STATE);
			builder.setMemberCachePolicy(MemberCachePolicy.VOICE.or(MemberCachePolicy.OWNER));
			builder.setChunkingFilter(ChunkingFilter.NONE);
			builder.addEventListeners(new MessageListener());
			builder.setActivity(Activity.playing("No songs in queue!"));
			builder.build();
		} catch (Exception e) {
			System.out.println("Init error " + e.getLocalizedMessage());
		}

	}

}
