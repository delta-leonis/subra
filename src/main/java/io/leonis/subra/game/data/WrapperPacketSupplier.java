package io.leonis.subra.game.data;

import org.robocup.ssl.Wrapper.WrapperPacket;

/**
 * @author jeroen.dejong.
 */
public interface WrapperPacketSupplier {
  WrapperPacket getWrapperPacket();
}
