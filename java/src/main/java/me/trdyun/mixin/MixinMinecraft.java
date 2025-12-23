package me.trdyun.mixin;

import me.trdyun.core.PatronPatcher;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.swing.*;

@Mixin(value = MinecraftClient.class,priority = 1)
public class MixinMinecraft {

    @Inject(method = "<init>", at = {@At(value = "INVOKE", target = "Lnet/minecraft/text/KeybindTranslations;setFactory(Ljava/util/function/Function;)V")})
    private static void preHackStartup(CallbackInfo ci) {
        PatronPatcher.onHackPreLoad();
    }
    @Inject(method = "<init>", at = {@At("RETURN")})
    private static void postHackStartup(CallbackInfo ci) {
        PatronPatcher.disablePatron();
    }
}
