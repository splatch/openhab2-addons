/**
 * Copyright (c) 2010-2020 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.tacmi.internal.schema;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.attoparser.ParseException;
import org.attoparser.config.ParseConfiguration;
import org.attoparser.config.ParseConfiguration.ElementBalancing;
import org.attoparser.config.ParseConfiguration.UniqueRootElementPresence;
import org.attoparser.simple.AbstractSimpleMarkupHandler;
import org.attoparser.simple.ISimpleMarkupParser;
import org.attoparser.simple.SimpleMarkupParser;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.util.B64Code;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.thing.binding.builder.ThingBuilder;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.eclipse.smarthome.core.types.State;
import org.openhab.binding.tacmi.internal.TACmiChannelTypeProvider;
import org.openhab.binding.tacmi.internal.TACmiHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link TACmiHandler} is responsible for handling commands, which are sent
 * to one of the channels.
 *
 * @author Christian Niessner - Initial contribution
 */
@NonNullByDefault
public class TACmiSchemaHandler extends BaseThingHandler {

    private final Logger logger = LoggerFactory.getLogger(TACmiSchemaHandler.class);

    private final HttpClient httpClient;
    private final TACmiChannelTypeProvider channelTypeProvider;
    private final Map<String, @Nullable ApiPageEntry> entries = new HashMap<>();
    private boolean online;
    private @Nullable String serverBase;
    private @Nullable URI schemaApiPage;
    private @Nullable String authHeader;
    private @Nullable ScheduledFuture<?> scheduledFuture;
    private final ParseConfiguration noRestrictions;

    public TACmiSchemaHandler(final Thing thing, final HttpClient httpClient,
            final TACmiChannelTypeProvider channelTypeProvider) {
        super(thing);
        this.httpClient = httpClient;
        this.channelTypeProvider = channelTypeProvider;

        // the default configuration for the parser
        this.noRestrictions = ParseConfiguration.xmlConfiguration();
        this.noRestrictions.setElementBalancing(ElementBalancing.NO_BALANCING);
        this.noRestrictions.setNoUnmatchedCloseElementsRequired(false);
        this.noRestrictions.setUniqueAttributesInElementRequired(false);
        this.noRestrictions.setXmlWellFormedAttributeValuesRequired(false);
        this.noRestrictions.setUniqueRootElementPresence(UniqueRootElementPresence.NOT_VALIDATED);
        this.noRestrictions.getPrologParseConfiguration().setValidateProlog(false);
    }

