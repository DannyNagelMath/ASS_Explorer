package com.dannynagel.assexplorer;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.text.DecimalFormat;

// Displays the solution(s) in a new stage: Diagnosis, Values, Drawing
// Should add functionality that it closes with the app as well as closes
// whenever a second SolveStage opens.
// Does not interact with ASSLabels.  All Labels are made/updated locally.
public class SolveStage {

    private Pane layout;

    // We need interact for its data and lines.
    private ASSInteract interact;

    private int numSolutions;

    // numSolutions and reason.
    private Text diagnosis;

    // Lines taken from interact. Ultimately added to PaneLongTriangle and Short
    private Line sideLong, sideShort, swingLong, swingShort, longGreen, shortGreen;

    // Displays the solution data.
    private Text textShortData, textLongData;

    // Store the acute and obtuse triangles respectively.
    private Pane paneLongTriangle, paneShortTriangle;

    // VBoxes store the textSolution and paneTriangle
    private VBox vBoxLong, vBoxShort;

    // Only used when numSolutions == 2.  Stores both VBoxes
    private HBox hBoxSolutions;

    // Angle Labels
    private Label labelAngleSideLong, labelAngleSideShort,
            labelAngleSwingLong, labelAngleSwingShort,
            labelAngleBaseLong, labelAngleBaseShort;

    // Side Labels
    private Label labelSideLong, labelSideShort,
            labelSwingLong, labelSwingShort,
            labelBaseLong, labelBaseShort;

    // Strings used in angle and side labels
    private String stringAngleSwing, stringAngleSide, stringAngleBase,
            stringSwing, stringSide, stringBase;

    // Use their data to determine the appropriate strings
    private ChoiceBox choiceBoxAngle, choiceBoxSide, choiceBoxSwing;


    SolveStage(ASSInteract interact, ChoiceBox angle, ChoiceBox side, ChoiceBox swing) {
        this.interact = interact;
        choiceBoxAngle = angle;
        choiceBoxSide = side;
        choiceBoxSwing = swing;

        Stage stage = new Stage();
        stage.setTitle("Solution");

        // Essential method, builds everything.
        setUpLayout();

        Scene scene = new Scene(layout);
        stage.setScene(scene);
        stage.show();
    }

    public void setUpLayout() {
        layout = new Pane();
        layout.setPadding(new Insets(0, 20, 20, 0));

        Background background = new Background(new BackgroundFill(Color.AZURE,
                CornerRadii.EMPTY, Insets.EMPTY));
        layout.setBackground(background);

        numSolutions = interact.getData().getNumSolutions();

        // Initializes side and angle strings used in labels
        setUpStrings();

        // Initializes diagnosis.  Does NOT add it to any parent yet.
        // Has to be called after setUpStrings to avoid nulls.
        setUpDiagnosis();

        // Initializes both paneTriangles (along with all their lines) and adds to layout.
        // Labels are added later.
        setUpTriangles();

        // Initializes the side labels and adds them to paneLongTriangle
        setUpLabelSidesLong();
        setUpLabelSidesShort();

        // Initializes the angle labels and adds them to paneLongTriangle
        setUpLabelsAnglesLong();
        setUpLabelsAnglesShort();

        if (numSolutions == 1) {
            textLongData = new Text(solutionTextLong());
            textLongData.setStyle("-fx-font-size:24");
            textLongData.setTranslateX(20);
            textLongData.setTranslateY(30);

            vBoxLong = new VBox();
            vBoxLong.getChildren().addAll(diagnosis, textLongData);
            paneLongTriangle.setTranslateY(-20);

            layout.getChildren().addAll(vBoxLong, paneLongTriangle);

        } else if (numSolutions == 2) {
            // why?
            layout.setMaxHeight(600);

            paneLongTriangle.setTranslateY(-200);

            textLongData = new Text(solutionTextLong());
            textLongData.setStyle("-fx-font-size:24");
            textLongData.setTranslateX(20);

            vBoxLong = new VBox();
            vBoxLong.getChildren().addAll(diagnosis, textLongData, paneLongTriangle);

            paneShortTriangle.setTranslateY(-200);

            textShortData = new Text(solutionTextShort());
            textShortData.setStyle("-fx-font-size:24");

            vBoxShort = new VBox();
            vBoxShort.getChildren().addAll(diagnosis, textShortData, paneShortTriangle);

            hBoxSolutions = new HBox();
            hBoxSolutions.setSpacing(50);
            hBoxSolutions.getChildren().addAll(vBoxLong, vBoxShort);

            // Holds diagnosis and then everything else below it in hBoxSolutions
            VBox vBox2Solutions = new VBox();
            vBox2Solutions.setSpacing(50);
            vBox2Solutions.getChildren().addAll(diagnosis, hBoxSolutions);

            layout.getChildren().addAll(vBox2Solutions);
        } else //(numSolutions == 0)
            layout.getChildren().addAll(diagnosis);
    }


