package me.nucha.core.npc;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.scheduler.BukkitRunnable;

import me.nucha.core.KokuminCore;

public class NPCManager {

	private static List<NPC> livingNPCs;

	public static void init() {
		livingNPCs = new ArrayList<NPC>();
		KokuminCore plugin = KokuminCore.getInstance();
		BukkitRunnable runnable = new BukkitRunnable() {
			Random rnd = new Random();

			@Override
			public void run() {
				for (NPC npc : livingNPCs) {
					npc.getEntityPlayer().ping = rnd.nextInt(140) + (30 - rnd.nextInt(20));
				}
			}
		};
		runnable.runTaskTimer(plugin, 200L, 200L);
		BukkitRunnable tickTask = new BukkitRunnable() {
			@Override
			public void run() {
				for (NPC npc : livingNPCs) {
					npc.tick();
				}
			}
		};
		tickTask.runTaskTimer(plugin, 1, 1);
	}

	public static List<NPC> getLivingNPCs() {
		return livingNPCs;
	}

	public static void register(NPC npc) {
		if (!contains(npc)) {
			livingNPCs.add(npc);
		}
	}

	public static void unregister(NPC npc) {
		if (contains(npc)) {
			livingNPCs.remove(npc);
		}
	}

	public static boolean contains(NPC npc) {
		return getById(npc.getId()) != null;
	}

	public static NPC getById(int id) {
		for (NPC npc : livingNPCs) {
			if (npc.getId() == id) {
				return npc;
			}
		}
		return null;
	}

}
