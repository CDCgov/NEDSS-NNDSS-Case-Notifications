package gov.cdc.casenotificationservice.service.std;

import gov.cdc.casenotificationservice.model.Netss;
import gov.cdc.casenotificationservice.model.generated.jaxb.NBSNNDIntermediaryMessage;
import gov.cdc.casenotificationservice.service.std.interfaces.IStdMapperService;
import org.springframework.stereotype.Service;

import static gov.cdc.casenotificationservice.util.StringHelper.*;

@Service
public class StdMapperService implements IStdMapperService {

    private final MapperUtilService mapperUtilService;

    public StdMapperService(MapperUtilService mapperUtilService) {
        this.mapperUtilService = mapperUtilService;
    }

    public Netss stdMapping(NBSNNDIntermediaryMessage in) {
        Netss netss = new Netss();

        netss.setRecordType("M");
        netss.setUpdate("9");
        netss.setCount("00001");

        String DEM162 = "";
        String NOT109 = "";
        String NOT109State = "";
        String INV137 = "";
        String LAB163 = "99999999";
        String INV111 = "";
        String INV120 = "";
        String INV121 = "";
        String INV165 = "";
        String INV166 = "";

        String INV173 = "";
        String INV136 = "";
        String EVT_DT = "";
        int INV177Date = 0;


        initiateNetssValue(netss);

        for (int i = 0; i < in.getMessageElement().size(); i++) {
            if (in.getMessageElement().get(i).getQuestionIdentifier() != null) {
                if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase("NOT109"))
                {
                    var result = mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    NOT109State = result;
                    netss.setState(result);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase("INV166"))
                {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    INV166 = result;
                    netss.setYear(strRight(result, 2));
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase("INV168"))
                {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    result = result.substring(5, 11);
                    netss.setCaseReportId(result);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase("INV107"))
                {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    netss.setSiteCode("NBS");
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase("INV165"))
                {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    INV165 = result;//IntToStr(StrToInt(result));
                    if (Integer.parseInt(result) < 10) {
                        result = "0" + Integer.valueOf(result);
                    }
                    netss.setWeek(result);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase("INV169"))
                {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    netss.setEvent(result);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase("DEM165"))
                {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    if (result.trim().isEmpty()) {
                        result = "999";
                    }
                    else {
                        result = strRight(result, 3);
                        netss.setCounty(result);
                    }
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase("DEM115"))
                {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    String dateOutput = "";
                    dateOutput = mapperUtilService.mapToTsType(result, dateOutput);
                    if (dateOutput.trim().isEmpty())
                    {
                        dateOutput = "99999999";
                    }
                    netss.setDateOfBirth(dateOutput);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase("INV2001"))
                {
                    String output1 = "";
                    String output2 = "";
                    if (in.getMessageElement().get(i).getDataElement().getQuestionDataTypeNND().equalsIgnoreCase("SN_WITH_UNIT"))
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
//                    if (in.getMessageElement().get(i).getDataElement().getCeDataType().LocalComplex#2#Grp1.snunitDataType.ceCodedValue.#PCDATA != null)
//                    {
//                        output2 = in.getMessageElement().get(i).dataElement.LocalComplex#2#Grp1.snunitDataType.ceCodedValue.#PCDATA;
//                    }
                    }
                    if (output1.isEmpty()) {
                        output1 = "999";
                    }
                    else {
                        if (Integer.parseInt(output1) < 10) {
                            output1 = "00" + output1;
                        } else if (Integer.parseInt(output1) < 100) {
                            output1 = "0" + output1;
                        } else if (Integer.parseInt(output1) < 999) {
                            output1 = output1;
                        } else {
                            output1 = "999";
                        }
                    }

                    netss.setAge(output1);
                    String ageType = mapperUtilService.mapToCodedAnswer(output2, "INV2002");
                    if (ageType != null && ageType.equalsIgnoreCase("NOT_MAPPED")){
                        netss.setAgeType("9");
                    }
                    else{
                        netss.setAgeType(ageType);
                    }

                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase("DEM113"))
                {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    String sex = mapperUtilService.mapToCodedAnswer(result, "DEM113");
                    if (sex.equalsIgnoreCase("NOT_MAPPED")){
                        netss.setSex("9");
                    }
                    else{
                        netss.setSex(sex);
                    }

                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase("INV163"))
                {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    if (result.contains("PHC178"))
                    {
                        netss.setCaseStatus("X");
                    }
                    else
                    {
                        netss.setCaseStatus(mapperUtilService.mapToCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier()));
                    }
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase("INV150"))
                {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    netss.setOutbreak(mapperUtilService.mapToCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier()));
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase("INV112"))
                {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    netss.setInfosrce(mapperUtilService.mapToCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier()));
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase("INV159"))
                {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    netss.setMethodCaseDetectn(mapperUtilService.mapToCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier()));
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase("DEM163"))
                {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    netss.setZip(result);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase("INV178"))
                {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    result = mapperUtilService.mapToCodedAnswer(in.getMessageElement().get(i).getQuestionIdentifier(), netss.getPregnantInitExam());
                    netss.setPregnantInitExam(result == null ? "" : result);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase("STD102"))
                {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    result = mapperUtilService.mapToCodedAnswer(in.getMessageElement().get(i).getQuestionIdentifier(), netss.getNeurologicalInvolvment());
                    netss.setNeurologicalInvolvment(result == null? "" : result);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase("DEM152"))
                {
                    String race = "";
                    String mapped = "";
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    String outputData = "CHECKER";
                    outputData = mapperUtilService.mapToMultiCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier(), "CHECKER", outputData);
                    race = "Asian?";
                    if (outputData.equals(race)) {
                        outputData = mapperUtilService.mapToMultiCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier(), race, outputData);
                        if (outputData.equals("Y"))
                        {
                            netss.setAsian(outputData);
                        }
                    }
                    race = "American Indian/ Alaska native?";
                    if (outputData.equals(race)) {
                        outputData = mapperUtilService.mapToMultiCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier(), race, outputData);
                        if (outputData.equals("Y"))
                        {
                            netss.setAmericanIndianAlaskanNative(outputData);
                        }
                    }
                    race = "Black/African American?";
                    if (outputData.equals(race)) {
                        outputData = mapperUtilService.mapToMultiCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier(), race, outputData);
                        if (outputData.equals("Y"))
                        {
                            netss.setBlackAfricanAmerican(outputData);
                        }
                    }
                    race = "Native Hawaiian/ Pacific Islander?";
                    if (outputData.equals(race)) {
                        outputData = mapperUtilService.mapToMultiCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier(), race, outputData);
                        if (outputData.equals("Y"))
                        {
                            netss.setNativeHawaiianPacificIslander(outputData);
                        }
                    }
                    race = "Other race?";
                    if (outputData.equals(race)) {
                        outputData = mapperUtilService.mapToMultiCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier(), race, outputData);
                        if (outputData.equals("Y"))
                        {
                            netss.setOtherRace(outputData);
                        }
                    }
                    race = "Refused to report race";
                    if (outputData.equals(race)) {
                        outputData = mapperUtilService.mapToMultiCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier(), race, outputData);
                        if (outputData.equals("Y"))
                        {
                            netss.setRefusedReportRace(outputData);
                        }
                    }
                    race = "White?";
                    if (outputData.equals(race)) {
                        outputData = mapperUtilService.mapToMultiCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier(), race, outputData);
                        if (outputData.equals("Y"))
                        {
                            netss.setWhite(outputData);
                        }
                    }
                    race = "Unknown race";
                    if (outputData.equals(race)) {
                        outputData = mapperUtilService.mapToMultiCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier(), race, outputData);
                        if (outputData.equals("Y"))
                        {
                            netss.setUnknownRace(outputData);
                        }
                    }
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase("DEM155")) {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    String outputData = "";
                    outputData = mapperUtilService.mapToCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier());
                    netss.setHispanicLatino(outputData);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase("DEM168")) {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    if (result.length() == 4)
                    {
                        result = result + "  ";
                    }
                    result = result.replaceAll(".", "");
                    netss.setCensusTract(result);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase("INV152")) {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    String outputData = "";
                    outputData = mapperUtilService.mapToCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier());
                    netss.setStdImport(outputData);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase("STD099")) {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    String dateOutput = "";
                    dateOutput = mapperUtilService.mapToTsType(result, dateOutput);
                    if (dateOutput.trim().isEmpty())
                    {
                        dateOutput = "99999999";
                    }
                    netss.setDateInitHealthExam(dateOutput);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase("INV177")) {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    String dateOutput = "";
                    dateOutput = mapperUtilService.mapToTsType(result, dateOutput);
                    if (dateOutput.trim().isEmpty())
                    {
                        dateOutput = "99999999";
                    }
                    netss.setDateFirstReportOfCase(dateOutput);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase("STD105")) {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    String dateOutput = "";
                    dateOutput = mapperUtilService.mapToTsType(result, dateOutput);
                    if (dateOutput.trim().isEmpty())
                    {
                        dateOutput = "99999999";
                    }
                    netss.setTreatmentDate(dateOutput);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase("STD106")) {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    String outputData = "";
                    outputData = mapperUtilService.mapToCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier());
                    netss.setHivStatus(outputData);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase("STD107")) {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    String outputData = "";
                    outputData = mapperUtilService.mapToCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier());
                    netss.setSexWithMale12m(outputData);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase("STD108")) {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    String outputData = "";
                    outputData = mapperUtilService.mapToCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier());
                    netss.setSexWithFemale12m(outputData);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase("STD109")) {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    String outputData = "";
                    outputData = mapperUtilService.mapToCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier());
                    netss.setSexWithAnnon12m(outputData);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase("STD110")) {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    String outputData = "";
                    outputData = mapperUtilService.mapToCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier());
                    netss.setSexWithPersonIdu12m(outputData);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase("STD111")) {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    String outputData = "";
                    outputData = mapperUtilService.mapToCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier());
                    netss.setSexWithPersonIntox12m(outputData);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase("STD112")) {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    String outputData = "";
                    outputData = mapperUtilService.mapToCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier());
                    netss.setSexExchDrugMoney(outputData);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase("STD113")) {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    String outputData = "";
                    outputData = mapperUtilService.mapToCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier());
                    netss.setSexPersonKnownToBeAnMsm(outputData);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase("STD114")) {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    String outputData = "";
                    outputData = mapperUtilService.mapToCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier());
                    netss.setInjectionDrugUse(outputData);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase("NBS235")) {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    String outputData = "";
                    outputData = mapperUtilService.mapToCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier());
                    netss.setCrackUse(outputData);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase("NBS237")) {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    String outputData = "";
                    outputData = mapperUtilService.mapToCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier());
                    netss.setCocianeUse(outputData);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase("NBS239")) {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    String outputData = "";
                    outputData = mapperUtilService.mapToCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier());
                    netss.setHeroinUse(outputData);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase("NBS236")) {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    String outputData = "";
                    outputData = mapperUtilService.mapToCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier());
                    netss.setNitratesPoppers(outputData);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase("NBS234")) {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    String outputData = "";
                    outputData = mapperUtilService.mapToCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier());
                    netss.setMethamphetamineUse(outputData);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase("NBS238")) {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    String outputData = "";
                    outputData = mapperUtilService.mapToCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier());
                    netss.setEdUse(outputData);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase("NBS240")) {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    String outputData = "";
                    outputData = mapperUtilService.mapToCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier());
                    netss.setOtherDrugUse(outputData);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase("NBS233")) {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    String outputData = "";
                    outputData = mapperUtilService.mapToCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier());
                    netss.setNoDrugUse(outputData);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase("STD117")) {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    String outputData = "";
                    outputData = mapperUtilService.mapToCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier());
                    netss.setPriorStd(outputData);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase("STD118")) {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    String outputData = "";
                    outputData = mapperUtilService.mapToCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier());
                    netss.setIncarcerated(outputData);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase("STD120")) {
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
                        result = "999";
                    }
                    netss.setTotalNbrSexPartner12Month(result);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase("STD119")) {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    String outputData = "";
                    outputData = mapperUtilService.mapToCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier());
                    netss.setSexPartnerThroughInternet(outputData);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase("STD121")) {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    if (result.contains("281088000"))
                    {
                        netss.setAAnusRectum("Y");
                    }
                    if (result.contains("18911002"))
                    {
                        netss.setBPenis("Y");
                    }
                    if (result.contains("20233005"))
                    {
                        netss.setCScrotum("Y");
                    }
                    if (result.contains("76784001"))
                    {
                        netss.setDVagina("Y");
                    }

                    if (result.contains("71252005"))
                    {
                        netss.setECervix("Y");
                    }
                    if (result.contains("71836000"))
                    {
                        netss.setFNasopharynx("Y");
                    }
                    if (result.contains("123851003"))
                    {
                        netss.setGMouthOral("Y");
                    }
                    if (result.contains("PHC1161"))
                    {
                        netss.setHEyeConjunctiva("Y");
                    }
                    if (result.contains("69536005"))
                    {
                        netss.setIHead("Y");
                    }
                    if (result.contains("22943007"))
                    {
                        netss.setJTorso("Y");
                    }
                    if (result.contains("66019005"))
                    {
                        netss.setKExtremities("Y");
                    }
                    if (result.contains("PHC1168"))
                    {
                        netss.setNNoLesionNoted("Y");
                    }
                    if (result.contains("PHC1170"))
                    {
                        netss.setOOther("Y");
                    }
                    if (result.contains("UNK"))
                    {
                        netss.setUUnknown("Y");
                    }
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase("STD122")) {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    String outputData = "";
                    outputData = mapperUtilService.mapToCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier());
                    if (outputData.isEmpty())
                    {
                        outputData = " ";
                    }
                    netss.setNonTreponemalSerologicTestForSyphilis(outputData);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase("STD123")) {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    String outputData = "";
                    outputData = mapperUtilService.mapToCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier());
                    netss.setQuantitativeSyphilisTestResult(outputData);
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase("STD126")) {
                    var result =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                    String outputData = "";
                    outputData = mapperUtilService.mapToCodedAnswer(result, in.getMessageElement().get(i).getQuestionIdentifier());
                    if (netss.getQuantitativeSyphilisTestResult().isEmpty())
                    {
                        netss.setQuantitativeSyphilisTestResult(outputData);
                    }
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase("NOT109")) {
                    NOT109 =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase("DEM162")) {
                    DEM162=  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase("INV137")) {
                    INV137=  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase("INV136")) {
                    INV136 =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase("LAB163")) {
                    LAB163 =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase("INV111")) {
                    INV111 =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase("INV120")) {
                    INV120 =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                }
                else if (in.getMessageElement().get(i).getQuestionIdentifier().equalsIgnoreCase("INV121")) {
                    INV121 =  mapperUtilService.mapToData(in.getMessageElement().get(i).getDataElement());
                }
            }



        }

        if (!NOT109State.contains(DEM162)) {
            netss.setCaseStatus("X");
        }
        String INV120Int = "";
        String INV136Int = "";
        String INV137Int = "";
        String INV121Int = "";
        String INV111Int = "";
        String LAB163Int = "";
        String wkYr = "";
        String INVEventDateInt = "";
        int CompINVEventDateInt = 0;
        int CompLAB163Int = 0;
        int CompINV120Int = 0;
        int CompINV121Int = 0;
        int CompINV136Int = 0;
        int CompINV137Int = 0;
        int CompEventDateInt = 0;
        int CompINV111Int = 0;

        if (!INV137.isEmpty()) {
            String dtOutput = "";
            mapperUtilService.mapToTsType(INV137, dtOutput);
            INV137Int = strRight(dtOutput, 6);
            CompINV137Int = strNumbers(dtOutput);
        }
        if (!INV136.isEmpty()) {
            String dtOutput = "";
            mapperUtilService.mapToTsType(INV136, dtOutput);
            INV136Int = strRight(dtOutput, 6);
            CompINV136Int = strNumbers(dtOutput);
        }
        if (!LAB163.isEmpty()) {
            String dtOutput = "";
            mapperUtilService.mapToTsType(LAB163, dtOutput);
            LAB163Int = strRight(dtOutput, 6);
            CompLAB163Int = strNumbers(dtOutput);
        }
        if (!INV111.isEmpty()) {
            String dtOutput = "";
            dtOutput = mapperUtilService.mapToTsType(INV111, dtOutput);
            INV111Int = strRight(dtOutput, 6);
            CompINV111Int = strNumbers(dtOutput);
        }
        if (!INV120.isEmpty()) {
            String dtOutput = "";
            mapperUtilService.mapToTsType(INV120, dtOutput);
            INV120Int = strRight(dtOutput, 6);
            CompINV120Int = strNumbers(dtOutput);
        }
        if (!INV121.isEmpty()) {
            String dtOutput = "";
            mapperUtilService.mapToTsType(INV121, dtOutput);
            INV121Int = strRight(dtOutput, 6);
            CompINV121Int = strNumbers(dtOutput);
        }
        if (!INV165.isEmpty() && !INV166.isEmpty()) {
            String result = "";
            mapperUtilService.mapToDate(INV165, INV166, result);
            String dtOutput = "";
            mapperUtilService.mapToTsType(result, dtOutput);
            wkYr = strRight(dtOutput, 6);
            CompINVEventDateInt = strNumbers(dtOutput);
        }
        String dtOutput = "";
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
            netss.setEventDate("999999");
            netss.setDateType("9");
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


        netss.setCounty("999");

        netss.setDateOfBirth("99999999");
        netss.setDateInitHealthExam("99999999");

        netss.setDateFirstReportOfCase("99999999");
        netss.setTreatmentDate("99999999");
        netss.setStdImport("U");
        netss.setRace("9");
        netss.setHispanic("9");
        netss.setCaseStatus("9");
        netss.setImported("9");
        netss.setOutbreak("9");
        netss.setFuture("99999");
        netss.setInfosrce("99");
        netss.setMethodCaseDetectn("88");
        netss.setZip("99999");
        netss.setCity("9999");
        netss.setPid("9");
        netss.setPregnantInitExam(" ");
        netss.setOrigin("9");
        netss.setDxDate("99999999");
        netss.setSpecimenSource("99");
        netss.setLabSpecimenCollDate("99999999");
        netss.setInterview("9");
        netss.setPartner("9");
        netss.setNeurologicalInvolvment(" "); // This become ""
        netss.setAmericanIndianAlaskanNative("U");
        netss.setAsian("U");
        netss.setBlackAfricanAmerican("U");
        netss.setNativeHawaiianPacificIslander("U");
        netss.setWhite("U");
        netss.setOtherRace("U");
        netss.setRefusedReportRace("U");
        netss.setUnknownRace("U");
        netss.setHispanicLatino("U");
        netss.setAsian("U");
        netss.setDateReportedToCdc("99999999");
        netss.setTotalNbrSexPartner12Month("999");
        netss.setAge("999");
        netss.setAgeType("9");
        netss.setSex("9");
        netss.setNetssVersion("04");
        netss.setHivStatus(" ");
        netss.setAAnusRectum("U");
        netss.setBPenis("U");
        netss.setCScrotum("U");
        netss.setDVagina("U");
        netss.setECervix("U");
        netss.setFNasopharynx("U");
        netss.setGMouthOral("U");
        netss.setHEyeConjunctiva("U");
        netss.setIHead("U");
        netss.setJTorso("U");
        netss.setKExtremities("U");
        netss.setNNoLesionNoted("U");
        netss.setOOther("U");
        netss.setUUnknown("U");
        netss.setNonTreponemalSerologicTestForSyphilis(" ");
        netss.setQuantitativeSyphilisTestResult("999999");
        netss.setEventDate("999999");
    }
}
