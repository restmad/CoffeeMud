package com.planet_ink.coffee_mud.Abilities.Druid;

import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import com.planet_ink.coffee_mud.utils.*;
import java.util.*;

public class Chant_SummonFire extends Chant
{
	private Room FireLocation=null;
	private Item littleFire=null;

	public Chant_SummonFire()
	{
		super();
		myID=this.getClass().getName().substring(this.getClass().getName().lastIndexOf('.')+1);
		name="Summon Fire";
		baseEnvStats().setLevel(5);

		recoverEnvStats();
	}

	public Environmental newInstance()
	{
		return new Chant_SummonFire();
	}

	public void unInvoke()
	{
		if(FireLocation==null)
			return;
		if(littleFire==null)
			return;
		FireLocation.show(invoker,null,Affect.MSG_OK_VISUAL,"The little magical fire goes out.");
		super.unInvoke();
		Item fire=littleFire; // protects against uninvoke loops!
		littleFire=null;
		fire.destroyThis();
		FireLocation.recoverRoomStats();
		FireLocation=null;
	}

	public boolean invoke(MOB mob, Vector commands, Environmental givenTarget, boolean auto)
	{
		if((mob.location().domainType()&Room.INDOORS)>0)
		{
			mob.tell("You must be outdoors for this chant to work.");
			return false;
		}
		if(mob.location().domainType()==Room.DOMAIN_OUTDOORS_CITY)
		{
			mob.tell("This magic will not work here.");
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto))
			return false;
		
		// now see if it worked
		boolean success=profficiencyCheck(0,auto);
		if(success)
		{
			FullMsg msg=new FullMsg(mob,null,this,affectType,auto?"":"<S-NAME> chant(s) for fire.");
			if(mob.location().okAffect(msg))
			{
				mob.location().send(mob,msg);
				Item I=CMClass.getItem("GenItem");
				I.baseEnvStats().setWeight(50);
				I.setName("a magical campire");
				I.setDisplayText("A roaring magical campire has been built here.");
				I.setDescription("It consists of magically burning flames, consuming no fuel.");
				I.recoverEnvStats();
				I.setMaterial(EnvResource.RESOURCE_NOTHING);
				I.setMiscText(I.text());
				Ability B=CMClass.getAbility("Burning");
				I.addNonUninvokableAffect(B);

				mob.location().addItem(I);
				mob.location().show(mob,null,Affect.MSG_OK_ACTION,"Suddenly, a little magical campfire begins burning here.");
				FireLocation=mob.location();
				littleFire=I;
				beneficialAffect(mob,I,0);
				mob.location().recoverEnvStats();
			}
		}
		else
			return beneficialWordsFizzle(mob,null,"<S-NAME> chant(s) for fire, but nothing happens.");

		// return whether it worked
		return success;
	}
}