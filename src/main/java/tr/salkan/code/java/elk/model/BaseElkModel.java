package tr.salkan.code.java.elk.model;

import java.io.Serializable;

public class BaseElkModel implements Serializable {

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BaseElkModel(String id) {
        this.id = id;
    }

    public BaseElkModel() {
    }
}
