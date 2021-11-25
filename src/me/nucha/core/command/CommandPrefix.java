package me.nucha.core.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.nucha.core.KokuminCore;
import me.nucha.core.sql.SQLManager;

public class CommandPrefix implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("prefix")) {
			Bukkit.getScheduler().runTaskAsynchronously(KokuminCore.getInstance(), new Runnable() {
				@Override
				public void run() {
					if (args.length == 1) {
						OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
						String prefix = SQLManager.getPrefix(target.getUniqueId(), false);
						if (prefix.isEmpty()) {
							sender.sendMessage("§6" + target.getName() + "にはPrefixがありません");
						} else {
							prefix = ChatColor.translateAlternateColorCodes('&', prefix);
							sender.sendMessage(target.getName() + "のPrefix: " + prefix);
						}
						return;
					}
					if (args.length == 2) {
						OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
						String prefix = args[1];
						SQLManager.setPrefix(target.getUniqueId(), prefix, false);
						prefix = ChatColor.translateAlternateColorCodes('&', prefix);
						sender.sendMessage(target.getName() + "のPrefixを変更: " + prefix);
						return;
					}
					sender.sendMessage("§cUsage: /prefix <player> --- prefixを確認");
					sender.sendMessage("§cUsage: /prefix <player> <prefix> --- prefixを設定");
				}
			});
			return true;
		}
		if (cmd.getName().equalsIgnoreCase("sprefix")) {
			Bukkit.getScheduler().runTaskAsynchronously(KokuminCore.getInstance(), new Runnable() {
				@Override
				public void run() {
					if (args.length == 1) {
						OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
						String prefix = SQLManager.getPrefix(target.getUniqueId(), true);
						if (prefix.isEmpty()) {
							sender.sendMessage("§6" + target.getName() + "にはShort Prefixがありません");
						} else {
							prefix = ChatColor.translateAlternateColorCodes('&', prefix);
							sender.sendMessage(target.getName() + "のShort Prefix: " + prefix);
						}
						return;
					}
					if (args.length == 2) {
						OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
						String prefix = args[1];
						SQLManager.setPrefix(target.getUniqueId(), prefix, true);
						prefix = ChatColor.translateAlternateColorCodes('&', prefix);
						sender.sendMessage(target.getName() + "のShort Prefixを変更: " + prefix);
						return;
					}
					sender.sendMessage("§cUsage: /sprefix <player> --- short prefixを確認");
					sender.sendMessage("§cUsage: /sprefix <player> <prefix> --- short prefixを設定");
				}
			});
			return true;
		}
		return true;
	}

}
