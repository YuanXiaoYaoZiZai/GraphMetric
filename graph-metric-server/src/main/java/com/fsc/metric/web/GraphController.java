package com.fsc.metric.web;

import com.fsc.common.utils.APIException;
import com.fsc.common.utils.StreamResponse;
import com.fsc.metric.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.fsc.metric.service.GraphService;
import com.fsc.metric.utils.HttpTools;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@CrossOrigin
@RestController
@RequestMapping("api/graph")
public class GraphController {

    private static Logger logger = LoggerFactory.getLogger(GraphController.class);

    @Autowired
    private GraphService graphService;

    @RequestMapping(value = "/cql/v1", method = RequestMethod.POST)
    public StreamResponse<GraphAll> queryCQL(HttpServletRequest request, HttpServletResponse response,
                                             @RequestBody GraphCQLReq req, @ModelAttribute GraphGrafanaParams params) throws APIException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST");
        StreamResponse<GraphAll> result = new StreamResponse<>();
        result.setData(graphService.getGraph(req, params));
        return result;
    }

    @RequestMapping(value = "/cql/execute/v1", method = RequestMethod.POST)
    public StreamResponse<Boolean> executeCQL(HttpServletRequest request, HttpServletResponse response,
                                              @RequestBody GraphReq req) throws APIException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST");
        StreamResponse<Boolean> result = new StreamResponse<>();
        req.setGraphAction(GraphAction.EXECUTE_CQL);
        graphService.addGraphActionToQueue(req);
        result.setData(true);
        return result;
    }

    @RequestMapping(value = "/node/save/v1", method = RequestMethod.POST)
    public StreamResponse<Boolean> saveNode(HttpServletRequest request, HttpServletResponse response,
                                            @RequestBody GraphReq req) throws APIException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST");
        StreamResponse<Boolean> result = new StreamResponse<>();
        req.setLastUpdateUser(HttpTools.getRemoteHost(request));
        req.setGraphAction(GraphAction.SAVE_NODE);
        graphService.addGraphActionToQueue(req);
        result.setData(true);
        return result;
    }

    @RequestMapping(value = "/node/update/v1", method = RequestMethod.POST)
    public StreamResponse<Boolean> updateNode(HttpServletRequest request, HttpServletResponse response,
                                              @RequestBody GraphReq req) throws APIException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST");
        StreamResponse<Boolean> result = new StreamResponse<>();
        req.setLastUpdateUser(HttpTools.getRemoteHost(request));
        req.setGraphAction(GraphAction.UPDATE_NODE);
        graphService.addGraphActionToQueue(req);
        result.setData(true);
        return result;
    }

    @RequestMapping(value = "/node/get/v1", method = RequestMethod.POST)
    public StreamResponse<GraphAll> getNode(HttpServletRequest request, HttpServletResponse response,
                                            @RequestBody GraphReq req) throws APIException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST");
        StreamResponse<GraphAll> result = new StreamResponse<>();
        req.setLastUpdateUser(HttpTools.getRemoteHost(request));
        result.setData(graphService.getNode(req));
        return result;
    }

    @RequestMapping(value = "/node/delete/v1", method = RequestMethod.POST)
    public StreamResponse<Boolean> deleteNode(HttpServletRequest request, HttpServletResponse response,
                                              @RequestBody GraphReq req) throws APIException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST");
        StreamResponse<Boolean> result = new StreamResponse<>();
        req.setLastUpdateUser(HttpTools.getRemoteHost(request));
        req.setGraphAction(GraphAction.DELETE_NODE);
        graphService.addGraphActionToQueue(req);
        result.setData(true);
        return result;
    }

    @RequestMapping(value = "/relationship/save/v1", method = RequestMethod.POST)
    public StreamResponse<Boolean> saveRelationship(HttpServletRequest request, HttpServletResponse response,
                                                    @RequestBody GraphReq req) throws APIException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST");
        StreamResponse<Boolean> result = new StreamResponse<>();
        req.setLastUpdateUser(HttpTools.getRemoteHost(request));
        req.setGraphAction(GraphAction.SAVE_RELATIONSHIP);
        graphService.addGraphActionToQueue(req);
        result.setData(true);
        return result;
    }

    @RequestMapping(value = "/relationship/update/v1", method = RequestMethod.POST)
    public StreamResponse<Boolean> updateRelationship(HttpServletRequest request, HttpServletResponse response,
                                                      @RequestBody GraphReq req) throws APIException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST");
        StreamResponse<Boolean> result = new StreamResponse<>();
        req.setLastUpdateUser(HttpTools.getRemoteHost(request));
        req.setGraphAction(GraphAction.UPDATE_RELATIONSHIP);
        graphService.addGraphActionToQueue(req);
        result.setData(true);
        return result;
    }

    @RequestMapping(value = "/relationship/get/v1", method = RequestMethod.POST)
    public StreamResponse<GraphAll> getRelationship(HttpServletRequest request, HttpServletResponse response,
                                                    @RequestBody GraphReq req) throws APIException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST");
        StreamResponse<GraphAll> result = new StreamResponse<>();
        req.setLastUpdateUser(HttpTools.getRemoteHost(request));
        result.setData(graphService.getRelationship(req));
        return result;
    }

    @RequestMapping(value = "/relationship/delete/v1", method = RequestMethod.POST)
    public StreamResponse<Boolean> deleteRelationship(HttpServletRequest request, HttpServletResponse response,
                                                      @RequestBody GraphReq req) throws APIException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST");
        StreamResponse<Boolean> result = new StreamResponse<>();
        req.setLastUpdateUser(HttpTools.getRemoteHost(request));
        req.setGraphAction(GraphAction.DELETE_RELATIONSHIP);
        graphService.addGraphActionToQueue(req);
        result.setData(true);
        return result;
    }

}
