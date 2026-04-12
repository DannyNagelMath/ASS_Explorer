package com.dannynagel.assexplorer;

import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;

import java.text.DecimalFormat;
import java.util.ArrayList;

// Contains all Label objects (A = 37, a = 12.5, etc) for ASS
// Communicates with ASSInteract object _interact_ by
// passing it as a parameter in the constructor.
public class ASSLabels {

    private ArrayList<Label> labels;
    private ASSInteract interact;

    private int choiceBoxAngleInt, choiceBoxSideInt, choiceBoxSwingInt;

    //The labels
    private Label swingAngleLabel, sideLenLabel, swingLenLabel, baseAngleLabel, sideAngleLabel, swingAngleValueLabel;

    private final String[] UPPER_CASE = {"A", "B", "C"};

    ASSLabels(ASSInteract interact, int choiceBoxAngleInt, int choiceBoxSideInt, int choiceBoxSwingInt) {
        labels = new ArrayList<>();
        this.interact = interact;
        this.choiceBoxAngleInt = choiceBoxAngleInt;
        this.choiceBoxSideInt = choiceBoxSideInt;
        this.choiceBoxSwingInt = choiceBoxSwingInt;

        setUpSwingAngleLabel();
        setUpSideLenLabel();
        setUpBaseAngleLabel();
        setUpSideAngleLabel();
    }

    private void setUpSwingAngleLabel() {
        swingAngleLabel = new Label(UPPER_CASE[choiceBoxAngleInt]);
        swingAngleLabel.setFont(new Font(20));
        swingAngleLabel.setTranslateX(interact.getTranslateX() + interact.getStartXBase() - 18);
        swingAngleLabel.setTranslateY(interact.getTranslateY() + interact.getStartYBase() - 12);
        swingAngleLabel.setOpacity(0);
        swingAngleLabel.setMouseTransparent(true);

        labels.add(swingAngleLabel);
    }

    private void setUpBaseAngleLabel() {
        int temp = findBaseAngleLabelInt();
        baseAngleLabel = new Label(UPPER_CASE[temp]);
        baseAngleLabel.setFont(new Font(20));
        baseAngleLabel.setTranslateX(interact.getTranslateX() + interact.getSwing().getStartX() - 18);
        baseAngleLabel.setTranslateY(interact.getTranslateY() + interact.getSwing().getStartY() - 24);
        baseAngleLabel.setOpacity(0);
        baseAngleLabel.setMouseTransparent(true);

        labels.add(baseAngleLabel);
    }

    private void setUpSideAngleLabel() {
        sideAngleLabel = new Label(UPPER_CASE[choiceBoxSideInt]);
        sideAngleLabel.setFont(new Font(20));
        sideAngleLabel.setTranslateX(205);
        sideAngleLabel.setMouseTransparent(true);
        sideAngleLabel.layoutXProperty().bind(interact.getCircle().centerXProperty());
        sideAngleLabel.layoutYProperty().bind(interact.getCircle().centerYProperty());
        sideAngleLabel.setOpacity(0);
        sideAngleLabel.setMouseTransparent(true);

        labels.add(sideAngleLabel);
    }

    private void setUpSideLenLabel() {
        sideLenLabel = new Label(formatDecimals(interact.getData().getSideLen()));
        sideLenLabel.setFont(new Font(20));

        //1st calculate midpoint of sideLen:
        double midX = 0.5 * (interact.getSide().getEndX() - interact.getSide().getStartX());
        double midY = 0.5 * (interact.getSide().getEndY() - interact.getSide().getStartY());
        sideLenLabel.setTranslateX(interact.getTranslateX() + interact.getSide().getStartX() + midX);
        sideLenLabel.setTranslateY(interact.getTranslateY() + interact.getSide().getStartY() + midY);

        // Then rotate
        Rotate rotate = new Rotate(-1 * interact.getData().getSwingAngle(),
                sideLenLabel.getLayoutX(),
                sideLenLabel.getLayoutY());
        sideLenLabel.getTransforms().addAll(rotate);

        // Finally move along normal
        sideLenLabel.setTranslateX(sideLenLabel.getTranslateX() + 30 * Math.cos((interact.getData().getSwingAngle() + 90) * Math.PI / 180));
        sideLenLabel.setTranslateY(sideLenLabel.getTranslateY() - 30 * Math.sin((interact.getData().getSwingAngle() + 90) * Math.PI / 180));

        sideLenLabel.setOpacity(0);
        sideLenLabel.setMouseTransparent(true);

        labels.add(sideLenLabel);
    }

