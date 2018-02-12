package io.leonis.subra.ipc.serialization.protobuf;

import io.leonis.algieba.Temporal;
import io.leonis.subra.game.data.*;
import io.leonis.subra.game.data.Referee.Supplier;
import io.leonis.subra.ipc.serialization.protobuf.SSLVisionDeducer.VisionPacket;
import io.leonis.subra.ipc.serialization.protobuf.vision.*;
import io.leonis.subra.ipc.serialization.protobuf.vision.DetectionFrameDeducer.DetectionFrame;
import io.leonis.subra.ipc.serialization.protobuf.vision.GeometryDeducer.GeometryFrame;
import io.leonis.zosma.game.engine.*;
import java.util.concurrent.Callable;
import lombok.*;
import lombok.experimental.Delegate;
import org.reactivestreams.Publisher;
import org.robocup.ssl.Wrapper.WrapperPacket;
import reactor.core.publisher.Flux;

/**
 * The Class SSLVisionDeducer.
 *
 * This class represents a {@link Deducer} which creates {@link VisionPacket} from {@link
 * WrapperPacket}.
 *
 * @author Jeroen de Jong
 * @author Rimon Oz
 */
@AllArgsConstructor
public class SSLVisionDeducer<I extends WrapperPacketSupplier> implements Deducer<I, VisionPacket> {

  @Override
  public Publisher<VisionPacket> apply(final Publisher<I> publisher) {
    return Flux.from(publisher)
        .map(WrapperPacketSupplier::getWrapperPacket)
        .transform(
            new ParallelDeducer<>(
                input -> Flux.from(input)
                    .filter(WrapperPacket::hasGeometry)
                    .map(WrapperPacket::getGeometry)
                    .transform(new GeometryDeducer()),
                input -> Flux.from(input)
                    .filter(WrapperPacket::hasDetection)
                    .map(WrapperPacket::getDetection)
                    .transform(new DetectionFrameDeducer()),
                VisionPacket::new));
  }

  @AllArgsConstructor
  public final static class VisionPacket implements Player.SetSupplier, GoalDimension.Supplier, Field.Supplier, Ball.SetSupplier, Temporal {
    @Delegate
    private final GeometryFrame geometryContainer;
    @Delegate
    private final DetectionFrame detectionContainer;
    @Getter
    private final long timestamp = System.currentTimeMillis();
  }
}
