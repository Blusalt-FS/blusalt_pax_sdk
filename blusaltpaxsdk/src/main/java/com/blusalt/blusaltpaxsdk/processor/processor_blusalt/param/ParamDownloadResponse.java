package com.blusalt.blusaltpaxsdk.processor.processor_blusalt.param;

import java.util.List;

public class ParamDownloadResponse {

    public String serialNumber;

    public String terminalId;
    public String merchantId;
    public String merchantName;
    public String acquirerName;
    public String acquirerImagePath;
    public List<Host> hosts;
    public boolean deviceEnabled;
    public Attributes attributes;

}