    // Initializes and adds to layout both paneTriangles and their lines.
    public void setUpTriangles() {
        sideLong = new Line(interact.getSide().getStartX(),
                interact.getSide().getStartY(),
                interact.getSide().getEndX(),
                interact.getSide().getEndY());
        sideLong.setStroke(Color.BLUE);
        sideLong.setStrokeWidth(3);

        longGreen = new Line(interact.getLongGreen().getStartX(),
                interact.getLongGreen().getStartY(),
                interact.getLongGreen().getEndX(),
                interact.getLongGreen().getEndY());
        longGreen.setStroke(Color.GREEN);
        longGreen.setStrokeWidth(3);

        swingLong = new Line(interact.getSwing().getStartX(),
                interact.getSwing().getStartY(),
                longGreen.getEndX(),
                longGreen.getEndY());
        swingLong.setStrokeWidth(3);
        swingLong.setStroke(Color.RED);

        paneLongTriangle = new Pane();
        paneLongTriangle.getChildren().addAll(sideLong, longGreen, swingLong);


        sideShort = new Line(interact.getSide().getStartX(),
                interact.getSide().getStartY(),
                interact.getSide().getEndX(),
                interact.getSide().getEndY());
        sideShort.setStroke(Color.BLUE);
        sideShort.setStrokeWidth(3);

        shortGreen = new Line(interact.getShortGreen().getStartX(),
                interact.getShortGreen().getStartY(),
                interact.getShortGreen().getEndX(),
                interact.getShortGreen().getEndY());
        shortGreen.setStroke(Color.GREEN);
        shortGreen.setStrokeWidth(3);

        swingShort = new Line(interact.getSwing().getStartX(),
                interact.getSwing().getStartY(),
                shortGreen.getEndX(),
                shortGreen.getEndY());
        swingShort.setStrokeWidth(3);
        swingShort.setStroke(Color.RED);

        paneShortTriangle = new Pane();
        paneShortTriangle.getChildren().addAll(sideShort, shortGreen, swingShort);
    }

    // Set up diagnosis.  Diagnosis is eventually added it to the top of the VBox
    public void setUpDiagnosis() {
        diagnosis = new Text(diagnosis());
        diagnosis.setStyle("-fx-font-size:16");
        diagnosis.setTranslateX(20);
        diagnosis.setTranslateY(20);
    }

    public void setUpLabelsAnglesLong() {
        labelAngleSwingLong = new Label(stringAngleSwing);
        labelAngleSwingLong.setFont(new Font(20));
        labelAngleSwingLong.setTranslateX(longGreen.getStartX() - 18);
        labelAngleSwingLong.setTranslateY(longGreen.getStartY() - 12);

        labelAngleBaseLong = new Label(stringAngleBase);
        labelAngleBaseLong.setFont(new Font(20));
        labelAngleBaseLong.setTranslateX(swingLong.getStartX() - 6);
        labelAngleBaseLong.setTranslateY(swingLong.getStartY() - 30);

        labelAngleSideLong = new Label(stringAngleSide);
        labelAngleSideLong.setFont(new Font(20));
        labelAngleSideLong.setTranslateX(longGreen.getEndX() + 6);
        labelAngleSideLong.setTranslateY(longGreen.getEndY() - 12);

        paneLongTriangle.getChildren().addAll(labelAngleSideLong, labelAngleBaseLong, labelAngleSwingLong);
    }

