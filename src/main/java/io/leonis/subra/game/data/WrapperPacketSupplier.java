package io.leonis.subra.game.data;

import org.robocup.ssl.Wrapper.WrapperPacket;

/**
 * The Interface WrapperPacketSupplier.
 *
 * Contains a {@link WrapperPacket}.
 * 
 * @author Jeroen de Jong
 */
public interface WrapperPacketSupplier {

  /**
   * @return The robocup ssl wrapper packet. 
   */
  WrapperPacket getWrapperPacket();
}
