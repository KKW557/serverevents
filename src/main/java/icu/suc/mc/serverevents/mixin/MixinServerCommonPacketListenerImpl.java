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
package icu.suc.mc.serverevents.mixin;

import icu.suc.mc.serverevents.ServerEvents;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.chat.Component;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerCommonPacketListenerImpl.class)
public abstract class MixinServerCommonPacketListenerImpl {
    /**
     * @see ServerEvents.Player.Kick#MODIFY_REASON
     */
    @ModifyArg(method = "disconnect(Lnet/minecraft/network/chat/Component;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/DisconnectionDetails;<init>(Lnet/minecraft/network/chat/Component;)V"), index = 0)
    private Component serverevents$Player$Kick$MODIFY_REASON(Component component) {
        var self = (Object) this;
        if (self instanceof ServerGamePacketListenerImpl serverGamePacketListener) {
            var player = serverGamePacketListener.player;
            return ServerEvents.Player.Kick.MODIFY_REASON.invoker().modifyKickReason(player, component);
        }
        return component;
    }

    /**
     * @see ServerEvents.Player.Kick#ALLOW
     */
    @Inject(method = "disconnect(Lnet/minecraft/network/DisconnectionDetails;)V", at = @At("HEAD"), cancellable = true)
    private void serverevents$Player$Kick$ALLOW(DisconnectionDetails disconnectionDetails, CallbackInfo ci) {
        var self = (Object) this;
        if (self instanceof ServerGamePacketListenerImpl serverGamePacketListener) {
            var player = serverGamePacketListener.player;
            var component = disconnectionDetails.reason();
            boolean bool = ServerEvents.Player.Kick.ALLOW.invoker().allowKick(player, component);
            if (bool) return;
            ci.cancel();
        }
    }
}
