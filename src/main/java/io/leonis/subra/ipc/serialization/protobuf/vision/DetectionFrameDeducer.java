package io.leonis.subra.ipc.serialization.protobuf.vision;

import io.leonis.subra.game.data.*;
import io.leonis.zosma.game.engine.Deducer;
import java.util.Set;
import lombok.Value;
import org.reactivestreams.Publisher;
import org.robocup.ssl.Detection.DetectionFrame;
import org.robocup.ssl.Wrapper.WrapperPacket;
import reactor.core.publisher.Flux;

/**
 * The Class DetectionFrameDeducer.
 *
 * This class represents a {@link Deducer} of {@link DetectionFrame} from {@link WrapperPacket}.
 *
 * @author Rimon Oz
 */
public class DetectionFrameDeducer implements Deducer<WrapperPacket, DetectionFrame> {
  @Override
  public Publisher<DetectionFrame> apply(final Publisher<WrapperPacket> detectionFramePublisher) {
    return Flux.from(detectionFramePublisher)
        .filter(WrapperPacket::hasDetection)
        .map(WrapperPacket::getDetection);
  }

  @Value
  public static class Detection implements Ball.SetSupplier, Player.SetSupplier {
    private final Set<Ball> balls;
    private final Set<Player> players;
  }
}
