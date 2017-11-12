package io.leonis.subra.math;

import io.leonis.subra.game.data.Strategy;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BinaryOperator;
import java.util.stream.*;
import io.leonis.subra.game.data.*;
import io.leonis.algieba.algebra.Group;

/**
 * The Class PlayerCommandGroup.
 *
 * This class contains the functionality for transforming {@link PlayerCommand}.
 *
 * @author Rimon Oz
 */
public interface PlayerCommandGroup extends Group<PlayerCommand> {

  /**
   * Limits the velocity vectors contained inside the strategy.
   *
   * @param strategy   The strategy to limit.
   * @param speedLimit max speed ratio (1.0 = 100%)
   * @return The limited strategy.
   */
  default Strategy.Supplier limit(
      final Strategy.Supplier strategy,
      final double speedLimit
  ) {
    return () -> strategy.getStrategy().entrySet().stream()
        .collect(Collectors.toMap(
            Entry::getKey,
            entry -> {
              final double velocityMagnitude
                  = Math.sqrt(Math.pow(entry.getValue().getVelocityX(), 2)
                  + Math.pow(entry.getValue().getVelocityX(), 2));
              return new PlayerCommand.State(
                  (float) (speedLimit / velocityMagnitude
                          * Math.tanh(entry.getValue().getVelocityX())),
                  (float) (speedLimit / velocityMagnitude
                          * Math.tanh(entry.getValue().getVelocityY())),
                  (float) (speedLimit / velocityMagnitude
                          * Math.tanh(entry.getValue().getVelocityR())),
                  entry.getValue().getFlatKick(),
                  entry.getValue().getChipKick(),
                  entry.getValue().getDribblerSpin());
            }));
  }

  /**
   * Computes the sum of two {@link PlayerCommand PlayerCommandGroup}.
   *
   * @param leftCommand  The first {@link PlayerCommand} to be summed.
   * @param rightCommand The second {@link PlayerCommand} to be summed.
   * @return The sum of the two {@link PlayerCommand commands}.
   */
  @Override
  default PlayerCommand add(final PlayerCommand leftCommand,
      final PlayerCommand rightCommand) {
    return new PlayerCommand.State(
        leftCommand.getVelocityX() + rightCommand.getVelocityX(),
        leftCommand.getVelocityY() + rightCommand.getVelocityY(),
        leftCommand.getVelocityR() + rightCommand.getVelocityR(),
        leftCommand.getFlatKick() + rightCommand.getFlatKick(),
        leftCommand.getChipKick() + rightCommand.getChipKick(),
        leftCommand.getDribblerSpin() + rightCommand.getDribblerSpin());
  }

  /**
   * Computes the additive inverse of the supplied {@link PlayerCommand}.
   *
   * @param command The {@link PlayerCommand} to compute the additive inverse of.
   * @return The additive inverse of the supplied {@link PlayerCommand}.
   */
  @Override
  default PlayerCommand getAdditiveInverse(final PlayerCommand command) {
    return new PlayerCommand.State(
        (-1f) * command.getVelocityX(),
        (-1f) * command.getVelocityY(),
        (-1f) * command.getVelocityR(),
        command.getFlatKick(),
        command.getChipKick(),
        command.getDribblerSpin());
  }

  /**
   * Computes the sum of two (or more) {@link PlayerCommand PlayerCommandGroup}.
   *
   * @param commands A primitive array of {@link PlayerCommand PlayerCommandGroup} to be
   *                 summed.
   * @return The sum of the supplied {@link PlayerCommand PlayerCommandGroup}.
   */
  default PlayerCommand add(final PlayerCommand... commands) {
    return Stream.of(commands).reduce(PlayerCommand.State.STOP, this::add);
  }

  /**
   * Combines the {@link PlayerCommand PlayerCommandGroup} by the {@link Player} they are
   * addressed to in the supplied strategies using the supplied binary operator.
   *
   * @param leftMap  The first {@link Map} to combine.
   * @param rightMap The second {@link Map} to combine.
   * @param combiner The {@link BinaryOperator combiner function}.
   * @return The combined {@link Strategy.Supplier}.
   */
  default Strategy.Supplier combineByPlayer(
      final Strategy.Supplier leftMap,
      final Strategy.Supplier rightMap,
      final BinaryOperator<PlayerCommand> combiner
  ) {
    return () -> leftMap.getStrategy().entrySet().stream()
        .map(entry ->
            new SimpleImmutableEntry<>(entry.getKey(),
                rightMap.getStrategy().containsKey(entry.getKey())
                    ? combiner
                    .apply(rightMap.getStrategy().get(entry.getKey()), entry.getValue())
                    : entry.getValue()))
        .collect(Collectors.toMap(
            SimpleImmutableEntry::getKey,
            SimpleImmutableEntry::getValue));
  }
}
