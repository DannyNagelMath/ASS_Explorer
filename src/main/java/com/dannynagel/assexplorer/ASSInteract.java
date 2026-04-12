package com.dannynagel.assexplorer;

import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;

public class ASSInteract extends Pane {

    private final double HEIGHT = 600;
    private final double WIDTH = 600;

    //Angle,Side,Side with angle bottom left on
    //horizontal base and then going clockwise.
    //angle and swing are opposite each other.
    //side is on the left.
    private double angle, sideLen, swingLen;

    private Line base, side, swing, shortGreen, longGreen, altitude;
    private Circle circle;
    private Rectangle longSwingRectangle, shortSwingRectangle, rightAngleRectangle;
    private Arc arc;
    private final double SCALE_FACTOR;
    private final double ANGLE_RADIAN;
    private ASSData data;
    private boolean dragOn; //method probably already exists in mouseEvent

    ASSInteract(double angle, double sideLen, double swingLen) {
        setPrefSize(WIDTH, HEIGHT);

        BackgroundFill background_fill = new BackgroundFill(Color.GAINSBORO,
                CornerRadii.EMPTY, Insets.EMPTY);
        Background background = new Background(background_fill);
        setBackground(background);

        //ASS inputs
        this.angle = angle;
        this.sideLen = sideLen;
        this.swingLen = swingLen;

        ANGLE_RADIAN = angle * Math.PI / 180;
        data = new ASSData(angle, sideLen, swingLen);

        //initializes base as well
        SCALE_FACTOR = setUpScaleAndBase();

        setUpSide();

        //swing's endX,endY are bound to circle in setUpCircle()
        setUpSwing();

        //also initializes rightAngleRectangle
        setUpAltitude();

        //longGreen,shortGreen are the possible solution sides
        setUpLongGreen();
        setUpShortGreen();

        //Most important node. All mouseDrag events start here.
        setUpCircle();

        //dragOn indicates if circle is in a drag state.
        //I only use this to have either triangle solution
        //persist when the user releases the mouse drag on circle.
        //See the mouseExited events in setUp(long/short)SwingRectangle
        //and the method checkIntersection().
        dragOn = false;

        //setUp(Long/Short)SwingRectangle is used for holding cursor
        //in place when dragging over a solution
        setUpLongSwingRectangle();
        setUpShortSwingRectangle();

        //used for displaying the angle
        setUpArc();

        //Important to start with circle on top.
        getChildren().addAll(arc, altitude, swing, base, side, longGreen, shortGreen,
                longSwingRectangle, shortSwingRectangle, rightAngleRectangle, circle);
    }

    // return is used to initialize SCALE_FACTOR.
    // Scales and initializes base as a function of angle, sideLen, swingLen.
    // Positions and scales base such that user can rotate swing completely.
    private double setUpScaleAndBase() {
        double yMin, yMax, xMin, xMax, yWidth, xWidth, scaleFactor;

        //yWidth is the necessary minimum width
        yMin = Math.min(0, sideLen * Math.sin(ANGLE_RADIAN) - swingLen);
        yMax = sideLen * Math.sin(ANGLE_RADIAN) + swingLen;
        yWidth = yMax - yMin;

        //xWidth is the necessary minimum width
        xMin = Math.min(0, sideLen * Math.cos(ANGLE_RADIAN) - swingLen);
        xMax = Math.max(0, sideLen * Math.cos(ANGLE_RADIAN) + swingLen);
        xWidth = xMax - xMin;

        // Use 80% so there is buffer.
        // Use max(xWidth,yWidth) to scale window to make sure everything fits.
        scaleFactor = 0.8 * WIDTH / Math.max(xWidth, yWidth);

        // Position base so that swing can rotate completely.
        // Want to improve in the future especially for obtuse case.
        base = new Line();
        if (xMin == 0)
            base.setStartX(WIDTH * 0.1);
        else
            base.setStartX(-1 * xMin * scaleFactor + WIDTH * 0.1);
        if (yMin == 0)
            base.setStartY(HEIGHT * 0.8);
        else
            base.setStartY(HEIGHT + yMin * scaleFactor - HEIGHT * 0.2);
        base.setEndX(WIDTH);
        base.setEndY(base.getStartY());

        base.getStrokeDashArray().addAll(20d, 10d);
        return scaleFactor;
    }

    private void setUpSide() {
        side = new Line(base.getStartX(),
                base.getStartY(),
                base.getStartX() + sideLen * SCALE_FACTOR * Math.cos(ANGLE_RADIAN),
                base.getStartY() - sideLen * SCALE_FACTOR * Math.sin(ANGLE_RADIAN));

        side.setStrokeWidth(3);
        side.setStroke(Color.BLUE);
    }

    private void setUpSwing() {
        swing = new Line(side.getEndX(),
                side.getEndY(),
                side.getEndX() + swingLen * SCALE_FACTOR,
                side.getEndY());

        swing.setStrokeWidth(3);
        swing.setStroke(Color.RED);
    }

