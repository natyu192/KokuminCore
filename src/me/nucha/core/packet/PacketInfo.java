package me.nucha.core.packet;

import org.bukkit.entity.Player;

import me.nucha.core.reflect.Reflection;
import net.minecraft.server.v1_8_R3.Packet;

public class PacketInfo {

	private Player player;
	private Packet packet;
	private boolean cancelled;

	public PacketInfo(Player player, Packet packet) {
		this.player = player;
		this.packet = packet;
		this.cancelled = false;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public Player getPlayer() {
		return player;
	}

	public Packet getPacket() {
		return packet;
	}

	public void setPacket(Packet packet) {
		this.packet = packet;
	}

	public String getPacketName() {
		return packet.getClass().getSimpleName();
	}

	public Object getPacketValue(String variableName) {
		return Reflection.getValue(packet, variableName);
	}

	public void setPacketValue(String variableName, Object value) {
		Reflection.setValue(packet, variableName, value);
	}

}
