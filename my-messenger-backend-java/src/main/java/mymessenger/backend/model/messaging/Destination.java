/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymessenger.backend.model.messaging;

import org.springframework.util.StringUtils;

/**
 *
 * @author guilherme
 */
public class Destination {
    
    private DestinationType type;
    private String id;

    public Destination(DestinationType type, String id) {
        this.type = type;
        this.id = id;
    }

    public Destination() {
    }
    
    public DestinationType getType() {
        return type;
    }

    public void setType(DestinationType type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Destination{" + "type=" + type + ", id=" + id + '}';
    }
}
