package io.leonis.subra.ipc.network;

import io.leonis.algieba.Temporal;
import io.leonis.subra.game.data.*;
import io.leonis.subra.ipc.network.GameDeducer.GameFrame;
import io.leonis.subra.ipc.serialization.protobuf.*;
import io.leonis.subra.ipc.serialization.protobuf.vision.GoalsDeducer;
import io.leonis.zosma.game.engine.*;
import java.util.Set;
import lombok.*;
import lombok.experimental.Delegate;
import org.reactivestreams.Publisher;
import org.robocup.ssl.Referee.SSL_Referee;
import org.robocup.ssl.Wrapper.WrapperPacket;
import reactor.core.publisher.Flux;

/**
 * The Class GameDeducer.
 *
 * This class deduces a {@link WrapperPacket} and {@link SSL_Referee} to a {@link GameFrame}.
 *
 * @author Rimon Oz
 */
@AllArgsConstructor
public final class GameDeducer<I extends DetectionSupplier & GeometrySupplier & SSLRefereeSupplier>
    implements Deducer<I, GameFrame> {

  @Override
  public Publisher<GameFrame> apply(final Publisher<I> entryPublisher) {
    return Flux.from(entryPublisher)
        .transform(new ParallelDeducer<>(
            new SSLVisionDeducer<>(),
            new SSLRefboxDeducer<>(),
            GameFrameWithoutGoals::new))
        .transform(new ParallelDeducer<>(
          new IdentityDeducer<>(),
          new GoalsDeducer<>(),
          GameFrame::new));
  }

  @Value
  private static class GameFrameWithoutGoals<J extends Player.SetSupplier & GoalDimension.Supplier & Field.Supplier & Ball.SetSupplier>
      implements Player.SetSupplier, GoalDimension.Supplier, Field.Supplier, Ball.SetSupplier,
      Referee.Supplier {
    private final Set<Player> players;
    private final GoalDimension goalDimension;
    private final Set<Ball> balls;
    private final Field field;
    private final Referee referee;

    GameFrameWithoutGoals(final J packet, final Referee.Supplier referee) {
      this.goalDimension = packet.getGoalDimension();
      this.players = packet.getPlayers();
      this.balls = packet.getBalls();
      this.field = packet.getField();
      this.referee = referee.getReferee();
    }
  }

  @Value
  public static class GameFrame
      implements Player.SetSupplier, Goal.SetSupplier, Field.Supplier, Ball.SetSupplier,
      Referee.Supplier, Temporal {
    @Delegate
    private final GameFrameWithoutGoals state;
    @Delegate
    private final Goal.SetSupplier goalSupplier;
    private final long timestamp = System.currentTimeMillis();
  }
}
