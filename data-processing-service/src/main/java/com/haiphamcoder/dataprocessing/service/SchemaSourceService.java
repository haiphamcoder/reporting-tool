package com.haiphamcoder.dataprocessing.service;

import java.util.List;

import com.haiphamcoder.dataprocessing.domain.dto.Mapping;


public interface SchemaSourceService {
    
    List<Mapping> getSchema(Long sourceId);

}
