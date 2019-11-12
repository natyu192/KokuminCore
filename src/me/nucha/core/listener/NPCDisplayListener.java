package me.nucha.core.listener;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Lists;

import me.nucha.core.KokuminCore;
import me.nucha.core.hologram.Hologram;
import me.nucha.core.hologram.HologramManager;
import me.nucha.core.npc.NPC;
import me.nucha.core.npc.NPCManager;
import me.nucha.core.packet.PacketInfo;
import me.nucha.core.packet.PacketListener;
import net.minecraft.server.v1_8_R3.PacketPlayOutMapChunk;

public class NPCDisplayListener implements PacketListener {

	@Override
	public void playerReceivePacket(PacketInfo packet) {
		if (packet.getPacket() instanceof PacketPlayOutMapChunk) {
			int ax = (int) packet.getPacketValue("a");
			int az = (int) packet.getPacketValue("b");
			for (NPC npc : Lists.newArrayList(NPCManager.getLivingNPCs())) {
				if (packet.getPlayer().getUniqueId().equals(npc.getPlayer().getUniqueId())) {
					Chunk bc = npc.getLocation().getChunk();
					double bx = bc.getX();
					double bz = bc.getZ();
					if (ax == bx && az == bz) {
						Bukkit.getScheduler().runTask(KokuminCore.getInstance(), new BukkitRunnable() {
							public void run() {
								npc.update();
								// npc.getPlayer().sendMessage(("§a[SUCCESS]update NPC (" + ax + ", " + az + "), (" + bx + ", " + bz +
								// ")"));
							}
						});
					}
				}
			}
			for (Hologram hologram : Lists.newArrayList(HologramManager.getHolograms())) {
				if (packet.getPlayer().getUniqueId().equals(hologram.getPlayer().getUniqueId())) {
					Chunk bc = hologram.getLocation().getChunk();
					double bx = bc.getX();
					double bz = bc.getZ();
					if (ax == bx && az == bz) {
						Bukkit.getScheduler().runTask(KokuminCore.getInstance(), new BukkitRunnable() {
							public void run() {
								hologram.update();
								// hologram.getPlayer()
								// .sendMessage(("§a[SUCCESS]update Hologram (" + ax + ", " + az + "), (" + bx + ", " + bz + ")"));
							}
						});
					}
				}
			}
		}
	}

	@Override
	public void playerSendPacket(PacketInfo packet) {
	}

	/*@EventHandler
	public void onChunkLoad(ChunkLoadEvent event) {
		Chunk ac = event.getChunk();
		int ax = ac.getX();
		int az = ac.getZ();
		for (NPC npc : NPCManager.getLivingNPCs()) {
			Chunk bc = npc.getLocation().getChunk();
			double bx = bc.getX();
			double bz = bc.getZ();
			if (ac.getWorld().equals(bc.getWorld()) && ax == bx && az == bz) {
				npc.update(npc.getLocation(), npc.isOnTab());
				npc.getPlayer().sendMessage(("[SUCCESS]update NPC (" + ax + ", " + az + "), (" + bx + ", " + bz + ")"));
			}
		}
		for (Hologram hologram : HologramManager.getHolograms()) {
			Chunk bc = hologram.getLocation().getChunk();
			double bx = bc.getX();
			double bz = bc.getZ();
			if (ac.getWorld().equals(bc.getWorld()) && ax == bx && az == bz) {
				hologram.update(hologram.getLocation());
				hologram.getPlayer().sendMessage(("§a[SUCCESS]update Hologram (" + ax + ", " + az + "), (" + bx + ", " + bz + ")"));
			}
		}
	}
	
	@EventHandler
	public void onChunkUnload(ChunkUnloadEvent event) {
		Chunk ac = event.getChunk();
		int ax = ac.getX();
		int az = ac.getZ();
		for (NPC npc : NPCManager.getLivingNPCs()) {
			Chunk bc = npc.getLocation().getChunk();
			int bx = bc.getX();
			int bz = bc.getZ();
			if (ac.getWorld().equals(bc.getWorld()) && ax == bx && az == bz) {
				npc.despawn();
				npc.getPlayer().sendMessage(("[SUCCESS]remove NPC (" + ax + ", " + az + "), (" + bx + ", " + bz + ")"));
			}
		}
		for (Hologram hologram : HologramManager.getHolograms()) {
			Chunk bc = hologram.getLocation().getChunk();
			int bx = bc.getX();
			int bz = bc.getZ();
			if (ac.getWorld().equals(bc.getWorld()) && ax == bx && az == bz) {
				hologram.despawn();
				hologram.getPlayer().sendMessage(("[SUCCESS]remove Hologram (" + ax + ", " + az + "), (" + bx + ", " + bz + ")"));
			}
		}
	}*/

}
