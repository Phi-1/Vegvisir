package dev.stormwatch.vegvisir.data;

public class Temperature {

    public static final int FIRE_RANGE_SHELTERED = 8;
    public static final int FIRE_RANGE_OUTSIDE = 3;

    public static class Altitude {
        // TODO: add slight curve to temp func, temp should increase faster going down from neutral, and decrease slower going up from neutral
        // TODO: rework temp values once temp system is flushed out
        private static final int yAtNeutralTemp = 64;
        private static final int yAtMaxTemp = 0;
        private static final int yAtMinTemp = 320;
        private static final double neutralTemp = 1.0;
        private static final double maxTemp = 1.5;
        private static final double minTemp = 0.2;

        // Below neutral Y function constants
        private static final double lowerDY = neutralTemp - maxTemp;
        private static final double lowerDX = yAtNeutralTemp - yAtMaxTemp;
        private static final double lowerB = neutralTemp - yAtNeutralTemp * (lowerDY / lowerDX);
        // Above neutral Y function constants
        private static final double higherDY = minTemp - neutralTemp;
        private static final double higherDX = yAtMinTemp - yAtNeutralTemp;
        private static final double higherB = neutralTemp - yAtNeutralTemp * (higherDY / higherDX);

        public static double calcAltitudinalTemperatureModifier(double yPos) {
            if (yPos <= yAtNeutralTemp) {
                if (yPos <= yAtMaxTemp) return maxTemp;
                return yPos * (lowerDY / lowerDX) + lowerB;
            }
            if (yPos >= yAtMinTemp) return minTemp;
            return yPos * (higherDY / higherDX) + higherB;
        }
    }

}
