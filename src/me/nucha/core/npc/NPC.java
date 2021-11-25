package me.nucha.core.npc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_8_R3.scoreboard.CraftScoreboard;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Team;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;

import me.nucha.core.KokuminCore;
import me.nucha.core.reflect.Reflection;
import net.minecraft.server.v1_8_R3.DataWatcher;
import net.minecraft.server.v1_8_R3.DataWatcher.WatchableObject;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.MathHelper;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.MobEffect;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutAnimation;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntity.PacketPlayOutEntityLook;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntity.PacketPlayOutRelEntityMove;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityEffect;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityHeadRotation;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_8_R3.PacketPlayOutRemoveEntityEffect;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardTeam;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import net.minecraft.server.v1_8_R3.PlayerInteractManager;
import net.minecraft.server.v1_8_R3.ScoreboardTeam;
import net.minecraft.server.v1_8_R3.WorldServer;

public class NPC {

	private EntityPlayer entityPlayer;
	private MinecraftServer ms;
	private WorldServer ws;
	private GameProfile gp;
	private PlayerInteractManager pim;
	private Player p;
	private String name;
	private boolean sneaking;
	private boolean sprinting;
	private boolean blocking;
	private Set<String> tags;
	private int noDamageTick;
	private Location location;
	private boolean tab;
	private ItemStack helmet;
	private ItemStack chestplate;
	private ItemStack leggings;
	private ItemStack boots;
	private ItemStack hand;
	private boolean shown;

	public NPC(Player p, String name, UUID uuid, World world) {
		this.p = p;
		this.sneaking = false;
		this.sprinting = false;
		this.name = name;
		this.tags = new HashSet<String>();
		this.noDamageTick = 0;
		if (Bukkit.getPlayer(name) != null) {
			Player base = Bukkit.getPlayer(name);
			this.sneaking = base.isSneaking();
			this.sprinting = base.isSprinting();
		}
		ms = ((CraftServer) Bukkit.getServer()).getServer();
		ws = ((CraftWorld) world).getHandle();
		gp = new GameProfile(uuid, name);
		pim = new PlayerInteractManager(ws);
		entityPlayer = new EntityPlayer(ms, ws, gp, pim);
		Random rnd = new Random();
		entityPlayer.ping = rnd.nextInt(140) + (30 - rnd.nextInt(20));
		this.shown = false;
	}

	public NPC(Player p, String name, String suffix, String prefix, UUID uuid, World world) {
		this.p = p;
		this.sneaking = false;
		this.sprinting = false;
		this.tags = new HashSet<String>();
		this.noDamageTick = 0;
		if (Bukkit.getPlayer(name) != null) {
			Player base = Bukkit.getPlayer(name);
			this.sneaking = base.isSneaking();
			this.sprinting = base.isSprinting();
		}
		MinecraftServer ms = ((CraftServer) Bukkit.getServer()).getServer();
		WorldServer ws = ((CraftWorld) world).getHandle();
		gp = new GameProfile(uuid, name);
		PlayerInteractManager pim = new PlayerInteractManager(((CraftWorld) world).getHandle());
		entityPlayer = new EntityPlayer(ms, ws, gp, pim);
		Random rnd = new Random();
		entityPlayer.ping = rnd.nextInt(140) + (30 - rnd.nextInt(20));
	}

	public Player getPlayer() {
		return p;
	}

	public void setEntityPlayer(Player p) {
		MinecraftServer ms = ((CraftServer) Bukkit.getServer()).getServer();
		WorldServer ws = ((CraftWorld) p.getWorld()).getHandle();
		GameProfile gp = new GameProfile(p.getUniqueId(), p.getName());
		PlayerInteractManager pim = new PlayerInteractManager(((CraftWorld) p.getWorld()).getHandle());
		entityPlayer = new EntityPlayer(ms, ws, gp, pim);
	}

	public void spawn(Location loc, boolean tab) {
		/*if (!p.getWorld().equals(loc.getWorld())) {
			return;
		}*/
		NPCManager.register(this);
		update(loc, tab);
	}

