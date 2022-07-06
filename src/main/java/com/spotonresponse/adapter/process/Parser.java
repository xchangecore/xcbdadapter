package com.spotonresponse.adapter.process;

import com.spotonresponse.adapter.model.MappedRecordJson;
import java.util.Map;
import java.util.Set;

public interface Parser {
    boolean isAutoClose();

    String getId();

    Map<String, MappedRecordJson> getJsonRecordMap();

    Set<String> getNotMatchedKeySet();
}

