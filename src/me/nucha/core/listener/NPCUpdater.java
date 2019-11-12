package me.nucha.core.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import me.nucha.core.KokuminCore;
import me.nucha.core.hologram.Hologram;
import me.nucha.core.hologram.HologramManager;
import me.nucha.core.npc.NPC;
import me.nucha.core.npc.NPCManager;

public class NPCUpdater implements Listener {

	public static void init() {
		KokuminCore plugin = KokuminCore.getInstance();
		plugin.getServer().getPluginManager().registerEvents(new NPCUpdater(), plugin);
		/*BukkitRunnable npcUpdateTask = new BukkitRunnable() {
		
			@Override
			public void run() {
				updateNPCs();
				updateHolograms();
			}
		};
		npcUpdateTask.runTaskTimer(plugin, 0L, 100L);*/
	}

	/*@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent event) {
		Bukkit.getScheduler().runTaskLater(KokuminCore.getInstance(), new BukkitRunnable() {
			@Override
			public void run() {
				updateNPCs();
				updateHolograms();
			}
		}, 1L);
	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		Bukkit.getScheduler().runTaskLater(KokuminCore.getInstance(), new BukkitRunnable() {
			@Override
			public void run() {
				updateNPCs();
				updateHolograms();
			}
		}, 1L);
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		Player p = event.getEntity();
		for (NPC npc : new ArrayList<>(NPCManager.getLivingNPCs())) {
			if (npc.getPlayer().getUniqueId().equals(p.getUniqueId())) {
				npc.despawn();
			}
		}
		for (Hologram hologram : new ArrayList<>(HologramManager.getHolograms())) {
			if (hologram.getPlayer().getUniqueId().equals(p.getUniqueId())) {
				hologram.despawn();
			}
		}
	}*/

	public static void updateNPCs() {
		for (NPC npc : NPCManager.getLivingNPCs()) {
			Player p = npc.getPlayer();
			if (npc.getLocation() == null || !p.getWorld().equals(npc.getLocation().getWorld())) {
				if (npc.getLocation() == null) {
					Bukkit.getConsoleSender().sendMessage("[KCore] NPC's location is null: " + p.getName());
				}
				if (npc.isShown()) {
					npc.despawn();
				}
				continue;
			}
			int distance = (int) p.getLocation().distance(npc.getLocation());
			if (distance <= 64 && !npc.isShown()) {
				npc.update();
			} else if (distance > 64 && npc.isShown()) {
				npc.despawn();
			}
		}
	}

	public static void updateHolograms() {
		for (Hologram hologram : HologramManager.getHolograms()) {
			Player p = hologram.getPlayer();
			if (hologram.getLocation() == null || !p.getWorld().equals(hologram.getLocation().getWorld())) {
				if (hologram.getLocation() == null) {
					Bukkit.getConsoleSender().sendMessage("[KCore] Hologram's location is null: " + p.getName());
				}
				if (hologram.isShown()) {
					hologram.despawn();
				}
				continue;
			}
			int distance = (int) p.getLocation().distance(hologram.getLocation());
			if (distance <= 64 && !hologram.isShown()) {
				hologram.update();
			} else if (distance > 64 && hologram.isShown()) {
				hologram.despawn();
			}
		}
	}

}
