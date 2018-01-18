package io.leonis.subra.ipc.serialization.protobuf.vision;

import io.leonis.subra.game.data.Ball;
import io.leonis.zosma.game.engine.Deducer;
import java.util.Set;
import java.util.stream.Collectors;
import org.reactivestreams.Publisher;
import org.robocup.ssl.Wrapper.WrapperPacket;
import reactor.core.publisher.Flux;

/**
 * The Class BallReadHandler
 *
 * Reads all the {@link Ball balls} from a {@link org.robocup.ssl.Detection}.
 *
 * @author Jeroen de Jong
 */
public class BallsDeducer implements Deducer<WrapperPacket, Set<Ball>> {
  @Override
  public Publisher<Set<Ball>> apply(final Publisher<WrapperPacket> detectionFramePublisher) {
    return Flux.from(detectionFramePublisher)
        .filter(WrapperPacket::hasDetection)
        .map(WrapperPacket::getDetection)
        .map(input ->
            input.getBallsList().stream()
                .map(ball ->
                    new Ball.State(
                        input.getTCapture(),
                        ball.getX(),
                        ball.getY(),
                        ball.getZ()))
                .collect(Collectors.toSet()));
  }
}
