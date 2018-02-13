package io.leonis.subra.ipc.network;

import io.leonis.subra.game.data.*;
import io.leonis.subra.ipc.network.VisionPublisher.VisionFrame;
import io.leonis.zosma.exception.UnsafeFunction;
import io.leonis.zosma.ipc.ip.MulticastPublisher;
import java.net.InetAddress;
import lombok.*;
import org.reactivestreams.*;
import org.robocup.ssl.Detection.DetectionFrame;
import org.robocup.ssl.Geometry.GeometryData;
import org.robocup.ssl.Wrapper.WrapperPacket;
import reactor.core.publisher.Flux;

/**
 * The Class VisionPublisher.
 *
 * Publishes a {@link WrapperPacket} with both {@link GeometryData} and {@link DetectionFrame},
 * since some implementations (grSim for example) that don't publish both frames in wrapper packets.
 *
 * @author Jeroen de Jong
 */
@AllArgsConstructor
public final class VisionPublisher implements Publisher<VisionFrame> {

  /**
   * Address to listen on.
   */
  private final InetAddress address;
  /**
   * Port to listen on.
   */
  private final int port;

  @Override
  public void subscribe(final Subscriber<? super VisionFrame> subscriber) {
    final Flux<WrapperPacket> wrapperPacketFlux = Flux.from(
        new MulticastPublisher<>(address, port, new UnsafeFunction<>(WrapperPacket::parseFrom)));

    Flux.combineLatest(
        wrapperPacketFlux.filter(WrapperPacket::hasGeometry).map(WrapperPacket::getGeometry),
        wrapperPacketFlux.filter(WrapperPacket::hasDetection).map(WrapperPacket::getDetection),
        VisionFrame::new)
      .subscribe(subscriber);
  }

  @Value
  public static class VisionFrame implements GeometrySupplier, DetectionSupplier {
    GeometryData geometry;
    DetectionFrame detection;
  }
}
