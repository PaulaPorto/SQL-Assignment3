package nz.ac.wgtn.swen301.a3.server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public class Persistency {
    /**
     * List of logging events.
     */
    public static List<JsonNode> DB = new ArrayList<>();
}