	public void update() {
		update(location, tab);
	}

	public void update(Location loc, boolean tab) {
		this.location = loc;
		if (!p.getWorld().equals(loc.getWorld())) {
			return;
		}
		sendPacket(new PacketPlayOutEntityDestroy(entityPlayer.getId()));
		this.entityPlayer.setPositionRotation(loc.getX(), loc.getY(), loc.getZ(), convertCompressedAngle(loc.getYaw()), convertCompressedAngle(loc.getPitch()));
		this.tab = tab;
		PacketPlayOutNamedEntitySpawn packet = new PacketPlayOutNamedEntitySpawn(entityPlayer);
		/*Reflection.setValue(packet, "a", getId());
		Reflection.setValue(packet, "b", gp.getId());
		Reflection.setValue(packet, "c", MathHelper.floor(loc.getX() * 32.0D));
		Reflection.setValue(packet, "d", MathHelper.floor(loc.getY() * 32.0D));
		Reflection.setValue(packet, "e", MathHelper.floor(loc.getZ() * 32.0D));
		Reflection.setValue(packet, "f", (byte) ((int) (loc.getYaw() * 256.0F / 360.0F)));
		Reflection.setValue(packet, "g", (byte) ((int) (loc.getPitch() * 256.0F / 360.0F)));
		Reflection.setValue(packet, "h", 0);
		DataWatcher w = new DataWatcher(null);
		w.a(6, (float) 20);
		w.a(10, (byte) 127);
		Reflection.setValue(packet, "i", w);*/
		boolean tabbed = false;
		if (Bukkit.getPlayer(entityPlayer.getName()) == null) {
			addToTablist();
			tabbed = true;
		}
		sendPacket(packet);
		teleport(loc, entityPlayer.onGround);
		updateEntityMetaData();
		updateEquipment();
		swing();
		if (tabbed && !tab) {
			Bukkit.getScheduler().runTaskLater(KokuminCore.getInstance(), new BukkitRunnable() {

				@Override
				public void run() {
					if (p != null && p.isOnline()) {
						removeFromTablist();
					}
				}
			}, 100L);
		}
		shown = true;
	}

	public void despawn() {
		sendPacket(new PacketPlayOutEntityDestroy(entityPlayer.getId()));
		shown = false;
	}

	public void destroy() {
		despawn();
		NPCManager.unregister(this);
		if (Bukkit.getPlayer(entityPlayer.getName()) == null) {
			removeFromTablist();
		}
	}

	public void addEffect(PotionEffectType type, int duration, int amplification) {
		MobEffect me = new MobEffect(type.getId(), duration, amplification, false, true);
		sendPacket(new PacketPlayOutEntityEffect(getId(), me));
	}

	public void removeEffect(PotionEffectType type) {
		MobEffect me = new MobEffect(type.getId(), 20);
		sendPacket(new PacketPlayOutRemoveEntityEffect(getId(), me));
	}

	public void swing() {
		PacketPlayOutAnimation packet = new PacketPlayOutAnimation(entityPlayer, (byte) 0);
		sendPacket(packet);
	}

	public void equipArmors(ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots, ItemStack hand) {
		if (helmet != null)
			equipHelmet(helmet);
		if (chestplate != null)
			equipChestplate(chestplate);
		if (leggings != null)
			equipLeggings(leggings);
		if (boots != null)
			equipBoots(boots);
		if (hand != null)
			equipHand(hand);
	}

	public void equipHelmet(ItemStack itemStack) {
		helmet = itemStack;
		net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
		sendPacket(new PacketPlayOutEntityEquipment(getId(), 4, nmsStack));
	}

	public void equipChestplate(ItemStack itemStack) {
		chestplate = itemStack;
		net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
		sendPacket(new PacketPlayOutEntityEquipment(getId(), 3, nmsStack));
	}

	public void equipLeggings(ItemStack itemStack) {
		leggings = itemStack;
		net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
		sendPacket(new PacketPlayOutEntityEquipment(getId(), 2, nmsStack));
	}

