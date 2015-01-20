package dk.muj.derius.swordfighting;

import org.bukkit.inventory.ItemStack;

import dk.muj.derius.ability.Ability;
import dk.muj.derius.ability.AbilityType;
import dk.muj.derius.entity.MPlayer;
import dk.muj.derius.skill.Skill;
import dk.muj.derius.swordfighting.entity.MConf;

public class FastHit extends Ability
{
	private static FastHit i = new FastHit();
	public static FastHit get() { return i; }
	
	// -------------------------------------------- //
	// DESCRIPTION
	// -------------------------------------------- //
	
	public FastHit()
	{
		this.setName("Doubledrop and replace");
		
		this.setDescription("gives doubledrop and sometimes replants it");
		
		this.setType(AbilityType.ACTIVE);
	}
	
	// -------------------------------------------- //
	// SKILL & ID
	// -------------------------------------------- //
	
	@Override
	public int getId()
	{
		return MConf.get().getFasthitId;
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
	public Object onActivate(MPlayer p, Object other)
	{
		ItemStack inHand = p.getPlayer().getItemInHand();
		// Apply lore to the sword
		// Apply sharpness to sword
		// apply speedeffect to player
		return inHand;
	}

	@Override
	public void onDeactivate(MPlayer p, Object other)
	{
		// remove lore
		// remove sharpness from sword
		// remove potioneffect from player
		// apply damage to sword
		// send a message that it cuts the player if the sword breaks
	}
	
	// -------------------------------------------- //
	// Level description
	// -------------------------------------------- //
	
	@Override
	public String getLvlDescription(int lvl)
	{
		// TODO Auto-generated method stub
		return null;
	}
}
