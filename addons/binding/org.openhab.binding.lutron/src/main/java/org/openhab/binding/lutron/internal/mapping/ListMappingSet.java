/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.lutron.internal.mapping;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.eclipse.smarthome.core.thing.ChannelUID;

/**
 * Set of mappings - keeps channel mappings which allows to lookup from component to channel and vice versa.
 *
 * @author Łuaksz Dywicki - initial contribution.
 *
 */
public class ListMappingSet implements MappingSet {

    private final List<ChannelMapping> mappings;

    public ListMappingSet(ChannelMapping... mappings) {
        this(Arrays.asList(mappings));
    }

    public ListMappingSet(List<ChannelMapping> mappings) {
        this.mappings = mappings;
    }

    @Override
    public Optional<ChannelMapping> fromChannel(ChannelUID id) {
        return all().filter(mapping -> mapping.matches(id)).findFirst();
    }

    @Override
    public Optional<ChannelMapping> fromComponent(int component) {
        return all().filter(mapping -> mapping.matches(component)).findFirst();
    }

    @Override
    public Stream<ChannelMapping> all() {
        return mappings.stream();
    }

}
