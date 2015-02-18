package dk.muj.derius.swordfighting;

import java.util.List;
import java.util.Optional;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.massivecraft.massivecore.util.MUtil;
import com.massivecraft.massivecore.util.Txt;

import dk.muj.derius.api.Ability;
import dk.muj.derius.api.DPlayer;
import dk.muj.derius.api.Skill;
import dk.muj.derius.entity.ability.DeriusAbility;
import dk.muj.derius.lib.ItemUtil;

public class SwiftHit extends DeriusAbility implements Ability
{
	private static SwiftHit i = new SwiftHit();
	public static SwiftHit get() { return i; }
	
	// -------------------------------------------- //
	// DESCRIPTION
	// -------------------------------------------- //
	
	public SwiftHit()
	{
		this.setName("Swift Hit");
		
		this.setDesc("A swift hit that makes the opponent weaker.");
		
		this.setType(AbilityType.ACTIVE);
		
		this.setTicksCooldown(20*60*5);
	}
	
	List<String> lore = MUtil.list(
			Txt.parse("<rose>Swift Sword"),
			Txt.parse("<i>It hits really fast")
			);
	
	// -------------------------------------------- //
	// SKILL & ID
	// -------------------------------------------- //
	
	@Override
	public String getId()
	{
		return "derius:swordfighting:swifthit";
	}
	
	@Override
	public Skill getSkill()
	{
		return SwordfightingSkill.get();
	}

	// -------------------------------------------- //
	// ABILITY ACTIVATION
	// -------------------------------------------- //
	
	@Override
	public Object onActivate(DPlayer dplayer, Object other)
	{
		// NULL check
		if ( ! dplayer.isPlayer()) return Optional.empty();
		if ( ! (other instanceof EntityDamageByEntityEvent)) return null;
		EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) other;
		
		Entity entity = event.getEntity();
		if ( ! (entity instanceof LivingEntity)) return null;
		LivingEntity opponent = (LivingEntity) entity;
		
		ItemStack inHand = dplayer.getPlayer().getItemInHand();
		
		// Add lore
		ItemUtil.addLore(inHand, lore);
		
		// Add Speed to player
		if (SwordfightingSkill.isSpeedbuffEnabled())
		{
			addSpeedEffect(dplayer);
		}

		// Add crippling PotionEffect for opponent
		if (SwordfightingSkill.isCripplingdebuffEnabled())
		{
			addCripplingEffect(dplayer, opponent);
		}

		return inHand;
	}

	@Override
	public void onDeactivate(DPlayer dplayer, Object other)
	{
		// NULL check
		if ( ! dplayer.isPlayer()) return;
		if ( ! (other instanceof ItemStack)) return;
		ItemStack inhand = (ItemStack) other;
		
		ItemUtil.removeLore(inhand, lore);
	}
	
	// -------------------------------------------- //
	// Level description
	// -------------------------------------------- //
	
	@Override
	public String getLvlDescriptionMsg(int lvl)
	{
		// Make this a real message when ability is designed.
		return Txt.parse("<i>Adds %s seconds of Speed to you and %s seconds of a crippling effect to the opponent.", speedDurationInSeconds(lvl), crippleDurationInSeconds(lvl));
	}
	
	// -------------------------------------------- //
	// POTION EFFECTS
	// -------------------------------------------- //
	
	private void addSpeedEffect(DPlayer dplayer)
	{
		// Build PotionEffect
		int level = dplayer.getLvl(getSkill());
		int duration = getSpeedDuration(level);
		int maxLevel = SwordfightingSkill.getMaxSpeedLevel();
		int potionLevel = getSpeedLevel(level);
		
		// Is the level still in range?
		potionLevel = potionLevel >= maxLevel ? maxLevel : potionLevel;
		
		// Apply effect
		new PotionEffect(PotionEffectType.SPEED, duration, potionLevel).apply(dplayer.getPlayer());
	}
	
	private void addCripplingEffect(DPlayer dplayer, LivingEntity opponent)
	{
		// Fields
		PotionEffectType type = null;
		int level = dplayer.getLvl(getSkill());
		Material swordMaterial = dplayer.getPlayer().getItemInHand().getType();
		
		// Change duration and level of crippling according to weapon category
		int stepLevels = SwordfightingSkill.getLevelsPerCripplingStep();
		if (swordMaterial == Material.IRON_SWORD) level += 2 * stepLevels;
		else if (swordMaterial == Material.GOLD_SWORD) level += 3 * stepLevels;
		else if (swordMaterial == Material.DIAMOND_SWORD) level += 5 * stepLevels;
		
		// Change effect according to where it hits most likely
		EntityEquipment equiped = opponent.getEquipment();
		boolean hasHeadWear = equiped.getHelmet() != null;
		boolean hasChestWear = equiped.getChestplate() != null;
		boolean hasLegWear = equiped.getLeggings() != null;
		boolean hasFootWear = equiped.getBoots() != null;
		
		if ( ! hasHeadWear) type = PotionEffectType.BLINDNESS;
		else if ( ! hasChestWear) type = PotionEffectType.POISON;
		else if ( ! hasLegWear) type = PotionEffectType.CONFUSION;
		else if ( ! hasFootWear) type = PotionEffectType.SLOW;
		
		// Build PotionEffect
		int duration = getCrippleDuration(level);
		int maxLevel = SwordfightingSkill.getMaxCripplingLevel();
		int potionLevel = getCrippleLevel(level);
		
		// Is the level still in range?
		potionLevel = potionLevel >= maxLevel ? maxLevel : potionLevel;

		// Apply effect
		new PotionEffect(type, duration, potionLevel).apply(opponent);
	}
	
	// -------------------------------------------- //
	// PRIVATE CALCULATIONS
	// -------------------------------------------- //
	
	// Speed
	private int getSpeedDuration(int level)
	{
		int swiftnessBaseDuration = SwordfightingSkill.getBaseSpeedDuration();
		double secondsModifier = SwordfightingSkill.getSpeedSecondsPerStep();

		int duration = swiftnessBaseDuration + (int) (getSpeedSteps(level) * secondsModifier) * 20;
		
		return duration;
	}
	
	private double speedDurationInSeconds(int level)
	{
		return getSpeedDuration(level) / 20;
	}
	
	private int getSpeedLevel(int level)
	{
		int baseLevel = 1;
		
		return baseLevel + getSpeedSteps(level) * SwordfightingSkill.getLevelsPerSpeedStep();
	}
	
	private int getSpeedSteps(int level)
	{
		int levelStep = SwordfightingSkill.getLevelsPerSpeedStep();
		
		return level / levelStep;
	}
	
	// Crippling
	private int getCrippleDuration(int level)
	{
		int swiftnessBaseDuration = SwordfightingSkill.getBaseCripplingDuration();
		double secondsModifier = SwordfightingSkill.getCripplingSecondsPerStep();

		int duration = swiftnessBaseDuration + (int) (getSpeedSteps(level) * secondsModifier) * 20;
		
		return duration;
	}
	
	private double crippleDurationInSeconds(int level)
	{
		return getCrippleDuration(level) / 20;
	}
	
	private int getCrippleLevel(int level)
	{
		int baseLevel = 1;
		
		return baseLevel + getCrippleSteps(level) * SwordfightingSkill.getLevelsPerCripplingStep();
	}
	
	private int getCrippleSteps(int level)
	{
		int levelStep = SwordfightingSkill.getLevelsPerCripplingStep();
		
		return level / levelStep;
	}

}
