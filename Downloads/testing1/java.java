import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * DlpTestGenerator.java
 *
 * Generates DLP test artifacts (FAKE data) to validate detection for:
 * - PCI (Luhn-valid card numbers)
 * - US SSN-like patterns
 * - Canada SIN-like patterns (format only, fake)
 * - Email/phone/address-like patterns
 * - PHI-like fields (fake patient records)
 *
 * Outputs: TXT, CSV, JSON, XML, Base64 TXT, and ZIP containing multiple files.
 *
 * Compile:
 *   javac DlpTestGenerator.java
 *
 * Run:
 *   java DlpTestGenerator [outputDir]
 *
 * Example:
 *   java DlpTestGenerator C:\temp\dlp-test
 */
public class DlpTestGenerator {

    private static final String MARKER = "DLP_TEST_DO_NOT_USE_REAL_DATA";
    private static final Random RND = new Random();

    public static void main(String[] args) throws Exception {
        Path outDir = (args.length > 0) ? Paths.get(args[0]) : Paths.get("dlp-test-output");
        Files.createDirectories(outDir);

        // Generate fake but detection-friendly values
        String ccVisaTest = generateLuhnValidCard("4", 16); // Visa-like, Luhn-valid
        String ccMcTest   = generateLuhnValidCard("5", 16); // MasterCard-like, Luhn-valid
        String ssnLike     = generateSsnLike();
        String sinLike     = generateSinLike();
        String email       = "dlp.test.user@example.com";
        String phone       = "+1-416-555-0199";
        String address     = "123 Test St, Mississauga, ON L5B 4N4, Canada";
        String dob         = LocalDate.of(1992, 7, 14).toString();
        String mrn         = "MRN-" + (100000 + RND.nextInt(900000)); // fake medical record number

        // Common content block (useful for plain text DLP scanning)
        String commonBlock = ""
                + "=== " + MARKER + " ===\n"
                + "Purpose: DLP policy validation (FAKE DATA)\n"
                + "Timestamp: " + new Date() + "\n\n"
                + "PCI:\n"
                + "  Visa-like (Luhn-valid): " + ccVisaTest + "\n"
                + "  MC-like (Luhn-valid):   " + ccMcTest + "\n\n"
                + "PII:\n"
                + "  SSN-like: " + ssnLike + "\n"
                + "  SIN-like: " + sinLike + "\n"
                + "  Email:    " + email + "\n"
                + "  Phone:    " + phone + "\n"
                + "  Address:  " + address + "\n\n"
                + "PHI (fake patient record):\n"
                + "  PatientName: Test Patient\n"
                + "  DOB: " + dob + "\n"
                + "  MRN: " + mrn + "\n"
                + "  Diagnosis: TEST-DIAGNOSIS\n";

        // 1) TXT
        Path txt = outDir.resolve("dlp_test_payload.txt");
        writeUtf8(txt, commonBlock);

        // 2) CSV
        Path csv = outDir.resolve("dlp_test_payload.csv");
        writeUtf8(csv, buildCsv(ccVisaTest, ccMcTest, ssnLike, sinLike, email, phone, address, dob, mrn));

        // 3) JSON
        Path json = outDir.resolve("dlp_test_payload.json");
        writeUtf8(json, buildJson(ccVisaTest, ccMcTest, ssnLike, sinLike, email, phone, address, dob, mrn));

        // 4) XML
        Path xml = outDir.resolve("dlp_test_payload.xml");
        writeUtf8(xml, buildXml(ccVisaTest, ccMcTest, ssnLike, sinLike, email, phone, address, dob, mrn));

        // 5) Base64-encoded content (useful to test “encoded data” detection)
        Path b64 = outDir.resolve("dlp_test_payload_base64.txt");
        String b64Content = Base64.getEncoder().encodeToString(commonBlock.getBytes(StandardCharsets.UTF_8));
        writeUtf8(b64, "BASE64(" + MARKER + ")\n" + b64Content + "\n");

        // 6) ZIP archive with multiple files inside (common DLP scenario)
        Path zip = outDir.resolve("dlp_test_bundle.zip");
        createZip(zip, Map.of(
                "readme.txt", ("ZIP BUNDLE - " + MARKER + "\n\n" + commonBlock),
                "payload.csv", buildCsv(ccVisaTest, ccMcTest, ssnLike, sinLike, email, phone, address, dob, mrn),
                "payload.json", buildJson(ccVisaTest, ccMcTest, ssnLike, sinLike, email, phone, address, dob, mrn)
        ));

        // Optional: Generate a larger file (for size/threshold testing)
        Path large = outDir.resolve("dlp_test_large.txt");
        writeUtf8(large, buildLargeFile(commonBlock, 2000)); // repeats block 2000 times

        System.out.println("DLP test files generated in: " + outDir.toAbsolutePath());
        System.out.println("Created:");
        System.out.println(" - " + txt.getFileName());
        System.out.println(" - " + csv.getFileName());
        System.out.println(" - " + json.getFileName());
        System.out.println(" - " + xml.getFileName());
        System.out.println(" - " + b64.getFileName());
        System.out.println(" - " + zip.getFileName());
        System.out.println(" - " + large.getFileName());
    }

