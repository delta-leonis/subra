package io.leonis.subra.ipc.serialization.protobuf;

import io.leonis.algieba.Temporal;
import io.leonis.subra.game.data.*;
import io.leonis.subra.ipc.serialization.protobuf.SSLVisionDeducer.VisionPacket;
import io.leonis.subra.ipc.serialization.protobuf.vision.*;
import io.leonis.zosma.game.engine.*;
import java.util.Set;
import lombok.*;
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
public class SSLVisionDeducer implements Deducer<WrapperPacket, VisionPacket> {

  @Override
  public Publisher<VisionPacket> apply(final Publisher<WrapperPacket> wrapperPacketPublisher) {
    return Flux.from(wrapperPacketPublisher)
        .transform(new ParallelDeducer<>(
            new PlayersDeducer(),
            new GoalsDeducer(),
            new BallsDeducer(),
            new FieldDeducer(),
            VisionPacket::new));
  }

  @Value
  public static class VisionPacket
      implements Player.SetSupplier, Goal.SetSupplier, Field.Supplier, Ball.SetSupplier, Temporal {
    private final Set<Player> players;
    private final Set<Goal> goals;
    private final Set<Ball> balls;
    private final Field field;
    private final long timestamp = System.currentTimeMillis();
  }
}
