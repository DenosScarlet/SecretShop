package com.secretshop.keycloak.custom;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.keycloak.representations.idm.GroupRepresentation;

import java.io.IOException;

public class CustomGroupDeserializer extends StdDeserializer<GroupRepresentation> {

    public CustomGroupDeserializer() {
        super(GroupRepresentation.class);
    }

    @Override
    public GroupRepresentation deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        // Пропустить неизвестные поля
        return p.readValueAs(GroupRepresentation.class);
    }
}


