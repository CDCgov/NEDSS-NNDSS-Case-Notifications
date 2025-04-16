//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.1 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2025.04.15 at 12:17:27 PM EDT 
//


package gov.cdc.xmlhl7parserservice.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for LabReportEvent complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LabReportEvent"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ResultedTest" maxOccurs="unbounded"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="LabReportEvent" type="{http://www.cdc.gov/NEDSS}LabReportEvent" maxOccurs="unbounded" minOccurs="0"/&gt;
 *                   &lt;element name="MessageElement" type="{http://www.cdc.gov/NEDSS}MessageElement" maxOccurs="unbounded"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="MessageElement" type="{http://www.cdc.gov/NEDSS}MessageElement" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LabReportEvent", propOrder = {
    "resultedTest",
    "messageElement"
})
public class LabReportEvent {

    @XmlElement(name = "ResultedTest", required = true)
    protected List<LabReportEvent.ResultedTest> resultedTest;
    @XmlElement(name = "MessageElement", required = true)
    protected List<MessageElement> messageElement;

    /**
     * Gets the value of the resultedTest property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the resultedTest property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getResultedTest().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LabReportEvent.ResultedTest }
     * 
     * 
     */
    public List<LabReportEvent.ResultedTest> getResultedTest() {
        if (resultedTest == null) {
            resultedTest = new ArrayList<>();
        }
        return this.resultedTest;
    }

    /**
     * Gets the value of the messageElement property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the messageElement property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMessageElement().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MessageElement }
     * 
     * 
     */
    public List<MessageElement> getMessageElement() {
        if (messageElement == null) {
            messageElement = new ArrayList<>();
        }
        return this.messageElement;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;sequence&gt;
     *         &lt;element name="LabReportEvent" type="{http://www.cdc.gov/NEDSS}LabReportEvent" maxOccurs="unbounded" minOccurs="0"/&gt;
     *         &lt;element name="MessageElement" type="{http://www.cdc.gov/NEDSS}MessageElement" maxOccurs="unbounded"/&gt;
     *       &lt;/sequence&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "labReportEvent",
        "messageElement"
    })
    public static class ResultedTest {

        @XmlElement(name = "LabReportEvent")
        protected List<LabReportEvent> labReportEvent;
        @XmlElement(name = "MessageElement", required = true)
        protected List<MessageElement> messageElement;

        /**
         * Gets the value of the labReportEvent property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the labReportEvent property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getLabReportEvent().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link LabReportEvent }
         * 
         * 
         */
        public List<LabReportEvent> getLabReportEvent() {
            if (labReportEvent == null) {
                labReportEvent = new ArrayList<>();
            }
            return this.labReportEvent;
        }

        /**
         * Gets the value of the messageElement property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the messageElement property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getMessageElement().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link MessageElement }
         * 
         * 
         */
        public List<MessageElement> getMessageElement() {
            if (messageElement == null) {
                messageElement = new ArrayList<>();
            }
            return this.messageElement;
        }

    }

}