    public void setUpLabelsAnglesShort() {
        labelAngleSwingShort = new Label(stringAngleSwing);
        labelAngleSwingShort.setFont(new Font(20));
        labelAngleSwingShort.setTranslateX(shortGreen.getStartX() - 18);
        labelAngleSwingShort.setTranslateY(shortGreen.getStartY() - 12);

        labelAngleBaseShort = new Label(stringAngleBase);
        labelAngleBaseShort.setFont(new Font(20));
        labelAngleBaseShort.setTranslateX(swingLong.getStartX() - 6);
        labelAngleBaseShort.setTranslateY(swingLong.getStartY() - 30);

        labelAngleSideShort = new Label(stringAngleSide);
        labelAngleSideShort.setFont(new Font(20));
        labelAngleSideShort.setTranslateX(shortGreen.getEndX() + 6);
        labelAngleSideShort.setTranslateY(shortGreen.getEndY() - 12);
        paneShortTriangle.getChildren().addAll(labelAngleSideShort, labelAngleBaseShort, labelAngleSwingShort);
    }

    public void setUpLabelSidesShort() {
        labelSwingShort = new Label(stringSwing);
        labelSwingShort.setFont(new Font(20));

        // Find the midpoint.
        double swingMidX = (swingShort.getStartX() + swingShort.getEndX()) * 0.5;
        double swingMidY = (swingShort.getStartY() + swingShort.getEndY()) * 0.5;
        labelSwingShort.setTranslateX(swingMidX);
        labelSwingShort.setTranslateY(swingMidY);

        // Translate along the normal of swingLong
        double slope = Math.atan((swingShort.getStartY() - swingShort.getEndY()) /
                (swingShort.getEndX() - swingShort.getStartX()));

        double normal = slope - Math.PI * 0.5;

        //move along normal
        labelSwingShort.setTranslateX(labelSwingShort.getTranslateX() + 6 * Math.cos(normal));
        labelSwingShort.setTranslateY(labelSwingShort.getTranslateY() - 6 * Math.sin(normal));

        labelSideShort = new Label(stringSide);
        labelSideShort.setFont(new Font(20));
        // Find the midpoint.
        double sideMidX = (sideShort.getStartX() + sideShort.getEndX()) * 0.5;
        double sideMidY = (sideShort.getStartY() + sideShort.getEndY()) * 0.5;
        labelSideShort.setTranslateX(sideMidX);
        labelSideShort.setTranslateY(sideMidY);
        // Translate along the normal of swingLong
        slope = Math.atan((sideShort.getStartY() - sideShort.getEndY()) /
                (sideShort.getEndX() - sideShort.getStartX()));
        normal = slope + Math.PI * 0.5;
        labelSideShort.setTranslateX(labelSideShort.getTranslateX() + 30 * Math.cos(normal));
        labelSideShort.setTranslateY(labelSideShort.getTranslateY() - 30 * Math.sin(normal));

        labelBaseShort = new Label(stringBase);
        labelBaseShort.setFont(new Font(20));
        double baseMidX = (shortGreen.getStartX() + shortGreen.getEndX()) * 0.5;
        double baseMidY = (shortGreen.getStartY() + shortGreen.getEndY()) * 0.5;
        labelBaseShort.setTranslateX(baseMidX);
        labelBaseShort.setTranslateY(baseMidY + 6);

        paneShortTriangle.getChildren().addAll(labelSwingShort, labelSideShort, labelBaseShort);
    }

