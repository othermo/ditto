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
package org.eclipse.ditto.model.base.entity.id;

import java.net.URI;
import java.text.MessageFormat;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;

import org.eclipse.ditto.json.JsonObject;
import org.eclipse.ditto.model.base.common.HttpStatusCode;
import org.eclipse.ditto.model.base.exceptions.DittoRuntimeException;
import org.eclipse.ditto.model.base.exceptions.DittoRuntimeExceptionBuilder;
import org.eclipse.ditto.model.base.headers.DittoHeaders;
import org.eclipse.ditto.model.base.json.JsonParsableException;

/**
 * Thrown if the Thing's ID is not valid (for example if it does not comply to the Thing ID REGEX).
 */
@Immutable
@JsonParsableException(errorCode = EntityNameInvalidException.ERROR_CODE)
public final class EntityNameInvalidException extends DittoRuntimeException {

    /**
     * Error code of this exception.
     */
    public static final String ERROR_CODE = "entity.name.invalid";

    private static final String MESSAGE_TEMPLATE = "Entity name ''{0}'' is not valid!";

    private static final String DEFAULT_DESCRIPTION = "The name must be a valid URI path segment according to RFC-2396";

    private static final long serialVersionUID = -2696854713409772158L;

    private EntityNameInvalidException(final DittoHeaders dittoHeaders,
            @Nullable final String message,
            @Nullable final String description,
            @Nullable final Throwable cause,
            @Nullable final URI href) {
        super(ERROR_CODE, HttpStatusCode.BAD_REQUEST, dittoHeaders, message, description, cause, href);
    }

    /**
     * A mutable builder for a {@code EntityNameInvalidException}.
     *
     * @param entityName the name of the entity.
     * @return the builder.
     */
    public static Builder forEntityName(final CharSequence entityName) {
        return new Builder(entityName);
    }

    /**
     * Constructs a new {@code EntityNameInvalidException} object with the exception message extracted from the
     * given JSON object.
     *
     * @param jsonObject the JSON to read the {@link JsonFields#MESSAGE} field from.
     * @param dittoHeaders the headers of the command which resulted in this exception.
     * @return the new ThingIdInvalidException.
     * @throws org.eclipse.ditto.json.JsonMissingFieldException if the {@code jsonObject} does not have the {@link JsonFields#MESSAGE} field.
     */
    public static EntityNameInvalidException fromJson(final JsonObject jsonObject, final DittoHeaders dittoHeaders) {
        return new Builder()
                .dittoHeaders(dittoHeaders)
                .message(readMessage(jsonObject))
                .description(readDescription(jsonObject).orElse(DEFAULT_DESCRIPTION))
                .href(readHRef(jsonObject).orElse(null))
                .build();
    }

    /**
     * A mutable builder with a fluent API for a {@link EntityNameInvalidException}.
     */
    @NotThreadSafe
    public static final class Builder extends DittoRuntimeExceptionBuilder<EntityNameInvalidException> {

        private Builder() {
            description(DEFAULT_DESCRIPTION);
        }

        private Builder(final CharSequence entityName) {
            this();
            message(MessageFormat.format(MESSAGE_TEMPLATE, entityName));
        }

        @Override
        protected EntityNameInvalidException doBuild(final DittoHeaders dittoHeaders,
                @Nullable final String message,
                @Nullable final String description,
                @Nullable final Throwable cause,
                @Nullable final URI href) {
            return new EntityNameInvalidException(dittoHeaders, message, description, cause, href);
        }
    }

}
