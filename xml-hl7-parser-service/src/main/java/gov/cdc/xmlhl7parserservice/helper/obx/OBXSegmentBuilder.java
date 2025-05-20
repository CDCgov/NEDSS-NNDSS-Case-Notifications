package gov.cdc.xmlhl7parserservice.helper.obx;

import ca.uhn.hl7v2.model.Type;

import ca.uhn.hl7v2.model.v25.datatype.*;
import ca.uhn.hl7v2.model.v25.segment.OBX;
import ca.uhn.hl7v2.model.v25.group.ORU_R01_ORDER_OBSERVATION;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.DataTypeException;
import gov.cdc.xmlhl7parserservice.model.generated.jaxb.MessageElement;
import gov.cdc.xmlhl7parserservice.helper.MessageState;
import gov.cdc.xmlhl7parserservice.util.HL7DateFormatUtil;
import gov.cdc.xmlhl7parserservice.model.Obx.ObxRepeatingElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

@Component
public class OBXSegmentBuilder {
    private static final Logger logger = LoggerFactory.getLogger(OBXSegmentBuilder.class);

    private final MessageState messageState;
    private final HL7DateFormatUtil dateFormatUtil;

    public OBXSegmentBuilder(MessageState messageState, HL7DateFormatUtil dateFormatUtil) {
        this.messageState = messageState;
        this.dateFormatUtil = dateFormatUtil;
    }