    public void setUpLabelSidesLong() {
        labelSwingLong = new Label(stringSwing);
        labelSwingLong.setFont(new Font(20));
        // Find the midpoint.
        double swingMidX = (swingLong.getStartX() + swingLong.getEndX()) * 0.5;
        double swingMidY = (swingLong.getStartY() + swingLong.getEndY()) * 0.5;
        labelSwingLong.setTranslateX(swingMidX);
        labelSwingLong.setTranslateY(swingMidY);
        // Translate along the normal of swingLong
        double slope = Math.atan((swingLong.getStartY() - swingLong.getEndY()) /
                (swingLong.getEndX() - swingLong.getStartX()));
        double normal = slope + Math.PI * 0.5;
        labelSwingLong.setTranslateX(labelSwingLong.getTranslateX() + 24 * Math.cos(normal));
        labelSwingLong.setTranslateY(labelSwingLong.getTranslateY() - 24 * Math.sin(normal));

        labelSideLong = new Label(stringSide);
        labelSideLong.setFont(new Font(20));
        double sideMidX = (sideLong.getStartX() + sideLong.getEndX()) * 0.5;
        double sideMidY = (sideLong.getStartY() + sideLong.getEndY()) * 0.5;
        labelSideLong.setTranslateX(sideMidX);
        labelSideLong.setTranslateY(sideMidY);
        slope = Math.atan((sideLong.getStartY() - sideLong.getEndY()) /
                (sideLong.getEndX() - sideLong.getStartX()));
        // If swingAngle is obtuse we move along the opposite normal
        if (interact.getData().getSwingAngle() < 90) {
            normal = slope + Math.PI * 0.5;
            labelSideLong.setTranslateX(labelSideLong.getTranslateX() + 30 * Math.cos(normal));
            labelSideLong.setTranslateY(labelSideLong.getTranslateY() - 30 * Math.sin(normal));
        } else {
            normal = slope - Math.PI * 0.5;
            labelSideLong.setTranslateX(labelSideLong.getTranslateX() + 12 * Math.cos(normal));
            labelSideLong.setTranslateY(labelSideLong.getTranslateY() - 12 * Math.sin(normal));
        }

        labelBaseLong = new Label(stringBase);
        labelBaseLong.setFont(new Font(20));
        double baseMidX = (longGreen.getStartX() + longGreen.getEndX()) * 0.5;
        double baseMidY = (longGreen.getStartY() + longGreen.getEndY()) * 0.5;
        labelBaseLong.setTranslateX(baseMidX);
        labelBaseLong.setTranslateY(baseMidY + 6);

        paneLongTriangle.getChildren().addAll(labelSwingLong, labelSideLong, labelBaseLong);
    }

    // Initializes all the string data to be used by various setUp___Label()
    public void setUpStrings() {
        String[] lowerCase = {"a", "b", "c"};
        String[] upperCase = {"A", "B", "C"};

        stringAngleSwing = upperCase[choiceBoxSwing.getSelectionModel().selectedIndexProperty().getValue()];
        stringAngleSide = upperCase[choiceBoxSide.getSelectionModel().selectedIndexProperty().getValue()];
        stringAngleBase = upperCase[findBaseAngleLabelInt()];

        stringSwing = lowerCase[choiceBoxSwing.getSelectionModel().selectedIndexProperty().getValue()];
        stringSide = lowerCase[choiceBoxSide.getSelectionModel().selectedIndexProperty().getValue()];
        stringBase = lowerCase[findBaseAngleLabelInt()];
    }

    public int findBaseAngleLabelInt() {
        int sum = choiceBoxSwing.getSelectionModel().selectedIndexProperty().getValue() +
                choiceBoxSide.getSelectionModel().selectedIndexProperty().getValue();
        switch (sum) {
            case 1:
                return 2;
            case 2:
                return 1;
            case 3:
                return 0;
            default:
                return sum / 2;
        }
    }