	public void equipBoots(ItemStack itemStack) {
		boots = itemStack;
		net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
		sendPacket(new PacketPlayOutEntityEquipment(getId(), 1, nmsStack));
	}

	public void equipHand(ItemStack itemStack) {
		hand = itemStack;
		net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
		sendPacket(new PacketPlayOutEntityEquipment(getId(), 0, nmsStack));
	}

	public void updateEquipment() {
		equipHelmet(helmet);
		equipChestplate(chestplate);
		equipLeggings(leggings);
		equipBoots(boots);
		equipHand(hand);
		/*sendPacket(new PacketPlayOutEntityEquipment(getId(), 4, helmet));
		sendPacket(new PacketPlayOutEntityEquipment(getId(), 3, chestplate));
		sendPacket(new PacketPlayOutEntityEquipment(getId(), 2, leggings));
		sendPacket(new PacketPlayOutEntityEquipment(getId(), 1, boots));
		sendPacket(new PacketPlayOutEntityEquipment(getId(), 0, hand));*/
	}

	public void setSneaking(boolean sneaking) {
		if (sneaking && !isSneaking()) {
			this.sneaking = true;
		} else if (!sneaking && isSneaking()) {
			this.sneaking = false;
		}
		updateEntityMetaData();
	}

	public boolean isSneaking() {
		return this.sneaking;
	}

	public void setSprinting(boolean sprinting) {
		if (sprinting && !isSprinting()) {
			// inEntityAction(EnumPlayerAction.START_SPRINTING);
			this.sprinting = true;
		} else if (!sprinting && isSprinting()) {
			// inEntityAction(EnumPlayerAction.STOP_SPRINTING);
			this.sprinting = false;
		}
		updateEntityMetaData();
	}

	public boolean isSprinting() {
		return this.sprinting;
	}

	public boolean isBlocking() {
		return blocking;
	}

	public void setBlocking(boolean blocking) {
		if (blocking && !isBlocking()) {
			this.blocking = true;
		} else if (!blocking && isBlocking()) {
			this.blocking = false;
		}
		updateEntityMetaData();
	}

	public void updateEntityMetaData() {
		int value = 0;
		if (blocking) {
			value += 16;
		}
		if (sprinting) {
			value += 8;
		}
		if (sneaking) {
			value += 2;
		}
		WatchableObject watchableObject = new WatchableObject(0, 0, (byte) value);
		Reflection.setValue(watchableObject, "d", false);
		List<WatchableObject> listWO = new ArrayList<>();
		listWO.add(watchableObject);
		PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata();
		Reflection.setValue(packet, "a", getId());
		Reflection.setValue(packet, "b", listWO);
		sendPacket(packet);
		DataWatcher watcher = entityPlayer.getDataWatcher();
		watcher.watch(10, (byte) 127);
		PacketPlayOutEntityMetadata packet2 = new PacketPlayOutEntityMetadata();
		Reflection.setValue(packet2, "a", getId());
		Reflection.setValue(packet2, "b", watcher);
		sendPacket(packet2);
	}

	public EntityPlayer getEntityPlayer() {
		return entityPlayer;
	}

	public int getId() {
		return entityPlayer.getBukkitEntity().getEntityId();
	}

