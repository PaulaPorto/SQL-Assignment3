package nz.ac.wgtn.swen301.a3.server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LogsServlet extends HttpServlet {
    /**
     * Sorted timestamp list.
     */
    public List<JsonNode> sorted = new ArrayList<>();

    /**
     * Hashmap containing all levels and their priority value accordingly.
     */
    public HashMap<String, Integer> levels = new HashMap<>(){{
        put("OFF", 1);
        put("FATAL", 2);
        put("ERROR", 3);
        put("WARN", 4);
        put("INFO", 5);
        put("DEBUG", 6);
        put("TRACE", 7);
        put("ALL", 8);
    }};

    /**
     * Gets the events from DB and prints them out in an array (JSON format).
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String lim = request.getParameter("limit");
        int num;
        String lev = request.getParameter("level");
        String error = "Input parameter error (not valid)";
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode arrayNode = mapper.createArrayNode();

        //Checks if level is valid
        if(lim != null && lev != null) {
            if (!levels.keySet().contains(lev)) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, error);
                return;
            }
            //Turn lim into an int
            try{
                num = Integer.parseInt(lim);
            }catch (NumberFormatException e){
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, error);
                return;
            }
            //Checks if num is valid
            if (!(num >= 0 && num < Integer.MAX_VALUE)) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, error);
                return;
            }
            List<JsonNode> jn = new ArrayList<>();
            //Output the Json objects
            for (int i = 0; i < Persistency.DB.size(); i++) {
                JsonNode n = Persistency.DB.get(i);
                String l = (n.get("level").textValue());
                if (levels.get(l) <= levels.get(lev)) {
                    //arrayNode.add(n);
                    jn.add(n);
                }
            }
            if (!Persistency.DB.isEmpty()){
                List<JsonNode> sorted = findBestTime(jn);
                if (sorted.size() < num){
                    num = sorted.size();
                }
                for (int j = 0; j < num; j++){
                    arrayNode.add(sorted.get(0));
                    sorted.remove(0);
                }
            }
            String format = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(arrayNode);
            out.print(format);
            response.setStatus(HttpServletResponse.SC_OK);
            out.close();
            return;
        }
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, error);
    }

    public List<JsonNode> findBestTime(List<JsonNode> jn) {
        List<JsonNode> unsorted = jn;
        JsonNode bestTime = null;
        for (int j = 0; j < unsorted.size(); j++) {
            JsonNode nodeJ = unsorted.get(j);
            if (bestTime != null) {
                String tstmp = bestTime.get("timestamp").asText(); //dd-mm-yyyy hh:mm:ss
                String[] stamp = tstmp.split(" "); //0 | dd-mm-yyyy 1| hh:mm:ss
                String[] date = stamp[0].split("-"); //0| dd 1|mm 2|yyyy
                String[] time = stamp[1].split(":"); //0|hh 1|mm 2|ss
                int year = Integer.parseInt(date[2]);
                int month = Integer.parseInt(date[1]);
                int day = Integer.parseInt(date[0]);
                int hour = Integer.parseInt(time[0]);
                int minute = Integer.parseInt(time[1]);
                int second = Integer.parseInt(time[2]);
                String tstmp2 = nodeJ.get("timestamp").asText(); //dd-mm-yyyy hh:mm:ss
                String[] stamp2 = tstmp2.split(" "); //0 | dd-mm-yyyy 1| hh:mm:ss
                String[] date2 = stamp2[0].split("-"); //0| dd 1|mm 2|yyyy
                String[] time2 = stamp2[1].split(":"); //0|hh 1|mm 2|ss
                int year2 = Integer.parseInt(date2[2]);
                int month2 = Integer.parseInt(date2[1]);
                int day2 = Integer.parseInt(date2[0]);
                int hour2 = Integer.parseInt(time2[0]);
                int minute2 = Integer.parseInt(time2[1]);
                int second2 = Integer.parseInt(time2[2]);
                if (year2 > year) {
                    bestTime = nodeJ;
                } else if (year2 == year) {
                    if (month2 > month) {
                        bestTime = nodeJ;
                    } else if (month2 == month) {
                        if (day2 > day) {
                            bestTime = nodeJ;
                        } else if (day2 == day) {
                            if (hour2 > hour) {
                                bestTime = nodeJ;
                            } else if (hour2 == hour) {
                                if (minute2 > minute) {
                                    bestTime = nodeJ;
                                } else if (minute2 == minute) {
                                    if (second2 > second) {
                                        bestTime = nodeJ;
                                    }
                                }
                            }
                        }
                    }
                }
                }else {bestTime = nodeJ; }
        }
        sorted.add(bestTime); //Add bestTime first.
        unsorted.remove(bestTime); // remove the best out of the list.
        while (unsorted.size() > 0){
          findBestTime(unsorted); //find best time with remaining nodes.
        }
        return sorted;
    }

    /**
     * Adds logging event to DB.
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        BufferedReader br = request.getReader();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(br);
        //Check if event is a valid LogEvent
        if ((node.get("id") != null) &&
                (node.get("message") != null) &&
                (node.get("timestamp") != null) &&
                (node.get("thread") != null) &&
                (node.get("logger") != null) &&
                (node.get("level") != null)){
            //Check if a log event with this id already exists
            for (int i = 0; i < Persistency.DB.size(); i++) {
                if(node.get("id").equals(Persistency.DB.get(i).get("id"))){
                    response.sendError(HttpServletResponse.SC_CONFLICT);
                    return;
                }
            }
            Persistency.DB.add(node);
            response.setStatus(HttpServletResponse.SC_CREATED);
        } else { response.sendError(HttpServletResponse.SC_BAD_REQUEST);}
    }

    /**
     * Deletes logging events in DB.
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Persistency.DB.clear();
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
