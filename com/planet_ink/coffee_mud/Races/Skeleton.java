package com.planet_ink.coffee_mud.Races;

import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import com.planet_ink.coffee_mud.utils.*;
import java.util.*;

public class Skeleton extends StdRace
{
	public String ID(){	return "Skeleton"; }
	public String name(){ return "Skeleton"; }
	protected int shortestMale(){return 64;}
	protected int shortestFemale(){return 60;}
	protected int heightVariance(){return 12;}
	protected int lightestWeight(){return 100;}
	protected int weightVariance(){return 100;}
	protected long forbiddenWornBits(){return 0;}
	public String racialCategory(){return "Undead";}
	
	protected static Vector resources=new Vector();
	public boolean playerSelectable(){return false;}

	public boolean okAffect(MOB myChar, Affect affect)
	{
		MOB mob=(MOB)myChar;
		if((affect.amITarget(mob))
	    &&(Util.bset(affect.targetCode(),Affect.MASK_HURT))
		&&(affect.tool()!=null)
		&&(affect.tool() instanceof Weapon)
		&&((((Weapon)affect.tool()).weaponType()==Weapon.TYPE_PIERCING)
			||(((Weapon)affect.tool()).weaponType()==Weapon.TYPE_SLASHING))
		&&(!mob.amDead()))
		{
			int recovery=(int)Math.round(Util.div((affect.targetCode()-Affect.MASK_HURT),2.0));
			affect.modify(affect.source(),affect.target(),affect.tool(),affect.sourceCode(),affect.sourceMessage(),affect.targetCode()-recovery,affect.targetMessage(),affect.othersCode(),affect.othersMessage());
		}
		return super.okAffect(myChar,affect);
	}


	public void affectCharStats(MOB affectedMOB, CharStats affectableStats)
	{
		super.affectCharStats(affectedMOB, affectableStats);
		affectableStats.setStat(CharStats.SAVE_POISON,affectableStats.getStat(CharStats.SAVE_POISON)+100);
		affectableStats.setStat(CharStats.SAVE_MIND,affectableStats.getStat(CharStats.SAVE_MIND)+100);
		affectableStats.setStat(CharStats.SAVE_GAS,affectableStats.getStat(CharStats.SAVE_GAS)+100);
		affectableStats.setStat(CharStats.SAVE_PARALYSIS,affectableStats.getStat(CharStats.SAVE_PARALYSIS)+100);
		affectableStats.setStat(CharStats.SAVE_UNDEAD,affectableStats.getStat(CharStats.SAVE_UNDEAD)+100);
		affectableStats.setStat(CharStats.SAVE_DISEASE,affectableStats.getStat(CharStats.SAVE_DISEASE)+100);
	}
	public Vector myResources()
	{
		synchronized(resources)
		{
			if(resources.size()==0)
			{
				resources.addElement(makeResource
					("knuckle bone",EnvResource.RESOURCE_BONE));
			}
		}
		return resources;
	}
}
