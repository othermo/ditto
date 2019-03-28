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
package org.eclipse.ditto.model.base.json;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.atteo.classindex.IndexAnnotated;

/**
 * This annotated marks a class as deserializable from Json when calling the specified
 * {@link JsonParsableException#method()} with {@link org.eclipse.ditto.json.JsonObject} as first
 * and {@link org.eclipse.ditto.model.base.headers.DittoHeaders} as second argument.
 * The {@link JsonParsableException#errorCode()} is used as identifier of this exception.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@IndexAnnotated
public @interface JsonParsableException {

    /**
     * Used as identifier of the exception.
     *
     * @return the error code.
     */
    String errorCode();

    /**
     * The name of the method accepting a {@link org.eclipse.ditto.json.JsonObject} as first argument and
     * {@link org.eclipse.ditto.model.base.headers.DittoHeaders} as seconds argument.
     * The Method must return an instance of the exception annotated with this annotation.
     *
     * @return the name of this method.
     */
    String method() default "fromJson";

}