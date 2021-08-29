package nz.ac.wgtn.swen301.a3.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.ServletException;
import java.io.IOException;

public class TestDeleteLogs {
    /**
     * Tests if doDelete works properly.
     *
     * @throws ServletException
     * @throws IOException
     */
    @Test
    public void testDelete() throws ServletException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        Persistency.DB.add(mapper.readTree("{\n" +
                " \"id\" : \"d290f1ee-6c54-4b01-90e6-d701748f0851\",\n" +
                " \"message\": \"application started\",\n" +
                " \"thread\": \"main\",\n" +
                " \"logger\": \"com.example.Foo\",\n" +
                " \"level\": \"OFF\",\n" +
                " \"errorDetails\": \"string\"\n" +
                "}"));
        Persistency.DB.add(mapper.readTree("{\n" +
                " \"id\" : \"d290f1ee-6c54-4b01-90e6-d701748f0852\",\n" +
                " \"message\": \"application started\",\n" +
                " \"thread\": \"main\",\n" +
                " \"logger\": \"com.example.Foo\",\n" +
                " \"level\": \"TRACE\",\n" +
                " \"errorDetails\": \"string\"\n" +
                "}"));
        Persistency.DB.add(mapper.readTree("{\n" +
                " \"id\" : \"d290f1ee-6c54-4b01-90e6-d701748f0853\",\n" +
                " \"message\": \"application started\",\n" +
                " \"thread\": \"main\",\n" +
                " \"logger\": \"com.example.Foo\",\n" +
                " \"level\": \"DEBUG\",\n" +
                " \"errorDetails\": \"string\"\n" +
                "}"));
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        LogsServlet logsServlet = new LogsServlet();
        logsServlet.doDelete(request, response);
        assert(200 == response.getStatus());
        assert (Persistency.DB.size() == 0);
    }

    /**
     * Tests if doDelete works after doGet.
     *
     * @throws ServletException
     * @throws IOException
     */
    @Test
    public void testDelete_2() throws ServletException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        Persistency.DB.add(mapper.readTree("{\n" +
                " \"id\" : \"d290f1ee-6c54-4b01-90e6-d701748f0851\",\n" +
                " \"message\": \"application started\",\n" +
                " \"thread\": \"main\",\n" +
                " \"logger\": \"com.example.Foo\",\n" +
                " \"level\": \"OFF\",\n" +
                " \"errorDetails\": \"string\"\n" +
                "}"));
        Persistency.DB.add(mapper.readTree("{\n" +
                " \"id\" : \"d290f1ee-6c54-4b01-90e6-d701748f0852\",\n" +
                " \"message\": \"application started\",\n" +
                " \"thread\": \"main\",\n" +
                " \"logger\": \"com.example.Foo\",\n" +
                " \"level\": \"TRACE\",\n" +
                " \"errorDetails\": \"string\"\n" +
                "}"));
        Persistency.DB.add(mapper.readTree("{\n" +
                " \"id\" : \"d290f1ee-6c54-4b01-90e6-d701748f0853\",\n" +
                " \"message\": \"application started\",\n" +
                " \"thread\": \"main\",\n" +
                " \"logger\": \"com.example.Foo\",\n" +
                " \"level\": \"DEBUG\",\n" +
                " \"errorDetails\": \"string\"\n" +
                "}"));
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        LogsServlet logsServlet = new LogsServlet();
        request.setParameter("limit", "7");
        request.setParameter("level", "WARN");
        logsServlet.doGet(request, response);
        assert(200 == response.getStatus());
        assert (Persistency.DB.size() == 3);

        logsServlet.doDelete(request, response);
        assert(200 == response.getStatus());
        assert (Persistency.DB.size() == 0);
    }
}
