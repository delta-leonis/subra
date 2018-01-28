package io.leonis.subra.ipc.peripheral;

import com.google.common.collect.ImmutableSet;
import io.leonis.subra.ipc.peripheral.ChangeAssignmentDeducer.Frame;
import io.leonis.zosma.game.Identity;
import io.leonis.zosma.game.engine.Deducer;
import io.leonis.zosma.ipc.peripheral.Controller;
import io.leonis.zosma.ipc.peripheral.Controller.*;
import io.leonis.zosma.ipc.peripheral.component.Dpad;
import java.util.*;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Value;
import lombok.experimental.Delegate;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toSet;

/**
 * @author jeroen.dejong.
 * todo This class should probably not return a Controller.SetSupplier, just to reduce it's responsibility.
 */
@Value
public class ChangeAssignmentDeducer<
    I extends Identity,
    C extends Controller & Dpad.Supplier,
    M extends Controller.MapSupplier<I>,
    S extends Controller.SetSupplier<C>>
implements Deducer<S, Frame<C, I>> {

  private BiFunction<Map<I, Set<ControllerIdentity>>, C, Map<I, Set<ControllerIdentity>>> combiner;

  private final AtomicReference<Map<I, Set<ControllerIdentity>>> currentMapping =
      new AtomicReference<>(Collections.emptyMap());

  public ChangeAssignmentDeducer(final Publisher<M> mapSource, final Comparator<I> comparator) {
    // Override current mapping with whatever comes through the publisher.
    Flux.from(mapSource)
        .map(MapSupplier::getControllerMapping)
        .subscribe(currentMapping::set);

    // Build the combiner based on the comparator in the constructor
    // TODO this should probably become a seperate function on it's own.
    this.combiner = (controllerMapping, controller) -> {
      final TreeSet<I> ids = controllerMapping.entrySet().stream()
          .map(Entry::getKey)
          .collect(Collectors.toCollection(() -> new TreeSet<>(comparator)));

      return controllerMapping.entrySet().stream()
          .collect(Collectors.toMap(
              Entry::getKey,
              entry -> {
                if (controller.getDpad().isRight()
                    && controllerMapping.containsKey(ids.higher(entry.getKey()))
                    && controllerMapping.get(ids.higher(entry.getKey()))
                    .contains(controller.getIdentity())
                    || (controller.getDpad().isLeft()
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
                      .filter(a -> a.equals(controller.getIdentity()))
                      .collect(Collectors.toSet());
                }
                return entry.getValue();
              }));
    };
  }

  @Override
  public Publisher<Frame<C, I>> apply(final Publisher<S> setPublisher) {
    return Flux.combineLatest(setPublisher,
        Flux.from(setPublisher)
          .map(c -> c.getControllerSet().stream()
              .reduce(currentMapping.get(),
                  combiner,
                  (a, b) -> Stream.of(a.entrySet(), b.entrySet())
                      .flatMap(Collection::stream)
                      .flatMap(entry -> entry.getValue().stream().map(controller -> new SimpleImmutableEntry<>(entry.getKey(), controller)))
                      .collect(groupingBy(Entry::getKey,
                          mapping(Entry::getValue, toSet())))))
          .doOnNext(currentMapping::set) // update currentMap to contain moved controllers
        , Frame::new);
  }

  @Value
  public static class Frame<C extends Controller & Dpad.Supplier, I extends Identity> implements Controller.SetSupplier<C>, Controller.MapSupplier<I> {
    @Delegate
    private final Controller.SetSupplier<C> setSupplier;
    private final Map<I, Set<ControllerIdentity>> controllerMapping;
  }
}
