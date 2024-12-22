package tr.salkan.code.java.elk.elkoperation.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Refresh;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tr.salkan.code.java.elk.annotation.GenericElkIndex;
import tr.salkan.code.java.elk.elkoperation.IElasticOperationService;
import tr.salkan.code.java.elk.model.BaseElkModel;

import java.io.IOException;
import java.util.Objects;

@Service
public class ElasticOperationService implements IElasticOperationService {

    private final ElasticsearchClient elasticsearchClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public ElasticOperationService(ElasticsearchClient elasticsearchClient, ObjectMapper objectMapper) {
        this.elasticsearchClient = elasticsearchClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean checkIfExistIndex(String indexname) throws IOException {

        BooleanResponse check =  elasticsearchClient.indices().exists(ExistsRequest.of(e-> e.index(indexname)));

        if(Objects.nonNull(check) && check.value())
        {
            return true;
        }

        return false;
    }

    @Override
    public String createIndex(String indexname) throws IOException {

        CreateIndexRequest req = CreateIndexRequest.of(c -> c
                .index(indexname)
        );

        CreateIndexResponse createIndexResponse = elasticsearchClient.indices().create(req);

        return createIndexResponse.index();
    }

    @Override
    public <T extends BaseElkModel> String insertModelGeneric(T model) throws IOException {

        GenericElkIndex genericElkIndex =  model.getClass().getAnnotation(GenericElkIndex.class);

        if(Objects.nonNull(genericElkIndex))
        {
            String indexName =  genericElkIndex.indexName();

            IndexRequest<T> request = IndexRequest.of(i->
                    i.index(indexName)
                            .id(model.getId())
                            .document(model).refresh(Refresh.True));

            IndexResponse response = elasticsearchClient.index(request);
            return response.result().toString();
        }

        throw new IOException("Can not create document ID :" + model.getId());
    }

    @Override
    public <T extends BaseElkModel> String insertModelGeneric(String id, T model) throws IOException {
        GenericElkIndex genericElkIndex =  model.getClass().getAnnotation(GenericElkIndex.class);

        if(Objects.nonNull(genericElkIndex))
        {
            String indexName =  genericElkIndex.indexName();

            IndexRequest<T> request = IndexRequest.of(i->
                    i.index(indexName)
                            .id(id)
                            .document(model).refresh(Refresh.True));

            IndexResponse response = elasticsearchClient.index(request);
            return response.result().toString();
        }

        throw new IOException("Can not create document ID :" + id);
    }


}