    //also initializes rightAngleRectangle
    private void setUpAltitude() {
        altitude = new Line(swing.getStartX(),
                swing.getStartY(),
                swing.getStartX(),
                base.getStartY());
        altitude.setStroke(Color.ORANGE);
        altitude.setStrokeWidth(3);

        rightAngleRectangle = new Rectangle(altitude.getEndX(),
                altitude.getEndY() - 10,
                10,
                10);
        rightAngleRectangle.setFill(null);
        rightAngleRectangle.setStroke(Color.ORANGE);
        rightAngleRectangle.setStrokeWidth(2);

    }

    public void showAltitude() {
        altitude.setOpacity(1);
        rightAngleRectangle.setOpacity(1);
    }

    public void hideAltitude() {
        altitude.setOpacity(0);
        rightAngleRectangle.setOpacity(0);
    }

    //longGreen opacity set to 1 when mouse
    //intersects (and subsequently "locks")
    // with longSwingRectangle.
    private void setUpLongGreen() {
        longGreen = new Line(base.getStartX(),
                base.getStartY(),
                base.getStartX() + data.getBaseLenLong() * SCALE_FACTOR,
                base.getStartY());

        longGreen.setStroke(Color.GREEN);
        longGreen.setStrokeWidth(3);
        longGreen.setOpacity(0);
    }

    private void setUpShortGreen() {
        shortGreen = new Line(base.getStartX(),
                base.getStartY(),
                base.getStartX() + data.getBaseLenShort() * SCALE_FACTOR,
                base.getStartY());

        shortGreen.setStroke(Color.GREEN);
        shortGreen.setStrokeWidth(3);
        shortGreen.setOpacity(0);
    }

    // Most important node... lots of stuff.
    // Circle is bound to the endpoint of swing
    // and is what the user grabs to rotate swing and find solutions.
    private void setUpCircle() {
        circle = new Circle(swing.getEndX(), swing.getEndY(), 7);

        //bind to swing's endpoints
        swing.endXProperty().bind(circle.centerXProperty());
        swing.endYProperty().bind(circle.centerYProperty());

        circle.setCursor(Cursor.HAND);

        //All mouse events: Entered, Pressed, Released, DragDetected, Dragged
        circle.setOnMouseEntered(event -> {
            //change the z-coordinate of the circle
            circle.toFront();
        });

        circle.setOnMousePressed(event -> {
            dragOn = true;

            //Setting circle to transparent allows the mouse
            //to find other nodes when you are passing over them
            //and the circle is still underneath your cursor.
            //General solution I believe.
            circle.setMouseTransparent(true);
        });

        circle.setOnMouseReleased(event -> {
            circle.setMouseTransparent(false);
            dragOn = false;
        });

        //This is vital. Learn more about it
        circle.setOnDragDetected(event -> {
            circle.startFullDrag();
        });

        //We limit the mouse's (x,y) to the circle generated
        //by rotating swing at pivot point swingStartX,swingStartY.
        //Effect: circle aligns (towards to cursor) along a radius from
        // swingStartX, swingStartY through the cursor.
        circle.setOnMouseDragged(event -> {
            double xDiff = event.getSceneX() - (swing.getStartX() + getTranslateX());
            double yDiff = event.getSceneY() - (swing.getStartY() + getTranslateY());
            double hypotenuse = Math.sqrt(yDiff * yDiff + xDiff * xDiff);

            circle.setCenterX(swing.getStartX() + swingLen * SCALE_FACTOR * xDiff / hypotenuse);
            circle.setCenterY(swing.getStartY() + swingLen * SCALE_FACTOR * yDiff / hypotenuse);
        });

    }

    // shortSwingRectangle is a linear extension of swing.
    // Used for the shortGreen intersection hold feeling.
    private void setUpShortSwingRectangle() {
        shortSwingRectangle = new Rectangle(swing.getStartX(), swing.getStartY(), 2000, 30);

        //degrees
        double angleRotateShortRectangle = data.getSideAngleShort();
        double angleNormalShort = angleRotateShortRectangle - 90;

        // Re-adjust along the perpendicular (angleNormalShort).
        // Had to use setLayoutX/Y.  setX() and setY() had funny behavior.
        shortSwingRectangle.setLayoutX(shortSwingRectangle.getHeight() * 0.5 * Math.cos(angleNormalShort * Math.PI / 180));
        shortSwingRectangle.setLayoutY(shortSwingRectangle.getHeight() * 0.5 * Math.sin(angleNormalShort * Math.PI / 180));

        //We adjusted before rotating?..but it works?
        Rotate rotateShort = new Rotate(angleRotateShortRectangle, swing.getStartX(), swing.getStartY());
        shortSwingRectangle.getTransforms().addAll(rotateShort);

        shortSwingRectangle.setOpacity(0);

        //All the mouseDrag events: Entered, DragOver, Exited

        //Sets circle at solution endpoint
        shortSwingRectangle.setOnMouseDragEntered(event -> {
            if (data.getNumSolutions() <= 1)
                return;
            shortGreen.setOpacity(1);
            base.setOpacity(0);
            circle.setCenterX(shortGreen.getEndX());
            circle.setCenterY(shortGreen.getEndY());
        });

        //Holds circle at solution endpoint
        shortSwingRectangle.setOnMouseDragOver(event -> {
            if (data.getNumSolutions() <= 1)
                return;
            circle.setCenterX(shortGreen.getEndX());
            circle.setCenterY(shortGreen.getEndY());
        });

        //No longer holds circle
        shortSwingRectangle.setOnMouseDragExited(event -> {
            if (data.getNumSolutions() <= 1)
                return;
            shortGreen.setOpacity(0);
            base.setOpacity(1);

            //For keeping shortGreen showing after releasing mouse-click.
            if (dragOn)
                return;
            else
                checkIntersection();
        });
    }