    public void processOBXFields(MessageElement messageElement, ORU_R01_ORDER_OBSERVATION orderObservation) throws HL7Exception, DataTypeException {
        if (messageElement.getQuestionIdentifierNND().equalsIgnoreCase("INV826") || messageElement.getQuestionIdentifierNND().equalsIgnoreCase("INV827") ) {
            var test = "";
        }

        String obxField = messageElement.getHl7SegmentField().trim();

        // Use state variables from MessageState
        int obxOrderGroupID = messageState.getObxOrderGroupID();
        int obxInc = messageState.getObxInc();
        int obx5ValueInc = messageState.getObx5ValueInc();
        String obx5ObservationSubID = messageState.getObx5ObservationSubID();
        boolean obxFound = messageState.isObxFound();
        String messageTypePattern = messageState.getMessageTypePattern();

        Pattern pattern = Pattern.compile(messageTypePattern);
        String questionIdentifier = messageElement.getQuestionIdentifier().trim();
        String questionIdentifierNND = messageElement.getQuestionIdentifierNND().trim();
        // find Match
        Matcher matcher = pattern.matcher(messageState.getMessageType());
        if (matcher.find() && (questionIdentifier.equals("LAB588_MTH")
                || questionIdentifier.equals("INV290_MTH")
                || questionIdentifier.equals("INV291_MTH")
                || questionIdentifier.equals("STD123_MTH")
                || questionIdentifier.equals("LAB167_MTH")))
        {
            //extract index of ObservationSubID
            int count = Integer.parseInt(messageElement.getObservationSubID().trim());
            if (count > messageState.getDupRepeatCongenitalCounter()) {
                messageState.setDupRepeatCongenitalCounter(count);
            }
        }
        else if ( questionIdentifierNND.equals("INV290")
                || questionIdentifierNND.equals("INV291"))
        {
            messageState.setInv290Inv291Counter(Integer.parseInt(messageElement.getObservationSubID().trim()));
        }

        if (messageElement.getOrderGroupId().trim().equals("1")) {
            messageState.setObxInc(messageState.getObx1Inc());
            messageState.setObxOrderGroupID(0);
        }else{
            messageState.setObxInc(messageState.getObx2Inc());
            messageState.setObxOrderGroupID(1);
        }
        List<ObxRepeatingElement> obxRepeatingElementArrayList = messageState.getObxRepeatingElementArrayList();
        for (int x = 0; x < obxRepeatingElementArrayList.size(); x++) {
            if (questionIdentifierNND.equals("INV290") || questionIdentifierNND.equals("INV291"))
            {
                obxFound = false;
            }
            else if (Objects.equals(obxRepeatingElementArrayList.get(x).getElementUid(), questionIdentifierNND))
            {
                if (messageElement.getQuestionGroupSeqNbr()!=null && messageElement.getQuestionGroupSeqNbr()!=null) {
                    if (obxRepeatingElementArrayList.get(x).getQuestionGroupSeqNbr().equals(messageElement.getQuestionGroupSeqNbr().trim())
                            && obxRepeatingElementArrayList.get(x).getObservationSubId().equals(messageElement.getObservationSubID().trim()))
                    {
                        obxFound = true;
                    }
                }
                else if ( messageElement.getQuestionGroupSeqNbr()==null && messageElement.getObservationSubID()!=null)
                {
                    if (obxRepeatingElementArrayList.get(x).getQuestionGroupSeqNbr().equals(messageElement.getQuestionGroupSeqNbr().trim())
                            && obxRepeatingElementArrayList.get(x).getObservationSubId().equals(messageElement.getObservationSubID().trim()))
                    {
                        obxFound = true;
                    }
                }
                else if (messageElement.getQuestionGroupSeqNbr()!=null && messageElement.getQuestionGroupSeqNbr()==null)
                {
                    if (obxRepeatingElementArrayList.get(x).getQuestionGroupSeqNbr().equals(messageElement.getQuestionGroupSeqNbr().trim())
                            && obxRepeatingElementArrayList.get(x).getObservationSubId()==null)
                    {
                        obxFound = true;
                    }
                }
                else if (messageElement.getQuestionGroupSeqNbr()==null && messageElement.getObservationSubID()==null)
                {
                    obxFound = true;
                }

                //HEP specific code for repeating INV826/INV827
                if (questionIdentifierNND.equals("INV826") || questionIdentifierNND.equals("INV827"))
                {
                    obxFound = false;
                }

                if (questionIdentifier.equals("INV826b") || questionIdentifier.equals("INV827b"))
                {
                    obxFound = false;
                }

                if (obxFound) {
                    //found existing element
                    obxRepeatingElementArrayList.get(x).setValueInc(obxRepeatingElementArrayList.get(x).getValueInc() + 1);
                    obx5ValueInc = obxRepeatingElementArrayList.get(x).getValueInc();
                    obxInc = obxRepeatingElementArrayList.get(x).getObxInc();
                    obx5ObservationSubID = obxRepeatingElementArrayList.get(x).getObservationSubId();
                }

            }
        }

        ObxRepeatingElement element = null;
        Optional<ObxRepeatingElement> match = obxRepeatingElementArrayList.stream()
                .filter(e -> messageElement.getQuestionIdentifierNND().equals(e.getElementUid()))
                .findFirst();
        if (match.isPresent()) {
            element = match.get();
        }
        OBX obx;
        if (
                obxFound
            //                    element != null
        )
        {
            int idx = element.getObxInc();
            obx = orderObservation.getOBSERVATION(idx).getOBX();
        }
        else {
            obx = orderObservation.getOBSERVATION(orderObservation.getOBSERVATIONAll().size()).getOBX();
        }

        if (!obxFound)
        {
            ObxRepeatingElement obxRepeatingElement = new ObxRepeatingElement();
            obxRepeatingElement.setElementUid(messageElement.getQuestionIdentifierNND());

            if (messageElement.getQuestionGroupSeqNbr() != null) {
                obxRepeatingElement.setQuestionGroupSeqNbr(messageElement.getQuestionGroupSeqNbr());
            }
            else
            {
                obxRepeatingElement.setQuestionGroupSeqNbr(null);
            }
            if (messageElement.getObservationSubID() != null)
            {
                obxRepeatingElement.setObservationSubId(messageElement.getObservationSubID());
            }
            else
            {
                obxRepeatingElement.setObservationSubId(null);
            }
            obxRepeatingElement.setValueInc(0);
            obxRepeatingElement.setObxInc(obxRepeatingElementArrayList.size());
            obxRepeatingElementArrayList.add(obxRepeatingElement);

        }
        /* This code will cover the situation with TB investigation where value is based off of question_identifer='INV121' and
		   question_identifier_nnd='INV177' and it is populated from frontend.*/
        if (questionIdentifierNND.equals("INV177"))
        {
            messageState.setInv177Found(true);
        }
        if (questionIdentifier.equals("INV111")
                || questionIdentifierNND.equals("INV120")
                || questionIdentifierNND.equals("INV121"))
        {
            if (questionIdentifier.equals("INV111") && messageElement.getDataElement().getQuestionDataTypeNND().equals("DT"))
            {
                int year = messageElement.getDataElement().getDtDataType().getDate().getYear();
                int month = messageElement.getDataElement().getDtDataType().getDate().getMonth();
                int day = messageElement.getDataElement().getDtDataType().getDate().getDay();
                messageState.setNewDate(String.format("%04d%02d%02d", year, month, day));
            }
            else
            {
                if (messageElement.getDataElement().getTsDataType() != null) {
                    int year = Integer.parseInt(messageElement.getDataElement().getTsDataType().getYear());
                    int month = messageElement.getDataElement().getTsDataType().getTime().getMonth();
                    int day = messageElement.getDataElement().getTsDataType().getTime().getDay();
                    messageState.setNewDate(String.format("%04d%02d%02d", year, month, day));
                }
            }
            if (messageState.getInv177Date().isEmpty())
            {
                messageState.setInv177Date(messageState.getNewDate());
            }
        }

        obx.getSetIDOBX().setValue(String.valueOf(obxInc));

        if (messageElement.getObservationSubID()!=null)
        {
            obx.getObservationSubID().setValue(messageElement.getObservationSubID().trim());
        }

        // 77998-3 is the SN_WITH_UNIT
        if (questionIdentifierNND.equals("SN_WITH_UNIT") || questionIdentifierNND.equals("77998-3"))
        {
            obx.getValueType().setValue("SN");
        }
        else
        {
            obx.getValueType().setValue(messageElement.getDataElement().getQuestionDataTypeNND().trim());
        }

        if (!obxFound)
        {
            obx.getObservationIdentifier().getIdentifier().setValue(questionIdentifierNND);
            String questionLabelNND = messageElement.getQuestionLabelNND().trim();
            obx.getObservationIdentifier().getText().setValue(questionLabelNND);
            String questionOID = messageElement.getQuestionOID().trim();
            obx.getObservationIdentifier().getNameOfCodingSystem().setValue(questionOID);
            obx.getObservationIdentifier().getAlternateIdentifier().setValue(questionIdentifier);
            String questionLabel = messageElement.getQuestionLabel().trim();
            obx.getObservationIdentifier().getAlternateText().setValue(questionLabel);

            obx.getObservationIdentifier().getNameOfAlternateCodingSystem().setValue("L");

            if (messageState.getMessageType().contains("CongenitalSyphilis_MMG_V1.0")
                    && questionIdentifier.equals("LAB588")
                    || questionIdentifier.equals("INV290")
                    || questionIdentifier.equals("INV291")
                    || questionIdentifier.equals("STD123")
                    || questionIdentifier.equals("LAB167")
                    || questionIdentifier.equals("STD123_1"))
            {
                obx.getObservationSubID().setValue("-"+messageElement.getObservationSubID().trim());
            }
            else if (messageElement.getObservationSubID()!=null)
            {
                obx.getObservationSubID().setValue(messageElement.getObservationSubID().trim());
            }
            else if (messageElement.getQuestionGroupSeqNbr()!=null)
            {
                obx.getObservationSubID().setValue(messageElement.getQuestionGroupSeqNbr().trim());
            }

        }
        //XPN datatype
        if (messageElement.getDataElement().getQuestionDataTypeNND().equals("XPN")) {
            String comparator = "";
            if (messageElement.getDataElement().getSnDataType().getComparator()!=null)
            {
                comparator = messageElement.getDataElement().getSnDataType().getComparator().trim();
            }
            String num1 = "";
            if (messageElement.getDataElement().getSnDataType().getNum1()!=null)
            {
                num1 = messageElement.getDataElement().getSnDataType().getNum1().trim();
            }
            String separatorSuffix = "";
            if (messageElement.getDataElement().getSnDataType().getSeparatorSuffix()!=null)
            {
                separatorSuffix = messageElement.getDataElement().getSnDataType().getSeparatorSuffix().trim();
            }

            String num2 = "";
            if (messageElement.getDataElement().getSnDataType().getNum2()!=null)
            {
                num2 = messageElement.getDataElement().getSnDataType().getNum2().trim();
            }



            Type obxValue = obx.getObservationValue(obx5ValueInc).getData();
            SN sn;
            if (obxValue instanceof SN) {
                sn = (SN) obxValue;
            } else {
                sn = new SN(obx.getMessage());
            }
            sn.getComparator().setValue(comparator);           // SN-1 (e.g., ">")
            sn.getNum1().setValue(String.valueOf(num1));       // SN-2
            sn.getSeparatorSuffix().setValue(separatorSuffix); // SN-3 (optional, may be null or blank)
            sn.getNum2().setValue(String.valueOf(num2));       // SN-4 (optional)

            obx.getObservationValue(obx5ValueInc).setData(sn);
        }



        //XTN datatype
        if (messageElement.getDataElement().getQuestionDataTypeNND().equals("XTN"))
        {
            String telecommunicationUseCode = "";
            if (messageElement.getDataElement().getXtnDataType().getTelecommunicationUseCode()!=null){
                telecommunicationUseCode = messageElement.getDataElement().getXtnDataType().getTelecommunicationUseCode().trim();
            }
            String telecommunicationEquipmentType = "";
            if (messageElement.getDataElement().getXtnDataType().getTelecommunicationEquipmentType()!=null){
                telecommunicationEquipmentType = messageElement.getDataElement().getXtnDataType().getTelecommunicationEquipmentType().trim();
            }
            String emailAddress = "";
            if (messageElement.getDataElement().getXtnDataType().getEmailAddress()!=null){
                emailAddress = messageElement.getDataElement().getXtnDataType().getEmailAddress().trim();
            }
            String areaOrCityCode = "";
            if (messageElement.getDataElement().getXtnDataType().getAreaOrCityCode()!=null){
                areaOrCityCode = messageElement.getDataElement().getXtnDataType().getAreaOrCityCode().trim();
            }

            String phoneNumber = "";
            if (messageElement.getDataElement().getXtnDataType().getPhoneNumber()!=null){
                phoneNumber = messageElement.getDataElement().getXtnDataType().getPhoneNumber().trim();
            }

            //build XTN object
            Type obxValue = obx.getObservationValue(obx5ValueInc).getData();
            XTN xtnDataType;
            if (obxValue instanceof XTN) {
                xtnDataType = (XTN) obxValue;
            } else {
                xtnDataType = new XTN(obx.getMessage());
            }


            xtnDataType.getTelecommunicationUseCode().setValue(telecommunicationUseCode);
            xtnDataType.getTelecommunicationEquipmentType().setValue(telecommunicationEquipmentType);
            xtnDataType.getEmailAddress().setValue(emailAddress);
            xtnDataType.getAreaCityCode().setValue(areaOrCityCode);
            xtnDataType.getTelephoneNumber().setValue(phoneNumber);

            obx.getObservationValue(obx5ValueInc).setData(xtnDataType);
        }

        //XPN datatype
        if (messageElement.getDataElement().getQuestionDataTypeNND().equals("XPN"))
        {
            String familyName = "";
            if (messageElement.getDataElement().getXpnDataType().getFamilyName()!=null){
                familyName = messageElement.getDataElement().getXpnDataType().getFamilyName().trim();
            }
            String givenName = "";
            if (messageElement.getDataElement().getXpnDataType().getGivenName()!=null){
                givenName = messageElement.getDataElement().getXpnDataType().getGivenName().trim();
            }

            //Build XPN object
            Type obxValue = obx.getObservationValue(obx5ValueInc).getData();
            XPN xpn;
            if (obxValue instanceof XPN) {
                xpn = (XPN) obxValue;
            } else {
                xpn = new XPN(obx.getMessage());
            }

            xpn.getXpn1_FamilyName().getFn1_Surname().setValue(familyName);
            xpn.getXpn2_GivenName().setValue(givenName);
            obx.getObservationValue(obx5ValueInc).setData(xpn);
        }

        //SN datatype
        if (messageElement.getDataElement().getQuestionDataTypeNND().equals("SN"))
        {
            String comparator = "";
            if (messageElement.getDataElement().getSnDataType().getComparator() != null)
            {
                comparator = messageElement.getDataElement().getSnDataType().getComparator();
            }
            String num1 = "";
            if (messageElement.getDataElement().getSnDataType().getNum1() != null) {
                num1 = messageElement.getDataElement().getSnDataType().getNum1();
            }

            String separatorSuffix = "";
            if (messageElement.getDataElement().getSnDataType().getSeparatorSuffix() != null) {
                separatorSuffix = messageElement.getDataElement().getSnDataType().getSeparatorSuffix();
            }

            String num2 = "";
            if (messageElement.getDataElement().getSnDataType().getNum2() != null) {
                num2 = messageElement.getDataElement().getSnDataType().getNum2();
            }

            //out.PATIENT_RESULT.ORDER_OBSERVATION[0].OBSERVATION[obxOrderGroupId].OBX[obxInc].ObservationValue[obx5ValueInc]
            Type obxValue = obx.getObservationValue(obx5ValueInc).getData();
            SN sn;
            if (obxValue instanceof SN) {
                sn = (SN) obxValue;
            } else {
                sn = new SN(obx.getMessage());
            }
            sn.getComparator().setValue(comparator);
            sn.getNum1().setValue(num1);
            sn.getSeparatorSuffix().setValue(separatorSuffix);
            sn.getNum2().setValue(num2);
            obx.getObservationValue(obx5ValueInc).setData(sn);


            //HEP Specific code for repeating INV826/INV827 and 11920_8/1742_6
            if (messageElement.getQuestionIdentifierNND().equalsIgnoreCase("INV827")
                    || messageElement.getQuestionIdentifierNND().equalsIgnoreCase("1742_6") || messageElement.getQuestionIdentifierNND().equalsIgnoreCase("1742-6"))
            {
                obx.getObservationSubID().setValue("1");
            }

            if (messageElement.getQuestionIdentifier().equalsIgnoreCase("INV827b")
                    || messageElement.getQuestionIdentifier().equalsIgnoreCase("11920_8") || messageElement.getQuestionIdentifier().equalsIgnoreCase("11920-8"))
            {
                obx.getObservationSubID().setValue("2");
            }
        }

        //SN data type with unit
        if (messageElement.getDataElement().getQuestionDataTypeNND().trim().equals("SN_WITH_UNIT"))
        {
            String comparator = "";
            if (messageElement.getDataElement().getSnunitDataType().getComparator()!=null){
                comparator = messageElement.getDataElement().getSnunitDataType().getComparator().trim();
            }

            String num1 = "";
            if (messageElement.getDataElement().getSnunitDataType().getNum1()!=null){
                num1 = messageElement.getDataElement().getSnunitDataType().getNum1().trim();
            }

            String separatorSuffix = "";
            if (messageElement.getDataElement().getSnunitDataType().getSeparatorSuffix()!=null){
                separatorSuffix = messageElement.getDataElement().getSnunitDataType().getSeparatorSuffix().trim();
            }
            String num2 = "";
            if (messageElement.getDataElement().getSnunitDataType().getNum2()!=null){
                num2 = messageElement.getDataElement().getSnunitDataType().getNum2().trim();
            }
            // out.PATIENT_RESULT.ORDER_OBSERVATION[0].OBSERVATION[obxOrderGroupId].OBX[obxInc].ObservationValue[obx5ValueInc]
            Type obxValue = obx.getObservationValue(obx5ValueInc).getData();
            SN snDataType;
            if (obxValue instanceof SN) {
                snDataType = (SN) obxValue;
            } else {
                snDataType = new SN(obx.getMessage());
            }

            snDataType.getComparator().setValue(comparator);
            snDataType.getNum1().setValue(num1);
            snDataType.getSeparatorSuffix().setValue(separatorSuffix);
            snDataType.getNum2().setValue(num2);
            obx.getObservationValue(obx5ValueInc).setData(snDataType);

            String codedValue = "";
            if (messageElement.getDataElement().getSnunitDataType().getCeCodedValue()!=null){
                codedValue = messageElement.getDataElement().getSnunitDataType().getCeCodedValue().trim();
            }
            String codedValueDescription = "";
            if (messageElement.getDataElement().getSnunitDataType().getCeCodedValueDescription()!=null){
                codedValueDescription = messageElement.getDataElement().getSnunitDataType().getCeCodedValueDescription().trim();
            }

            String codedValueCodingSystem = "";
            if (messageElement.getDataElement().getSnunitDataType().getCeCodedValueCodingSystem()!=null){
                codedValueCodingSystem = messageElement.getDataElement().getSnunitDataType().getCeCodedValueCodingSystem().trim();
            }

            String localCodedValue = "";
            if (messageElement.getDataElement().getSnunitDataType().getCeLocalCodedValue()!=null){
                localCodedValue = messageElement.getDataElement().getSnunitDataType().getCeLocalCodedValue().trim();
            }
            String localCodedValueDescription = "";
            if (messageElement.getDataElement().getSnunitDataType().getCeLocalCodedValueDescription()!=null){
                localCodedValueDescription = messageElement.getDataElement().getSnunitDataType().getCeLocalCodedValueDescription().trim();
            }

            String localCodedValueCodingSystem = "";
            if (messageElement.getDataElement().getSnunitDataType().getCeLocalCodedValueCodingSystem()!=null){
                localCodedValueCodingSystem = messageElement.getDataElement().getSnunitDataType().getCeLocalCodedValueCodingSystem().trim();
            }

            obx.getUnits().getIdentifier().setValue(codedValue);
            obx.getUnits().getText().setValue(codedValueDescription);
            obx.getUnits().getNameOfCodingSystem().setValue(codedValueCodingSystem);
            obx.getUnits().getAlternateIdentifier().setValue(localCodedValue);
            obx.getUnits().getAlternateText().setValue(localCodedValueDescription);
            obx.getUnits().getNameOfAlternateCodingSystem().setValue(localCodedValueCodingSystem);

            logger.info("TEST {}", obx.getMessage());
        }

        // MapToSpCXType
        if ((messageState.getMessageType().contains("Measles_MMG_V1.0") || messageState.getMessageType().contains("Rubella_MMG_V1.0")
                || messageState.getMessageType().contains("CRS_MMG_V1.0") || messageState.getMessageType().contains("Varicella_MMG_V3.0")
                || messageState.getMessageType().contains("Pertussis_MMG_V1.0") || messageState.getMessageType().contains("Mumps_MMG_V1.0")))
        {
            //TODO - 23149
            String stData = messageElement.getDataElement().getStDataType() != null ? messageElement.getDataElement().getStDataType().getStringData().trim() : "";
            String questionMap = messageElement.getQuestionMap() != null ? messageElement.getQuestionMap().trim() : "";
            String cxData = messageElement.getDataElement().getCxDataType() != null ? messageElement.getDataElement().getCxDataType().getCxData().trim() : "";
            if (questionIdentifierNND.equals("LAB143")) {
                String combined = questionIdentifierNND.trim()+"~"+obx5ObservationSubID;

                String output = "";
                if (!cxData.isEmpty() && cxData.contains(combined)){
                    int start = cxData.indexOf(combined);
                    String substring = cxData.substring(start);
                    int end = substring.indexOf("|"); //if not found, will return -1
                    if (end == -1){
                        end = substring.length() - (questionIdentifierNND.trim().length()+ messageElement.getObservationSubID().length()+1);
                    }
                    String cxString = substring.substring(0, end);

                    if (cxString.contains(":")) {
                        output = cxString.substring(cxString.indexOf(":") + 1);
                    } else {
                        output = cxString;
                    }

                    int part1 = output.indexOf("^");
                    String identifier = output.substring(0, part1);
                    String rest = output.substring(part1 + 1);

                    int part2 = rest.indexOf("^");
                    String description = rest.substring(0, part2);
                    String descriptionValue = rest.substring(part2 + 1);
                    obx.getValueType().setValue("ST");
                    obx.getSetIDOBX().setValue(String.valueOf(obxInc+1));
                    obx.getObservationIdentifier().getIdentifier().setValue(questionIdentifierNND);
                    obx.getObservationIdentifier().getText().setValue(messageElement.getQuestionLabelNND().trim());
                    obx.getObservationIdentifier().getNameOfCodingSystem().setValue(messageElement.getQuestionOID().trim());
                    obx.getObservationIdentifier().getAlternateIdentifier().setValue(messageElement.getQuestionIdentifier().trim());
                    obx.getObservationIdentifier().getAlternateText().setValue(messageElement.getQuestionLabelNND());
                    obx.getObservationIdentifier().getNameOfAlternateCodingSystem().setValue("L");
                    Type obxValue = obx.getObservationValue(0).getData();
                    ST stDataType;
                    if (obxValue instanceof ST) {
                        stDataType = (ST) obxValue;
                    } else {
                        stDataType = new ST(obx.getMessage());
                    }


                    stDataType.setValue(messageElement.getDataElement().getStDataType().getStringData().trim());
                    obx.getObservationValue(0).setData(stDataType);
                    obx.getObservationResultStatus().setValue("F");
                    obx.getObservationSubID().setValue(messageElement.getObservationSubID().trim());
                    obxInc +=1;
                    obx.getValueType().setValue("CX");
                    obx.getSetIDOBX().setValue(String.valueOf(obxInc+1));
                    obx.getObservationIdentifier().getIdentifier().setValue(identifier);
                    obx.getObservationIdentifier().getText().setValue(description);
                    obx.getObservationIdentifier().getNameOfCodingSystem().setValue(messageElement.getQuestionOID().trim());
                    obx.getObservationIdentifier().getAlternateIdentifier().setValue(identifier);
                    obx.getObservationIdentifier().getAlternateText().setValue(description);
                    obx.getObservationIdentifier().getNameOfAlternateCodingSystem().setValue("L");
                    String stringData = messageElement.getDataElement().getStDataType().getStringData().trim();
                    Type obxValue2 = obx.getObservationValue(0).getData();
                    ST stTypeForObservationValue;
                    if (obxValue2 instanceof ST) {
                        stTypeForObservationValue = (ST) obxValue2;
                    } else {
                        stTypeForObservationValue = new ST(obx.getMessage());
                    }


                    stTypeForObservationValue.setValue(descriptionValue+"^^^&"+stringData+"&ISO");
                    obx.getObservationValue(0).setData(stTypeForObservationValue);
                    obx.getObservationResultStatus().setValue("F");
                    obx.getObservationSubID().setValue(messageElement.getObservationSubID().trim());
                    stData += "|"+questionIdentifierNND+"~"+obx5ObservationSubID+":"+stringData;
                }else{
                    obx.getValueType().setValue("ST");
                    obx.getSetIDOBX().setValue(String.valueOf(obxInc+1));
                    obx.getObservationIdentifier().getIdentifier().setValue(questionIdentifierNND);
                    obx.getObservationIdentifier().getText().setValue(questionIdentifierNND);
                    obx.getObservationIdentifier().getNameOfCodingSystem().setValue(messageElement.getQuestionOID().trim());
                    obx.getObservationIdentifier().getAlternateIdentifier().setValue(questionIdentifierNND);
                    obx.getObservationIdentifier().getAlternateText().setValue(messageElement.getQuestionLabelNND().trim());
                    obx.getObservationIdentifier().getNameOfAlternateCodingSystem().setValue("L");

                }

            }else if (questionIdentifierNND.equals("CX") && !questionMap.isEmpty() ) {
                if (!stData.isEmpty()){
                    int startIndex = stData.indexOf(questionMap+"~"+messageElement.getObservationSubID().trim());
                    String subString = stData.substring(stData.length()-startIndex);
                    int endIndex = subString.indexOf("|");
                    if (endIndex == -1){
                        endIndex = subString.length()- (questionMap+"~"+messageElement.getObservationSubID().trim()).length()-1;
                    }

                    String STString = subString.substring(endIndex);
                    obx.getValueType().setValue("CX");
                    obx.getSetIDOBX().setValue(String.valueOf(obxInc+1));
                    obx.getObservationIdentifier().getIdentifier().setValue(questionIdentifierNND);
                    obx.getObservationIdentifier().getText().setValue(messageElement.getQuestionLabel().trim());
                    obx.getObservationIdentifier().getAlternateIdentifier().setValue(messageElement.getQuestionLabelNND());
                    obx.getObservationIdentifier().getNameOfAlternateCodingSystem().setValue("L");

                    Type obxValue = obx.getObservationValue(0).getData();
                    CX cxDataType;
                    if (obxValue instanceof CX) {
                        cxDataType = (CX) obxValue;
                    } else {
                        cxDataType = new CX(obx.getMessage());
                    }


                    cxDataType.getCx1_IDNumber().setValue(cxData);
                    cxDataType.getCx4_AssigningAuthority().getUniversalID().setValue(STString);
                    cxDataType.getCx4_AssigningAuthority().getNamespaceID().setValue("&ISO");
                    obx.getObservationValue(0).setData(cxDataType);
                    obx.getObservationResultStatus().setValue("F");
                    obx.getObservationSubID().setValue(messageElement.getObservationSubID().trim());

                }else{
                    cxData +=cxData +"|"+ questionMap + "~" + messageElement.getObservationSubID().trim() + ":" + questionIdentifierNND+"^"+messageElement.getQuestionLabelNND().trim()+"^" +messageElement.getDataElement().getCxDataType().getCxData().trim();
                    obxInc -=1;
                }
            }
        }
        else if (messageState.getMessageType().contains("Arbo_Case_Map_v1.0") && questionIdentifier.equals("INV173")
                && messageElement.getDataElement().getQuestionDataTypeNND().trim().equals("ST"))
        {
            messageState.setDefaultNull(false);

            // out.PATIENT_RESULT.ORDER_OBSERVATION[0].OBSERVATION[obxOrderGroupId].OBX[obxInc].ObservationValue[obx5ValueInc]
            Type obxValue = obx.getObservationValue(obx5ValueInc).getData();
            ST stringData;
            if (obxValue instanceof ST) {
                stringData = (ST) obxValue;
            } else {
                stringData = new ST(obx.getMessage());
            }

            stringData.setValue(messageElement.getDataElement().getStDataType().getStringData().trim());
            obx.getObservationValue(obx5ValueInc).setData(stringData);
        }
        else if (messageElement.getDataElement().getQuestionDataTypeNND().trim().equals("ST"))
        {
            Type obxValue = obx.getObservationValue(obx5ValueInc).getData();
            ST stringData;
            if (obxValue instanceof ST) {
                stringData = (ST) obxValue;
            } else {
                stringData = new ST(obx.getMessage());
            }

            stringData.setValue(messageElement.getDataElement().getStDataType().getStringData().trim());
            obx.getObservationValue(obx5ValueInc).setData(stringData);
        }
        //TX datatype
        if (messageElement.getDataElement().getQuestionDataTypeNND().trim().equals("TX")){
            Type obxValue = obx.getObservationValue(obx5ValueInc).getData();
            TX textData;
            if (obxValue instanceof TX) {
                textData = (TX) obxValue;
            } else {
                textData = new TX(obx.getMessage());
            }

            String td = messageElement.getDataElement().getTxDataType().getTextData().trim();
            textData.setValue(td);
            obx.getObservationValue(obx5ValueInc).setData(textData);

            if (questionIdentifierNND.equals("77999-1") && messageState.getGenericMMGv20()) {
                textData.setValue(td+messageState.getHcw());
                messageState.setHcwObxInc(messageState.getObxInc());
                messageState.setHcwObxOrderGroupId(messageState.getObxOrderGroupID());
                messageState.setHcwObx5ValueInc(messageState.getObx5ValueInc());
                obx.getObservationValue(messageState.getObx5ValueInc()).setData(textData);
                messageState.setHcwTextBeforeCodedInd(true);
            }
        }
        //ID datatype
        if (messageElement.getDataElement().getQuestionDataTypeNND().trim().equals("ID")){
            String idCodedValue = messageElement.getDataElement().getIdDataType().getIdCodedValue().trim();
            Type obxValue = obx.getObservationValue(obx5ValueInc).getData();
            ID idType;
            if (obxValue instanceof ID) {
                idType = (ID) obxValue;
            } else {
                idType = new ID(obx.getMessage());
            }

            idType.setValue(idCodedValue);
            obx.getObservationValue(obx5ValueInc).setData(idType);
        }
        // IS datatype
        if (messageElement.getDataElement().getQuestionDataTypeNND().trim().equals("IS")){
            String isCodedValue = messageElement.getDataElement().getIsDataType().getIsCodedValue().trim();

            Type obxValue = obx.getObservationValue(obx5ValueInc).getData();
            IS isType;
            if (obxValue instanceof IS) {
                isType = (IS) obxValue;
            } else {
                isType = new IS(obx.getMessage());
            }

            isType.setValue(isCodedValue);
            obx.getObservationValue(obx5ValueInc).setData(isType);
        }
        // CWE datatype
        if (messageElement.getDataElement().getQuestionDataTypeNND().trim().equals("CWE")){
            String codedValue = "";
            String codedValueDescription = "";
            String codedValueCodingSystem = "";
            String localCodedValue = "";
            String localCodedValueDescription = "";
            String localCodedValueCodingSystem = "";
            if (messageElement.getDataElement().getCweDataType().getCweCodedValue()!=null){
                codedValue = messageElement.getDataElement().getCweDataType().getCweCodedValue().trim();
            }
            if (messageElement.getDataElement().getCweDataType().getCweCodedValueDescription()!=null){
                codedValueDescription = messageElement.getDataElement().getCweDataType().getCweCodedValueDescription().trim();
            }
            if (messageElement.getDataElement().getCweDataType().getCweCodedValueCodingSystem()!=null){
                codedValueCodingSystem = messageElement.getDataElement().getCweDataType().getCweCodedValueCodingSystem().trim();
            }

            if (messageElement.getDataElement().getCweDataType().getCweLocalCodedValue()!=null){
                localCodedValue = messageElement.getDataElement().getCweDataType().getCweLocalCodedValue().trim();
            }
            if (messageElement.getDataElement().getCweDataType().getCweLocalCodedValueDescription()!=null){
                localCodedValueDescription = messageElement.getDataElement().getCweDataType().getCweLocalCodedValueDescription().trim();
            }
            if (messageElement.getDataElement().getCweDataType().getCweLocalCodedValueCodingSystem()!=null){
                localCodedValueCodingSystem = messageElement.getDataElement().getCweDataType().getCweLocalCodedValueCodingSystem().trim();
            }

            String originalText = "";
            if (messageElement.getDataElement().getCweDataType().getCweOriginalText()!=null){
                originalText = messageElement.getDataElement().getCweDataType().getCweOriginalText().trim();
                if (!originalText.isEmpty()){
                    originalText = originalText.replace("\\","\\E\\");
                    originalText = originalText.replace("|","\\F\\");
                    originalText = originalText.replace("~","\\R\\");
                    originalText = originalText.replace("^","\\S\\");
                    originalText = originalText.replace("&","\\T\\");
                }
            }

            if (messageState.getMessageType().contains("Arbo_Case_Map_v1.0")|| messageState.getMessageType().contains("Gen_Case_Map_v1.0")
                    || messageState.getMessageType().contains("TB_Case_Map_v2.0") || messageState.getMessageType().contains("Var_Case_Map_v2.0"))
            {
                //TODO - 23329
                Type obxValue = obx.getObservationValue(obx5ValueInc).getData();
                CWE cweDatatype;
                if (obxValue instanceof CWE) {
                    cweDatatype = (CWE) obxValue;
                } else {
                    cweDatatype = new CWE(obx.getMessage());
                }


                if (codedValue.isEmpty()){
                    cweDatatype.getCwe5_AlternateText().setValue(localCodedValueDescription);
                    obx.getObservationValue(obx5ValueInc).setData(cweDatatype);
                }else{
                    cweDatatype.getAlternateText().setValue(codedValue+"^"+codedValueDescription+"^"+codedValueCodingSystem+"^"+originalText);
                    obx.getObservationValue(obx5ValueInc).setData(cweDatatype);
                }
            }
            else
            {
                if (codedValue.isEmpty()){
                    //TODO - 23329
                    Type obxValue = obx.getObservationValue(obx5ValueInc).getData();
                    CWE cweDataTYpe;
                    if (obxValue instanceof CWE) {
                        cweDataTYpe = (CWE) obxValue;
                    } else {
                        cweDataTYpe = new CWE(obx.getMessage());
                    }

                    if (messageElement.getDataElement().getCweDataType().getCweCodedValue()==null && messageElement.getDataElement().getCweDataType().getCweLocalCodedValueDescription() != null)
                    {
                        codedValueDescription = messageElement.getDataElement().getCweDataType().getCweLocalCodedValueDescription().trim();
                        if (!codedValueDescription.isEmpty()){
                            codedValueDescription = codedValueDescription.replace("\\","\\E\\");
                            codedValueDescription = codedValueDescription.replace("|","\\F\\");
                            codedValueDescription = codedValueDescription.replace("~","\\R\\");
                            codedValueDescription = codedValueDescription.replace("^","\\S\\");
                            codedValueDescription = codedValueDescription.replace("&","\\T\\");
                        }
                        cweDataTYpe.getCwe9_OriginalText().setValue(codedValueDescription);
                        obx.getObservationValue(obx5ValueInc).setData(cweDataTYpe);
                    }
                    else if (Objects.equals(messageElement.getDataElement().getCweDataType().getCweCodedValue(), "OTH"))
                    {
                        cweDataTYpe.getCwe1_Identifier().setValue(localCodedValue);                      // CWE.1
                        cweDataTYpe.getCwe2_Text().setValue(localCodedValueDescription);                // CWE.2
                        cweDataTYpe.getCwe3_NameOfCodingSystem().setValue(localCodedValueCodingSystem); // CWE.3
                        cweDataTYpe.getCwe9_OriginalText().setValue(originalText);                      // CWE.9

                        obx.getObservationValue(obx5ValueInc).setData(cweDataTYpe);

                    }
                    else
                    {



                        cweDataTYpe.getCwe1_Identifier().setValue(localCodedValue);                         // CWE.1
                        cweDataTYpe.getCwe2_Text().setValue(localCodedValueDescription);                   // CWE.2
                        cweDataTYpe.getCwe3_NameOfCodingSystem().setValue("L");                            // CWE.3
                        cweDataTYpe.getCwe9_OriginalText().setValue(originalText);                         // CWE.9

                        obx.getObservationValue(obx5ValueInc).setData(cweDataTYpe);
                    }
                }
                else
                {
                    Type obxValue = obx.getObservationValue(obx5ValueInc).getData();
                    CWE cwe;
                    if (obxValue instanceof CWE) {
                        cwe = (CWE) obxValue;
                    } else {
                        cwe = new CWE(obx.getMessage());
                    }

                    cwe.getIdentifier().setValue(codedValue);                     // OBX-5.1
                    cwe.getText().setValue(codedValueDescription);               // OBX-5.2
                    cwe.getNameOfCodingSystem().setValue(codedValueCodingSystem); // OBX-5.3

                    cwe.getAlternateIdentifier().setValue(localCodedValue);       // OBX-5.4
                    cwe.getAlternateText().setValue(localCodedValueDescription); // OBX-5.5
                    cwe.getNameOfAlternateCodingSystem().setValue(localCodedValueCodingSystem); // OBX-5.6

                    cwe.getOriginalText().setValue(originalText);                 // OBX-5.9

                    obx.getObservationValue(obx.getObservationValue().length).setData(cwe);

                }
            }
        }
        //CE datatype
        if (messageElement.getDataElement().getQuestionDataTypeNND().equals("CE")){
            String codedValue = "";
            if (messageElement.getDataElement().getCeDataType().getCeCodedValue()!=null){
                codedValue = messageElement.getDataElement().getCeDataType().getCeCodedValue().trim();
            }
            String codedValueDescription = "";
            if (messageElement.getDataElement().getCeDataType().getCeCodedValueDescription()!=null){
                codedValueDescription = messageElement.getDataElement().getCeDataType().getCeCodedValueDescription().trim();
            }
            String codedValueCodingSystem = "";
            if (messageElement.getDataElement().getCeDataType().getCeCodedValueCodingSystem()!=null){
                codedValueCodingSystem = messageElement.getDataElement().getCeDataType().getCeCodedValueCodingSystem().trim();
            }
            String localCodedValue = "";
            if (messageElement.getDataElement().getCeDataType().getCeLocalCodedValue()!=null){
                localCodedValue = messageElement.getDataElement().getCeDataType().getCeLocalCodedValue().trim();
            }
            String localCodedValueCodingSystem = "";
            if (messageElement.getDataElement().getCeDataType().getCeLocalCodedValueCodingSystem()!=null){
                localCodedValueCodingSystem = messageElement.getDataElement().getCeDataType().getCeLocalCodedValueCodingSystem();
            }

            Type obxValue = obx.getObservationValue(obx5ValueInc).getData();
            CE ceDataType;
            if (obxValue instanceof CE) {
                ceDataType = (CE) obxValue;
            } else {
                ceDataType = new CE(obx.getMessage());
            }


            ceDataType.getCe1_Identifier().setValue(codedValue);
            ceDataType.getCe2_Text().setValue(codedValueDescription);
            ceDataType.getCe3_NameOfCodingSystem().setValue(codedValueCodingSystem);
            ceDataType.getCe4_AlternateIdentifier().setValue(localCodedValue);
            ceDataType.getCe6_NameOfAlternateCodingSystem().setValue(localCodedValueCodingSystem);

            obx.getObservationValue(obx5ValueInc).setData(ceDataType);
        }
        //DT datatype
        if (messageElement.getDataElement().getQuestionDataTypeNND().equals("DT")
                && messageState.isINV162RepeatIndicator() && questionIdentifier.equals("INV162"))
        {
            // do nothing as this is a repeat date and we only keep the first date
        }
        else if (messageElement.getDataElement().getQuestionDataTypeNND().equals("DT"))
        {
            DT dtDataType = (obx.getObservationValue(obx5ValueInc).getData() instanceof DT)
                    ? (DT) obx.getObservationValue(obx5ValueInc).getData()
                    : new DT(obx.getMessage());


            if (questionIdentifier.equals("INV162"))
            {
                messageState.setINV162RepeatIndicator(true);
            }
            if (messageElement.getDataElement().getDtDataType().getYear()!=null)
            {
                dtDataType.setValue(messageElement.getDataElement().getDtDataType().getYear().trim());
                obx.getObservationValue(obx5ValueInc).setData(dtDataType);
            }
            else
            {
                int year = messageElement.getDataElement().getDtDataType().getDate().getYear();
                int month = messageElement.getDataElement().getDtDataType().getDate().getMonth();
                int day = messageElement.getDataElement().getDtDataType().getDate().getDay();
                dtDataType.setValue(String.format("%04d%02d%02d", year, month, day));
                obx.getObservationValue(obx5ValueInc).setData(dtDataType);
            }
            //HEP specific code for repeating INV826/INV
            if (questionIdentifierNND.equals("INV826"))
            {
                obx.getObservationSubID().setValue("1");
            }
            if (questionIdentifierNND.equals("INV826b"))
            {
                obx.getObservationSubID().setValue("2");
            }
        }
        //TS data type
        if (messageElement.getDataElement().getQuestionDataTypeNND().equals("TS"))
        {
            String timeOutput = "";
            Type obxValue = obx.getObservationValue(obx5ValueInc).getData();
            TS tsDataType;
            if (obxValue instanceof TS) {
                tsDataType = (TS) obxValue;
            } else {
                tsDataType = new TS(obx.getMessage());
            }

            if (messageElement.getDataElement().getTsDataType().getYear() != null) {
                timeOutput = dateFormatUtil.formatDate(
                        messageElement.getDataElement().getTsDataType().getYear().trim(),
                        questionIdentifierNND,
                        messageState.getMessageType(),
                        messageElement.getDataElement().getQuestionDataTypeNND().trim()
                );
            } else {
                timeOutput = dateFormatUtil.formatDate(
                        messageElement.getDataElement().getTsDataType().getTime().toString(),
                        questionIdentifierNND,
                        messageState.getMessageType(),
                        messageElement.getDataElement().getQuestionDataTypeNND().trim()
                );
            }
            tsDataType.getTs2_DegreeOfPrecision().setValue(timeOutput);
            obx.getObservationValue(obx5ValueInc).setData(tsDataType);
        }
        // NM datatype
        if (messageElement.getDataElement().getQuestionDataTypeNND().equals("NM"))
        {
            Type obxValue = obx.getObservationValue(obx5ValueInc).getData();
            NM nmDataType;
            if (obxValue instanceof NM) {
                nmDataType = (NM) obxValue;
            } else {
                nmDataType = new NM(obx.getMessage());
            }
            nmDataType.setValue(messageElement.getDataElement().getNmDataType().getNum().trim());
            obx.getObservationValue(obx5ValueInc).setData(nmDataType);
        }

        //Literal value "F" specified in messaging spec as ALWAYS being sent here
        obx.getObservationResultStatus().setValue("F");

        String existingObservationIdentifier = obx.getObservationIdentifier().getIdentifier().toString();
        if ((existingObservationIdentifier != null) && (existingObservationIdentifier.equals("2653") ||
                existingObservationIdentifier.equals("3304") ||
                existingObservationIdentifier.equals("6816") ||
                existingObservationIdentifier.equals("N0000166993") ||
                existingObservationIdentifier.equals("PHC1160") ||
                existingObservationIdentifier.equals("PHC1166") ||
                existingObservationIdentifier.equals("PHC1167") ||
                existingObservationIdentifier.equals("PHC1308")))
        {
            messageState.setDrugCounter(messageState.getDrugCounter() + 1);
            String codedText = "";
            String observationValue = obx.getObservationValue(0).toString().trim();
            switch (existingObservationIdentifier) {
                case "2653" -> {
                    codedText = obx.getObservationIdentifier().getIdentifier().getValue().trim() + "^Cocaine^2.16.840.1.113883.6.88";
                    obx.getObservationIdentifier().getIdentifier().setValue(codedText);
                }
                case "3304" -> {
                    codedText = obx.getObservationIdentifier().getIdentifier().getValue().trim() + "^Heroin^2.16.840.1.113883.6.88";
                    obx.getObservationIdentifier().getIdentifier().setValue(codedText);
                }
                case "6816" -> {
                    codedText = obx.getObservationIdentifier().getIdentifier().getValue().trim() + "^Methamphetamines^2.16.840.1.113883.6.88";
                    obx.getObservationIdentifier().getIdentifier().setValue(codedText);
                }
                case "N0000166993" -> {
                    codedText = obx.getObservationIdentifier().getIdentifier().getValue().trim() + "^Crack^2.16.840.1.113883.3.26.1.5";
                    obx.getObservationIdentifier().getIdentifier().setValue(codedText);
                }
                case "PHC1160" -> {
                    codedText = obx.getObservationIdentifier().getIdentifier().getValue().trim() + "^Erectile dysfunction medications (e.g., Viagra)^2.16.840.1.114222.4.5.274";
                    obx.getObservationIdentifier().getIdentifier().setValue(codedText);
                }
                case "PHC1166" -> {
                    codedText = obx.getObservationIdentifier().getIdentifier().getValue().trim() + "^Nitrates/Poppers^2.16.840.1.114222.4.5.274";
                    obx.getObservationIdentifier().getIdentifier().setValue(codedText);
                }
                case "PHC1167" -> {
                    codedText = obx.getObservationIdentifier().getIdentifier().getValue().trim() + "^No drug use reported^2.16.840.1.114222.4.5.274";
                    obx.getObservationIdentifier().getIdentifier().setValue(codedText);
                }
                case "PHC1308" -> {
                    codedText = obx.getObservationIdentifier().getIdentifier().getValue().trim() + "^Other Drugs Used^2.16.840.1.114222.4.5.274";
                    obx.getObservationIdentifier().getIdentifier().setValue(codedText);
                }
            }
            obx.getObservationSubID().setValue("2");
            obx.getObservationIdentifier().getIdentifier().setValue("STD115");
            obx.getObservationIdentifier().getText().setValue("Drugs Used");
            obx.getObservationIdentifier().getNameOfCodingSystem().setValue("2.16.840.1.114222.4.5.232");
            obx.getObservationIdentifier().getAlternateIdentifier().setValue("STD115");
            obx.getObservationIdentifier().getAlternateText().setValue("Drugs Used");
            obx.getObservationIdentifier().getNameOfAlternateCodingSystem().setValue("L");
            obx.getObservationSubID().setValue(String.valueOf(messageState.getDrugCounter()));
            obxInc = obxInc+1;
            messageState.setObx2Inc(messageState.getObx2Inc() + 1);
            obx.getValueType().setValue("CWE");
            obx.getSetIDOBX().setValue(String.valueOf(obxInc+1));
            obx.getObservationSubID().setValue(String.valueOf(messageState.getDrugCounter()));

            Type obxValue = obx.getObservationValue(0).getData();
            ST st;
            if (obxValue instanceof ST) {
                st = (ST) obxValue;
            } else {
                st = new ST(obx.getMessage());
            }

            st.setValue(observationValue);
            obx.getObservationValue(0).setData(st);

            obx.getObservationIdentifier().getIdentifier().setValue("STD116");
            obx.getObservationIdentifier().getText().setValue("Drugs Used Indicator");
            obx.getObservationIdentifier().getNameOfCodingSystem().setValue("2.16.840.1.114222.4.5.232");
            obx.getObservationIdentifier().getAlternateIdentifier().setValue("STD116");
            obx.getObservationIdentifier().getAlternateText().setValue("Drugs Used Indicator");
            obx.getObservationIdentifier().getNameOfAlternateCodingSystem().setValue("L");
            obx.getObservationResultStatus().setValue("F");
        }

        if (!obxFound)
        {
            if (messageElement.getOrderGroupId().equalsIgnoreCase("1")) {
                messageState.setObx1Inc(messageState.getObx1Inc() + 1);
            }
            else {
                messageState.setObx2Inc(messageState.getObx2Inc() + 1);
            }
        }

        // Update state variables before returning
        messageState.setObxOrderGroupID(obxOrderGroupID);
        messageState.setObxInc(obxInc + 1);
        messageState.setObx5ValueInc(obx5ValueInc);
        messageState.setObx5ObservationSubID(obx5ObservationSubID);
        messageState.setObxFound(obxFound);

    }

    public void reset() {
        // No need to reset state here as it's handled by MessageState
    }
}