    private static void writeUtf8(Path path, String content) throws IOException {
        Files.write(path, content.getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    private static String buildCsv(String cc1, String cc2, String ssn, String sin, String email,
                                   String phone, String address, String dob, String mrn) {
        // Simple CSV with quoted fields to avoid delimiter issues
        return ""
                + "marker,cc_visa,cc_mc,ssn_like,sin_like,email,phone,address,dob,mrn\n"
                + quote(MARKER) + ","
                + quote(cc1) + ","
                + quote(cc2) + ","
                + quote(ssn) + ","
                + quote(sin) + ","
                + quote(email) + ","
                + quote(phone) + ","
                + quote(address) + ","
                + quote(dob) + ","
                + quote(mrn) + "\n";
    }

    private static String buildJson(String cc1, String cc2, String ssn, String sin, String email,
                                    String phone, String address, String dob, String mrn) {
        // Minimal JSON (no external libs)
        return "{\n"
                + "  \"marker\": \"" + escapeJson(MARKER) + "\",\n"
                + "  \"pci\": {\n"
                + "    \"visa_like_luhn_valid\": \"" + escapeJson(cc1) + "\",\n"
                + "    \"mc_like_luhn_valid\": \"" + escapeJson(cc2) + "\"\n"
                + "  },\n"
                + "  \"pii\": {\n"
                + "    \"ssn_like\": \"" + escapeJson(ssn) + "\",\n"
                + "    \"sin_like\": \"" + escapeJson(sin) + "\",\n"
                + "    \"email\": \"" + escapeJson(email) + "\",\n"
                + "    \"phone\": \"" + escapeJson(phone) + "\",\n"
                + "    \"address\": \"" + escapeJson(address) + "\"\n"
                + "  },\n"
                + "  \"phi\": {\n"
                + "    \"patient_name\": \"Test Patient\",\n"
                + "    \"dob\": \"" + escapeJson(dob) + "\",\n"
                + "    \"mrn\": \"" + escapeJson(mrn) + "\",\n"
                + "    \"diagnosis\": \"TEST-DIAGNOSIS\"\n"
                + "  }\n"
                + "}\n";
    }

    private static String buildXml(String cc1, String cc2, String ssn, String sin, String email,
                                   String phone, String address, String dob, String mrn) {
        return ""
                + "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<dlpTest>\n"
                + "  <marker>" + escapeXml(MARKER) + "</marker>\n"
                + "  <pci>\n"
                + "    <visaLikeLuhnValid>" + escapeXml(cc1) + "</visaLikeLuhnValid>\n"
                + "    <mcLikeLuhnValid>" + escapeXml(cc2) + "</mcLikeLuhnValid>\n"
                + "  </pci>\n"
                + "  <pii>\n"
                + "    <ssnLike>" + escapeXml(ssn) + "</ssnLike>\n"
                + "    <sinLike>" + escapeXml(sin) + "</sinLike>\n"
                + "    <email>" + escapeXml(email) + "</email>\n"
                + "    <phone>" + escapeXml(phone) + "</phone>\n"
                + "    <address>" + escapeXml(address) + "</address>\n"
                + "  </pii>\n"
                + "  <phi>\n"
                + "    <patientName>Test Patient</patientName>\n"
                + "    <dob>" + escapeXml(dob) + "</dob>\n"
                + "    <mrn>" + escapeXml(mrn) + "</mrn>\n"
                + "    <diagnosis>TEST-DIAGNOSIS</diagnosis>\n"
                + "  </phi>\n"
                + "</dlpTest>\n";
    }

    private static void createZip(Path zipPath, Map<String, String> files) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipPath))) {
            for (Map.Entry<String, String> e : files.entrySet()) {
                ZipEntry entry = new ZipEntry(e.getKey());
                zos.putNextEntry(entry);
                byte[] data = e.getValue().getBytes(StandardCharsets.UTF_8);
                zos.write(data);
                zos.closeEntry();
            }
        }
    }

    private static String buildLargeFile(String block, int repeats) {
        StringBuilder sb = new StringBuilder(block.length() * Math.min(repeats, 50));
        sb.append("LARGE FILE - ").append(MARKER).append("\n\n");
        for (int i = 1; i <= repeats; i++) {
            sb.append("----- BLOCK ").append(i).append(" -----\n");
            sb.append(block).append("\n");
        }
        return sb.toString();
    }

    // ---------- Fake pattern generators ----------

    private static String generateSsnLike() {
        // SSN-like format: AAA-GG-SSSS (avoid common invalid blocks? not necessary for DLP tests)
        int a = 100 + RND.nextInt(900);
        int g = 10 + RND.nextInt(90);
        int s = 1000 + RND.nextInt(9000);
        return String.format("%03d-%02d-%04d", a, g, s);
    }

    private static String generateSinLike() {
        // SIN-like format (9 digits). This is just "looks like", not validated.
        int[] digits = new int[9];
        for (int i = 0; i < 9; i++) digits[i] = RND.nextInt(10);
        StringBuilder sb = new StringBuilder();
        for (int d : digits) sb.append(d);
        return sb.toString();
    }

    /**
     * Generates a Luhn-valid card number string of specified length using a given prefix digit.
     * Example prefix: "4" for Visa-like, "5" for MasterCard-like.
     */
    private static String generateLuhnValidCard(String prefix, int length) {
        if (prefix == null || prefix.isEmpty()) throw new IllegalArgumentException("prefix required");
        if (length < prefix.length() + 1) throw new IllegalArgumentException("length too small");

        StringBuilder sb = new StringBuilder(prefix);
        while (sb.length() < length - 1) {
            sb.append(RND.nextInt(10));
        }

        int check = luhnCheckDigit(sb.toString());
        sb.append(check);
        return sb.toString();
    }

    private static int luhnCheckDigit(String numberWithoutCheckDigit) {
        int sum = 0;
        boolean doubleIt = true; // because we are computing for the next digit
        for (int i = numberWithoutCheckDigit.length() - 1; i >= 0; i--) {
            int d = numberWithoutCheckDigit.charAt(i) - '0';
            if (doubleIt) {
                d *= 2;
                if (d > 9) d -= 9;
            }
            sum += d;
            doubleIt = !doubleIt;
        }
        return (10 - (sum % 10)) % 10;
    }

    // ---------- Small helpers ----------

    private static String quote(String s) {
        return "\"" + s.replace("\"", "\"\"") + "\"";
    }

    private static String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }

    private static String escapeXml(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&apos;");
    }
}
