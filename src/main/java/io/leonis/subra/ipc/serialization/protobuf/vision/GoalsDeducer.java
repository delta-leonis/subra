package io.leonis.subra.ipc.serialization.protobuf.vision;

import com.google.common.collect.ImmutableSet;
import io.leonis.algieba.geometry.Vectors;
import io.leonis.subra.game.data.*;
import io.leonis.subra.game.data.Goal.SetSupplier;
import io.leonis.zosma.game.engine.Deducer;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

/**
 * @author Jeroen de Jong
 */
public class GoalsDeducer<I extends Referee.Supplier & GoalDimension.Supplier & Field.Supplier> implements Deducer<I, SetSupplier> {

  @Override
  public Publisher<SetSupplier> apply(final Publisher<I> iPublisher) {
    return Flux.from(iPublisher)
        .map(frame ->
            () -> ImmutableSet.of(
                new Goal.State(
                    frame.getGoalDimension(),
                    Vectors.columnVector((frame.getField().getLength() + frame.getGoalDimension().getDepth()) / 2f, 0f),
                    frame.getReferee().getPositiveHalfTeam(),
                    FieldHalf.POSITIVE),
                new Goal.State(
                    frame.getGoalDimension(),
                    Vectors.columnVector(-1f * ((frame.getField().getLength() + frame.getGoalDimension().getDepth()) / 2f), 0f),
                    frame.getReferee().getNegativeHalfTeam(),
                    FieldHalf.NEGATIVE)));
  }
}