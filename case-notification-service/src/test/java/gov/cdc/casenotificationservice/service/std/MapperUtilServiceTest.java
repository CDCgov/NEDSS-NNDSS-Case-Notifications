package gov.cdc.casenotificationservice.service.std;

import gov.cdc.casenotificationservice.model.generated.jaxb.MessageElement;
import gov.cdc.casenotificationservice.repository.msg.LookupMmwrRepository;
import gov.cdc.casenotificationservice.repository.msg.LookupNNDLookupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.GregorianCalendar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class MapperUtilServiceTest {

    @Mock
    private LookupNNDLookupRepository lookupNNDLookupRepository;

    @Mock
    private LookupMmwrRepository lookupMmwrRepository;

    @InjectMocks
    private MapperUtilService mapperUtilService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testMapToCodedAnswer() {
        when(lookupNNDLookupRepository.findToCodeByFromUniqueIdAndConceptCd("QCODE", "input"))
                .thenReturn("mappedValue");
        String result = mapperUtilService.mapToCodedAnswer("input", "QCODE");
        assertEquals("mappedValue", result);
    }

    @Test
    void testMapToData_CWE() {
        MessageElement.DataElement dataElement = new MessageElement.DataElement();
        dataElement.setQuestionDataTypeNND("CWE");
        var cwe = new MessageElement.DataElement.CweDataType();
        cwe.setCweCodedValue("cweValue");
        dataElement.setCweDataType(cwe);
        assertEquals("cweValue", mapperUtilService.mapToData(dataElement));
    }

    @Test
    void testMapToData_DifferentTypes() throws DatatypeConfigurationException {
        assertEquals("ceValue", mapperUtilService.mapToData(createDataElement("CE", "ceValue")));
        assertEquals("cxData", mapperUtilService.mapToData(createDataElement("CX", "cxData")));
        assertEquals("isValue", mapperUtilService.mapToData(createDataElement("IS", "isValue")));
        assertEquals("textData", mapperUtilService.mapToData(createDataElement("TX", "textData")));
        assertEquals("stringData", mapperUtilService.mapToData(createDataElement("ST", "stringData")));
        assertEquals("123", mapperUtilService.mapToData(createDataElement("SN", "123")));

        // TS type
        var tsElement = new MessageElement.DataElement();
        tsElement.setQuestionDataTypeNND("TS");
        var ts = new MessageElement.DataElement.TsDataType();
        LocalDateTime ldt = LocalDateTime.of(2024, 4, 28, 12, 0);
        GregorianCalendar gregorianCalendar = GregorianCalendar.from(ldt.atZone(ZoneId.systemDefault()));
        XMLGregorianCalendar xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);

        ts.setTime(xmlGregorianCalendar);
        tsElement.setTsDataType(ts);
        assertEquals("2024-04-28T12:00:00.000-07:00", mapperUtilService.mapToData(tsElement));

        // DT type
        var dtElement = new MessageElement.DataElement();
        dtElement.setQuestionDataTypeNND("DT");
        var dt = new MessageElement.DataElement.DtDataType();
        dt.setYear("2024");
        dtElement.setDtDataType(dt);
        assertEquals("2024", mapperUtilService.mapToData(dtElement));
    }

    @Test
    void testMapToDate() {
        when(lookupMmwrRepository.findTopWeekEndingByMmwrWeekAndMmwrYear(10, 2024))
                .thenReturn(Date.valueOf(LocalDate.of(2024, 1, 1)));
        String result = mapperUtilService.mapToDate("10", "2024", "");
        assertEquals("2024-01-01", result);
    }

    @Test
    void testMapToMultiCodedAnswer_Checker() {
        when(lookupNNDLookupRepository.findToCodeByFromUniqueIdAndConceptCd("QCODE", "input"))
                .thenReturn("mappedChecker");
        String result = mapperUtilService.mapToMultiCodedAnswer("input", "QCODE", "toUniqueId", "CHECKER");
        assertEquals("mappedChecker", result);
    }

    @Test
    void testMapToMultiCodedAnswer_Normal() {
        when(lookupNNDLookupRepository.findToCodeByFromUniqueIdToUniqueIdAndConceptCd("QCODE", "toUniqueId", "input"))
                .thenReturn("mappedMulti");
        String result = mapperUtilService.mapToMultiCodedAnswer("input", "QCODE", "toUniqueId", "nonChecker");
        assertEquals("mappedMulti", result);
    }

    @Test
    void testMapToStringValue() {
        assertEquals("ceValue", mapperUtilService.mapToStringValue(createDataElement("CE", "ceValue")));
        assertEquals("cweValue", mapperUtilService.mapToStringValue(createDataElement("CWE", "cweValue")));
        assertEquals("cxData", mapperUtilService.mapToStringValue(createDataElement("CX", "cxData")));
        assertEquals("isValue", mapperUtilService.mapToStringValue(createDataElement("IS", "isValue")));
        assertEquals("textData", mapperUtilService.mapToStringValue(createDataElement("TX", "textData")));
        assertEquals("stringData", mapperUtilService.mapToStringValue(createDataElement("ST", "stringData")));
    }

    @Test
    void testMapToTsType() {
        assertEquals("20240405", mapperUtilService.mapToTsType("4/5/2024", ""));
        assertEquals("20240405", mapperUtilService.mapToTsType("2024-04-05", ""));
        assertEquals("202404", mapperUtilService.mapToTsType("202404", ""));
    }

    private MessageElement.DataElement createDataElement(String type, String value) {
        var data = new MessageElement.DataElement();
        data.setQuestionDataTypeNND(type);

        switch (type) {
            case "CWE" -> {
                var cwe = new MessageElement.DataElement.CweDataType();
                cwe.setCweCodedValue(value);
                data.setCweDataType(cwe);
            }
            case "CE" -> {
                var ce = new MessageElement.DataElement.CeDataType();
                ce.setCeCodedValue(value);
                data.setCeDataType(ce);
            }
            case "CX" -> {
                var cx = new MessageElement.DataElement.CxDataType();
                cx.setCxData(value);
                data.setCxDataType(cx);
            }
            case "IS" -> {
                var isType = new MessageElement.DataElement.IsDataType();
                isType.setIsCodedValue(value);
                data.setIsDataType(isType);
            }
            case "TX" -> {
                var tx = new MessageElement.DataElement.TxDataType();
                tx.setTextData(value);
                data.setTxDataType(tx);
            }
            case "ST" -> {
                var st = new MessageElement.DataElement.StDataType();
                st.setStringData(value);
                data.setStDataType(st);
            }
            case "SN" -> {
                var sn = new MessageElement.DataElement.SnDataType();
                sn.setNum1(value);
                data.setSnDataType(sn);
            }
        }

        return data;
    }
}