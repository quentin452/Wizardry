package electroblob.wizardry.entity.living;

import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;

import electroblob.wizardry.EnumParticleType;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.WizardryUtilities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSilverfish;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.Facing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntitySilverfishMinion extends EntitySummonedCreature
{
	/** A cooldown before this entity will search for another Silverfish to join them in battle. */
	private int allySummonCooldown;
	public EntitySilverfishMinion(World world){
		super(world);
	}

	public EntitySilverfishMinion(World world, double x, double y, double z, EntityLivingBase caster, int lifetime){

		super(world, x, y, z, caster, lifetime);
		this.setSize(0.3F, 0.7F);
	}

	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(8.0D);
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.6000000238418579D);
		this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(1.0D);
		this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(8.0D);
	}

	@Override
	public boolean hasRangedAttack() {
		return false;
	}

	/**
	 * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
	 * prevent them from trampling crops
	 */
	protected boolean canTriggerWalking()
	{
		return false;
	}

	/**
	 * Returns the sound this mob makes while it's alive.
	 */
	protected String getLivingSound()
	{
		return "mob.silverfish.say";
	}

	/**
	 * Returns the sound this mob makes when it is hurt.
	 */
	protected String getHurtSound()
	{
		return "mob.silverfish.hit";
	}

	/**
	 * Returns the sound this mob makes on death.
	 */
	protected String getDeathSound()
	{
		return "mob.silverfish.kill";
	}

	/**
	 * Called when the entity is attacked.
	 */
	public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_)
	{
		if (this.isEntityInvulnerable())
		{
			return false;
		}
		else
		{
			if (this.allySummonCooldown <= 0 && (p_70097_1_ instanceof EntityDamageSource || p_70097_1_ == DamageSource.magic))
			{
				this.allySummonCooldown = 20;
			}

			return super.attackEntityFrom(p_70097_1_, p_70097_2_);
		}
	}

	/**
	 * Basic mob attack. Default to touch of death in EntityCreature. Overridden by each mob to define their attack.
	 */
	protected void attackEntity(Entity p_70785_1_, float p_70785_2_)
	{
		if (this.attackTime <= 0 && p_70785_2_ < 1.2F && p_70785_1_.boundingBox.maxY > this.boundingBox.minY && p_70785_1_.boundingBox.minY < this.boundingBox.maxY)
		{
			this.attackTime = 20;
			this.attackEntityAsMob(p_70785_1_);
		}
	}

	protected void func_145780_a(int p_145780_1_, int p_145780_2_, int p_145780_3_, Block p_145780_4_)
	{
		this.playSound("mob.silverfish.step", 0.15F, 1.0F);
	}

	protected Item getDropItem()
	{
		return Item.getItemById(0);
	}

	@Override
	public void onSpawn(){
		if(this.worldObj.isRemote){
			for(int i=0;i<15;i++){
				Wizardry.proxy.spawnParticle(EnumParticleType.DARK_MAGIC, worldObj, this.posX + this.rand.nextFloat(), this.posY + this.rand.nextFloat(), this.posZ + this.rand.nextFloat(), 0.0d, 0.0d, 0.0d, 0, 0.3f, 0.3f, 0.3f);
			}
		}
	}

	@Override
	public void despawn(){
		if(this.worldObj.isRemote){
			for(int i=0;i<15;i++){
				Wizardry.proxy.spawnParticle(EnumParticleType.DARK_MAGIC, worldObj, this.posX + this.rand.nextFloat(), this.posY + this.rand.nextFloat(), this.posZ + this.rand.nextFloat(), 0.0d, 0.0d, 0.0d, 0, 0.3f, 0.3f, 0.3f);
			}
		}
		super.despawn();
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	public void onUpdate()
	{
		// Adds subtle particle effect while alive
		if(this.worldObj.isRemote && rand.nextInt(8) == 0) Wizardry.proxy.spawnParticle(EnumParticleType.DARK_MAGIC, worldObj, this.posX, this.posY + rand.nextDouble()*1.5, this.posZ, 0.0d, 0.0d, 0.0d, 0, 0.1f, 0.0f, 0.0f);

		this.renderYawOffset = this.rotationYaw;
		super.onUpdate();
	}

	protected void updateEntityActionState()
	{
		super.updateEntityActionState();

		if (!this.worldObj.isRemote)
		{
			int i;
			int j;
			int k;
			int i1;

			if (this.allySummonCooldown > 0)
			{
				--this.allySummonCooldown;

				if (this.allySummonCooldown == 0)
				{
					i = MathHelper.floor_double(this.posX);
					j = MathHelper.floor_double(this.posY);
					k = MathHelper.floor_double(this.posZ);
					boolean flag = false;

					for (int l = 0; !flag && l <= 5 && l >= -5; l = l <= 0 ? 1 - l : 0 - l)
					{
						for (i1 = 0; !flag && i1 <= 10 && i1 >= -10; i1 = i1 <= 0 ? 1 - i1 : 0 - i1)
						{
							for (int j1 = 0; !flag && j1 <= 10 && j1 >= -10; j1 = j1 <= 0 ? 1 - j1 : 0 - j1)
							{
								if (this.worldObj.getBlock(i + i1, j + l, k + j1) == Blocks.monster_egg)
								{
									if (!this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing"))
									{
										int k1 = this.worldObj.getBlockMetadata(i + i1, j + l, k + j1);
										ImmutablePair immutablepair = BlockSilverfish.func_150197_b(k1);
										this.worldObj.setBlock(i + i1, j + l, k + j1, (Block)immutablepair.getLeft(), ((Integer)immutablepair.getRight()).intValue(), 3);
									}
									else
									{
										this.worldObj.func_147480_a(i + i1, j + l, k + j1, false);
									}

									Blocks.monster_egg.onBlockDestroyedByPlayer(this.worldObj, i + i1, j + l, k + j1, 0);

									if (this.rand.nextBoolean())
									{
										flag = true;
										break;
									}
								}
							}
						}
					}
				}
			}

			if (this.entityToAttack == null && !this.hasPath())
			{
				i = MathHelper.floor_double(this.posX);
				j = MathHelper.floor_double(this.posY + 0.5D);
				k = MathHelper.floor_double(this.posZ);
				int l1 = this.rand.nextInt(6);
				Block block = this.worldObj.getBlock(i + Facing.offsetsXForSide[l1], j + Facing.offsetsYForSide[l1], k + Facing.offsetsZForSide[l1]);
				i1 = this.worldObj.getBlockMetadata(i + Facing.offsetsXForSide[l1], j + Facing.offsetsYForSide[l1], k + Facing.offsetsZForSide[l1]);

				if (BlockSilverfish.func_150196_a(block))
				{
					this.worldObj.setBlock(i + Facing.offsetsXForSide[l1], j + Facing.offsetsYForSide[l1], k + Facing.offsetsZForSide[l1], Blocks.monster_egg, BlockSilverfish.func_150195_a(block, i1), 3);
					this.spawnExplosionParticle();
					this.setDead();
				}
				else
				{
					this.updateWanderPath();
				}
			}
			else if (this.entityToAttack != null && !this.hasPath())
			{
				this.entityToAttack = null;
			}
		}
	}

	/**
	 * Takes a coordinate in and returns a weight to determine how likely this creature will try to path to the block.
	 * Args: x, y, z
	 */
	public float getBlockPathWeight(int p_70783_1_, int p_70783_2_, int p_70783_3_)
	{
		return this.worldObj.getBlock(p_70783_1_, p_70783_2_ - 1, p_70783_3_) == Blocks.stone ? 10.0F : super.getBlockPathWeight(p_70783_1_, p_70783_2_, p_70783_3_);
	}

	/**
	 * Checks to make sure the light is not too bright where the mob is spawning
	 */
	protected boolean isValidLightLevel()
	{
		return true;
	}

	/**
	 * Get this Entity's EnumCreatureAttribute
	 */
	public EnumCreatureAttribute getCreatureAttribute()
	{
		return EnumCreatureAttribute.ARTHROPOD;
	}
}
