import java.util.List;

public interface ScanTool {
    String name();
    // C'est cette ligne qui force Gobuster à avoir 2 arguments
    List<Faille> scan(String target, String typeScan);
}
