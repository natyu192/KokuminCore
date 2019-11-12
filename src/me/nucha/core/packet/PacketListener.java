package me.nucha.core.packet;

public interface PacketListener {

	public void playerSendPacket(PacketInfo packetInfo);

	public void playerReceivePacket(PacketInfo packetInfo);

}
