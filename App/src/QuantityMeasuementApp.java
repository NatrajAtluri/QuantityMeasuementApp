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
         * Instance method: Add another QuantityLength
         * Result is returned in the unit of THIS object
         */
        public QuantityLength add(QuantityLength other) {
            if (other == null) {
                throw new IllegalArgumentException("Other quantity cannot be null");
            }

            double sumInFeet = this.toBaseUnit() + other.toBaseUnit();
            double resultValue = this.unit.fromFeet(sumInFeet);

            return new QuantityLength(resultValue, this.unit);
        }

        /**
         * Static method: flexible addition with explicit target unit
         */
        public static QuantityLength add(QuantityLength q1, QuantityLength q2, LengthUnit targetUnit) {
            if (q1 == null || q2 == null || targetUnit == null) {
                throw new IllegalArgumentException("Arguments cannot be null");
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
     * Demonstration methods
     */
    public static void demonstrateAddition(QuantityLength q1, QuantityLength q2) {
        QuantityLength result = q1.add(q2);
        System.out.println("add(" + q1 + ", " + q2 + ") → " + result);
    }

    public static void demonstrateAddition(QuantityLength q1, QuantityLength q2, LengthUnit target) {
        QuantityLength result = QuantityLength.add(q1, q2, target);
        System.out.println("add(" + q1 + ", " + q2 + ", " + target + ") → " + result);
    }

    /**
     * Main method for testing UC6
     */
    public static void main(String[] args) {

        // Same unit
        demonstrateAddition(
                new QuantityLength(1.0, LengthUnit.FEET),
                new QuantityLength(2.0, LengthUnit.FEET)
        );

        // Cross-unit (Feet + Inches)
        demonstrateAddition(
                new QuantityLength(1.0, LengthUnit.FEET),
                new QuantityLength(12.0, LengthUnit.INCHES)
        );

        // Cross-unit (Inches + Feet)
        demonstrateAddition(
                new QuantityLength(12.0, LengthUnit.INCHES),
                new QuantityLength(1.0, LengthUnit.FEET)
        );

        // Yard + Feet
        demonstrateAddition(
                new QuantityLength(1.0, LengthUnit.YARDS),
                new QuantityLength(3.0, LengthUnit.FEET)
        );

        // Inches + Yard
        demonstrateAddition(
                new QuantityLength(36.0, LengthUnit.INCHES),
                new QuantityLength(1.0, LengthUnit.YARDS)
        );

        // Centimeter + Inch
        demonstrateAddition(
                new QuantityLength(2.54, LengthUnit.CENTIMETERS),
                new QuantityLength(1.0, LengthUnit.INCHES)
        );

        // Zero case
        demonstrateAddition(
                new QuantityLength(5.0, LengthUnit.FEET),
                new QuantityLength(0.0, LengthUnit.INCHES)
        );

        // Negative values
        demonstrateAddition(
                new QuantityLength(5.0, LengthUnit.FEET),
                new QuantityLength(-2.0, LengthUnit.FEET)
        );
    }
}