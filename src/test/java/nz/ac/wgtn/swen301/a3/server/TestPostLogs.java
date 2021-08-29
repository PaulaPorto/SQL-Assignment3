package nz.ac.wgtn.swen301.a3.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;

public class TestPostLogs {

    /**
     * Tests a valid log to be added to DB.
     *
     * @throws ServletException
     * @throws IOException
     */
    @Test
    public void testDoPostValid() throws ServletException, IOException {
        Persistency.DB.clear();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        String log = "{\n" +
                " \"id\" : \"d290f1ee-6c54-4b01-90e6-d701748f0851\",\n" +
                " \"message\": \"application started\",\n" +
                " \"timestamp\": \"04-05-2021 10:12:00\",\n" +
                " \"thread\": \"main\",\n" +
                " \"logger\": \"com.example.Foo\",\n" +
                " \"level\": \"WARN\",\n" +
                " \"errorDetails\": \"string\"\n" +
                "}";
        byte[] byteArray = log.getBytes();
        request.setContent(byteArray);
        LogsServlet logsServlet = new LogsServlet();
        logsServlet.doPost(request,response);
        assert (201 == response.getStatus());
        assert(Persistency.DB.size() == 1);
    }

    /**
     * Tests an invalid log (logger is null).
     *
     * @throws ServletException
     * @throws IOException
     */
    @Test
    public void testDoPostInvalid() throws ServletException, IOException {
        Persistency.DB.clear();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        String log = "{\n" +
                " \"id\" : \"d290f1ee-6c54-4b01-90e6-d701748f0851\",\n" +
                " \"message\": \"application started\",\n" +
                " \"timestamp\": \"04-05-2021 10:12:00\",\n" +
                " \"thread\": \"main\",\n" +
                " \"level\": \"WARN\",\n" +
                " \"errorDetails\": \"string\"\n" +
                "}";
        byte[] byteArray = log.getBytes();
        request.setContent(byteArray);
        LogsServlet logsServlet = new LogsServlet();
        logsServlet.doPost(request,response);
        assert (400 == response.getStatus());
        assert(Persistency.DB.size() == 0);
    }

    /**
     * Tests that a log with the same id is not added twice to DB.
     *
     * @throws ServletException
     * @throws IOException
     */
    @Test
    public void testDoPostInvalidSameID() throws ServletException, IOException {
        Persistency.DB.clear();
        ObjectMapper mapper = new ObjectMapper();
        Persistency.DB.add(mapper.readTree("{\n" +
                " \"id\" : \"d290f1ee-6c54-4b01-90e6-d701748f0851\",\n" +
                " \"message\": \"application started\",\n" +
                " \"thread\": \"main\",\n" +
                " \"logger\": \"com.example.Foo\",\n" +
                " \"level\": \"OFF\",\n" +
                " \"errorDetails\": \"string\"\n" +
                "}"));
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        String log = "{\n" +
                " \"id\" : \"d290f1ee-6c54-4b01-90e6-d701748f0851\",\n" +
                " \"message\": \"application started\",\n" +
                " \"timestamp\": \"04-05-2021 10:12:00\",\n" +
                " \"thread\": \"main\",\n" +
                " \"logger\": \"com.example.Foo\",\n" +
                " \"level\": \"WARN\",\n" +
                " \"errorDetails\": \"string\"\n" +
                "}";
        byte[] byteArray = log.getBytes();
        request.setContent(byteArray);
        LogsServlet logsServlet = new LogsServlet();
        logsServlet.doPost(request,response);
        assert (409 == response.getStatus());
        assert(Persistency.DB.size() == 1);
    }
}
