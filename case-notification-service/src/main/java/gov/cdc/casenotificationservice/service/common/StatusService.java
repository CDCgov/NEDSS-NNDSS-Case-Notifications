package gov.cdc.casenotificationservice.service.common;

import gov.cdc.casenotificationservice.model.ApiStatusResponseModel;
import gov.cdc.casenotificationservice.model.view_model.CaseNotificationDltVM;
import gov.cdc.casenotificationservice.model.view_model.CnTransportOutVM;
import gov.cdc.casenotificationservice.model.view_model.NetssTransportQOutVM;
import gov.cdc.casenotificationservice.model.view_model.TransportOutVM;
import gov.cdc.casenotificationservice.repository.msg.CaseNotificationDltRepository;
import gov.cdc.casenotificationservice.repository.msg.NetssTransportQOutRepository;
import gov.cdc.casenotificationservice.repository.msg.TransportQOutRepository;
import gov.cdc.casenotificationservice.repository.odse.CNTraportqOutRepository;
import gov.cdc.casenotificationservice.repository.odse.model.CNTransportqOut;
import gov.cdc.casenotificationservice.service.common.interfaces.IDltService;
import gov.cdc.casenotificationservice.service.common.interfaces.IStatusService;
import org.springframework.stereotype.Service;

@Service
public class StatusService implements IStatusService {
    private final CNTraportqOutRepository cnTraportqOutRepository;
    private final TransportQOutRepository transportQOutRepository;
    private final NetssTransportQOutRepository netssTransportQOutRepository;
    private final CaseNotificationDltRepository caseNotificationDltRepository;

    public StatusService(CNTraportqOutRepository cnTraportqOutRepository,
                         TransportQOutRepository transportQOutRepository,
                         NetssTransportQOutRepository netssTransportQOutRepository,
                         CaseNotificationDltRepository caseNotificationDltRepository) {
        this.cnTraportqOutRepository = cnTraportqOutRepository;
        this.transportQOutRepository = transportQOutRepository;
        this.netssTransportQOutRepository = netssTransportQOutRepository;
        this.caseNotificationDltRepository = caseNotificationDltRepository;
    }

    public ApiStatusResponseModel getProcessedStatus(Long cnTraportqOutId) {
        ApiStatusResponseModel aStatusResponseModel = new ApiStatusResponseModel();

        var cnTransportOpt = cnTraportqOutRepository.findById(cnTraportqOutId);
        if (cnTransportOpt.isPresent()) {
            CNTransportqOut cnTraportqOut = cnTransportOpt.get();

            // Convert CNTransportqOut to VM
            aStatusResponseModel.setCnTransportqOut(new CnTransportOutVM(cnTraportqOut));

            // Convert TransportQOut list to VM list
            var transportOpt = transportQOutRepository.findByNotificationLocalUid(cnTraportqOut.getNotificationLocalId());
            if (transportOpt != null) {
                transportOpt.stream()
                        .map(TransportOutVM::new)
                        .forEach(aStatusResponseModel.getTransportQOut()::add);
            }

            // Convert NetssTransportQOut list to VM list
            var netssOpt = netssTransportQOutRepository.findByNotificationLocalUid(cnTraportqOut.getNotificationLocalId());
            if (netssOpt != null) {
                netssOpt.stream()
                        .map(NetssTransportQOutVM::new)
                        .forEach(aStatusResponseModel.getNetssTransportQOut()::add);
            }

            // Convert CaseNotificationDlt list to VM list
            var dlt = caseNotificationDltRepository.findDltByCnTranportqOutUid(cnTraportqOutId);
            if (dlt != null) {
                dlt.stream()
                        .map(CaseNotificationDltVM::new)
                        .forEach(aStatusResponseModel.getCaseNotificationDlt()::add);
            }
        }

        return aStatusResponseModel;
    }

}
