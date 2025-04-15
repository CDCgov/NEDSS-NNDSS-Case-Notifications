package gov.cdc.nonstdprocessorservice.non_std.cache;

import gov.cdc.nonstdprocessorservice.non_std.model.PHINMSProperties;

import java.util.ArrayList;
import java.util.List;

public class NonStdQueue {
    public List<PHINMSProperties> phinmsPropertiesList = new ArrayList<>();
    private static final NonStdQueue INSTANCE = new NonStdQueue();

    private NonStdQueue() {
        // private constructor to prevent instantiation
    }

    public static NonStdQueue getInstance() {
        return INSTANCE;
    }


    public void addPHINMSProperties(PHINMSProperties phinmsProperties) {
        phinmsPropertiesList.add(phinmsProperties);
    }

    public void clearPHINMSProperties() {
        phinmsPropertiesList.clear();
    }


    public List<PHINMSProperties> getPhinmsPropertiesList() {
        return new ArrayList<>(phinmsPropertiesList); // defensive copy
    }
}
