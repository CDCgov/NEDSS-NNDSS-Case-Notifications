package gov.cdc.casenotificationservice.model;

import gov.cdc.casenotificationservice.model.view_model.CaseNotificationDltVM;
import gov.cdc.casenotificationservice.model.view_model.CnTransportOutVM;
import gov.cdc.casenotificationservice.model.view_model.NetssTransportQOutVM;
import gov.cdc.casenotificationservice.model.view_model.TransportOutVM;
import gov.cdc.casenotificationservice.repository.msg.model.CaseNotificationDlt;
import gov.cdc.casenotificationservice.repository.msg.model.NetssTransportQOut;
import gov.cdc.casenotificationservice.repository.msg.model.TransportQOut;
import gov.cdc.casenotificationservice.repository.odse.model.CNTransportqOut;
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
