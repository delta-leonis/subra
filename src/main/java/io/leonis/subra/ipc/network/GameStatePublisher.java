package io.leonis.subra.ipc.network;

import io.leonis.algieba.Temporal;
import io.leonis.subra.game.data.*;
import io.leonis.subra.ipc.network.GameStatePublisher.GameState;
import io.leonis.subra.ipc.serialization.protobuf.*;
import io.leonis.subra.ipc.serialization.protobuf.SSLVisionDeducer.VisionPacket;
import io.leonis.subra.ipc.serialization.protobuf.vision.GoalsDeducer;
import io.leonis.zosma.game.engine.*;
import lombok.*;
import lombok.experimental.Delegate;
import org.reactivestreams.*;
import org.robocup.ssl.Wrapper.WrapperPacket;
import reactor.core.publisher.Flux;

/**
 * The Class SSLGameStatePublisher.
 *
 * This class contains the functionality of a {@link Publisher} of {@link GameState}.
 *
 * @author Rimon Oz
 */
@AllArgsConstructor
public final class GameStatePublisher implements Publisher<GameState> {
  private final Publisher<WrapperPacket> visionPublisher;
  private final Publisher<org.robocup.ssl.RefereeOuterClass.Referee> refboxPublisher;

  @Override
  public void subscribe(final Subscriber<? super GameState> subscriber) {
    Flux.combineLatest(
        Flux.from(this.visionPublisher).transform(new SSLVisionDeducer()),
        Flux.from(this.refboxPublisher).transform(new SSLRefboxDeducer()),
        GameStateWithoutGoals::new)
      .transform(new ParallelDeducer<>(
          new IdentityDeducer<>(),
          new GoalsDeducer<>(),
          GameState::new
      ))
      .subscribe(subscriber);
  }

  @Value
  public static class GameStateWithoutGoals
      implements Player.SetSupplier, GoalDimension.Supplier, Field.Supplier, Ball.SetSupplier,
      Referee.Supplier {
    @Delegate
    private final VisionPacket visionPacket;
    private final Referee referee;
  }

  @Value
  public static class GameState
      implements Player.SetSupplier, Goal.SetSupplier, Field.Supplier, Ball.SetSupplier,
      Referee.Supplier, Temporal {
    @Delegate
    private final GameStateWithoutGoals gameStateWithoutGoals;
    @Delegate
    private final Goal.SetSupplier goalsSupplier;
    private final long timestamp = System.currentTimeMillis();
  }
}