	public void addToTablist() {
		PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, entityPlayer);
		sendPacket(packet);
	}

	public void removeFromTablist() {
		PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, entityPlayer);
		sendPacket(packet);
	}

	public void setSkin(Property property) {
		PropertyMap propertyMap = gp.getProperties();
		if (propertyMap == null) {
			throw new IllegalStateException("Profile doesn't contain a property map");
		}
		propertyMap.put("textures", property);
		if (shown) {
			despawn();
		}
		entityPlayer = new EntityPlayer(ms, ws, gp, pim);
		if (shown) {
			update();
		}
	}

	public void teleport(Location loc, boolean onGround) {
		entityPlayer.setPositionRotation(loc.getX(), loc.getY(), loc.getZ(), convertCompressedAngle(loc.getYaw()), convertCompressedAngle(loc.getPitch()));
		PacketPlayOutEntityLook look = new PacketPlayOutEntityLook(getId(), convertCompressedAngle(loc.getYaw()), convertCompressedAngle(loc.getPitch()), true);
		entityPlayer.onGround = onGround;
		PacketPlayOutEntityHeadRotation headRotation = new PacketPlayOutEntityHeadRotation(entityPlayer, convertCompressedAngle(loc.getYaw()));
		PacketPlayOutEntityTeleport teleport = new PacketPlayOutEntityTeleport(entityPlayer);
		// sendPacket(look, teleport, headRotation);
		sendPacket(look);
		sendPacket(headRotation);
		sendPacket(teleport);
	}

	public void move(Location loc) {
		PacketPlayOutEntityLook packet = new PacketPlayOutEntityLook(getId(), convertCompressedAngle(loc.getYaw()), convertCompressedAngle(loc.getPitch()),
				entityPlayer.onGround);
		PacketPlayOutEntityHeadRotation headRotation = new PacketPlayOutEntityHeadRotation();
		PacketPlayOutEntityTeleport teleport = new PacketPlayOutEntityTeleport(entityPlayer);
		Reflection.setValue(headRotation, "a", getId());
		Reflection.setValue(headRotation, "b", convertCompressedAngle(loc.getYaw()));
		entityPlayer.setPositionRotation(loc.getX(), loc.getY(), loc.getZ(), convertCompressedAngle(loc.getYaw()), convertCompressedAngle(loc.getPitch()));
		entityPlayer.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
		sendPacket(packet, headRotation, teleport);
	}

	public void move(float yaw, float pitch, boolean isOnGround) {
		PacketPlayOutEntityLook packet = new PacketPlayOutEntityLook(getId(), convertCompressedAngle(yaw), convertCompressedAngle(pitch), isOnGround);
		PacketPlayOutEntityHeadRotation headRotation = new PacketPlayOutEntityHeadRotation();
		PacketPlayOutEntityTeleport teleport = new PacketPlayOutEntityTeleport(entityPlayer);
		Reflection.setValue(headRotation, "a", getId());
		Reflection.setValue(headRotation, "b", convertCompressedAngle(yaw));
		entityPlayer.setLocation(entityPlayer.locX, entityPlayer.locY, entityPlayer.locZ, yaw, pitch);
		sendPacket(packet, headRotation, teleport);
	}

	public void move(double x, double y, double z, boolean isOnGround) {
		byte rX = (byte) getRelativeLocation(x, entityPlayer.locX);
		byte rY = (byte) getRelativeLocation(y, entityPlayer.locY);
		byte rZ = (byte) getRelativeLocation(z, entityPlayer.locZ);
		PacketPlayOutRelEntityMove packet = new PacketPlayOutRelEntityMove(getId(), rX, rY, rZ, isOnGround);
		PacketPlayOutEntityTeleport teleport = new PacketPlayOutEntityTeleport(entityPlayer);
		entityPlayer.setLocation(x, y, z, entityPlayer.yaw, entityPlayer.pitch);
		sendPacket(packet, teleport);
	}

	public void move(double x, double y, double z, float yaw, float pitch, boolean isOnGround) {
		byte rX = (byte) getRelativeLocation(x, entityPlayer.locX);
		byte rY = (byte) getRelativeLocation(y, entityPlayer.locY);
		byte rZ = (byte) getRelativeLocation(z, entityPlayer.locZ);
		PacketPlayOutRelEntityMoveLook packet = new PacketPlayOutRelEntityMoveLook(getId(), rX, rY, rZ, convertCompressedAngle(yaw),
				convertCompressedAngle(pitch), isOnGround);
		PacketPlayOutEntityHeadRotation headRotation = new PacketPlayOutEntityHeadRotation();
		PacketPlayOutEntityTeleport teleport = new PacketPlayOutEntityTeleport(entityPlayer);
		Reflection.setValue(headRotation, "a", getId());
		Reflection.setValue(headRotation, "b", convertCompressedAngle(yaw));
		entityPlayer.setLocation(x, y, z, yaw, pitch);
		sendPacket(packet, headRotation, teleport);
	}

	public String getName() {
		return name;
	}

	public void sendPacket(Packet packet) {
		if (p == null) {
			return;
		}
		PlayerConnection pc = ((CraftPlayer) p).getHandle().playerConnection;
		pc.sendPacket(packet);
	}

	public void sendPacket(Packet... packet) {
		if (p == null) {
			return;
		}
		PlayerConnection pc = ((CraftPlayer) p).getHandle().playerConnection;
		for (Packet pk : packet) {
			pc.sendPacket(pk);
		}
	}

	public void setLookingPos(Location loc) {
		double x = loc.getX() - entityPlayer.locX;
		double z = loc.getZ() - entityPlayer.locZ;
		double y = loc.getY() - entityPlayer.locY;
		double helper = MathHelper.sqrt(x * x + z * z);

		float newYaw = (float) ((Math.toDegrees(-Math.atan(x / z))));
		float newPitch = (float) -Math.toDegrees(Math.atan(y / helper));

		if (z < 0 && x < 0) {
			newYaw = (float) (90D + Math.toDegrees(Math.atan(z / x)));
		} else if (z < 0 && x > 0) {
			newYaw = (float) (-90D + Math.toDegrees(Math.atan(z / x)));
		}

		move(entityPlayer.locX, entityPlayer.locY, entityPlayer.locZ, newYaw, newPitch, entityPlayer.onGround);
	}

	private byte convertCompressedAngle(float value) {
		return (byte) MathHelper.floor((value * 256.0F) / 360.0F);
	}

	private double getRelativeLocation(double current, double previous) {
		return (current - previous);
	}

	public int getFixLocation(double pos) {
		return MathHelper.floor(pos * 32.0D);
	}

	public byte getFixRotation(float yawpitch) {
		return (byte) ((int) (yawpitch * 256.0F / 360.0F));
	}

	public void damageEffect() {
		if (noDamageTick == 0) {
			/*PacketPlayOutEntityStatus packet1 = new PacketPlayOutEntityStatus(entityPlayer, EntityEffect.HURT.getData());
			((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet1);
			Location l = p.getLocation();
			double x = l.getX();
			double y = l.getY();
			double z = l.getZ();
			PacketPlayOutNamedSoundEffect packet2 = new PacketPlayOutNamedSoundEffect(CraftSound.getSound(Sound.HURT_FLESH), x, y, z, 1f, 1f);
			noDamageTick = 10;
			((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet2);*/
		}
	}

	public void setNameTagVisibility(boolean flag) {
		Team team = p.getScoreboard().getTeam("HideTagForNPCs");
		if (team == null) {
			team = p.getScoreboard().registerNewTeam("HideTagForNPCs");
			team.setNameTagVisibility(NameTagVisibility.NEVER);
		}
		net.minecraft.server.v1_8_R3.Scoreboard nmsSb = ((CraftScoreboard) p.getScoreboard()).getHandle();
		ScoreboardTeam nmsSbTeam = new ScoreboardTeam(nmsSb, "HideTagForNPCs");
		if (flag) {
			if (team.hasPlayer(entityPlayer.getBukkitEntity())) {
				team.removePlayer(entityPlayer.getBukkitEntity());
			}
			PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam(nmsSbTeam, team.getEntries(), 4); // 4 = remove player
			sendPacket(packet);
		} else {
			if (!team.hasPlayer(entityPlayer.getBukkitEntity())) {
				team.addPlayer(entityPlayer.getBukkitEntity());
			}
			PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam(nmsSbTeam, team.getEntries(), 3); // 3 = add player
			sendPacket(packet);
		}
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

	public void tick() {
		if (noDamageTick > 0) {
			noDamageTick--;
		}
	}

	public Location getLocation() {
		return location;
	}

	public boolean isOnTab() {
		return tab;
	}

	public boolean isShown() {
		return shown;
	}

	/*private byte getFixRotation(float yawpitch) {
		return (byte) ((int) (yawpitch * 256.0F / 360.0F));
	}*/
}
