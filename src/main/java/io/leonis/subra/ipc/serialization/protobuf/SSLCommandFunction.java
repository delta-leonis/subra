package io.leonis.subra.ipc.serialization.protobuf;

import io.leonis.subra.game.data.Strategy;
import io.leonis.subra.protocol.Robot.Command;
import io.reactivex.functions.Function;
import java.util.Optional;

/**
 * The Class SSLCommandFunction.
 *
 * Deduces a {@link io.leonis.subra.game.data.Strategy} to individual {@link Command Commands}.
 *
 * @author Jeroen de Jong
 */
public class SSLCommandFunction implements Function<Strategy.Supplier, Optional<Command>> {

  @Override
  public Optional<Command> apply(final Strategy.Supplier strategyContainer) {
    return strategyContainer.getStrategy().entrySet().stream()
        .map(robotCommandEntry ->
            Command.newBuilder()
                .setRobotId(robotCommandEntry.getKey().getId())
                .setMove(Command.Move.newBuilder()
                    .setX(robotCommandEntry.getValue().getVelocityX())
                    .setY(robotCommandEntry.getValue().getVelocityY())
                    .setR(robotCommandEntry.getValue().getVelocityR())
                    .build())
                .setAction(Command.Action.newBuilder()
                    .setKick(robotCommandEntry.getValue().getFlatKick())
                    .setChip(robotCommandEntry.getValue().getChipKick())
                    .setDribble(robotCommandEntry.getValue().getDribblerSpin())
                    .build())
                .build())
        .findFirst();
  }
}
