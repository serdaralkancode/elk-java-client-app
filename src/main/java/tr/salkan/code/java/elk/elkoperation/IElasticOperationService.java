package tr.salkan.code.java.elk.elkoperation;

import tr.salkan.code.java.elk.model.BaseElkModel;

import java.io.IOException;

public interface IElasticOperationService {
    boolean checkIfExistIndex(String indexname)  throws IOException;

    String createIndex(String indexname)  throws IOException;

    <T extends BaseElkModel> String insertModelGeneric (T model) throws IOException;

    <T extends BaseElkModel> String insertModelGeneric (String id,T model) throws IOException;
}
