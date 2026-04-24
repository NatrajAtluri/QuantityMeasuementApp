public enum LengthUnit {

    FEET(1.0),
    INCHES(1.0 / 12.0),
    YARDS(3.0),
    CENTIMETERS(1.0 / 30.48);

    private final double toFeetFactor;

    LengthUnit(double toFeetFactor) {
        this.toFeetFactor = toFeetFactor;
    }

    /**
     * Convert value in this unit → base unit (feet)
     */
    public double convertToBaseUnit(double value) {
        return value * toFeetFactor;
    }

    /**
     * Convert value from base unit (feet) → this unit
     */
    public double convertFromBaseUnit(double baseValue) {
        return baseValue / toFeetFactor;
    }

    public double getConversionFactor() {
        return toFeetFactor;
    }
}
public class QuantityMeasuementApp {

    /**
     * Immutable Quantity class (refactored)
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
            return unit.convertToBaseUnit(value);
        }

        /**
         * Convert to another unit
         */
        public QuantityLength convertTo(LengthUnit targetUnit) {
            validate(value, targetUnit);

            double base = this.toBaseUnit();
            double converted = targetUnit.convertFromBaseUnit(base);

            return new QuantityLength(converted, targetUnit);
        }

        /**
         * Addition with explicit target unit (UC7 + UC8)
         */
        public QuantityLength add(QuantityLength other, LengthUnit targetUnit) {
            return addInternal(this, other, targetUnit);
        }

        /**
         * UC6 compatibility (default → this.unit)
         */
        public QuantityLength add(QuantityLength other) {
            return addInternal(this, other, this.unit);
        }

        /**
         * Static API
         */
        public static QuantityLength add(QuantityLength q1, QuantityLength q2, LengthUnit targetUnit) {
            return addInternal(q1, q2, targetUnit);
        }

        /**
         * DRY private helper
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

            double sumBase = q1.toBaseUnit() + q2.toBaseUnit();
            double result = targetUnit.convertFromBaseUnit(sumBase);

            return new QuantityLength(result, targetUnit);
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
     * Demo methods
     */
    public static void main(String[] args) {

        QuantityLength q1 = new QuantityLength(1.0, LengthUnit.FEET);
        QuantityLength q2 = new QuantityLength(12.0, LengthUnit.INCHES);

        // Conversion
        System.out.println(q1 + " → " + q1.convertTo(LengthUnit.INCHES));

        // Equality
        System.out.println(q2 + " equals " +
                new QuantityLength(1.0, LengthUnit.YARDS) + " → " +
                q2.equals(new QuantityLength(1.0, LengthUnit.YARDS)));

        // Addition (explicit target)
        System.out.println("Add → " +
                q1.add(q2, LengthUnit.FEET));

        // Addition (yards)
        System.out.println("Add → " +
                new QuantityLength(1.0, LengthUnit.YARDS)
                        .add(new QuantityLength(3.0, LengthUnit.FEET), LengthUnit.YARDS));

        // CM conversion
        System.out.println(
                new QuantityLength(2.54, LengthUnit.CENTIMETERS)
                        .convertTo(LengthUnit.INCHES)
        );
    }
}