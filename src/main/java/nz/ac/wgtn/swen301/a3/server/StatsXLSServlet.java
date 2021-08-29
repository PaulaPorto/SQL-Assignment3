package nz.ac.wgtn.swen301.a3.server;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

public class StatsXLSServlet extends HttpServlet {
    public Map<String, List<Integer>> logs = new HashMap();

    /**
     * Prints output in Excel.
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        getOutput();
        response.setHeader("Content-Disposition", "attachment; filename=stats.xlsx");
        response.setContentType("application/vnd.ms-excel");
        OutputStream out = response.getOutputStream();
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet workBook = workbook.createSheet("stats");
        String [] headers = {
                "logger",
                "ALL",
                "TRACE",
                "DEBUG",
                "INFO",
                "WARN",
                "ERROR",
                "FATAL",
                "OFF"
        };
        int r = 0;
        int c = 0;
        Row rows = workBook.createRow(r++);
        Cell cells;
        for (int i = 0; i < headers.length; i++){
            cells = rows.createCell(c++);
            cells.setCellValue(headers[i]);
        }
        for (String l : logs.keySet()){
            c = 0;
            rows = workBook.createRow(r++);
            cells = rows.createCell(c++);
            cells.setCellValue(l);
            List<Integer> num = logs.get(l);
            for (int k = 0; k < num.size(); k++){
                cells = rows.createCell(c++);
                cells.setCellValue(num.get(k));
            }
        }
        workbook.write(out);
    }
    /**
     * Gets the needed values for the table being printed for XLS.
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
