package io.leonis.subra.ipc.peripheral;

import com.google.common.collect.ImmutableSet;
import io.leonis.zosma.game.Identity;
import io.leonis.zosma.ipc.peripheral.Controller;
import io.leonis.zosma.ipc.peripheral.Controller.ControllerIdentity;
import io.leonis.zosma.ipc.peripheral.component.Dpad;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;

/**
 * The Class MoveControllerFunction
 *
 * A wierdly name class that doesn't work as supposed yet...
 *
 * @author Rimon Oz
 */
@AllArgsConstructor
public final class MoveControllerFunction<I extends Identity, C extends Controller & Dpad.Supplier> implements BiFunction<Map<I, Set<ControllerIdentity>>, C, Map<I, Set<ControllerIdentity>>> {
  private final Comparator<I> keyComparator;

  @Override
  public Map<I, Set<ControllerIdentity>> apply(final Map<I, Set<ControllerIdentity>> controllerMapping,
      final C controller) {
      final TreeSet<I> ids = controllerMapping.entrySet().stream()
          .map(Entry::getKey)
          .collect(Collectors.toCollection(() -> new TreeSet<>(keyComparator)));
      return controllerMapping.entrySet().stream()
          .collect(Collectors.toMap(
              Entry::getKey,
              entry -> {
                if (controller.getDpad().isLeft()
                    && controllerMapping.containsKey(ids.higher(entry.getKey()))
                    && controllerMapping.get(ids.higher(entry.getKey()))
                    .contains(controller.getIdentity())
                    || (controller.getDpad().isRight()
                    && controllerMapping.containsKey(ids.lower(entry.getKey()))
                    && controllerMapping.get(ids.lower(entry.getKey()))
                    .contains(controller.getIdentity()))
                    ) {
                  return ImmutableSet.<ControllerIdentity>builder()
                      .addAll(entry.getValue())
                      .add(controller.getIdentity())
                      .build();
                } else if (controller.getDpad().isLeft() || controller.getDpad().isRight()) {
                  return entry.getValue().stream()
                      .filter(a -> !a.equals(controller.getIdentity()))
                      .collect(Collectors.toSet());
                }
                return entry.getValue();
              }));
  }
}
