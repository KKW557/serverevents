/*
 * MIT License
 *
 * Copyright (c) 2025 sucj
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package icu.suc.mc.serverevents;

import net.fabricmc.fabric.api.event.Event;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;

/// Provides a legacy event priority abstraction.
///
/// This class exists primarily for compatibility and developer familiarity,
/// mirroring the conventional event priority model used in older APIs such as
/// Bukkit. It maps human-readable priority levels to [Identifier]-based
/// Fabric event phases.
///
/// The defined priorities are actively applied to events exposed under
/// `ServerEvents.*`, where they are used as phase identifiers during
/// event registration and invocation.
///
/// Outside of `ServerEvents.*`, these priorities do not have intrinsic
/// meaning unless explicitly integrated into the event dispatching logic.
public final class ServerEventPriority {
    @ApiStatus.Internal
    public static final Identifier[] PRIORITIES = new Identifier[6];

    @ApiStatus.Internal
    public static Identifier register(int id, Identifier identifier) {
        return PRIORITIES[id] = identifier;
    }

    private ServerEventPriority() {}

    /// Executed first.
    public static final Identifier LOWEST = register(0, Identifier.fromNamespaceAndPath(ServerEvents.ID, "lowest"));

    /// Executed after [#LOWEST].
    public static final Identifier LOW = register(1, Identifier.fromNamespaceAndPath(ServerEvents.ID, "low"));

    /// Executed after [#LOW].
    /// Default priority.
    ///
    /// @see Event#DEFAULT_PHASE
    public static final Identifier NORMAL = register(2, Event.DEFAULT_PHASE);

    /// Executed after [#NORMAL].
    public static final Identifier HIGH = register(3, Identifier.fromNamespaceAndPath(ServerEvents.ID, "high"));

    /// Executed after [#HIGH].
    public static final Identifier HIGHEST = register(4, Identifier.fromNamespaceAndPath(ServerEvents.ID, "highest"));

    /// Executed after [#HIGHEST].
    /// For observation only.
    public static final Identifier MONITOR = register(5, Identifier.fromNamespaceAndPath(ServerEvents.ID, "monitor"));

    public static Identifier getById(int id) {
        return PRIORITIES[id];
    }
}