    //see setUpShortRectangle
    private void setUpLongSwingRectangle() {

        longSwingRectangle = new Rectangle(swing.getStartX(), swing.getStartY(), 2000, 30);

        //degrees
        double angleRotateLongRectangle = data.getSideAngleLong();
        double angleNormalLong = angleRotateLongRectangle - 90;

        longSwingRectangle.setLayoutX(longSwingRectangle.getHeight() * 0.5 * Math.cos(angleNormalLong * Math.PI / 180));
        longSwingRectangle.setLayoutY(longSwingRectangle.getHeight() * 0.5 * Math.sin(angleNormalLong * Math.PI / 180));

        Rotate rotate = new Rotate(angleRotateLongRectangle, swing.getStartX(), swing.getStartY());
        longSwingRectangle.getTransforms().addAll(rotate);

        longSwingRectangle.setOpacity(0);

        //All the mouseDrag events: Entered, DragOver, Exited
        longSwingRectangle.setOnMouseDragEntered(event -> {
            if (data.getNumSolutions() == 0)
                return;
            longGreen.setOpacity(1);
            base.setOpacity(0);
            circle.setCenterX(longGreen.getEndX());
            circle.setCenterY(longGreen.getEndY());
        });

        longSwingRectangle.setOnMouseDragOver(event -> {
            if (data.getNumSolutions() == 0)
                return;
            circle.setCenterX(longGreen.getEndX());
            circle.setCenterY(longGreen.getEndY());
        });

        longSwingRectangle.setOnMouseDragExited(event -> {
            longGreen.setOpacity(0);
            base.setOpacity(1);

            if (dragOn)
                return;
            else
                checkIntersection();
        });
    }

    //overall shit.  need to improve
    private void setUpArc() {
        arc = new Arc();
        arc.setCenterX(base.getStartX());
        arc.setCenterY(base.getStartY());
        arc.setRadiusX(20.0f);
        arc.setRadiusY(20.0f);
        arc.setStartAngle(0);
        arc.setLength(angle);
        arc.setType(ArcType.OPEN);
        arc.setFill(null);
        arc.setStroke(Color.RED);
        arc.setStrokeWidth(2);
    }

    //Slick way to check shape intersection.
    //.intersect() creates the Shape of the intersection.
    //We check if that shape has actual width (!= -1)
    //We only use to persist a solution after user releases mouse.
    public void checkIntersection() {
        Shape intersectLong = Shape.intersect(circle, longSwingRectangle);
        Shape intersectShort = Shape.intersect(circle, shortSwingRectangle);

        //check longSwingRectangle intersection with circle
        if (intersectLong.getBoundsInLocal().getWidth() != -1) {
            if (data.getNumSolutions() == 0)
                return;
            circle.setCenterX(longGreen.getEndX());
            circle.setCenterY(longGreen.getEndY());
            longGreen.setOpacity(1);
            base.setOpacity(0);
        }

        //check shortSwingRectangle intersection with circle
        else if (intersectShort.getBoundsInLocal().getWidth() != -1) {
            if (data.getNumSolutions() <= 1)
                return;
            circle.setCenterX(shortGreen.getEndX());
            circle.setCenterY(shortGreen.getEndY());
            shortGreen.setOpacity(1);
            base.setOpacity(0);
        } else {
            longGreen.setOpacity(0);
            base.setOpacity(1);
        }
    }

    public ASSData getData() {
        return data;
    }

    public Line getSide() {
        return side;
    }

    public Line getLongGreen() {
        return longGreen;
    }

    public Line getShortGreen() {
        return shortGreen;
    }

    public Circle getCircle() {
        return circle;
    }

    public Line getSwing() {
        return swing;
    }

    public double getStartXBase() {
        return base.getStartX();
    }

    public double getStartYBase() {
        return base.getStartY();
    }
}
