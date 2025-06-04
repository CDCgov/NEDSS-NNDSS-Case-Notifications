package gov.cdc.casenotificationservice.model;

import gov.cdc.casenotificationservice.model.view_model.CaseNotificationDltVM;
import gov.cdc.casenotificationservice.model.view_model.CnTransportOutVM;
import gov.cdc.casenotificationservice.model.view_model.NetssTransportQOutVM;
import gov.cdc.casenotificationservice.model.view_model.TransportOutVM;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ApiStatusResponseModel {
    private CnTransportOutVM cnTransportqOut;
    private List<TransportOutVM> transportQOut = new ArrayList<>();
    private List<NetssTransportQOutVM> netssTransportQOut = new ArrayList<>();
    private List<CaseNotificationDltVM> caseNotificationDlt = new ArrayList<>();
}
