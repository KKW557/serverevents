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

import com.llamalad7.mixinextras.sugar.Local;
import icu.suc.mc.serverevents.ServerEvents;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity {
    @Shadow protected abstract void onEffectsRemoved(Collection<MobEffectInstance> collection);

    @Shadow @Final private Map<Holder<MobEffect>, MobEffectInstance> activeEffects;

    /**
     * @see ServerEvents.LivingEntity.Effect#ADD
     */
    @Inject(method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;canBeAffected(Lnet/minecraft/world/effect/MobEffectInstance;)Z", shift = At.Shift.AFTER), cancellable = true)
    private void serverevents$LivingEntity$Effect$ADD(MobEffectInstance mobEffectInstance, Entity entity, @NotNull CallbackInfoReturnable<Boolean> cir) {
        var self = (LivingEntity) (Object) this;
        boolean bool = ServerEvents.LivingEntity.Effect.ADD.invoker().addEffect(self, mobEffectInstance, entity);
        if (bool) return;
        cir.setReturnValue(false);
    }

    /**
     * @see ServerEvents.LivingEntity.Effect#OVERRIDE
     */
    @Redirect(method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/effect/MobEffectInstance;update(Lnet/minecraft/world/effect/MobEffectInstance;)Z"))
    private boolean serverevents$LivingEntity$Effect$OVERRIDE(@NotNull MobEffectInstance instance, MobEffectInstance mobEffectInstance, @Local(argsOnly = true) Entity entity) {
        var self = (LivingEntity) (Object) this;
        boolean updated = instance.update(mobEffectInstance);
        return ServerEvents.LivingEntity.Effect.OVERRIDE.invoker().overrideEffect(self, instance, mobEffectInstance, entity, updated);
    }

    /**
     * @see ServerEvents.LivingEntity.Effect#REMOVE
     */
    @Redirect(method = "tickEffects", at = @At(value = "INVOKE", target = "Ljava/util/Iterator;remove()V"))
    private void serverevents$LivingEntity$Effect$REMOVE(@NotNull Iterator<Holder<MobEffectInstance>> instance, @Local MobEffectInstance mobEffectInstance) {
        var self = (LivingEntity) (Object) this;
        ServerEvents.LivingEntity.Effect.REMOVE.invoker().removeEffect(self, mobEffectInstance);
        instance.remove();
    }

    /**
     * @see ServerEvents.LivingEntity.Effect#REMOVE
     */
    @Redirect(method = "tickEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;onEffectsRemoved(Ljava/util/Collection;)V"))
    private void serverevents$LivingEntity$Effect$REMOVE(LivingEntity instance, Collection<MobEffectInstance> collection, @Local MobEffectInstance mobEffectInstance) {
        boolean bool = ServerEvents.LivingEntity.Effect.REMOVE.invoker().removeEffect(instance, mobEffectInstance);
        if (bool) {
            onEffectsRemoved(collection);
        }
    }

    /**
     * @see ServerEvents.LivingEntity.Effect#REMOVE
     */
    @Inject(method = "triggerOnDeathMobEffects", at = @At("HEAD"), cancellable = true)
    private void serverevents$LivingEntity$Effect$REMOVE(ServerLevel serverLevel, Entity.RemovalReason removalReason, CallbackInfo ci) {
        var iterator = this.activeEffects.entrySet().iterator();
        var self = (LivingEntity) (Object) this;
        while (iterator.hasNext()) {
            var effect = iterator.next().getValue();
            boolean bool = ServerEvents.LivingEntity.Effect.REMOVE.invoker().removeEffect(self, effect);
            if (bool) {
                effect.onMobRemoved(serverLevel, self, removalReason);
                iterator.remove();
            }
        }
        ci.cancel();
    }

    /**
     * @see ServerEvents.LivingEntity.Effect#REMOVE
     */
    @Inject(method = "removeAllEffects", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Maps;newHashMap(Ljava/util/Map;)Ljava/util/HashMap;"), cancellable = true)
    private void serverevents$LivingEntity$Effect$REMOVE(CallbackInfoReturnable<Boolean> cir) {
        var iterator = this.activeEffects.entrySet().iterator();
        var toRemove = new LinkedList<MobEffectInstance>();
        var self = (LivingEntity) (Object) this;
        while (iterator.hasNext()) {
            var effect = iterator.next().getValue();
            boolean bool = ServerEvents.LivingEntity.Effect.REMOVE.invoker().removeEffect(self, effect);
            if (bool) {
                iterator.remove();
                toRemove.add(effect);
            }
        }
        this.onEffectsRemoved(toRemove);
        cir.setReturnValue(!toRemove.isEmpty());
    }

    /**
     * @see ServerEvents.LivingEntity.Effect#REMOVE
     */
    @Inject(method = "removeEffectNoUpdate", at = @At("HEAD"), cancellable = true)
    private void serverevents$LivingEntity$Effect$REMOVE(Holder<MobEffect> holder, CallbackInfoReturnable<MobEffectInstance> cir) {
        var self = (LivingEntity) (Object) this;
        var effect = this.activeEffects.get(holder);
        boolean bool = ServerEvents.LivingEntity.Effect.REMOVE.invoker().removeEffect(self, effect);
        if (bool) {
            var removed = this.activeEffects.remove(holder);
            cir.setReturnValue(removed);
            return;
        }
        cir.setReturnValue(null);
    }
}
