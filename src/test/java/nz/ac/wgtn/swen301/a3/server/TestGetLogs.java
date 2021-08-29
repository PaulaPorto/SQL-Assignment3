package nz.ac.wgtn.swen301.a3.server;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class TestGetLogs {

    /**
     * Tests an invalid input (null).
     *
     * @throws ServletException
     * @throws IOException
     */
    @Test
    public void testInvalidRequestResponse_1() throws ServletException, IOException {
        Persistency.DB.clear();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        LogsServlet logsServlet = new LogsServlet();
        logsServlet.doGet(request, response);

        assert(400 == response.getStatus());
    }

    /**
     * Tests an invalid input.
     *
     * @throws ServletException
     * @throws IOException
     */
    @Test
    public void testInvalidRequestResponse_2() throws ServletException, IOException {
        Persistency.DB.clear();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setParameter("invalid input", "8");
        LogsServlet logsServlet = new LogsServlet();
        logsServlet.doGet(request, response);

        assert(400 == response.getStatus());
    }

    /**
     * Tests an invalid limit input.
     *
     * @throws ServletException
     * @throws IOException
     */
    @Test
    public void testInvalidLimitInput_1() throws ServletException, IOException {
        Persistency.DB.clear();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setParameter("DEBUG", "invalid limit");
        LogsServlet logsServlet = new LogsServlet();
        logsServlet.doGet(request, response);

        assert(400 == response.getStatus());
    }

    /**
     * Tests an invalid limit input (less than 0).
     *
     * @throws ServletException
     * @throws IOException
     */
    @Test
    public void testInvalidLimitInput_2() throws ServletException, IOException {
        Persistency.DB.clear();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setParameter("limit", "-1");
        request.setParameter("level", "WARN");
        LogsServlet logsServlet = new LogsServlet();
        logsServlet.doGet(request, response);

        assert(400 == response.getStatus());
    }

    /**
     * Tests an invalid limit input.
     *
     * @throws ServletException
     * @throws IOException
     */
    @Test
    public void testInvalidLimitInput_3() throws ServletException, IOException {
        Persistency.DB.clear();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setParameter("limit", "wrong");
        request.setParameter("level", "WARN");
        LogsServlet logsServlet = new LogsServlet();
        logsServlet.doGet(request, response);

        assert(400 == response.getStatus());
    }

    /**
     * Tests an invalid level.
     *
     * @throws ServletException
     * @throws IOException
     */
    @Test
    public void testInvalidLevelInput() throws ServletException, IOException {
        Persistency.DB.clear();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setParameter("limit", "1");
        request.setParameter("level", "wrong");
        LogsServlet logsServlet = new LogsServlet();
        logsServlet.doGet(request, response);

        assert(400 == response.getStatus());
    }

    /**
     * Tests a valid input.
     *
     * @throws ServletException
     * @throws IOException
     */
    @Test
    public void testValidInput_1() throws ServletException, IOException {
        Persistency.DB.clear();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setParameter("limit", "0");
        request.setParameter("level", "TRACE");
        LogsServlet logsServlet = new LogsServlet();
        logsServlet.doGet(request, response);

        assert(200 == response.getStatus());
    }

    /**
     * Tests a valid input.
     *
     * @throws ServletException
     * @throws IOException
     */
    @Test
    public void testValidInput_2() throws ServletException, IOException {
        Persistency.DB.clear();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setParameter("limit", "7");
        request.setParameter("level", "OFF");
        LogsServlet logsServlet = new LogsServlet();
        logsServlet.doGet(request, response);

        assert(200 == response.getStatus());
    }

    /**
     * Tests that findBestTime works properly for doGet.
     *
     * @throws ServletException
     * @throws IOException
     */
    @Test
    public void testTimestamp() throws ServletException, IOException {
        Persistency.DB.clear();
        List <JsonNode> list = new ArrayList<>();
        ObjectMapper map = new ObjectMapper();
        String log1 = "{\n" +
                " \"id\" : \"d290f1ee-6c54-4b01-90e6-d701748f0852\",\n" +
                " \"message\": \"application started\",\n" +
                " \"timestamp\": \"04-05-2021 10:12:01\",\n" +
                " \"thread\": \"main\",\n" +
                " \"logger\": \"com.example.Foo\",\n" +
                " \"level\": \"WARN\",\n" +
                " \"errorDetails\": \"string\"\n" +
                "}";
        String log2 = "{\n" +
                " \"id\" : \"id-2\",\n" +
                " \"message\": \"application started\",\n" +
                " \"timestamp\": \"04-05-2022 10:12:00\",\n" +
                " \"thread\": \"main\",\n" +
                " \"logger\": \"com.example.Foo\",\n" +
                " \"level\": \"OFF\",\n" +
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
        list.add(node1);
        list.add(node2);
        list.add(node3);
        LogsServlet logsServlet = new LogsServlet();
        List <JsonNode> ordered = logsServlet.findBestTime(list);
        List<JsonNode> sorted = new ArrayList<>();
        sorted.add(node2);
        sorted.add(node1);
        sorted.add(node3);

        assert(sorted.equals(ordered));
    }

    /**
     * Tests that the output is working.
     *
     * @throws ServletException
     * @throws IOException
     */
    @Test
    public void testOutput() throws ServletException, IOException {
        Persistency.DB.clear();
        ObjectMapper mapper = new ObjectMapper();
        Persistency.DB.add(mapper.readTree("{\n" +
                " \"id\" : \"d290f1ee-6c54-4b01-90e6-d701748f0851\",\n" +
                " \"message\": \"application started\",\n" +
                " \"timestamp\": \"04-05-2020 10:12:00\",\n" +
                " \"thread\": \"main\",\n" +
                " \"logger\": \"com.example.Foo\",\n" +
                " \"level\": \"OFF\",\n" +
                " \"errorDetails\": \"string\"\n" +
                "}"));
        Persistency.DB.add(mapper.readTree("{\n" +
                " \"id\" : \"d290f1ee-6c54-4b01-90e6-d701748f0852\",\n" +
                " \"message\": \"application started\",\n" +
                " \"timestamp\": \"04-05-2021 09:12:00\",\n" +
                " \"thread\": \"main\",\n" +
                " \"logger\": \"com.example.Foo\",\n" +
                " \"level\": \"TRACE\",\n" +
                " \"errorDetails\": \"string\"\n" +
                "}"));
        Persistency.DB.add(mapper.readTree("{\n" +
                " \"id\" : \"d290f1ee-6c54-4b01-90e6-d701748f0853\",\n" +
                " \"message\": \"application started\",\n" +
                " \"timestamp\": \"04-05-2021 10:12:00\",\n" +
                " \"thread\": \"main\",\n" +
                " \"logger\": \"com.example.Foo\",\n" +
                " \"level\": \"WARN\",\n" +
                " \"errorDetails\": \"string\"\n" +
                "}"));
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setParameter("limit", "7");
        request.setParameter("level", "WARN");
        LogsServlet logsServlet = new LogsServlet();
        logsServlet.doGet(request, response);

        assert(200 == response.getStatus());
    }

    /**
     * Tests that the right content is being "printed out".
     *
     * @throws ServletException
     * @throws IOException
     */
    @Test
    public void testRightContent() throws ServletException, IOException{
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
                " \"level\": \"OFF\",\n" +
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
        request.setParameter("limit", "3");
        request.setParameter("level", "OFF");
        MockHttpServletResponse response = new MockHttpServletResponse();
        LogsServlet logsServlet = new LogsServlet();
        logsServlet.doGet(request, response);
        String res = response.getContentAsString();
        ArrayNode array = map.createArrayNode();
        array.add(map.readTree(log2));
        String expected = map.writerWithDefaultPrettyPrinter().writeValueAsString(array);

        assertEquals(expected, res);
    }
}
