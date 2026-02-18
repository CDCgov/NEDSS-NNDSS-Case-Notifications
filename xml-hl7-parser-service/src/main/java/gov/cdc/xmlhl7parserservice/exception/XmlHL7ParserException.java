package gov.cdc.xmlhl7parserservice.exception;

/**
 * <ul>
 *     <li>1118 - require constructor complaint</li>
 *     <li>125 - comment complaint</li>
 *     <li>6126 - String block complaint</li>
 *     <li>1135 - todos complaint</li>
 * </ul>
 * */
@SuppressWarnings({"java:S1118", "java:S125", "java:S6126", "java:S1135"})
public class XmlHL7ParserException extends Exception {

    public XmlHL7ParserException(String message) {
        super(message);
    }
}
