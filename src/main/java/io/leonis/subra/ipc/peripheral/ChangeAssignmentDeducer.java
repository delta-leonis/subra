package io.leonis.subra.ipc.peripheral;

import com.google.common.collect.ImmutableSet;
import io.leonis.subra.ipc.peripheral.ChangeAssignmentDeducer.Frame;
import io.leonis.zosma.game.Identity;
import io.leonis.zosma.game.engine.Deducer;
import io.leonis.zosma.ipc.peripheral.Controller;
import io.leonis.zosma.ipc.peripheral.Controller.ControllerIdentity;
import io.leonis.zosma.ipc.peripheral.component.Dpad;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Value;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toSet;

/**
 * @author jeroen.dejong.
 */
@Value
public class ChangeAssignmentDeducer<
    I extends Identity,
    C extends Controller & Dpad.Supplier,
    T extends Controller.MapSupplier<I> & Controller.SetSupplier<C>>
implements Deducer<T, Frame<I>> {

  private Comparator<I> comparator;

  private BiFunction<Map<I, Set<ControllerIdentity>>, C, Map<I, Set<ControllerIdentity>>> f =
      (controllerMapping, controller) -> {
        final TreeSet<I> ids = controllerMapping.entrySet().stream()
            .map(Entry::getKey)
            .collect(Collectors.toCollection(() -> new TreeSet<>(comparator)));

        return controllerMapping.entrySet().stream()
            .collect(Collectors.toMap(
                Entry::getKey,
                entry -> {
                  if (controller.getDpad().isLeft()
                      && controllerMapping.get(ids.higher(entry.getKey()))
                      .contains(controller.getIdentity())
                      || (controller.getDpad().isRight()
                      && controllerMapping.get(ids.lower(entry.getKey()))
                      .contains(controller.getIdentity()))
                      ) {
                    return ImmutableSet.<ControllerIdentity>builder()
                        .addAll(entry.getValue())
                        .add(controller.getIdentity())
                        .build();
                  }
                  else if (controller.getDpad().isLeft() || controller.getDpad().isRight()) {
                    return entry.getValue().stream()
                        .filter(a -> a.equals(controller.getIdentity()))
                        .collect(Collectors.toSet());
                  }
                  return entry.getValue();
                }));
      };

  @Override
  public Publisher<Frame<I>> apply(final Publisher<T> tPublisher) {
    return Flux.from(tPublisher)
        .map(c -> c.getControllerSet().stream()
            .reduce(c.getControllerMapping(),
                f,
                (a, b) -> Stream.of(a.entrySet(), b.entrySet())
                    .flatMap(Collection::stream)
                    .flatMap(entry -> entry.getValue().stream().map(controller -> new SimpleImmutableEntry<>(entry.getKey(), controller)))
                    .collect(groupingBy(SimpleImmutableEntry::getKey,
                        mapping(SimpleImmutableEntry::getValue, toSet())))))
        .map(Frame::new);
  }

  @Value
  static class Frame<I extends Identity> implements Controller.MapSupplier<I> {
    private final Map<I, Set<ControllerIdentity>> controllerMapping;
  }
}
