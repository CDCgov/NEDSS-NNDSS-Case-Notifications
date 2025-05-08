package gov.cdc.xmlhl7parserservice.model;

public class EIElement {
    private String entityIdentifier;
    private String namespaceID;
    private String universalID;
    private String universalIDType;

    public EIElement() {}

    public EIElement(String entityIdentifier, String namespaceID, String universalID, String universalIDType) {
        this.entityIdentifier = entityIdentifier;
        this.namespaceID = namespaceID;
        this.universalID = universalID;
        this.universalIDType = universalIDType;
    }

    public String getEntityIdentifier() {
        return entityIdentifier;
    }

    public void setEntityIdentifier(String entityIdentifier) {
        this.entityIdentifier = entityIdentifier;
    }

    public String getNamespaceID() {
        return namespaceID;
    }

    public void setNamespaceID(String namespaceID) {
        this.namespaceID = namespaceID;
    }

    public String getUniversalID() {
        return universalID;
    }

    public void setUniversalID(String universalID) {
        this.universalID = universalID;
    }

    public String getUniversalIDType() {
        return universalIDType;
    }

    public void setUniversalIDType(String universalIDType) {
        this.universalIDType = universalIDType;
    }
}

