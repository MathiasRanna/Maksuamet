package ee.maksuamet.helpers;

public class Helpers {

    public int quarterToInt(String quarter) {
        switch (quarter) {
            case "I": return 1;
            case "II": return 2;
            case "III": return 3;
            case "IV": return 4;
            default: throw new IllegalArgumentException("Unexpected quarter: " + quarter);
        }
    }

    public String IntToQuarter(int quarter) {
        switch (quarter) {
            case 1: return "I";
            case 2: return "II";
            case 3: return "III";
            case 4: return "IV";
            default: throw new IllegalArgumentException("Unexpected quarter: " + quarter);
        }
    }
}
