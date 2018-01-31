package io.leonis.subra.ipc.network;

import io.leonis.algieba.Temporal;
import io.leonis.subra.game.data.*;
import io.leonis.subra.ipc.network.GameStatePublisher.GameState;
import io.leonis.subra.ipc.serialization.protobuf.*;
import io.leonis.subra.ipc.serialization.protobuf.SSLVisionDeducer.VisionPacket;
import java.util.Set;
import lombok.*;
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
        Flux.from(this.visionPublisher)
            .transform(new SSLVisionDeducer()),
        Flux.from(this.refboxPublisher)
            .transform(new SSLRefboxDeducer()),
        GameState::build)
        .subscribe(subscriber);
  }

  @Value
  @AllArgsConstructor
  public static class GameState
      implements Player.SetSupplier, Goal.SetSupplier, Field.Supplier, Ball.SetSupplier,
      Referee.Supplier, Temporal {
    private final Set<Player> players;
    private final Set<Goal> goals;
    private final Set<Ball> balls;
    private final Field field;
    private final Referee referee;
    private final long timestamp = System.currentTimeMillis();

    /**
     * Constructs a new GameState from a {@link VisionPacket} and a {@link Referee}.
     *
     * @param visionPacket The {@link VisionPacket} to extract the players, goals, balls, and field
     *                     from.
     * @param refSupplier The {@link Referee.Supplier}.
     * @param <V>          The type of {@link VisionPacket}.
     * @return The {@link VisionPacket} reconstructed as an {@link GameState}.
     */
    public static <V extends Player.SetSupplier & Goal.SetSupplier & Field.Supplier & Ball.SetSupplier & Temporal>
    GameState build(final V visionPacket, final Referee.Supplier refSupplier) {
      return new GameState(
          visionPacket.getPlayers(),
          visionPacket.getGoals(),
          visionPacket.getBalls(),
          visionPacket.getField(),
          refSupplier.getReferee());
    }
  }
}
