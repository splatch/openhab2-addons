/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.lutron.internal.mapping;

import java.util.Optional;
import java.util.stream.Stream;

import org.eclipse.smarthome.core.thing.ChannelUID;

/**
 * Interface for keeping lutron mappings.
 *
 * @author Łukasz Dywicki
 */
public interface MappingSet {

    Optional<ChannelMapping> fromChannel(ChannelUID id);

    Optional<ChannelMapping> fromComponent(int component);

    Stream<ChannelMapping> all();

}