    //returns reason for numSolutions
    public String diagnosis() {
        //obtuse or rt cases
        if (interact.getData().getSwingAngle() >= 90) {
            if (numSolutions == 0)
                return numSolutions + " triangles.\n" +
                        "Angle is not acute but the Opposite Side is too short.\n" +
                        stringAngleSwing + " >= 90 but " + stringSwing + " <= " + stringSide;
            else //numSolutions == 1
                return numSolutions + " triangle.\n" +
                        "Angle is not acute and the Opposite Side is long enough to reach.\n" +
                        stringAngleSwing + " >= 90 and " + stringSwing + " > " + stringSide;
        } else { //swingAngle < 90
            if (numSolutions == 0)
                return numSolutions + " triangles.\n" +
                        "Angle is acute but the opposite side is too short because it's less than the altitude.\n" +
                        stringAngleSwing + " < 90 but " + stringSwing + " < altitude: " +
                        stringSwing + " < " + stringSide + "*sin(" + stringAngleSwing + ") = " +
                        formatDecimals(interact.getData().getAltitudeLen());
            else if (numSolutions == 1) {
                if (Math.abs(interact.getData().getSwingLen() - interact.getData().getAltitudeLen()) <= 0.00001) //close enough?
                    return numSolutions + " triangle.\n" +
                            "Angle is acute and the opposite side is equal to the altitude. It is a right triangle.\n" +
                            stringAngleSwing + " < 90 and " + stringSwing + " = altitude: " + stringSwing + " = " +
                            stringSide + "*sin(" + stringAngleSwing + ") = " +
                            formatDecimals(interact.getData().getAltitudeLen());
                else //swingLen > sideLen
                    return numSolutions + " triangle.\n" +
                            "Angle is acute and the opposite side is greater than or equal to the adjacent side.\n" +
                            stringAngleSwing + " < 90 and " + stringSwing + " >= " + stringSide;
            } else //numSolutions == 2 {
                return numSolutions + " triangles.\n" +
                        "Angle is acute and the opposite side is greater than the altitude but less than the adjacent side.\n" +
                        stringAngleSwing + " < 90 and " + stringSide + "*sin(" + stringAngleSwing + ") = " +
                        formatDecimals(interact.getData().getAltitudeLen()) + " < " + stringSwing + " < " + stringSide;
        }
    }

    public String solutionTextLong() {
        String ret = "";

        if (interact.getData().getSwingAngle() == 90)
            ret += "Right Triangle:\n";
        else if (interact.getData().getSwingAngle() > 90)
            ret += "Obtuse Triangle:\n";
        else
            ret += "Acute Triangle:\n";

        ret += stringAngleSwing + " = " + formatDecimals(interact.getData().getSwingAngle()) + ", " +
                stringAngleBase + " = " + formatDecimals(interact.getData().getBaseAngleLong()) + ", " +
                stringAngleSide + " = " + formatDecimals(interact.getData().getSideAngleLong()) + "\n";
        ret += stringSwing + " = " + formatDecimals(interact.getData().getSwingLen()) + ", " +
                stringBase + " = " + formatDecimals(interact.getData().getBaseLenLong()) + ", " +
                stringSide + " = " + formatDecimals(interact.getData().getSideLen()) + "\n";

        return ret;
    }

    // Only used when numSolutions == 2
    public String solutionTextShort() {
        String ret = "";

        ret += "Obtuse Triangle:\n" +
                stringAngleSwing + " = " + formatDecimals(interact.getData().getSwingAngle()) + ", " +
                stringAngleBase + " = " + formatDecimals(interact.getData().getBaseAngleShort()) + ", " +
                stringAngleSide + " = " + formatDecimals(interact.getData().getSideAngleShort()) + "\n";
        ret += stringSwing + " = " + formatDecimals(interact.getData().getSwingLen()) + ", " +
                stringBase + " = " + formatDecimals(interact.getData().getBaseLenShort()) + ", " +
                stringSide + " = " + formatDecimals(interact.getData().getSideLen()) + "\n";

        return ret;
    }

    public String formatDecimals(double num) {
        DecimalFormat df = new DecimalFormat("#.###");
        return df.format(num);
    }
}
