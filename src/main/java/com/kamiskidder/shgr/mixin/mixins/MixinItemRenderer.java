package com.kamiskidder.shgr.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.kamiskidder.shgr.module.render.SwordRotator;
import com.kamiskidder.shgr.util.Util;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;

@Mixin(ItemRenderer.class)
public abstract class MixinItemRenderer implements Util {
    @Shadow
    public abstract void renderItemSide(EntityLivingBase entitylivingbaseIn, ItemStack heldStack, ItemCameraTransforms.TransformType transform, boolean leftHanded);

    private float angle = 0.0f;
    private boolean flag = false;

    @Inject(method = "renderItemInFirstPerson(Lnet/minecraft/client/entity/AbstractClientPlayer;FFLnet/minecraft/util/EnumHand;FLnet/minecraft/item/ItemStack;F)V", at = @At("HEAD"))
    public void renderItemInFirstPersonPre(AbstractClientPlayer player, float f4, float f5, EnumHand hand, float j, ItemStack stack, float f1, CallbackInfo ci) {
        flag = SwordRotator.INSTANCE.isToggled() && SwordRotator.INSTANCE.isPressed && stack.getItem() instanceof ItemSword;
    }

    @ModifyArg(method = "renderItemInFirstPerson(Lnet/minecraft/client/entity/AbstractClientPlayer;FFLnet/minecraft/util/EnumHand;FLnet/minecraft/item/ItemStack;F)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemRenderer;transformSideFirstPerson(Lnet/minecraft/util/EnumHandSide;F)V"), index = 1)
    public float transformSideFirstPerson(float p_187459_2_) {
        return flag ? 0 : p_187459_2_;
    }

    @Inject(method = "renderItemInFirstPerson(Lnet/minecraft/client/entity/AbstractClientPlayer;FFLnet/minecraft/util/EnumHand;FLnet/minecraft/item/ItemStack;F)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;sqrt(F)F"), cancellable = true)
    public void renderItemInFirstPerson(AbstractClientPlayer player, float f4, float f5, EnumHand hand, float j, ItemStack stack, float f1, CallbackInfo ci) {
        if (flag) {
            angle += SwordRotator.INSTANCE.speed.getValue();
            EnumHandSide enumhandside = hand == EnumHand.MAIN_HAND ? player.getPrimaryHand() : player.getPrimaryHand().opposite();
            int i = enumhandside == EnumHandSide.RIGHT ? 1 : -1;
            GlStateManager.translate((float)i * 0.56F, -0.52F + f1 * -0.6F, -0.72F);
            String type = SwordRotator.INSTANCE.type.getValue();
            float x = type.equalsIgnoreCase("x") ? 1.0f : 0f;
            float y = type.equalsIgnoreCase("y") ? 1.0f : 0f;
            float z = type.equalsIgnoreCase("z") ? 1.0f : 0f;
            GlStateManager.rotate(angle, x, y, z);
            this.renderItemSide(player, stack, ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, false);
            GlStateManager.popMatrix();
            ci.cancel();
        }
    }
}
