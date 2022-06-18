package me.nucha.core.command;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.nucha.core.KokuminCore;
import me.nucha.core.sql.PrefixManager;
import me.nucha.core.sql.dao.Prefix;

public class CommandPrefix implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("prefix")) {
			Bukkit.getScheduler().runTaskAsynchronously(KokuminCore.getInstance(), new Runnable() {
				@Override
				public void run() {
					if (args.length == 1) {
						if (args[0].equalsIgnoreCase("unicodes")) {
							HashMap<String, String> map = PrefixManager.getUnicodeChars();
							sender.sendMessage("§a------ Unicode文字の一覧 ------");
							for (String k : map.keySet()) {
								sender.sendMessage("{" + k + "} => " + map.get(k));
							}
							return;
						}
					}
					if (args.length == 2) {
						if (args[0].equalsIgnoreCase("list")) {
							OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
							UUID uuid = target.getUniqueId();
							List<Prefix> prefixes = PrefixManager.getPrefixes(uuid);
							String prefix = PrefixManager.getPrefix(uuid);
							if (prefix.isEmpty()) {
								sender.sendMessage("§6" + target.getName() + "にはPrefixがありません");
							} else {
								sender.sendMessage("プレビュー: " + prefix + target.getName());
								sender.sendMessage("内訳:");
								for (int i = 0; i < prefixes.size(); i++) {
									Prefix pr = prefixes.get(i);
									sender.sendMessage((i + 1) + ". " + pr.getId() + ": " + pr.getPrefix() + " --- " + pr.getDescription());
								}
							}
							return;
						}
					}
					if (args.length == 3) {
						if (args[0].equalsIgnoreCase("remove")) {
							OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
							String id = args[2];
							PrefixManager.removePrefix(target.getUniqueId(), id);
							sender.sendMessage(target.getName() + "のPrefixを削除: " + id);
							return;
						}
					}
					if (args.length >= 4) {
						if (args[0].equalsIgnoreCase("add")) {
							OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
							String id = args[2];
							String prefix = args[3];
							StringBuilder descriptionBuilder = new StringBuilder();
							if (args.length >= 5) {
								for (int i = 4; i < args.length; i++) {
									descriptionBuilder.append(args[i]);
									if (i != args.length - 1) {
										descriptionBuilder.append(" ");
									}
								}
							}
							String description = descriptionBuilder.toString();
							PrefixManager.addPrefix(target.getUniqueId(), new Prefix(id, prefix, description));
							prefix = ChatColor.translateAlternateColorCodes('&', prefix);
							sender.sendMessage(target.getName() + "のPrefixを追加: " + id + ", " + prefix + ", " + description);
							return;
						}
					}
					sender.sendMessage("§cUsage: /prefix list <player> --- prefixを確認");
					sender.sendMessage("§cUsage: /prefix add <player> <id> <prefix> [description ...] --- prefixを追加");
					sender.sendMessage("§cUsage: /prefix remove <player> <id> --- prefixを削除");
					sender.sendMessage("§cUsage: /prefix unicodes --- 使用可能なUnicode文字の一覧を表示");
				}
			});
			return true;
		}
		return true;
	}

}
