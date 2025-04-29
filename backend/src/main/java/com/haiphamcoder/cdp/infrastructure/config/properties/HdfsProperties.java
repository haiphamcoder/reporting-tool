package com.haiphamcoder.cdp.infrastructure.config.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HdfsProperties {
    private String user;
    private String hdfsSiteConf;
    private String coreSiteConf;
    private String rootFolder;
    private int fileMaxSize;
}
