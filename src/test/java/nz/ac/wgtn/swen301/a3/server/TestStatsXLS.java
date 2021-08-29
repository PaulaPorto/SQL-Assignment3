package nz.ac.wgtn.swen301.a3.server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.ServletException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestStatsXLS {
    /**
     * Checks doGet works.
     *
     * @throws ServletException
     * @throws IOException
     */
    @Test
    public void testXLSHeader() throws ServletException, IOException {
        Persistency.DB.clear();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = new MockHttpServletRequest();
        StatsXLSServlet xls = new StatsXLSServlet();
        xls.doGet(request, response);
        byte[] output = response.getContentAsByteArray();
        InputStream stream = new ByteArrayInputStream(output);
        XSSFWorkbook workbook = new XSSFWorkbook(stream);
        Sheet sheet = workbook.getSheet("stats");
        Row header = sheet.getRow(0);
        assertEquals(header.getCell(header.getFirstCellNum()).toString(), "logger");
        assertEquals(header.getCell(1).getStringCellValue(), "ALL");
        assertEquals(header.getCell(2).getStringCellValue(), "TRACE");
        assertEquals(header.getCell(3).getStringCellValue(), "DEBUG");
        assertEquals(header.getCell(4).getStringCellValue(), "INFO");
        assertEquals(header.getCell(5).getStringCellValue(), "WARN");
        assertEquals(header.getCell(6).getStringCellValue(), "ERROR");
        assertEquals(header.getCell(7).getStringCellValue(), "FATAL");
        assertEquals(header.getCell(8).getStringCellValue(), "OFF");
        assertEquals(200, response.getStatus());
    }

    /**
     * Checks that the output is being organized correctly.
     * Being added to the map as it should for the table.
     *
     * @throws ServletException
     * @throws IOException
     */
    @Test
    public void testXLSOutput_1() throws ServletException, IOException {
        Persistency.DB.clear();
        ObjectMapper map = new ObjectMapper();
        String log1 = "{\n" +
                " \"id\" : \"d290f1ee-6c54-4b01-90e6-d701748f0852\",\n" +
                " \"message\": \"application started\",\n" +
                " \"timestamp\": \"04-05-2021 10:12:00\",\n" +
                " \"thread\": \"main\",\n" +
                " \"logger\": \"com.example.Foo\",\n" +
                " \"level\": \"ALL\",\n" +
                " \"errorDetails\": \"string\"\n" +
                "}";
        String log2 = "{\n" +
                " \"id\" : \"id-2\",\n" +
                " \"message\": \"application started\",\n" +
                " \"timestamp\": \"04-05-2021 10:12:00\",\n" +
                " \"thread\": \"main\",\n" +
                " \"logger\": \"com.example.Foo\",\n" +
                " \"level\": \"TRACE\",\n" +
                " \"errorDetails\": \"string\"\n" +
                "}";
        String log3 = "{\n" +
                " \"id\" : \"d290f1ee-6c54-4b01-90e6-d701748f0851\",\n" +
                " \"message\": \"application started\",\n" +
                " \"timestamp\": \"04-05-2021 10:12:00\",\n" +
                " \"thread\": \"main\",\n" +
                " \"logger\": \"com.example.Foo\",\n" +
                " \"level\": \"DEBUG\",\n" +
                " \"errorDetails\": \"string\"\n" +
                "}";
        JsonNode node1 = map.readTree(log1);
        JsonNode node2 = map.readTree(log2);
        JsonNode node3 = map.readTree(log3);
        Persistency.DB.add(node1);
        Persistency.DB.add(node2);
        Persistency.DB.add(node3);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        StatsXLSServlet xls = new StatsXLSServlet();
        xls.doGet(request, response);
        byte[] output = response.getContentAsByteArray();
        InputStream stream = new ByteArrayInputStream(output);
        XSSFWorkbook workbook = new XSSFWorkbook(stream);
        Sheet sheet = workbook.getSheet("stats");
        Row outputs = sheet.getRow(1);
        assertEquals("com.example.Foo", outputs.getCell(outputs.getFirstCellNum()).toString());
        List<Double> values = new ArrayList<>(Arrays.asList(1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0));
        for(int i = 1 ; i < 9; i++){
            Double exp = values.get(i - 1);
            Double actual =  outputs.getCell(i).getNumericCellValue();
            assertEquals(exp, actual);
        }

        List<Integer> logName1 = xls.logs.get("com.example.Foo");
        List<Integer> correct = new ArrayList<>(Arrays.asList(1, 1, 1, 0, 0, 0, 0, 0));

        assertEquals(correct, logName1);
        assertEquals(200, response.getStatus());
    }

    /**
     * Checks that the output is being organized correctly.
     *
     * @throws ServletException
     * @throws IOException
     */
    @Test
    public void testXLSOutput_2() throws ServletException, IOException {
        Persistency.DB.clear();
        ObjectMapper map = new ObjectMapper();
        String log1 = "{\n" +
                " \"id\" : \"d290f1ee-6c54-4b01-90e6-d701748f0852\",\n" +
                " \"message\": \"application started\",\n" +
                " \"timestamp\": \"04-05-2021 10:12:00\",\n" +
                " \"thread\": \"main\",\n" +
                " \"logger\": \"com.example.Foo\",\n" +
                " \"level\": \"INFO\",\n" +
                " \"errorDetails\": \"string\"\n" +
                "}";
        String log2 = "{\n" +
                " \"id\" : \"id-2\",\n" +
                " \"message\": \"application started\",\n" +
                " \"timestamp\": \"04-05-2021 10:12:00\",\n" +
                " \"thread\": \"main\",\n" +
                " \"logger\": \"com.example.Foo\",\n" +
                " \"level\": \"WARN\",\n" +
                " \"errorDetails\": \"string\"\n" +
                "}";
        String log3 = "{\n" +
                " \"id\" : \"d290f1ee-6c54-4b01-90e6-d701748f0854\",\n" +
                " \"message\": \"application started\",\n" +
                " \"timestamp\": \"04-05-2021 10:12:00\",\n" +
                " \"thread\": \"main\",\n" +
                " \"logger\": \"com.Foo\",\n" +
                " \"level\": \"ERROR\",\n" +
                " \"errorDetails\": \"string\"\n" +
                "}";
        String log4 = "{\n" +
                " \"id\" : \"d290f1ee-6c54-4b01-90e6-d701748f0855\",\n" +
                " \"message\": \"application started\",\n" +
                " \"timestamp\": \"04-05-2021 10:12:00\",\n" +
                " \"thread\": \"main\",\n" +
                " \"logger\": \"com.testing\",\n" +
                " \"level\": \"FATAL\",\n" +
                " \"errorDetails\": \"string\"\n" +
                "}";
        String log5 = "{\n" +
                " \"id\" : \"d290f1ee-6c54-4b01-90e6-d701748f0856\",\n" +
                " \"message\": \"application started\",\n" +
                " \"timestamp\": \"04-05-2021 10:12:00\",\n" +
                " \"thread\": \"main\",\n" +
                " \"logger\": \"com.testing\",\n" +
                " \"level\": \"FATAL\",\n" +
                " \"errorDetails\": \"string\"\n" +
                "}";
        String log6 = "{\n" +
                " \"id\" : \"d290f1ee-6c54-4b01-90e6-d701748f0857\",\n" +
                " \"message\": \"application started\",\n" +
                " \"timestamp\": \"04-05-2021 10:12:00\",\n" +
                " \"thread\": \"main\",\n" +
                " \"logger\": \"com.testing\",\n" +
                " \"level\": \"WARN\",\n" +
                " \"errorDetails\": \"string\"\n" +
                "}";
        String log7 = "{\n" +
                " \"id\" : \"d290f1ee-6c54-4b01-90e6-d701748f0857\",\n" +
                " \"message\": \"application started\",\n" +
                " \"timestamp\": \"04-05-2021 10:12:00\",\n" +
                " \"thread\": \"main\",\n" +
                " \"logger\": \"com.testing\",\n" +
                " \"level\": \"OFF\",\n" +
                " \"errorDetails\": \"string\"\n" +
                "}";
        JsonNode node1 = map.readTree(log1);
        JsonNode node2 = map.readTree(log2);
        JsonNode node3 = map.readTree(log3);
        JsonNode node4 = map.readTree(log4);
        JsonNode node5 = map.readTree(log5);
        JsonNode node6 = map.readTree(log6);
        JsonNode node7 = map.readTree(log7);
        Persistency.DB.add(node1);
        Persistency.DB.add(node2);
        Persistency.DB.add(node3);
        Persistency.DB.add(node4);
        Persistency.DB.add(node5);
        Persistency.DB.add(node6);
        Persistency.DB.add(node7);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        StatsXLSServlet xls = new StatsXLSServlet();
        xls.doGet(request, response);

        List<Integer> logName1 = xls.logs.get("com.example.Foo");
        List<Integer> correct = new ArrayList<>(Arrays.asList(0, 0, 0, 1, 1, 0, 0, 0));

        List<Integer> logName2 = xls.logs.get("com.Foo");
        List<Integer> correct2 = new ArrayList<>(Arrays.asList(0, 0, 0, 0, 0, 1, 0, 0));

        List<Integer> logName3 = xls.logs.get("com.testing");
        List<Integer> correct3 = new ArrayList<>(Arrays.asList(0, 0, 0, 0, 1, 0, 2, 1));

        assertEquals(correct, logName1);
        assertEquals(correct2, logName2);
        assertEquals(correct3, logName3);

        byte[] output = response.getContentAsByteArray();
        InputStream stream = new ByteArrayInputStream(output);
        XSSFWorkbook workbook = new XSSFWorkbook(stream);
        Sheet sheet = workbook.getSheet("stats");

        Row out1 = sheet.getRow(1);
        assertEquals("com.Foo", out1.getCell(0).getStringCellValue());
        Row out2 = sheet.getRow(2);
        assertEquals("com.testing", out2.getCell(0).getStringCellValue());
        Row out3 = sheet.getRow(3);
        assertEquals("com.example.Foo", out3.getCell(0).getStringCellValue());
        assertEquals(200, response.getStatus());

        List<Double> values1 = new ArrayList<>(Arrays.asList(0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0));
        for(int i = 1 ; i < 9; i++){
            Double exp = values1.get(i - 1);
            Double actual =  out1.getCell(i).getNumericCellValue();
            assertEquals(exp, actual);
        }
        List<Double> values2 = new ArrayList<>(Arrays.asList(0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 2.0, 1.0));
        for(int i = 1 ; i < 9; i++){
            Double exp = values2.get(i - 1);
            Double actual =  out2.getCell(i).getNumericCellValue();
            assertEquals(exp, actual);
        }
        List<Double> values3 = new ArrayList<>(Arrays.asList(0.0, 0.0, 0.0, 1.0, 1.0, 0.0, 0.0, 0.0));
        for(int i = 1 ; i < 9; i++){
            Double exp = values3.get(i - 1);
            Double actual =  out3.getCell(i).getNumericCellValue();
            assertEquals(exp, actual);
        }
    }

}
