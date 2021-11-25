package me.nucha.core.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.nucha.core.listener.NPCDisplayListener;

public class CommandNPCUpdater implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		NPCDisplayListener.active = !NPCDisplayListener.active;
		if (NPCDisplayListener.active) {
			sender.sendMessage("§a[KokuminCore] NPC Updating has been enabled.");
		} else {
			sender.sendMessage("§a[KokuminCore] NPC Updating has been §cdisabled§a.");
		}
		return true;
	}

}
