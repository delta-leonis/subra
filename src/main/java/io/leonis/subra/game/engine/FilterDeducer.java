package io.leonis.subra.game.engine;

import io.leonis.subra.game.engine.FilterDeducer.FilteredGameState;
import io.leonis.subra.math.filter.*;
import io.leonis.zosma.game.engine.*;
import java.util.Set;
import lombok.*;
import org.reactivestreams.Publisher;
import io.leonis.subra.game.data.*;
import io.leonis.algieba.Temporal;
import reactor.core.publisher.Flux;

/**
 * The Class FilterDeducer.
 *
 * This class represents an {@link Deducer} which applies noise filtering and/or state estimation on
 * raw ssl-vision data.
 *
 * @author Rimon Oz
 */
@Value
@AllArgsConstructor
public class FilterDeducer<I extends MovingPlayer.SetSupplier & Goal.SetSupplier & Field.Supplier & MovingBall.SetSupplier & Referee.Supplier & Temporal>
    implements Deducer<I, FilteredGameState> {
  private final Deducer<I, MovingBall> ballFilter;
  private final Deducer<I, Set<MovingPlayer>> robotFilter;

  /**
   * Constructs a new FilterDeducer with a set of first-order {@link MovingBallsKalmanFilter} and {@link
   * MovingPlayersKalmanFilter}.
   */
  public FilterDeducer() {
    this(new MovingBallsKalmanFilter<>(), new MovingPlayersKalmanFilter<>());
  }

  @Override
  public Publisher<FilteredGameState> apply(final Publisher<I> soccerGamePublisher) {
    return Flux.from(soccerGamePublisher)
        .transform(new ParallelDeducer<>(
            new Deducer.Identity<>(),
            this.getBallFilter(),
            this.getRobotFilter(),
            FilteredGameState::build));
  }

  @Value
  public static class FilteredGameState
      implements MovingPlayer.SetSupplier, Goal.SetSupplier, Field.Supplier, MovingBall.Supplier,
      Referee.Supplier, Temporal {
    private final Set<MovingPlayer> agents;
    private final Set<Goal> goals;
    private final MovingBall ball;
    private final Field field;
    private final Referee referee;
    private final long timestamp = System.currentTimeMillis();

    public static <I extends MovingPlayer.SetSupplier & Goal.SetSupplier & Field.Supplier & MovingBall.SetSupplier & Referee.Supplier & Temporal>
    FilteredGameState build(final I unfilteredGameState, final MovingBall ball, final Set<MovingPlayer> robots) {
      return new FilteredGameState(
          robots,
          unfilteredGameState.getGoals(),
          ball,
          unfilteredGameState.getField(),
          unfilteredGameState.getReferee());
    }
  }
}
