# KokuminCore
NPCとホログラムがかんたんに作れるライブラリ？です<br>
あとPacketListener的なこともできます（パケット書き換えなど）
# 仕様
・サーバー停止時にリセットされる<br>
・プレイヤーごとに個別のNPCが表示されている<br>
・NPCを削除する場合はdestroy()、Hologramを削除する場合はremove()を使う<br>
　↑をしない限り、必ず表示される（はず）<br>
# クリックイベントを読み取る
流れとしては、PacketPlayInUseEntityをプレイヤーから受け取ったとき、クリックした対象のエンティティを取得し、そのIDがNPCと一致するかをチェックしてNPCにつけておいたTagでいろいろ処理する感じ...
### CustomNPCListener.class (PacketListenerの役割）
~~~
package me.nucha.souppvp.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Lists;

import me.nucha.core.npc.NPC;
import me.nucha.core.packet.PacketInfo;
import me.nucha.core.packet.PacketListener;
import me.nucha.souppvp.SoupPvPPlugin;
import me.nucha.souppvp.listener.gui.GuiShop;
import me.nucha.souppvp.listener.gui.GuiStats;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity.EnumEntityUseAction;

public class CustomNPCListener implements PacketListener {

  @Override
  public void playerSendPacket(PacketInfo packet) {
    if (packet.getPacket() instanceof PacketPlayInUseEntity) {
      Player p = packet.getPlayer();
      int id = (int) packet.getPacketValue("a");
      EnumEntityUseAction ee = (EnumEntityUseAction) packet.getPacketValue("action");
      if (ee == EnumEntityUseAction.INTERACT) {
        for (NPC n : Lists.newArrayList(JoinListener.npcs)) {
          if (n.getPlayer().getUniqueId().equals(p.getUniqueId()) && n.getId() == id) {
            if (n.hasTag("Stats")) {
              Bukkit.getScheduler().runTask(SoupPvPPlugin.getInstance(), new BukkitRunnable() {
                @Override
                public void run() {
                  GuiStats.open(p);
                }
              });
              return;
            }
            if (n.hasTag("Shop")) {
              Bukkit.getScheduler().runTask(SoupPvPPlugin.getInstance(), new BukkitRunnable() {
                @Override
                public void run() {
                  GuiShop.open(p);
                }
              });
              return;
            }
          }
        }
      }
    }
  }

  @Override
  public void playerReceivePacket(PacketInfo packet) {
  }

}
~~~
### SoupPvPPlugin.class (プラグインのメインクラス)
~~~
public class SoupPvPPlugin extends JavaPlugin {

	private static CustomNPCListener customNpcListener;
  
	@Override
	public void onEnable() {
    // Listenerのインスタンス作成＆登録
		PacketHandler.registerPacketListener(customNpcListener = new CustomNPCListener());
  }
  
	@Override
	public void onDisable() {
    // Listenerの登録解除（これを忘れるとListenerの登録だけ何度もして複数回イベントが処理されるなんてことが...）
		PacketHandler.unregisterPacketListener(customNpcListener);
  }
~~~

# NPCにスキンを付ける方法
~~~
public class JoinListener implements Listener {

  private static HashMap<String, Property> properties;

  public JoinListener() {
    properties = new HashMap<>();
    properties.put("tyuro", new Property("textures",
	"eyJ0aW1lc3RhbXAiOjE1NTMxNzA5OTI4MDEsInByb2ZpbGVJZCI6ImE1OTQwMTgzOGZkOTQyNzJiOWVhMGQyOGM1NzgwZTY4IiwicHJvZmlsZU5hbWUiOiJrb2t1bWluIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS81MmVmMzU4ZjRjYWY0M2E3ZWM3NGIxMTcyNjJkODlmYTQzODc0NzNhOTY3Mzk0MTY2NWZmYWEyMjc2NmM5Mjg2In19fQ==",
	"xArKCRHBmkBviAd2MXo2w4iNz+5uTPuSXsbdXl8XnxmpGrND1XMxIjhCTlzYHKR/8QVG7ajcZ/pVrMRgFFuXg4LyI+SsJOxD1SGCu1RtUX90KEaebx+czawSYsF2OTCv8B43uSmxxCbsN2up3u90WfbUEQjhqD67SyJHZ9hna+2xy6PsI2V/D0JbnAloH5s4fiRRbnf+MA1gFujOkb74Tp8cPY9AeLEYYcmYIIKwaeZqnABlzVPAN3PUHAvNZoBIGZ8nm+25yNKRdrr+a8AZxoMBOWgep1450s0x+cMAwXFdxrkHQLZLqXf6lLK93fR0kX4Rs8wayB7sENnDU1zhTypCXeHX3G5ZaSE/eraq5SYRlphtxCNYZrlpWa7sMLt/irmVLODoi9SFAzjmsZH+TWgvoNtBCpYluhnvo3EpLW0EzfumKzt8Df3A3bIzmdjsGh/xFvGQN+mmc/0BbgTeirMEmWJxzqwzYtic9uA+x6UYgG3dN5DYONcaOSE7NOSoljOHVJaFblm7VqcKM75ZEi7pNtZq32hECiXfh0A5Qk5SBuNbFMaGALtcnPa4wsPaA+2YN0TKulG64bh0ZWypfSu3/Qk+Pk5v9ZsI/YH1bZioi3K6Y3vOnxKQ92hdymz1PtC4I6EkISvknL4+15eeF/lhrp8T9VSycbT3etW7izo="));
  }

  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    Player p = event.getPlayer();
    NPC npc = new NPC(p, "Perk Shop(CLICK)", UUID.randomUUID(), p.getWorld());
    npc.setSkin(properties.get("tyuro"));
    npc.spawn(LocationUtil.get("npc-shop"), false);
  }
}
~~~
