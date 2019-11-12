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
