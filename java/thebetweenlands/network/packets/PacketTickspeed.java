package thebetweenlands.network.packets;

import io.netty.buffer.ByteBuf;
import thebetweenlands.network.base.IPacket;

public class PacketTickspeed implements IPacket {
	private float ticksPerSecond;

	public PacketTickspeed() {
	}

	public PacketTickspeed(float ticksPerSecond) {
		this.ticksPerSecond = ticksPerSecond;
	}

	@Override
	public void deserialize(ByteBuf buffer) {
		ticksPerSecond = buffer.readFloat();
	}

	@Override
	public void serialize(ByteBuf buffer) {
		buffer.writeFloat(ticksPerSecond);
	}

	public float getTicksPerSecond() {
		return ticksPerSecond;
	}
}
