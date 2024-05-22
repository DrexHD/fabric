/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.fabric.test.item;

import net.minecraft.block.BlockState;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class UpdatingItem extends Item {
	private static final EntityAttributeModifier PLUS_FIVE = new EntityAttributeModifier(
			ATTACK_DAMAGE_MODIFIER_ID, 5, EntityAttributeModifier.Operation.ADD_VALUE);

	private final boolean allowUpdateAnimation;

	public UpdatingItem(boolean allowUpdateAnimation) {
		super(new Settings());
		this.allowUpdateAnimation = allowUpdateAnimation;
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
		if (!world.isClient) {
			stack.set(ItemUpdateAnimationTest.TICKS, Math.max(0, stack.getOrDefault(ItemUpdateAnimationTest.TICKS, 0) + 1));
		}
	}

	@Override
	public boolean allowComponentsUpdateAnimation(PlayerEntity player, Hand hand, ItemStack originalStack, ItemStack updatedStack) {
		return allowUpdateAnimation;
	}

	@Override
	public boolean allowContinuingBlockBreaking(PlayerEntity player, ItemStack oldStack, ItemStack newStack) {
		return true; // set to false and you won't be able to break a block in survival with this item
	}

	// True for 15 seconds every 30 seconds
	private boolean isEnabled(ItemStack stack) {
		return !stack.contains(ItemUpdateAnimationTest.TICKS) || stack.getOrDefault(ItemUpdateAnimationTest.TICKS, 0) % 600 < 300;
	}

	@Override
	public AttributeModifiersComponent getAttributeModifiers(ItemStack stack) {
		// Give + 5 attack damage for 15 seconds every 30 seconds.
		return AttributeModifiersComponent.builder()
				.add(EntityAttributes.GENERIC_ATTACK_DAMAGE, PLUS_FIVE, AttributeModifierSlot.MAINHAND)
				.build();
	}

	@Override
	public float getMiningSpeed(ItemStack stack, BlockState state) {
		return isEnabled(stack) ? 20 : super.getMiningSpeed(stack, state);
	}
}
