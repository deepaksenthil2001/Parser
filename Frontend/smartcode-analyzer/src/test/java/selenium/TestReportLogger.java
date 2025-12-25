package selenium;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TestReportLogger {

    static class Result {
        int sl;
        String name;
        String status;
        String message;
        String time;
    }

    private static List<Result> results = new ArrayList<>();
    private static int slNo = 1;

    // ADD RESULT
    public static void log(String testName, boolean passed, String message) {
        Result r = new Result();
        r.sl = slNo++;
        r.name = testName;
        r.status = passed ? "PASS" : "FAIL";
        r.message = message;
        r.time = new Date().toString();
        results.add(r);
    }

    // SAVE HTML FILE
    public static void generateReport() {
        try {
            FileWriter fw = new FileWriter("test-report.html");

            fw.write("""
                <html>
                <head>
                  <title>Selenium Test Report</title>
                  <style>
                    table { width: 100%; border-collapse: collapse; font-family: Arial; }
                    th, td { border: 1px solid #555; padding: 10px; text-align: center; }
                    th { background: black; color: white; }
                    .pass { color: green; font-weight: bold; }
                    .fail { color: red; font-weight: bold; }
                  </style>
                </head>
                <body>
                <h2 style='text-align:center;'>🚀 Selenium Test Report</h2>
                <table>
                  <tr>
                    <th>Sl</th>
                    <th>Test Name</th>
                    <th>Status</th>
                    <th>Message</th>
                    <th>Time</th>
                  </tr>
            """);

            for (Result r : results) {
                fw.write(String.format("""
                    <tr>
                      <td>%d</td>
                      <td>%s</td>
                      <td class='%s'>%s</td>
                      <td>%s</td>
                      <td>%s</td>
                    </tr>
                """,
                        r.sl, r.name,
                        r.status.equals("PASS") ? "pass" : "fail",
                        r.status, r.message, r.time));
            }

            fw.write("""
                </table>
                </body>
                </html>
            """);

            fw.close();
            System.out.println("📄 REPORT GENERATED: test-report.html");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
