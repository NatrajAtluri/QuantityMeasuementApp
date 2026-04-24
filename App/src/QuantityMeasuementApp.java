public class QuantityMeasuementApp {

    // Inner class representing Feet measurement
    static class Feet {
        private final double value;

        // Constructor
        public Feet(double value) {
            this.value = value;
        }

        // Getter (optional)
        public double getValue() {
            return value;
        }

        // Override equals method
        @Override
        public boolean equals(Object obj) {
            // Check same reference (reflexive)
            if (this == obj) return true;

            // Null check and type check
            if (obj == null || getClass() != obj.getClass()) return false;

            // Cast safely
            Feet other = (Feet) obj;

            // Compare using Double.compare
            return Double.compare(this.value, other.value) == 0;
        }

        // Override hashCode when equals is overridden
        @Override
        public int hashCode() {
            return Double.hashCode(value);
        }
    }

    // Main method to test functionality
    public static void main(String[] args) {
        Feet f1 = new Feet(1.0);
        Feet f2 = new Feet(1.0);

        boolean result = f1.equals(f2);

        System.out.println("Input: 1.0 ft and 1.0 ft");
        System.out.println("Output: Equal (" + result + ")");
    }
}