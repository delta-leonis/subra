package io.leonis.subra.gui.field;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.Panel;
import io.leonis.algieba.spatial.PotentialField;
import io.leonis.subra.game.data.*;
import io.leonis.subra.math.spatial.GaussianPotentialField;
import io.leonis.torch.component.graph.Gradient;
import java.awt.Color;
import java.util.function.BiFunction;
import lombok.AllArgsConstructor;

/**
 * @author Jeroen de Jong
 */
@AllArgsConstructor
public class GaussianField<S extends Player.SetSupplier & Ball.SetSupplier & io.leonis.subra.game.data.Field.Supplier & PotentialField.Supplier> extends
    Panel {

  public GaussianField(
      final S data
  ) {
    final BiFunction<Integer, Integer, TextColor> backgroundSupplier = new GaussianPotentialFieldBackground(
        (GaussianPotentialField) data.getPotentialField(), new Gradient(Color.GREEN, Color.RED)
    );
    this.addComponent(new EmptySpace(backgroundSupplier));
    this.addComponent(new FieldLines(data.getField().getLines(), backgroundSupplier));
    this.addComponent(new Balls(data.getBalls()));
    this.addComponent(new Robots(data.getPlayers()));
  }
}
