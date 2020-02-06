/*
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
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
package org.eclipse.ditto.services.thingsearch.persistence.write.streaming;

import static org.assertj.core.api.Java6Assertions.assertThat;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletionStage;

import org.eclipse.ditto.model.base.headers.DittoHeaders;
import org.eclipse.ditto.model.policies.PolicyId;
import org.eclipse.ditto.model.things.ThingId;
import org.eclipse.ditto.services.models.policies.commands.sudo.SudoRetrievePolicyRevision;
import org.eclipse.ditto.services.models.policies.commands.sudo.SudoRetrievePolicyRevisionResponse;
import org.eclipse.ditto.services.thingsearch.persistence.write.model.Metadata;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import akka.testkit.javadsl.TestKit;

/**
 * Tests {@link org.eclipse.ditto.services.thingsearch.persistence.write.streaming.BackgroundSyncStream}.
 */
public final class BackgroundSyncStreamTest {

    private static ActorSystem actorSystem;
    private static ActorMaterializer materializer;

    @BeforeClass
    public static void init() {
        actorSystem = ActorSystem.create();
        materializer = ActorMaterializer.create(actorSystem);
    }

    @AfterClass
    public static void shutdown() {
        if (actorSystem != null) {
            TestKit.shutdownActorSystem(actorSystem);
        }
    }

    @Test
    public void mergeMetadataStreams() {
        final Duration toleranceWindow = Duration.ofHours(1L);

        final Source<Metadata, NotUsed> persisted = Source.from(List.of(
                Metadata.of(ThingId.of("x:0-only-persisted"), 1L, null, 0L),
                Metadata.of(ThingId.of("x:2-within-tolerance"), 3L, null, 0L),
                Metadata.of(ThingId.of("x:3-revision-mismatch"), 3L, "x:3", 0L),
                Metadata.of(ThingId.of("x:4-policy-id-mismatch"), 3L, "x:4", 0L),
                Metadata.of(ThingId.of("x:5-policy-revision-mismatch"), 3L, "x:5", 0L),
                Metadata.of(ThingId.of("x:6-all-up-to-date"), 3L, "x:6", 0L)
        ));

        final Source<Metadata, NotUsed> indexed = Source.from(List.of(
                Metadata.of(ThingId.of("x:1-only-indexed"), 1L, null, 0L),
                Metadata.of(ThingId.of("x:2-within-tolerance"), 1L, null, 0L, Instant.now()),
                Metadata.of(ThingId.of("x:3-revision-mismatch"), 2L, "x:3", 1L),
                Metadata.of(ThingId.of("x:4-policy-id-mismatch"), 3L, "x:mismatched", 0L),
                Metadata.of(ThingId.of("x:5-policy-revision-mismatch"), 3L, "x:5", 3L),
                Metadata.of(ThingId.of("x:6-all-up-to-date"), 5L, "x:6", 6L)
        ));

        new TestKit(actorSystem) {{
            final BackgroundSyncStream underTest =
                    BackgroundSyncStream.of(getRef(), Duration.ofSeconds(3L), toleranceWindow, 100,
                            Duration.ofSeconds(10L));
            final CompletionStage<List<String>> inconsistentThingIds =
                    underTest.filterForInconsistencies(persisted, indexed)
                            .map(metadata -> metadata.getThingId().toString())
                            .runWith(Sink.seq(), materializer);

            expectMsg(SudoRetrievePolicyRevision.of(PolicyId.of("x:5"), DittoHeaders.empty()));
            reply(SudoRetrievePolicyRevisionResponse.of(PolicyId.of("x:5"), 6L, DittoHeaders.empty()));

            expectMsg(SudoRetrievePolicyRevision.of(PolicyId.of("x:6"), DittoHeaders.empty()));
            reply(SudoRetrievePolicyRevisionResponse.of(PolicyId.of("x:6"), 6L, DittoHeaders.empty()));

            assertThat(inconsistentThingIds.toCompletableFuture().join()).containsExactly(
                    "x:0-only-persisted",
                    "x:1-only-indexed",
                    "x:3-revision-mismatch",
                    "x:4-policy-id-mismatch",
                    "x:5-policy-revision-mismatch"
            );
        }};
    }
}
