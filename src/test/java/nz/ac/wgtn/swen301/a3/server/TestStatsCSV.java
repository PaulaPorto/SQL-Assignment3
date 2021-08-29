package nz.ac.wgtn.swen301.a3.server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestStatsCSV {

    /**
     * Checks doGet works.
     *
     * @throws ServletException
     * @throws IOException
     */
    @Test
    public void testCSV() throws ServletException, IOException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = new MockHttpServletRequest();
        StatsCSVServlet csv = new StatsCSVServlet();
        csv.doGet(request, response);
        String levels = "logger\tALL\tTRACE\tDEBUG\tINFO\tWARN\tERROR\tFATAL\tOFF\t";
        String expected = response.getContentAsString();

        assert (levels.equals(expected));
        assert (response.getStatus() == 200);
    }

    /**
     * Checks that the output is being organized correctly.
     * Being added to the map as it should for the table.
     *
     * @throws ServletException
     * @throws IOException
     */
    @Test
    public void testCSVOutput_1() throws ServletException, IOException {
        Persistency.DB.clear();
        ObjectMapper map = new ObjectMapper();
        String log1 = "{\n" +
                " \"id\" : \"d290f1ee-6c54-4b01-90e6-d701748f0852\",\n" +
                " \"message\": \"application started\",\n" +
                " \"timestamp\": \"04-05-2021 10:12:00\",\n" +
                " \"thread\": \"main\",\n" +
                " \"logger\": \"com.example.Foo\",\n" +
                " \"level\": \"WARN\",\n" +
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
                " \"logger\": \"com.testing\",\n" +
                " \"level\": \"FATAL\",\n" +
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
        StatsCSVServlet csvServlet = new StatsCSVServlet();
        csvServlet.doGet(request, response);

        List<Integer> logName1 = csvServlet.logs.get("com.example.Foo");
        List<Integer> correct = new ArrayList<>(Arrays.asList(0, 1, 0, 0, 1, 0, 0, 0));

        List<Integer> logName2 = csvServlet.logs.get("com.testing");
        List<Integer> correct2 = new ArrayList<>(Arrays.asList(0, 0, 0, 0, 0, 0, 1, 0));

        assertEquals(correct, logName1);
        assertEquals(correct2, logName2);
        assertEquals(200, response.getStatus());
    }

    /**
     * Checks that the output is being organized correctly.
     *
     * @throws ServletException
     * @throws IOException
     */
    @Test
    public void testCSVOutput_2() throws ServletException, IOException {
        Persistency.DB.clear();
        ObjectMapper map = new ObjectMapper();
        String log1 = "{\n" +
                " \"id\" : \"d290f1ee-6c54-4b01-90e6-d701748f0852\",\n" +
                " \"message\": \"application started\",\n" +
                " \"timestamp\": \"04-05-2021 10:12:00\",\n" +
                " \"thread\": \"main\",\n" +
                " \"logger\": \"com.example.Foo\",\n" +
                " \"level\": \"OFF\",\n" +
                " \"errorDetails\": \"string\"\n" +
                "}";
        String log2 = "{\n" +
                " \"id\" : \"id-2\",\n" +
                " \"message\": \"application started\",\n" +
                " \"timestamp\": \"04-05-2021 10:12:00\",\n" +
                " \"thread\": \"main\",\n" +
                " \"logger\": \"com.example.Foo\",\n" +
                " \"level\": \"OFF\",\n" +
                " \"errorDetails\": \"string\"\n" +
                "}";
        String log3 = "{\n" +
                " \"id\" : \"d290f1ee-6c54-4b01-90e6-d701748f0854\",\n" +
                " \"message\": \"application started\",\n" +
                " \"timestamp\": \"04-05-2021 10:12:00\",\n" +
                " \"thread\": \"main\",\n" +
                " \"logger\": \"com.testing1\",\n" +
                " \"level\": \"INFO\",\n" +
                " \"errorDetails\": \"string\"\n" +
                "}";
        String log4 = "{\n" +
                " \"id\" : \"d290f1ee-6c54-4b01-90e6-d701748f0855\",\n" +
                " \"message\": \"application started\",\n" +
                " \"timestamp\": \"04-05-2021 10:12:00\",\n" +
                " \"thread\": \"main\",\n" +
                " \"logger\": \"com.testing\",\n" +
                " \"level\": \"ALL\",\n" +
                " \"errorDetails\": \"string\"\n" +
                "}";
        String log5 = "{\n" +
                " \"id\" : \"d290f1ee-6c54-4b01-90e6-d701748f0856\",\n" +
                " \"message\": \"application started\",\n" +
                " \"timestamp\": \"04-05-2021 10:12:00\",\n" +
                " \"thread\": \"main\",\n" +
                " \"logger\": \"com.testing\",\n" +
                " \"level\": \"DEBUG\",\n" +
                " \"errorDetails\": \"string\"\n" +
                "}";
        String log6 = "{\n" +
                " \"id\" : \"d290f1ee-6c54-4b01-90e6-d701748f0857\",\n" +
                " \"message\": \"application started\",\n" +
                " \"timestamp\": \"04-05-2021 10:12:00\",\n" +
                " \"thread\": \"main\",\n" +
                " \"logger\": \"com.testing\",\n" +
                " \"level\": \"DEBUG\",\n" +
                " \"errorDetails\": \"string\"\n" +
                "}";
        String log7 = "{\n" +
                " \"id\" : \"d290f1ee-6c54-4b01-90e6-d701748f0857\",\n" +
                " \"message\": \"application started\",\n" +
                " \"timestamp\": \"04-05-2021 10:12:00\",\n" +
                " \"thread\": \"main\",\n" +
                " \"logger\": \"com.testing\",\n" +
                " \"level\": \"ERROR\",\n" +
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
        StatsCSVServlet csvServlet = new StatsCSVServlet();
        csvServlet.doGet(request, response);

        List<Integer> logName1 = csvServlet.logs.get("com.example.Foo");
        List<Integer> correct = new ArrayList<>(Arrays.asList(0, 0, 0, 0, 0, 0, 0, 2));

        List<Integer> logName2 = csvServlet.logs.get("com.testing1");
        List<Integer> correct2 = new ArrayList<>(Arrays.asList(0, 0, 0, 1, 0, 0, 0, 0));

        List<Integer> logName3 = csvServlet.logs.get("com.testing");
        List<Integer> correct3 = new ArrayList<>(Arrays.asList(1, 0, 2, 0, 0, 1, 0, 0));

        assertEquals(correct, logName1);
        assertEquals(correct2, logName2);
        assertEquals(correct3, logName3);
        assertEquals(200, response.getStatus());
    }
}
