package me.nucha.core.hologram;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_8_R3.PlayerConnection;

public class Hologram {

	private Player player;
	private Set<String> tags;
	private List<String> texts;
	private Location location;
	private List<EntityArmorStand> nmsArmorStands;
	private boolean shown;

	public Hologram(Player player, Location location, String... texts) {
		this.player = player;
		this.tags = new HashSet<>();
		this.location = location.clone().add(0, -2.1, 0);
		this.texts = Arrays.asList(texts);
		this.nmsArmorStands = Lists.newArrayList();
		this.shown = false;
	}

	public Location getLocation() {
		return location;
	}

	public List<String> getTexts() {
		return texts;
	}

	public Player getPlayer() {
		return player;
	}

	public void display() {
		update();
		HologramManager.registerHologram(this);
	}

	public void despawn() {
		if (player == null) {
			return;
		}
		for (EntityArmorStand entityArmorStand : nmsArmorStands) {
			PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(entityArmorStand.getId());
			sendPacket(packet);
		}
		nmsArmorStands.clear();
		shown = false;
	}

	public void remove() {
		HologramManager.unregisterHologram(this);
		despawn();
	}

	public Set<String> getTags() {
		return tags;
	}

	public void addTag(String tag) {
		tags.add(tag);
	}

	public void removeTag(String tag) {
		tags.remove(tag);
	}

	public void clearTags() {
		tags.clear();
	}

	public boolean hasTag(String tag) {
		return tags.contains(tag);
	}

	public void update() {
		update(location);
	}

	public void update(Location location) {
		if (!player.getWorld().equals(location.getWorld())) {
			return;
		}
		this.location = location;
		for (EntityArmorStand entityArmorStand : nmsArmorStands) {
			PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(entityArmorStand.getId());
			sendPacket(packet);
		}
		nmsArmorStands.clear();
		double y = 0;
		double offsetY = 0.4;
		location = location.clone().add(0, offsetY * texts.size(), 0);
		for (String text : texts) {
			EntityArmorStand armorStand = new EntityArmorStand(((CraftWorld) location.getWorld()).getHandle());
			PacketPlayOutSpawnEntityLiving packetSpawn = new PacketPlayOutSpawnEntityLiving(armorStand);
			armorStand.setPosition(location.getX(), location.getY() + y, location.getZ());
			armorStand.setInvisible(true);
			armorStand.setCustomName(text);
			armorStand.setCustomNameVisible(true);
			armorStand.setGravity(false);
			PacketPlayOutEntityTeleport packetTeleport = new PacketPlayOutEntityTeleport(armorStand);
			// sendPacket(packetTeleport, packetSpawn);
			sendPacket(packetSpawn, packetTeleport);
			y -= offsetY;
			nmsArmorStands.add(armorStand);
		}
		shown = true;
	}

	public boolean isShown() {
		return shown;
	}

	public void sendPacket(Packet packet) {
		if (player == null) {
			return;
		}
		PlayerConnection pc = ((CraftPlayer) player).getHandle().playerConnection;
		pc.sendPacket(packet);
	}

	public void sendPacket(Packet... packet) {
		if (player == null) {
			return;
		}
		PlayerConnection pc = ((CraftPlayer) player).getHandle().playerConnection;
		for (Packet pk : packet) {
			pc.sendPacket(pk);
		}
	}

}
