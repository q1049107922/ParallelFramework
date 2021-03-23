package com.parallel.framework;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Data
public class ProcessContext {
    protected static Logger logger = LoggerFactory.getLogger(ProcessContext.class);
    @JsonIgnore
    protected List<Processer> processers = new ArrayList<>();
}
