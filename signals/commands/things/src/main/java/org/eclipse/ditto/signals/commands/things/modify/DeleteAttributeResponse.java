/*
 * Copyright (c) 2017 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.ditto.signals.commands.things.modify;

import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.function.Predicate;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.eclipse.ditto.json.JsonFactory;
import org.eclipse.ditto.json.JsonField;
import org.eclipse.ditto.json.JsonFieldDefinition;
import org.eclipse.ditto.json.JsonObject;
import org.eclipse.ditto.json.JsonObjectBuilder;
import org.eclipse.ditto.json.JsonPointer;
import org.eclipse.ditto.model.base.common.HttpStatusCode;
import org.eclipse.ditto.model.base.headers.DittoHeaders;
import org.eclipse.ditto.model.base.json.FieldType;
import org.eclipse.ditto.model.base.json.JsonParsableCommandResponse;
import org.eclipse.ditto.model.base.json.JsonSchemaVersion;
import org.eclipse.ditto.model.things.id.ThingId;
import org.eclipse.ditto.signals.commands.base.AbstractCommandResponse;
import org.eclipse.ditto.signals.commands.base.CommandResponseJsonDeserializer;

/**
 * Response to a {@link DeleteAttribute} command.
 */
@Immutable
@JsonParsableCommandResponse(type = DeleteAttributeResponse.TYPE)
public final class DeleteAttributeResponse extends AbstractCommandResponse<DeleteAttributeResponse> implements
        ThingModifyCommandResponse<DeleteAttributeResponse> {

    /**
     * Type of this response.
     */
    public static final String TYPE = TYPE_PREFIX + DeleteAttribute.NAME;

    static final JsonFieldDefinition<String> JSON_ATTRIBUTE =
            JsonFactory.newStringFieldDefinition("attribute", FieldType.REGULAR, JsonSchemaVersion.V_1,
                    JsonSchemaVersion.V_2);

    private final ThingId thingId;
    private final JsonPointer attributePointer;

    private DeleteAttributeResponse(final ThingId thingId, final JsonPointer attributePointer,
            final DittoHeaders dittoHeaders) {
        super(TYPE, HttpStatusCode.NO_CONTENT, dittoHeaders);
        this.thingId = requireNonNull(thingId, "thing ID");
        this.attributePointer = requireNonNull(attributePointer, "attribute pointer");
    }

    /**
     * Creates a response to a {@link DeleteAttribute} command.
     *
     * @param thingId the Thing ID of the deleted attribute.
     * @param attributePointer the JSON pointer of the deleted attribute.
     * @param dittoHeaders the headers of the preceding command.
     * @return the response.
     * @throws NullPointerException if {@code statusCode} or {@code dittoHeaders} is {@code null}.
     */
    public static DeleteAttributeResponse of(final ThingId thingId, final JsonPointer attributePointer,
            final DittoHeaders dittoHeaders) {
        return new DeleteAttributeResponse(thingId, attributePointer, dittoHeaders);
    }

    /**
     * Creates a response to a {@link DeleteAttribute} command from a JSON string.
     *
     * @param jsonString the JSON string of which the response is to be created.
     * @param dittoHeaders the headers of the preceding command.
     * @return the response.
     * @throws NullPointerException if any argument is {@code null}.
     * @throws IllegalArgumentException if {@code jsonString} is empty.
     * @throws org.eclipse.ditto.json.JsonParseException if the passed in {@code jsonString} was not in the expected
     * format.
     */
    public static DeleteAttributeResponse fromJson(final String jsonString, final DittoHeaders dittoHeaders) {
        return fromJson(JsonFactory.newObject(jsonString), dittoHeaders);
    }

    /**
     * Creates a response to a {@link DeleteAttribute} command from a JSON object.
     *
     * @param jsonObject the JSON object of which the response is to be created.
     * @param dittoHeaders the headers of the preceding command.
     * @return the response.
     * @throws NullPointerException any argument is {@code null}.
     * @throws org.eclipse.ditto.json.JsonParseException if the passed in {@code jsonObject} was not in the expected
     * format.
     */
    public static DeleteAttributeResponse fromJson(final JsonObject jsonObject, final DittoHeaders dittoHeaders) {
        return new CommandResponseJsonDeserializer<DeleteAttributeResponse>(TYPE, jsonObject)
                .deserialize((statusCode) -> {
                    final String extractedThingId =
                            jsonObject.getValueOrThrow(ThingModifyCommandResponse.JsonFields.JSON_THING_ID);
                    final ThingId thingId = ThingId.of(extractedThingId);
                    final String extractedPointerString = jsonObject.getValueOrThrow(JSON_ATTRIBUTE);
                    final JsonPointer extractedPointer = JsonFactory.newPointer(extractedPointerString);

                    return of(thingId, extractedPointer, dittoHeaders);
                });
    }

    @Override
    public ThingId getThingEntityId() {
        return thingId;
    }

    /**
     * Returns the JSON pointer of the deleted attribute.
     *
     * @return the pointer.
     */
    public JsonPointer getAttributePointer() {
        return attributePointer;
    }

    @Override
    public JsonPointer getResourcePath() {
        return JsonFactory.newPointer("/attributes" + attributePointer);
    }

    @Override
    protected void appendPayload(final JsonObjectBuilder jsonObjectBuilder, final JsonSchemaVersion schemaVersion,
            final Predicate<JsonField> thePredicate) {
        final Predicate<JsonField> predicate = schemaVersion.and(thePredicate);
        jsonObjectBuilder.set(ThingModifyCommandResponse.JsonFields.JSON_THING_ID, thingId.toString(), predicate);
        jsonObjectBuilder.set(JSON_ATTRIBUTE, attributePointer.toString(), predicate);
    }

    @Override
    public DeleteAttributeResponse setDittoHeaders(final DittoHeaders dittoHeaders) {
        return of(thingId, attributePointer, dittoHeaders);
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final DeleteAttributeResponse that = (DeleteAttributeResponse) o;
        return that.canEqual(this) && super.equals(o) && Objects.equals(thingId, that.thingId)
                && Objects.equals(attributePointer, that.attributePointer);
    }

    @Override
    protected boolean canEqual(@Nullable final Object other) {
        return (other instanceof DeleteAttributeResponse);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), thingId, attributePointer);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" + super.toString() + ", thingId=" + thingId
                + ", attributePointer=" + attributePointer + "]";
    }

}
