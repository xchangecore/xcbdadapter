package com.spotonresponse.adapter.process;

import com.spotonresponse.adapter.model.MappedRecordJson;

import java.util.Map;
import java.util.Set;

public interface Parser {

    public boolean isAutoClose();
    public String getId();
    public Map<String, MappedRecordJson> getJsonRecordMap();
    public Set<String> getNotMatchedKeySet();
}
