package dk.muj.derius.swordfighting;

import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.massivecraft.massivecore.collections.WorldExceptionSet;
import com.massivecraft.massivecore.util.Txt;

import dk.muj.derius.api.Ability;
import dk.muj.derius.api.DPlayer;
import dk.muj.derius.api.Skill;
import dk.muj.derius.entity.ability.DeriusAbility;
import dk.muj.derius.swordfighting.entity.MConf;

public class SwordTraining extends DeriusAbility implements Ability
{
	private static SwordTraining i = new SwordTraining();
	public static SwordTraining get() { return i; }
	
	// -------------------------------------------- //
	// DESCRIPTION
	// -------------------------------------------- //
	
	public SwordTraining()
	{
		this.setName("Sword Training");
		
		this.setDesc("The higher your level is, the more damage you deal.");
		
		this.setType(AbilityType.PASSIVE);
	}
	
	// -------------------------------------------- //
	// SKILL & ID
	// -------------------------------------------- //
	
	@Override
	public String getId()
	{
		return MConf.get().getSwordTrainingId;
	}
	
	@Override
	public Skill getSkill()
	{
		return SwordfightingSkill.get();
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public Object onActivate(DPlayer dplayer, Object other)
	{
		if ( ! (other instanceof EntityDamageByEntityEvent)) return null;
		EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) other;
		
		double damage = event.getDamage();
		int playerLevel = dplayer.getLvl(SwordfightingSkill.get());
		double modifier = MConf.get().getDamagePerLevels();
		int perLevel = MConf.get().getLevelsPerDamageIncrease();
		damage = damage + (playerLevel / perLevel) * modifier;
		
		event.setDamage(damage);
		return null;
	}

	@Override
	public void onDeactivate(DPlayer dplayer, Object other)
	{
		// Nothing to do here
	}

	// -------------------------------------------- //
	// Level description
	// -------------------------------------------- //

	@Override
	public String getLvlDescriptionMsg(int lvl)
	{
		double modifier = MConf.get().getDamagePerLevels();
		int perLevel = MConf.get().getLevelsPerDamageIncrease();
		double bonusDamage = (lvl / perLevel) * modifier;
		return Txt.parse("Your bonus damage for swords is %s.", bonusDamage);
	}

	@Override
	public void setWorldsEarn(WorldExceptionSet worldsUse)
	{
		// TODO Auto-generated method stub
		
	}
}
