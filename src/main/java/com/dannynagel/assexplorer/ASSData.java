package com.dannynagel.assexplorer;

// Stores data (numSolutions, sides, angles, altitude, etc) for ASS
// All data is UNSCALED!!!
public class ASSData {
    private int numSolutions;

    //angles
    private double swingAngle, sideAngleLong, sideAngleShort, baseAngleLong, baseAngleShort;
    private final double SWING_ANGLE_RADIAN;

    //side lengths
    private double swingLen, baseLenShort, baseLenLong, sideLen, altitudeLen;

    ASSData(double angle, double sideLen, double swingLen) {
        swingAngle = angle;
        this.sideLen = sideLen;
        this.swingLen = swingLen;
        SWING_ANGLE_RADIAN = swingAngle * Math.PI / 180;
        altitudeLen = sideLen * Math.sin(SWING_ANGLE_RADIAN);

        //initializes numSolutions and the rest of the sides,angles
        solveASS();
    }

    // Solve how many solutions then call appropriate method.
    // This method initializes numSolutions.
    private void solveASS() {
        //obtuse and rt angle case
        if (swingAngle >= 90) {
            if (swingLen > sideLen)
                numSolutions = 1;
            else
                numSolutions = 0;
        }
        // acute case
        else {
            // rt angle case.  Use a tolerance.
            if (Math.abs(swingLen - altitudeLen) <= 0.00001)
                numSolutions = 1;
            else if (swingLen >= sideLen)
                numSolutions = 1;
            else if ((swingLen > altitudeLen) && (swingLen < sideLen))
                numSolutions = 2;
            else
                numSolutions = 0;
        }

        //call appropriate method
        switch (numSolutions) {
            case 1:
                solve1Sides();
                break;
            case 2:
                solve2Sides();
                break;
            default:
                break;
        }
    }

    //Law of sines. Initializes only baseLenLong, baseAngleLong, sideAngleLong.
    //Leaves baseLenShort, baseAngleShort, sideAngleShort == 0
    private void solve1Sides() {
        double sideAngleLong_Radian = Math.asin(sideLen * Math.sin(SWING_ANGLE_RADIAN) / swingLen);
        sideAngleLong = sideAngleLong_Radian * 180 / Math.PI;

        double baseAngleLong_Radian = Math.PI - SWING_ANGLE_RADIAN - sideAngleLong_Radian;
        baseAngleLong = 180 - swingAngle - sideAngleLong;

        baseLenLong = Math.sin(baseAngleLong_Radian) * swingLen / Math.sin(SWING_ANGLE_RADIAN);
    }

    //Law of sines to initialize the rest
    private void solve2Sides() {
        double sideAngleLong_Radian = Math.asin(sideLen * Math.sin(SWING_ANGLE_RADIAN) / swingLen);
        double sideAngleShort_Radian = (Math.PI - sideAngleLong_Radian);

        sideAngleLong = sideAngleLong_Radian * 180 / Math.PI;
        sideAngleShort = sideAngleShort_Radian * 180 / Math.PI;

        double baseAngleLong_Radian = Math.PI - SWING_ANGLE_RADIAN - sideAngleLong_Radian;
        double baseAngleShort_Radian = Math.PI - SWING_ANGLE_RADIAN - sideAngleShort_Radian;
        baseAngleLong = 180 - swingAngle - sideAngleLong;
        baseAngleShort = 180 - swingAngle - sideAngleShort;

        baseLenLong = Math.sin(baseAngleLong_Radian) * swingLen / Math.sin(SWING_ANGLE_RADIAN);
        baseLenShort = Math.sin(baseAngleShort_Radian) * swingLen / Math.sin(SWING_ANGLE_RADIAN);
    }

    // Getters.  Actually important here.
    public double getSwingAngle() {
        return swingAngle;
    }

    public double getSideAngleLong() {
        return sideAngleLong;
    }

    public double getSideAngleShort() {
        return sideAngleShort;
    }

    public double getBaseAngleShort() {
        return baseAngleShort;
    }

    public double getBaseAngleLong() {
        return baseAngleLong;
    }

    public double getSwingLen() {
        return swingLen;
    }

    public double getBaseLenShort() {
        return baseLenShort;
    }

    public double getBaseLenLong() {
        return baseLenLong;
    }

    public double getSideLen() {
        return sideLen;
    }

    public double getAltitudeLen() {
        return altitudeLen;
    }

    public int getNumSolutions() {
        return numSolutions;
    }
}