    public int findBaseAngleLabelInt() {
        int sum = choiceBoxSwingInt + choiceBoxSideInt;
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

    public String formatDecimals(double num) {
        DecimalFormat df = new DecimalFormat("#.###");
        return df.format(num);
    }

    public void changeLabel(Label label, int index) {
        if (index == 0)
            label.setText("A");
        else if (index == 1)
            label.setText("B");
        else
            label.setText("C");
    }

    public Label getSwingAngleLabel() {
        return swingAngleLabel;
    }

    public Label getSideAngleLabel() {
        return sideAngleLabel;
    }

    public Label getBaseAngleLabel() {
        return baseAngleLabel;
    }

    public ArrayList<Label> getLabels() {
        return labels;
    }


//****************************END OF WORKING CODE****************************************


    //POS doesn't work.  New hard problem...but it shouldn't be hard
    private void setUpSwingLenLabel() {
        swingLenLabel = new Label(String.valueOf(interact.getData().getSwingLen()));
        swingLenLabel.setFont(new Font(20));

        //1st calculate midpoint of sideLen:
        double midX = 0.5 * (interact.getSwing().getEndX() - interact.getSwing().getStartX());
        double midY = 0.5 * (interact.getSwing().getEndY() - interact.getSwing().getStartY());


        swingLenLabel.setTranslateX(interact.getTranslateX() + interact.getSwing().getStartX() + midX);
        swingLenLabel.setTranslateY(interact.getTranslateY() + interact.getSwing().getStartY() + midY);
        swingLenLabel.setOpacity(0);

        //calculate swing's current angle
        double deltaY = interact.getSwing().getStartY() - interact.getSwing().getEndY();
        double deltaX = interact.getSwing().getEndX() - interact.getSwing().getStartX();
        double swingCurrentAngle = Math.atan(deltaY / deltaX) * 180 / Math.PI;


        Rotate rotate = new Rotate(swingCurrentAngle,
                swingLenLabel.getLayoutX(),
                swingLenLabel.getLayoutY());
        swingLenLabel.getTransforms().addAll(rotate);

        //move along normal
        swingLenLabel.setTranslateX(swingLenLabel.getTranslateX() + 24 * Math.cos((swingCurrentAngle + 90) * Math.PI / 180));
        swingLenLabel.setTranslateY(swingLenLabel.getTranslateY() - 30 * Math.sin((swingCurrentAngle + 90) * Math.PI / 180));


        labels.add(swingLenLabel);
    }

    public void upDateSwingLenLabel() {

        //1st calculate midpoint
        double midX = 0.5 * (interact.getSwing().getEndX() - interact.getSwing().getStartX());
        double midY = 0.5 * (interact.getSwing().getEndY() - interact.getSwing().getStartY());

        swingLenLabel.setTranslateX(interact.getTranslateX() + interact.getSwing().getStartX() + midX);
        swingLenLabel.setTranslateY(interact.getTranslateY() + interact.getSwing().getStartY() + midY);
        swingLenLabel.setOpacity(1);

        //calculate swing's current angle
        double deltaY = interact.getSwing().getEndY() - interact.getSwing().getStartY();
        double deltaX = interact.getSwing().getEndX() - interact.getSwing().getStartX();
        double swingCurrentAngle = Math.atan(deltaY / deltaX) * 180 / Math.PI;

        Rotate rotate = new Rotate(swingCurrentAngle,
                swingLenLabel.getTranslateX(),
                swingLenLabel.getTranslateY());
        swingLenLabel.getTransforms().addAll(rotate);

        //move along normal
        swingLenLabel.setTranslateX(swingLenLabel.getTranslateX() + 24 * Math.cos((swingCurrentAngle + 90) * Math.PI / 180));
        swingLenLabel.setTranslateY(swingLenLabel.getTranslateY() - 30 * Math.sin((swingCurrentAngle + 90) * Math.PI / 180));
    }
}