    @Override
    public void initialize() {
        final TACmiSchemaConfiguration config = getConfigAs(TACmiSchemaConfiguration.class);

        if (config.host.trim().isEmpty()) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, "No host configured!");
            return;
        }
        if (config.username.trim().isEmpty()) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, "No username configured!");
            return;
        }
        if (config.password.trim().isEmpty()) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, "No password configured!");
            return;
        }
        this.online = false;
        updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_PENDING);

        this.authHeader = "Basic "
                + B64Code.encode(config.username + ":" + config.password, StandardCharsets.ISO_8859_1);

        final String serverBase = "http://" + config.host + "/";
        this.serverBase = serverBase;
        this.schemaApiPage = buildUri("schematic_files/" + config.schemaId + ".cgi");

        refreshData();
        if (config.pollInterval <= 0) {
            config.pollInterval = 10;
        }
        this.scheduledFuture = scheduler.scheduleWithFixedDelay(this::refreshData, config.pollInterval,
                config.pollInterval, TimeUnit.SECONDS);
    }

    protected URI buildUri(String path) {
        return URI.create(serverBase + path);
    }

    private Request prepareRequest(final URI uri) {
        final Request req = httpClient.newRequest(uri).method(HttpMethod.GET).timeout(10000, TimeUnit.MILLISECONDS);
        req.header(HttpHeader.ACCEPT_LANGUAGE, "en"); // we want the on/off states in english
        final String ah = this.authHeader;
        if (ah != null) {
            req.header(HttpHeader.AUTHORIZATION, ah);
        }
        return req;
    }

    protected <PP extends AbstractSimpleMarkupHandler> PP parsePage(URI uri, PP pp)
            throws ParseException, InterruptedException, TimeoutException, ExecutionException {
        final ContentResponse response = prepareRequest(uri).send();

        String responseString = null;
        String encoding = response.getEncoding();
        if (encoding == null || encoding.trim().isEmpty()) {
            // the C.M.I. dosn't sometime return a valid encoding - but it defaults to UTF-8 instead of ISO...
            responseString = new String(response.getContent(), StandardCharsets.UTF_8);
        } else {
            responseString = response.getContentAsString();
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Response body was: {} ", responseString);
        }

        final ISimpleMarkupParser parser = new SimpleMarkupParser(this.noRestrictions);
        parser.parse(responseString, pp);
        return pp;
    }

    private void refreshData() {
        URI schemaApiPage = this.schemaApiPage;
        if (schemaApiPage == null) {
            return;
        }
        try {
            final ApiPageParser pp = parsePage(schemaApiPage,
                    new ApiPageParser(this, entries, this.channelTypeProvider));

            if (pp.isConfigChanged()) {
                // we have to update our channels...
                final List<Channel> channels = pp.getChannels();
                final ThingBuilder thingBuilder = editThing();
                thingBuilder.withChannels(channels);
                updateThing(thingBuilder.build());
            }
            if (!this.online) {
                updateStatus(ThingStatus.ONLINE);
                this.online = true;
            }
        } catch (final InterruptedException e) {
            // binding shutdown is in progress
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.NONE);
            this.online = false;
        } catch (final ParseException | TimeoutException | ExecutionException | RuntimeException e) {
            // we need the stack trace here to get an idea what happened when this happens to somebody and we try to
            // troubleshoot this
            logger.debug("Error loading API Scheme: {} ", e.getMessage(), e);
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, "Error: " + e.getMessage());
            this.online = false;
        }
    }

    @Override
    public void handleCommand(final ChannelUID channelUID, final Command command) {
        if (command instanceof RefreshType) {
            // TODO how to debounce this? we could trigger refreshData() but during startup
            // this issues lots of requests... :-/
            return;
        }
        final ApiPageEntry e = this.entries.get(channelUID.getId());
        if (e == null) {
            logger.warn("Got command for unknown channel {}: {}", channelUID, command);
            return;
        }
        final Request reqUpdate;
        switch (e.type) {
            case SWITCH_BUTTON:
                reqUpdate = prepareRequest(buildUri("INCLUDE/change.cgi?changeadrx2=" + e.address + "&changetox2="
                        + (command == OnOffType.ON ? "1" : "0")));
                reqUpdate.header(HttpHeader.REFERER, this.serverBase + "schema.html"); // required...
                break;
            case SWITCH_FORM:
                ChangerX2Entry cx2e = e.changerX2Entry;
                if (cx2e != null) {
                    reqUpdate = prepareRequest(buildUri("INCLUDE/change.cgi?changeadrx2=" + cx2e.address
                            + "&changetox2=" + (command == OnOffType.ON ? "1" : "0")));
                    reqUpdate.header(HttpHeader.REFERER, this.serverBase + "schema.html"); // required...
                } else {
                    logger.warn("Got command for uninitalized channel {}: {}", channelUID, command);
                    return;
                }
                break;
            case STATE_FORM:
                ChangerX2Entry cx2sf = e.changerX2Entry;
                if (cx2sf != null) {
                    String val = cx2sf.options.get(((StringType) command).toFullString());
                    if (val != null) {
                        reqUpdate = prepareRequest(
                                buildUri("INCLUDE/change.cgi?changeadrx2=" + cx2sf.address + "&changetox2=" + val));
                        reqUpdate.header(HttpHeader.REFERER, this.serverBase + "schema.html"); // required...
                    } else {
                        logger.warn("Got unknown form command {} for channel {}; Valid commands are: {}", command,
                                channelUID, cx2sf.options.keySet());
                        return;
                    }
                } else {
                    logger.warn("Got command for uninitalized channel {}: {}", channelUID, command);
                    return;
                }
                break;
            case READ_ONLY_NUMERIC:
            case READ_ONLY_STATE:
            case READ_ONLY_SWITCH:
                logger.warn("Got command for ReadOnly channel {}: {}", channelUID, command);
                return;
            default:
                logger.warn("Got command for unhandled type {} channel {}: {}", e.type, channelUID, command);
                return;
        }
        try {
            ContentResponse res = reqUpdate.send();
            if (res.getStatus() == 200) {
                // update ok, we update the state
                updateState(channelUID, (State) command);
            } else {
                logger.error("Error sending update for {} = {}: {} {}", channelUID, command, res.getStatus(),
                        res.getReason());
            }
        } catch (InterruptedException | TimeoutException | ExecutionException ex) {
            logger.error("Error sending update for {} = {}: {}", channelUID, command, ex.getMessage(), ex);
        }
    }

    // make it accessible for ApiPageParser
    @Override
    protected void updateState(final ChannelUID channelUID, final State state) {
        super.updateState(channelUID, state);
    }

    @Override
    public void dispose() {
        final ScheduledFuture<?> scheduledFuture = this.scheduledFuture;
        if (scheduledFuture != null) {
            try {
                scheduledFuture.cancel(true);
                this.scheduledFuture = null;
            } catch (final Exception e) {
                // swallow this
            }
        }
        super.dispose();
    }
}
