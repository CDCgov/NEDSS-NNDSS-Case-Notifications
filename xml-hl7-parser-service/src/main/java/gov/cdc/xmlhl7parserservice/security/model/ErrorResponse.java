package gov.cdc.xmlhl7parserservice.security.model;

import lombok.Getter;
import lombok.Setter;


/**
 * <ul>
 *     <li>1118 - require constructor complaint</li>
 *     <li>125 - comment complaint</li>
 *     <li>6126 - String block complaint</li>
 *     <li>1135 - todos complaint</li>
 * </ul>
 */
@Getter
@Setter
@SuppressWarnings({"java:S1118", "java:S125", "java:S6126", "java:S1135"})
public class ErrorResponse {
  private int statusCode;
  private String message;
  private String details;
}
