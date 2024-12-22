package tr.salkan.code.java.elk.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tr.salkan.code.java.elk.elkoperation.IElasticOperationService;
import tr.salkan.code.java.elk.model.servicemodel.ProjectElkModel;

import java.io.IOException;

@RestController
@RequestMapping("/elastic-client-service")
public class ElasticJavaClientController {

    private final IElasticOperationService elasticOperationService;

    @Autowired
    public ElasticJavaClientController(IElasticOperationService elasticOperationService) {
        this.elasticOperationService = elasticOperationService;
    }

    @PostMapping(value = "/saveModel")
    public ResponseEntity<String> saveModel(@RequestBody ProjectElkModel projectElkModel)
    {

        try {
            String response = elasticOperationService.insertModelGeneric(projectElkModel);
            return new ResponseEntity<>(response,HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
