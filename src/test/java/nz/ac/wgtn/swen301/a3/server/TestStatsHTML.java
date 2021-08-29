package nz.ac.wgtn.swen301.a3.server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.*;
import static org.junit.Assert.assertEquals;

public class TestStatsHTML {
    /**
     * Checks doGet works.
     *
     * @throws ServletException
     * @throws IOException
     */
    @Test
    public void testHTML() throws ServletException, IOException {
        Persistency.DB.clear();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = new MockHttpServletRequest();

        StatsServlet stats = new StatsServlet();
        stats.doGet(request, response);

        Document doc = Jsoup.parse(response.getContentAsString());
        String[] headers = Jsoup.clean(doc.select("th").toString(),
                Whitelist.none()).split(" ");
        assertEquals("logger", headers[0]);
        assertEquals("ALL", headers[1]);
        assertEquals("TRACE", headers[2]);
        assertEquals("DEBUG", headers[3]);
        assertEquals("INFO", headers[4]);
        assertEquals("WARN", headers[5]);
        assertEquals("ERROR", headers[6]);
        assertEquals("FATAL", headers[7]);
        assertEquals("OFF", headers[8]);
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
    public void testHTMLOutput_1() throws ServletException, IOException {
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
                " \"level\": \"ERROR\",\n" +
                " \"errorDetails\": \"string\"\n" +
                "}";
        String log3 = "{\n" +
                " \"id\" : \"d290f1ee-6c54-4b01-90e6-d701748f0851\",\n" +
                " \"message\": \"application started\",\n" +
                " \"timestamp\": \"04-05-2021 10:12:00\",\n" +
                " \"thread\": \"main\",\n" +
                " \"logger\": \"com.example.Foo\",\n" +
                " \"level\": \"OFF\",\n" +
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
        StatsServlet stats = new StatsServlet();
        stats.doGet(request, response);

        List<Integer> logName1 = stats.logs.get("com.example.Foo");
        List<Integer> correct = new ArrayList<>(Arrays.asList(0, 0, 0, 1, 0, 1, 0, 1));

        assertEquals(correct, logName1);
        Document doc = Jsoup.parse(response.getContentAsString());
        String body = doc.body().wholeText();
        String[] output = body.split("\n");
        String logOne = output[1];
        assertEquals("com.example.Foo00010101", logOne);
        assertEquals(200, response.getStatus());
    }

    /**
     * Checks that the output is being organized correctly.
     *
     * @throws ServletException
     * @throws IOException
     */
    @Test
    public void testHTMLOutput_2() throws ServletException, IOException {
        Persistency.DB.clear();
        ObjectMapper map = new ObjectMapper();
        String log1 = "{\n" +
                " \"id\" : \"d290f1ee-6c54-4b01-90e6-d701748f0852\",\n" +
                " \"message\": \"application started\",\n" +
                " \"timestamp\": \"04-05-2021 10:12:00\",\n" +
                " \"thread\": \"main\",\n" +
                " \"logger\": \"com.example.Foo\",\n" +
                " \"level\": \"DEBUG\",\n" +
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
                " \"id\" : \"d290f1ee-6c54-4b01-90e6-d701748f0854\",\n" +
                " \"message\": \"application started\",\n" +
                " \"timestamp\": \"04-05-2021 10:12:00\",\n" +
                " \"thread\": \"main\",\n" +
                " \"logger\": \"com.Foo\",\n" +
                " \"level\": \"DEBUG\",\n" +
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
        StatsServlet stats = new StatsServlet();
        stats.doGet(request, response);

        List<Integer> logName1 = stats.logs.get("com.example.Foo");
        List<Integer> correct = new ArrayList<>(Arrays.asList(0, 1, 1, 0, 0, 0, 0, 0));

        List<Integer> logName2 = stats.logs.get("com.Foo");
        List<Integer> correct2 = new ArrayList<>(Arrays.asList(0, 0, 1, 0, 0, 0, 0, 0));

        List<Integer> logName3 = stats.logs.get("com.testing");
        List<Integer> correct3 = new ArrayList<>(Arrays.asList(1, 0, 0, 0, 1, 1, 1, 0));

        assertEquals(correct, logName1);
        assertEquals(correct2, logName2);
        assertEquals(correct3, logName3);

        Document doc = Jsoup.parse(response.getContentAsString());
        String body = doc.body().wholeText();
        String[] output = body.split("\n");
        String outputs = output[1];

        assertEquals("com.Foo00100000com.testing10001110com.example.Foo01100000", outputs);
        assertEquals(200, response.getStatus());
    }
}
