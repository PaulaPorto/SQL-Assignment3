package nz.ac.wgtn.swen301.a3.server;

import com.fasterxml.jackson.databind.JsonNode;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatsServlet extends HttpServlet {
    public Map<String, List<Integer>> logs = new HashMap();

    /**
     * Gets the output for HTML.
     * Prints the output of logs in a table on a browser.
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        getOutput();
        response.setContentType("text/html");
        PrintWriter writer = response.getWriter();
        writer.print("<html>");
        writer.print("<body>");
        writer.print("<table border>");
        writer.println("<th>logger</th><th>ALL</th><th>TRACE</" +
                "th><th>DEBUG</th><th>INFO</th><th>WARN</" +
                "th><th>ERROR</th><th>FATAL</th><th>OFF</th>");

        for (String i : logs.keySet()){
            writer.print("<tr><td>");
            writer.print(i);
            List<Integer> num = logs.get(i);
            for (int j = 0; j < num.size(); j++){
                writer.print("</td><td>");
                writer.print(num.get(j));
            }
            writer.print("</td></tr>");
        }
        writer.print("</table>");
        writer.print("</body>");
        writer.print("</html>");

        writer.close();
    }

    /**
     * Gets the needed values for the table being printed for HTML.
     */
    public void getOutput(){
        List<String> logName = new ArrayList<>();
        for (JsonNode log : Persistency.DB){
            if (!logName.contains(log.get("logger").asText())){
                logName.add(log.get("logger").asText());
            }
        }
        for (String name : logName){
            List<Integer> intValues = new ArrayList<>();
            int all = 0;
            int trace = 0;
            int debug = 0;
            int info = 0;
            int warn = 0;
            int error = 0;
            int fatal = 0;
            int off = 0;
            for (JsonNode l : Persistency.DB){
                if (l.get("logger").asText().equals(name)){
                    if (l.get("level").asText().equals("ALL")){
                        all++;
                    }
                    if (l.get("level").asText().equals("TRACE")){
                        trace++;
                    }
                    if (l.get("level").asText().equals("DEBUG")){
                        debug++;
                    }
                    if (l.get("level").asText().equals("INFO")){
                        info++;
                    }
                    if (l.get("level").asText().equals("WARN")){
                        warn++;
                    }
                    if (l.get("level").asText().equals("ERROR")){
                        error++;
                    }
                    if (l.get("level").asText().equals("FATAL")){
                        fatal++;
                    }
                    if (l.get("level").asText().equals("OFF")){
                        off++;
                    }
                }
            }
            intValues.add(all);
            intValues.add(trace);
            intValues.add(debug);
            intValues.add(info);
            intValues.add(warn);
            intValues.add(error);
            intValues.add(fatal);
            intValues.add(off);
            logs.put(name, intValues);
        }
    }
}
