package client;

import stored.LabWork;

import java.util.Vector;

public class ClientCollectionManager {
    private static Vector<LabWork> clientCollection = new Vector<>();

    public static Vector<LabWork> getClientCollection() {
        return clientCollection;
    }

    public static void setClientCollection(Vector<LabWork> collection) {
        clientCollection = collection;
    }
}
