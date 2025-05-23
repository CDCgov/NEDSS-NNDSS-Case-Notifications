package gov.cdc.casenotificationservice.service.std;

import gov.cdc.casenotificationservice.model.Netss;
import gov.cdc.casenotificationservice.model.generated.jaxb.NBSNNDIntermediaryMessage;
import gov.cdc.casenotificationservice.service.std.interfaces.IStdMapperService;
import org.springframework.stereotype.Service;

import static gov.cdc.casenotificationservice.constant.StdConstantValue.*;
import static gov.cdc.casenotificationservice.util.StringHelper.strNumbers;
import static gov.cdc.casenotificationservice.util.StringHelper.strRight;

@Service
public class StdMapperService implements IStdMapperService {

    private final MapperUtilService mapperUtilService;

    public StdMapperService(MapperUtilService mapperUtilService) {
        this.mapperUtilService = mapperUtilService;
    }

    public Netss stdMapping(NBSNNDIntermediaryMessage in) {
        Netss netss = new Netss();

        netss.setRecordType(NETSS_RECORD_TYPE);
        netss.setUpdate(NETSS_RECORD_UPDATE);
        netss.setCount(NETSS_COUNT);

        String DEM162 = NETSS_EMPTY;
        String NOT109 = NETSS_EMPTY;
        String NOT109State = NETSS_EMPTY;
        String INV137 = NETSS_EMPTY;
        String LAB163 = NETSS_99999999;
        String INV111 = NETSS_EMPTY;
        String INV120 = NETSS_EMPTY;
        String INV121 = NETSS_EMPTY;
        String INV165 = NETSS_EMPTY;
        String INV166 = NETSS_EMPTY;

        String INV173 = NETSS_EMPTY;
        String INV136 = NETSS_EMPTY;
        String EVT_DT;
        int INV177Date = 0;


        initiateNetssValue(netss);

        for (int i = 0; i < in.getMessageElement().size(); i++) {
            if (in.getMessageElement().get(i).getQuestionIdentifier() != null) {
                if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase(NETSS_NOT109))
                {
                    var result = mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    NOT109State = result;
                    netss.setState(result);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase(NETSS_INV166))
                {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    INV166 = result;
                    netss.setYear(strRight(result, 2));
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase(NETSS_INV168))
                {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    if (result.length() >= 11) {
                        result = result.substring(5, 11);
                    }
                    netss.setCaseReportId(result);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase(NETSS_INV107))
                {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    netss.setSiteCode(NETSS_NBS);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase(NETSS_INV165))
                {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    INV165 = result;//IntToStr(StrToInt(result));
                    if (Integer.parseInt(result) < 10) {
                        result = "0" + Integer.valueOf(result);
                    }
                    netss.setWeek(result);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase(NETSS_INV169))
                {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    netss.setEvent(result);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase(NETSS_DEM165))
                {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    if (result.trim().isEmpty()) {
                        result = NETSS_999;
                    }
                    else {
                        result = strRight(result, 3);
                        netss.setCounty(result);
                    }
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase(NETSS_DEM115))
                {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    String dateOutput = NETSS_EMPTY;
                    dateOutput = mapperUtilService.mapToTsType(result, dateOutput);
                    if (dateOutput.trim().isEmpty())
                    {
                        dateOutput = NETSS_99999999;
                    }
                    netss.setDateOfBirth(dateOutput);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase(NETSS_INV2001))
                {
                    String output1 = NETSS_EMPTY;
                    String output2 = NETSS_EMPTY;
                    if (in.getMessageElement().get(i).getDataElement().getQuestionDataTypeNND().equalsIgnoreCase(NETSS_SN_UNIT))
                    {
                        if (in.getMessageElement().get(i).getDataElement().getSnunitDataType() != null
                                && in.getMessageElement().get(i).getDataElement().getSnunitDataType().getNum1() != null)
                        {
                            output1 = in.getMessageElement().get(i).getDataElement().getSnunitDataType().getNum1();
                        }
                        if (in.getMessageElement().get(i).getDataElement().getSnunitDataType() != null &&
                                in.getMessageElement().get(i).getDataElement().getSnunitDataType().getCeCodedValue() != null)
                        {
                            output2 = in.getMessageElement().get(i).getDataElement().getSnunitDataType().getCeCodedValue();
                        }
                    }
                    if (output1.isEmpty()) {
                        output1 = NETSS_999;
                    }
                    else {
                        if (Integer.parseInt(output1) < 10) {
                            output1 = "00" + output1;
                        } else if (Integer.parseInt(output1) < 100) {
                            output1 = "0" + output1;
                        } else if (Integer.parseInt(output1) < 999) {
                            output1 = output1;
                        } else {
                            output1 = NETSS_999;
                        }
                    }

                    netss.setAge(output1);
                    String ageType = mapperUtilService.mapToCodedAnswer(output2, NETSS_INV2002);
                    if (ageType != null && ageType.equalsIgnoreCase(NOT_MAPPED)){
                        netss.setAgeType(NETSS_9);
                    }
                    else{
                        netss.setAgeType(ageType);
                    }

                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase(NETSS_DEM113))
                {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    String sex = mapperUtilService.mapToCodedAnswer(result, NETSS_DEM113);
                    if (sex.equalsIgnoreCase(NOT_MAPPED)){
                        netss.setSex(NETSS_9);
                    }
                    else{
                        netss.setSex(sex);
                    }

                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase(NETSS_INV163))
                {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    if (result.contains(NETSS_PHC178))
                    {
                        netss.setCaseStatus(STATUS_X);
                    }
                    else
                    {
                        netss.setCaseStatus(mapperUtilService.mapToCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier()));
                    }
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase(NETSS_INV150))
                {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    netss.setOutbreak(mapperUtilService.mapToCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier()));
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase(NETSS_INV112))
                {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    netss.setInfosrce(mapperUtilService.mapToCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier()));
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase(NETSS_INV159))
                {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    netss.setMethodCaseDetectn(mapperUtilService.mapToCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier()));
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase(NETSS_DEM163))
                {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    netss.setZip(result);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase(NETSS_INV178))
                {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    result = mapperUtilService.mapToCodedAnswer(in.getMessageElement().get(i).getQuestionIdentifier(), netss.getPregnantInitExam());
                    netss.setPregnantInitExam(result == null ? NETSS_EMPTY : result);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase(NETSS_STD102))
                {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    result = mapperUtilService.mapToCodedAnswer(in.getMessageElement().get(i).getQuestionIdentifier(), netss.getNeurologicalInvolvment());
                    netss.setNeurologicalInvolvment(result == null? NETSS_EMPTY : result);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase(NETSS_DEM152))
                {
                    String race;
                    String mapped = NETSS_EMPTY;
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    String outputData = CHECKER;
                    outputData = mapperUtilService.mapToMultiCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier(), CHECKER, outputData);
                    race = ASIAN;
                    if (outputData.equals(race)) {
                        outputData = mapperUtilService.mapToMultiCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier(), race, outputData);
                        if (outputData.equals(STATUS_Y))
                        {
                            netss.setAsian(outputData);
                        }
                    }
                    race = AMERICAN;
                    if (outputData.equals(race)) {
                        outputData = mapperUtilService.mapToMultiCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier(), race, outputData);
                        if (outputData.equals(STATUS_Y))
                        {
                            netss.setAmericanIndianAlaskanNative(outputData);
                        }
                    }
                    race = BLACK;
                    if (outputData.equals(race)) {
                        outputData = mapperUtilService.mapToMultiCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier(), race, outputData);
                        if (outputData.equals(STATUS_Y))
                        {
                            netss.setBlackAfricanAmerican(outputData);
                        }
                    }
                    race = HAWAII;
                    if (outputData.equals(race)) {
                        outputData = mapperUtilService.mapToMultiCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier(), race, outputData);
                        if (outputData.equals(STATUS_Y))
                        {
                            netss.setNativeHawaiianPacificIslander(outputData);
                        }
                    }
                    race = OTHER;
                    if (outputData.equals(race)) {
                        outputData = mapperUtilService.mapToMultiCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier(), race, outputData);
                        if (outputData.equals(STATUS_Y))
                        {
                            netss.setOtherRace(outputData);
                        }
                    }
                    race = REFUSED;
                    if (outputData.equals(race)) {
                        outputData = mapperUtilService.mapToMultiCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier(), race, outputData);
                        if (outputData.equals(STATUS_Y))
                        {
                            netss.setRefusedReportRace(outputData);
                        }
                    }
                    race = WHITE;
                    if (outputData.equals(race)) {
                        outputData = mapperUtilService.mapToMultiCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier(), race, outputData);
                        if (outputData.equals(STATUS_Y))
                        {
                            netss.setWhite(outputData);
                        }
                    }
                    race = UNKNOWN;
                    if (outputData.equals(race)) {
                        outputData = mapperUtilService.mapToMultiCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier(), race, outputData);
                        if (outputData.equals(STATUS_Y))
                        {
                            netss.setUnknownRace(outputData);
                        }
                    }
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase(NETSS_DEM155)) {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    String outputData;
                    outputData = mapperUtilService.mapToCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier());
                    netss.setHispanicLatino(outputData);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase(NETSS_DEM168)) {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    if (result.length() == 4)
                    {
                        result = result + NETSS_SPACE_DOUBLE;
                    }
                    result = result.replaceAll(".", NETSS_EMPTY);
                    netss.setCensusTract(result);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase(NETSS_INV152)) {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    String outputData;
                    outputData = mapperUtilService.mapToCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier());
                    netss.setStdImport(outputData);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase(NETSS_STD099)) {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    String dateOutput = NETSS_EMPTY;
                    dateOutput = mapperUtilService.mapToTsType(result, dateOutput);
                    if (dateOutput.trim().isEmpty())
                    {
                        dateOutput = NETSS_99999999;
                    }
                    netss.setDateInitHealthExam(dateOutput);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase(NETSS_INV177)) {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    String dateOutput = NETSS_EMPTY;
                    dateOutput = mapperUtilService.mapToTsType(result, dateOutput);
                    if (dateOutput.trim().isEmpty())
                    {
                        dateOutput = NETSS_99999999;
                    }
                    netss.setDateFirstReportOfCase(dateOutput);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase(NETSS_STD105)) {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    String dateOutput = NETSS_EMPTY;
                    dateOutput = mapperUtilService.mapToTsType(result, dateOutput);
                    if (dateOutput.trim().isEmpty())
                    {
                        dateOutput = NETSS_99999999;
                    }
                    netss.setTreatmentDate(dateOutput);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase(NETSS_STD106)) {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    String outputData;
                    outputData = mapperUtilService.mapToCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier());
                    netss.setHivStatus(outputData);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase(NETSS_STD107)) {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    String outputData;
                    outputData = mapperUtilService.mapToCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier());
                    netss.setSexWithMale12m(outputData);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase(NETSS_STD108)) {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    String outputData;
                    outputData = mapperUtilService.mapToCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier());
                    netss.setSexWithFemale12m(outputData);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase(NETSS_STD109)) {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    String outputData;
                    outputData = mapperUtilService.mapToCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier());
                    netss.setSexWithAnnon12m(outputData);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase(NETSS_STD110)) {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    String outputData;
                    outputData = mapperUtilService.mapToCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier());
                    netss.setSexWithPersonIdu12m(outputData);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase(NETSS_STD111)) {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    String outputData;
                    outputData = mapperUtilService.mapToCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier());
                    netss.setSexWithPersonIntox12m(outputData);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase(NETSS_STD112)) {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    String outputData;
                    outputData = mapperUtilService.mapToCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier());
                    netss.setSexExchDrugMoney(outputData);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase(NETSS_STD113)) {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    String outputData;
                    outputData = mapperUtilService.mapToCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier());
                    netss.setSexPersonKnownToBeAnMsm(outputData);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase(NETSS_STD114)) {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    String outputData;
                    outputData = mapperUtilService.mapToCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier());
                    netss.setInjectionDrugUse(outputData);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase(NETSS_NBS235)) {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    String outputData;
                    outputData = mapperUtilService.mapToCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier());
                    netss.setCrackUse(outputData);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase(NETSS_NBS237)) {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    String outputData;
                    outputData = mapperUtilService.mapToCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier());
                    netss.setCocianeUse(outputData);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase(NETSS_NBS239)) {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    String outputData;
                    outputData = mapperUtilService.mapToCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier());
                    netss.setHeroinUse(outputData);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase(NETSS_NBS236)) {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    String outputData;
                    outputData = mapperUtilService.mapToCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier());
                    netss.setNitratesPoppers(outputData);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase(NETSS_NBS234)) {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    String outputData;
                    outputData = mapperUtilService.mapToCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier());
                    netss.setMethamphetamineUse(outputData);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase(NETSS_NBS238)) {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    String outputData;
                    outputData = mapperUtilService.mapToCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier());
                    netss.setEdUse(outputData);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase(NETSS_NBS240)) {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    String outputData;
                    outputData = mapperUtilService.mapToCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier());
                    netss.setOtherDrugUse(outputData);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase(NETSS_NBS233)) {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    String outputData;
                    outputData = mapperUtilService.mapToCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier());
                    netss.setNoDrugUse(outputData);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase(NETSS_STD117)) {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    String outputData;
                    outputData = mapperUtilService.mapToCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier());
                    netss.setPriorStd(outputData);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase(NETSS_STD118)) {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    String outputData;
                    outputData = mapperUtilService.mapToCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier());
                    netss.setIncarcerated(outputData);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase(NETSS_STD120)) {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    if (Integer.parseInt(result) < 10)
                    {
                        result = "00" + result;
                    }
                    else if (Integer.parseInt(result) < 100)
                    {
                        result = "0" + result;
                    }
                    else if (Integer.parseInt(result) < 1000)
                    {
                        result = result;
                    }
                    else
                    {
                        result = NETSS_999;
                    }
                    netss.setTotalNbrSexPartner12Month(result);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase(NETSS_STD119)) {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    String outputData;
                    outputData = mapperUtilService.mapToCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier());
                    netss.setSexPartnerThroughInternet(outputData);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase(NETSS_STD121)) {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    if (result.contains("281088000"))
                    {
                        netss.setAAnusRectum(STATUS_Y);
                    }
                    if (result.contains("18911002"))
                    {
                        netss.setBPenis(STATUS_Y);
                    }
                    if (result.contains("20233005"))
                    {
                        netss.setCScrotum(STATUS_Y);
                    }
                    if (result.contains("76784001"))
                    {
                        netss.setDVagina(STATUS_Y);
                    }

                    if (result.contains("71252005"))
                    {
                        netss.setECervix(STATUS_Y);
                    }
                    if (result.contains("71836000"))
                    {
                        netss.setFNasopharynx(STATUS_Y);
                    }
                    if (result.contains("123851003"))
                    {
                        netss.setGMouthOral(STATUS_Y);
                    }
                    if (result.contains("PHC1161"))
                    {
                        netss.setHEyeConjunctiva(STATUS_Y);
                    }
                    if (result.contains("69536005"))
                    {
                        netss.setIHead(STATUS_Y);
                    }
                    if (result.contains("22943007"))
                    {
                        netss.setJTorso(STATUS_Y);
                    }
                    if (result.contains("66019005"))
                    {
                        netss.setKExtremities(STATUS_Y);
                    }
                    if (result.contains("PHC1168"))
                    {
                        netss.setNNoLesionNoted(STATUS_Y);
                    }
                    if (result.contains("PHC1170"))
                    {
                        netss.setOOther(STATUS_Y);
                    }
                    if (result.contains("UNK"))
                    {
                        netss.setUUnknown(STATUS_Y);
                    }
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase(NETSS_STD122)) {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    String outputData;
                    outputData = mapperUtilService.mapToCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier());
                    if (outputData.isEmpty())
                    {
                        outputData = NETSS_SPACE_SINGLE;
                    }
                    netss.setNonTreponemalSerologicTestForSyphilis(outputData);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase(NETSS_STD123)) {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    String outputData;
                    outputData = mapperUtilService.mapToCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier());
                    netss.setQuantitativeSyphilisTestResult(outputData);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase(NETSS_STD126)) {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    String outputData;
                    outputData = mapperUtilService.mapToCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier());
                    if (netss.getQuantitativeSyphilisTestResult().isEmpty())
                    {
                        netss.setQuantitativeSyphilisTestResult(outputData);
                    }
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase(NETSS_NOT109)) {
                    NOT109 =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase(NETSS_DEM162)) {
                    DEM162=  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase(NETSS_INV137)) {
                    INV137=  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase(NETSS_INV136)) {
                    INV136 =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase(NETSS_LAB163)) {
                    LAB163 =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase(NETSS_INV111)) {
                    INV111 =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase(NETSS_INV120)) {
                    INV120 =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase(NETSS_INV121)) {
                    INV121 =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                }
            }



        }

        if (!NOT109State.contains(DEM162)) {
            netss.setCaseStatus(STATUS_X);
        }
        String INV120Int = NETSS_EMPTY;
        String INV136Int = NETSS_EMPTY;
        String INV137Int = NETSS_EMPTY;
        String INV121Int = NETSS_EMPTY;
        String INV111Int = NETSS_EMPTY;
        String LAB163Int = NETSS_EMPTY;
        String wkYr = NETSS_EMPTY;
        String INVEventDateInt = NETSS_EMPTY;
        int CompINVEventDateInt = 0;
        int CompLAB163Int = 0;
        int CompINV120Int = 0;
        int CompINV121Int = 0;
        int CompINV136Int = 0;
        int CompINV137Int = 0;
        int CompEventDateInt;
        int CompINV111Int = 0;

        if (!INV137.isEmpty()) {
            String dtOutput = NETSS_EMPTY;
            mapperUtilService.mapToTsType(INV137, dtOutput);
            INV137Int = strRight(dtOutput, 6);
            CompINV137Int = strNumbers(dtOutput);
        }
        if (!INV136.isEmpty()) {
            String dtOutput = NETSS_EMPTY;
            mapperUtilService.mapToTsType(INV136, dtOutput);
            INV136Int = strRight(dtOutput, 6);
            CompINV136Int = strNumbers(dtOutput);
        }
        if (!LAB163.isEmpty()) {
            String dtOutput = NETSS_EMPTY;
            mapperUtilService.mapToTsType(LAB163, dtOutput);
            LAB163Int = strRight(dtOutput, 6);
            CompLAB163Int = strNumbers(dtOutput);
        }
        if (!INV111.isEmpty()) {
            String dtOutput = NETSS_EMPTY;
            dtOutput = mapperUtilService.mapToTsType(INV111, dtOutput);
            INV111Int = strRight(dtOutput, 6);
            CompINV111Int = strNumbers(dtOutput);
        }
        if (!INV120.isEmpty()) {
            String dtOutput = NETSS_EMPTY;
            mapperUtilService.mapToTsType(INV120, dtOutput);
            INV120Int = strRight(dtOutput, 6);
            CompINV120Int = strNumbers(dtOutput);
        }
        if (!INV121.isEmpty()) {
            String dtOutput = NETSS_EMPTY;
            mapperUtilService.mapToTsType(INV121, dtOutput);
            INV121Int = strRight(dtOutput, 6);
            CompINV121Int = strNumbers(dtOutput);
        }
        if (!INV165.isEmpty() && !INV166.isEmpty()) {
            String result = NETSS_EMPTY;
            mapperUtilService.mapToDate(INV165, INV166, result);
            String dtOutput = NETSS_EMPTY;
            mapperUtilService.mapToTsType(result, dtOutput);
            wkYr = strRight(dtOutput, 6);
            CompINVEventDateInt = strNumbers(dtOutput);
        }
        String dtOutput = NETSS_EMPTY;
        EVT_DT = INV137Int;
        netss.setDateType("1");
        CompEventDateInt = CompINV137Int;
        if ((CompINV136Int > 0 && CompINV136Int < CompEventDateInt) || EVT_DT.trim().isEmpty()){
            CompEventDateInt = CompINV136Int;
            EVT_DT = INV136Int;
            netss.setDateType("2");
        }
        if ((CompLAB163Int > 0 && CompLAB163Int < CompEventDateInt) || EVT_DT.trim().isEmpty()){
            CompEventDateInt = CompLAB163Int;
            EVT_DT = LAB163Int;
            netss.setDateType("3");
        }
        if ((CompINV111Int > 0 && CompINV111Int < CompEventDateInt) || EVT_DT.trim().isEmpty()){
            CompEventDateInt = CompINV111Int;
            EVT_DT = INV111Int;
            netss.setDateType("4");
        }
        if ((CompINV120Int > 0 && CompINV120Int < CompEventDateInt) || EVT_DT.trim().isEmpty()){
            CompEventDateInt = CompINV120Int;
            EVT_DT = INV120Int;
            netss.setDateType("4");
        }
        if ((CompINV121Int > 0 && CompINV121Int < CompEventDateInt) || EVT_DT.trim().isEmpty()){
            CompEventDateInt = CompINV121Int;
            EVT_DT = INV121Int;
            netss.setDateType("4"); 
        }
        if ((CompINVEventDateInt > 0 && CompINVEventDateInt < CompEventDateInt) || EVT_DT.trim().isEmpty()){
            CompEventDateInt = CompINVEventDateInt;
            EVT_DT = wkYr;
            netss.setDateType("5");
        }
        if (!EVT_DT.trim().isEmpty()){
            netss.setEventDate(EVT_DT); 
        }else{
            netss.setEventDate(NETSS_999999);
            netss.setDateType(NETSS_9);
        }
        if (CompINV111Int > 0) {
            INV177Date = CompINV111Int;
            netss.setDateFirstReportOfCase(String.valueOf(CompINV111Int));
        }
        if (CompINV120Int > 0 && CompINV120Int < INV177Date) {
            INV177Date = CompINV120Int;
            netss.setDateFirstReportOfCase(String.valueOf(CompINV120Int));
        }
        if (CompINV121Int > 0 && CompINV121Int < INV177Date) {
            INV177Date = CompINV121Int;
            netss.setDateFirstReportOfCase(String.valueOf(CompINV121Int));
        }

        return netss;
    }

    protected void initiateNetssValue(Netss netss) {


        netss.setCounty(NETSS_999);

        netss.setDateOfBirth(NETSS_99999999);
        netss.setDateInitHealthExam(NETSS_99999999);

        netss.setDateFirstReportOfCase(NETSS_99999999);
        netss.setTreatmentDate(NETSS_99999999);
        netss.setStdImport(NETSS_U);
        netss.setRace(NETSS_9);
        netss.setHispanic(NETSS_9);
        netss.setCaseStatus(NETSS_9);
        netss.setImported(NETSS_9);
        netss.setOutbreak(NETSS_9);
        netss.setFuture(NETSS_99999);
        netss.setInfosrce(NETSS_99);
        netss.setMethodCaseDetectn(NETSS_88);
        netss.setZip(NETSS_99999);
        netss.setCity(NETSS_9999);
        netss.setPid(NETSS_9);
        netss.setPregnantInitExam(NETSS_SPACE_SINGLE);
        netss.setOrigin(NETSS_9);
        netss.setDxDate(NETSS_99999999);
        netss.setSpecimenSource(NETSS_99);
        netss.setLabSpecimenCollDate(NETSS_99999999);
        netss.setInterview(NETSS_9);
        netss.setPartner(NETSS_9);
        netss.setNeurologicalInvolvment(NETSS_SPACE_SINGLE); // This become NETSS_EMPTY
        netss.setAmericanIndianAlaskanNative(NETSS_U);
        netss.setAsian(NETSS_U);
        netss.setBlackAfricanAmerican(NETSS_U);
        netss.setNativeHawaiianPacificIslander(NETSS_U);
        netss.setWhite(NETSS_U);
        netss.setOtherRace(NETSS_U);
        netss.setRefusedReportRace(NETSS_U);
        netss.setUnknownRace(NETSS_U);
        netss.setHispanicLatino(NETSS_U);
        netss.setAsian(NETSS_U);
        netss.setDateReportedToCdc(NETSS_99999999);
        netss.setTotalNbrSexPartner12Month(NETSS_999);
        netss.setAge(NETSS_999);
        netss.setAgeType(NETSS_9);
        netss.setSex(NETSS_9);
        netss.setNetssVersion("04");
        netss.setHivStatus(NETSS_SPACE_SINGLE);
        netss.setAAnusRectum(NETSS_U);
        netss.setBPenis(NETSS_U);
        netss.setCScrotum(NETSS_U);
        netss.setDVagina(NETSS_U);
        netss.setECervix(NETSS_U);
        netss.setFNasopharynx(NETSS_U);
        netss.setGMouthOral(NETSS_U);
        netss.setHEyeConjunctiva(NETSS_U);
        netss.setIHead(NETSS_U);
        netss.setJTorso(NETSS_U);
        netss.setKExtremities(NETSS_U);
        netss.setNNoLesionNoted(NETSS_U);
        netss.setOOther(NETSS_U);
        netss.setUUnknown(NETSS_U);
        netss.setNonTreponemalSerologicTestForSyphilis(NETSS_SPACE_SINGLE);
        netss.setQuantitativeSyphilisTestResult(NETSS_999999);
        netss.setEventDate(NETSS_999999);
    }
}
