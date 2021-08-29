package nz.ac.wgtn.swen301.a3.server;

import com.fasterxml.jackson.databind.JsonNode;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class StatsCSVServlet extends HttpServlet {
    public Map<String, List<Integer>> logs = new HashMap();

    /**
     * Gets the output for CSV.
     * Prints table of logs.
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        getOutput();
        response.setContentType("text/csv");
        PrintWriter writer = response.getWriter();
        String[] levels = {"logger\t", "ALL\t", "TRACE\t", "DEBUG\t", "INFO\t",
                "WARN\t", "ERROR\t", "FATAL\t", "OFF\t",};
        for (String lev : levels){
            writer.print(lev);
        }
        for (String l : logs.keySet()){
            writer.print("\n"+ l +"\t");
            List<Integer> num = logs.get(l);
            for(Integer lev : num){
                writer.print(lev + "\t");
            }
        }
        writer.close();
    }

    /**
     * Gets the needed values for the table being printed for CSV.
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
