public class QuantityMeasuementApp {

    /**
     * Enum representing supported length units (base: FEET)
     */
    enum LengthUnit {
        FEET(1.0),
        INCHES(1.0 / 12.0),
        YARDS(3.0),
        CENTIMETERS(0.0328084);

        private final double toFeetFactor;

        LengthUnit(double toFeetFactor) {
            this.toFeetFactor = toFeetFactor;
        }

        public double toFeet(double value) {
            return value * toFeetFactor;
        }

        public double fromFeet(double feetValue) {
            return feetValue / toFeetFactor;
        }
    }

    /**
     * Immutable value object for length
     */
    static class QuantityLength {
        private final double value;
        private final LengthUnit unit;

        private static final double EPSILON = 1e-6;

        public QuantityLength(double value, LengthUnit unit) {
            validate(value, unit);
            this.value = value;
            this.unit = unit;
        }

        private static void validate(double value, LengthUnit unit) {
            if (unit == null) {
                throw new IllegalArgumentException("Unit cannot be null");
            }
            if (!Double.isFinite(value)) {
                throw new IllegalArgumentException("Value must be finite");
            }
        }

        private double toBaseUnit() {
            return unit.toFeet(value);
        }

        /**
         * UC6 method (default → result in this.unit)
         */
        public QuantityLength add(QuantityLength other) {
            return addInternal(this, other, this.unit);
        }

        /**
         * UC7 method (explicit target unit)
         */
        public QuantityLength add(QuantityLength other, LengthUnit targetUnit) {
            return addInternal(this, other, targetUnit);
        }

        /**
         * Static version (explicit target unit)
         */
        public static QuantityLength add(QuantityLength q1, QuantityLength q2, LengthUnit targetUnit) {
            return addInternal(q1, q2, targetUnit);
        }

        /**
         * Private DRY utility method for addition logic
         */
        private static QuantityLength addInternal(QuantityLength q1,
                                                  QuantityLength q2,
                                                  LengthUnit targetUnit) {

            if (q1 == null || q2 == null) {
                throw new IllegalArgumentException("Operands cannot be null");
            }
            if (targetUnit == null) {
                throw new IllegalArgumentException("Target unit cannot be null");
            }

            double sumInFeet = q1.toBaseUnit() + q2.toBaseUnit();
            double resultValue = targetUnit.fromFeet(sumInFeet);

            return new QuantityLength(resultValue, targetUnit);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;

            QuantityLength other = (QuantityLength) obj;
            return Math.abs(this.toBaseUnit() - other.toBaseUnit()) < EPSILON;
        }

        @Override
        public int hashCode() {
            return Double.hashCode(toBaseUnit());
        }

        @Override
        public String toString() {
            return "Quantity(" + value + ", " + unit + ")";
        }
    }

    /**
     * Demo helpers
     */
    public static void demonstrateAddition(QuantityLength q1, QuantityLength q2, LengthUnit target) {
        QuantityLength result = QuantityLength.add(q1, q2, target);
        System.out.println("add(" + q1 + ", " + q2 + ", " + target + ") → " + result);
    }

    /**
     * Main method for UC7 demo
     */
    public static void main(String[] args) {

        // Explicit target = FEET
        demonstrateAddition(
                new QuantityLength(1.0, LengthUnit.FEET),
                new QuantityLength(12.0, LengthUnit.INCHES),
                LengthUnit.FEET
        );

        // Explicit target = INCHES
        demonstrateAddition(
                new QuantityLength(1.0, LengthUnit.FEET),
                new QuantityLength(12.0, LengthUnit.INCHES),
                LengthUnit.INCHES
        );

        // Explicit target = YARDS
        demonstrateAddition(
                new QuantityLength(1.0, LengthUnit.FEET),
                new QuantityLength(12.0, LengthUnit.INCHES),
                LengthUnit.YARDS
        );

        // Yard + Feet → Yards
        demonstrateAddition(
                new QuantityLength(1.0, LengthUnit.YARDS),
                new QuantityLength(3.0, LengthUnit.FEET),
                LengthUnit.YARDS
        );

        // Inches + Yard → Feet
        demonstrateAddition(
                new QuantityLength(36.0, LengthUnit.INCHES),
                new QuantityLength(1.0, LengthUnit.YARDS),
                LengthUnit.FEET
        );

        // CM + Inch → CM
        demonstrateAddition(
                new QuantityLength(2.54, LengthUnit.CENTIMETERS),
                new QuantityLength(1.0, LengthUnit.INCHES),
                LengthUnit.CENTIMETERS
        );

        // Zero case
        demonstrateAddition(
                new QuantityLength(5.0, LengthUnit.FEET),
                new QuantityLength(0.0, LengthUnit.INCHES),
                LengthUnit.YARDS
        );

        // Negative values
        demonstrateAddition(
                new QuantityLength(5.0, LengthUnit.FEET),
                new QuantityLength(-2.0, LengthUnit.FEET),
                LengthUnit.INCHES
        );
    }
}