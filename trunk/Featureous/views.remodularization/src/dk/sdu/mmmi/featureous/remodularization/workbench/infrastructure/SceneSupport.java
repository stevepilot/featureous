/*
 * Featureous is distributed under the GPLv3 license.
 *
 * University of Southern Denmark, 2011
 */
package dk.sdu.mmmi.featureous.remodularization.workbench.infrastructure;

import org.netbeans.api.visual.widget.Scene;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.widget.Widget;

/**
 * @author David Kaspar
 */
public class SceneSupport {

    public static void sleep(int delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static final class ShiftKeySwitchToolAction extends WidgetAction.Adapter {

        public static final String REFACTOR_CLASS = "moveClass";
        public static final String ALIGN = "align";
        private final boolean editable;

        public ShiftKeySwitchToolAction(Scene scene, boolean editable) {
            this.editable = editable;
            scene.setActiveTool(ALIGN);
        }

        public State keyPressed(Widget widget, WidgetKeyEvent event) {
            if (event.getKeyCode() == KeyEvent.VK_SHIFT && editable) {
                widget.getScene().setActiveTool(REFACTOR_CLASS);
            }
            return State.REJECTED;
        }

        public State keyReleased(Widget widget, WidgetKeyEvent event) {
            if (event.getKeyCode() == KeyEvent.VK_SHIFT) {
                widget.getScene().setActiveTool(ALIGN);
            }
            return State.REJECTED;
        }
    }

    public static class SloppyStroke implements Stroke {

        BasicStroke stroke;
        float sloppiness;

        public SloppyStroke(float width, float sloppiness) {
            this.stroke = new BasicStroke(width); // Used to stroke modified shape
            this.sloppiness = sloppiness; // How sloppy should we be?
        }

        public Shape createStrokedShape(Shape shape) {
            GeneralPath newshape = new GeneralPath(); // Start with an empty shape

            // Iterate through the specified shape, perturb its coordinates, and
            // use them to build up the new shape.
            float[] coords = new float[6];
            for (PathIterator i = shape.getPathIterator(null); !i.isDone(); i.next()) {
                int type = i.currentSegment(coords);
                switch (type) {
                    case PathIterator.SEG_MOVETO:
                        perturb(coords, 2);
                        newshape.moveTo(coords[0], coords[1]);
                        break;
                    case PathIterator.SEG_LINETO:
                        perturb(coords, 2);
                        newshape.lineTo(coords[0], coords[1]);
                        break;
                    case PathIterator.SEG_QUADTO:
                        perturb(coords, 4);
                        newshape.quadTo(coords[0], coords[1], coords[2], coords[3]);
                        break;
                    case PathIterator.SEG_CUBICTO:
                        perturb(coords, 6);
                        newshape.curveTo(coords[0], coords[1], coords[2], coords[3],
                                coords[4], coords[5]);
                        break;
                    case PathIterator.SEG_CLOSE:
                        newshape.closePath();
                        break;
                }
            }

            // Finally, stroke the perturbed shape and return the result
            return stroke.createStrokedShape(newshape);
        }

        // Randomly modify the specified number of coordinates, by an amount
        // specified by the sloppiness field.
        void perturb(float[] coords, int numCoords) {
            for (int i = 0; i < numCoords; i++) {
                coords[i] += (float) ((Math.random() * 2 - 1.0) * sloppiness);
            }
        }
    }
}
