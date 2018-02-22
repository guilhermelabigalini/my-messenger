package my.messenger.androidclient.api.model;

/**
 * Created by guilherme on 1/26/2018.
 */

public class Destination {
    private String id;
    private DestinationType type;

    public Destination() {
    }

    public Destination(String id, DestinationType type) {
        this.id = id;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DestinationType getType() {
        return type;
    }

    public void setType(DestinationType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Destination{" +
                "id='" + id + '\'' +
                ", type=" + type +
                '}';
    }
}
