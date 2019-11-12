package me.nucha.core.packet;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import me.nucha.core.KokuminCore;
import net.minecraft.server.v1_8_R3.Packet;

public class PacketHandler implements Listener {

	private static List<PacketListener> packetListeners;

	public static void init() {
		packetListeners = new ArrayList<PacketListener>();
		KokuminCore plugin = KokuminCore.getInstance();
		plugin.getServer().getPluginManager().registerEvents(getInstance(), plugin);
	}

	public static PacketHandler getInstance() {
		return new PacketHandler();
	}

	@EventHandler
	public void onLogin(PlayerJoinEvent event) {
		registerPlayer(event.getPlayer());
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		unregisterPlayer(event.getPlayer());
	}

	public static void registerPlayer(Player p) {
		ChannelDuplexHandler channelDuplexHandler = new ChannelDuplexHandler() {

			@Override
			public void channelRead(ChannelHandlerContext channelHandlerContext, Object packet) throws Exception {
				PacketInfo packetInfo = new PacketInfo(p, (Packet) packet);
				// Bukkit.getServer().getConsoleSender().sendMessage("§a" + packet.toString() + " (" + packetInfo.getPacketName() + ")");
				for (PacketListener packetListener : packetListeners) {
					packetListener.playerSendPacket(packetInfo);
				}
				if (packetInfo.isCancelled()) {
					return;
				}
				super.channelRead(channelHandlerContext, packetInfo.getPacket());
			}

			@Override
			public void write(ChannelHandlerContext channelHandlerContext, Object packet, ChannelPromise arg2) throws Exception {
				PacketInfo packetInfo = new PacketInfo(p, (Packet) packet);
				// Bukkit.getServer().getConsoleSender().sendMessage("§c" + packet.toString() + " (" + packetInfo.getPacketName() + ")");
				for (PacketListener packetListener : packetListeners) {
					packetListener.playerReceivePacket(packetInfo);
				}
				if (packetInfo.isCancelled()) {
					return;
				}
				super.write(channelHandlerContext, packetInfo.getPacket(), arg2);
			}
		};
		ChannelPipeline pipeline = ((CraftPlayer) p).getHandle().playerConnection.networkManager.channel.pipeline();
		pipeline.addBefore("packet_handler", "NPCDisplayPlugin_" + p.getName(), channelDuplexHandler);
	}

	public static void unregisterPlayer(Player p) {
		Channel channel = ((CraftPlayer) p).getHandle().playerConnection.networkManager.channel;
		channel.eventLoop().submit(() -> {
			channel.pipeline().remove(p.getName());
			return null;
		});
	}

	public static void registerPacketListener(PacketListener packetListener) {
		packetListeners.add(packetListener);
	}

	public static void unregisterPacketListener(PacketListener packetListener) {
		packetListeners.remove(packetListener);
	}

	public static void unregisterAllPacketListeners() {
		packetListeners.clear();
	}

	public static List<PacketListener> getPacketListeners() {
		return packetListeners;
	}

}
