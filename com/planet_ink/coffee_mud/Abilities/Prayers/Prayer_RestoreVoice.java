package com.planet_ink.coffee_mud.Abilities.Prayers;

import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import com.planet_ink.coffee_mud.utils.*;
import java.util.*;

public class Prayer_RestoreVoice extends Prayer
{
	public Prayer_RestoreVoice()
	{
		super();
		myID=this.getClass().getName().substring(this.getClass().getName().lastIndexOf('.')+1);
		name="Restore Voice";

		baseEnvStats().setLevel(17);
		quality=Ability.OK_OTHERS;
		holyQuality=Prayer.HOLY_GOOD;

		recoverEnvStats();
	}

	public Environmental newInstance()
	{
		return new Prayer_RestoreVoice();
	}

	public Vector returnOffensiveAffects(MOB caster, Environmental fromMe)
	{
		MOB newMOB=(MOB)CMClass.getMOB("StdMOB").newInstance();
		Vector offenders=new Vector();

		for(int a=0;a<fromMe.numAffects();a++)
		{
			Ability A=fromMe.fetchAffect(a);
			if(A!=null)
			{
				newMOB.recoverEnvStats();
				A.affectEnvStats(newMOB,newMOB.envStats());
				if((!Sense.canSpeak(newMOB))
				&&((A.invoker()==null)
				   ||((A.invoker()!=null)
					  &&(A.invoker().envStats().level()<=caster.envStats().level()+1))))
						offenders.addElement(A);
			}
		}
		return offenders;
	}

	public boolean invoke(MOB mob, Vector commands, Environmental givenTarget, boolean auto)
	{
		MOB target=this.getTarget(mob,commands,givenTarget);
		if(target==null) return false;

		if(!super.invoke(mob,commands,givenTarget,auto))
			return false;

		boolean success=profficiencyCheck(0,auto);
		Vector offensiveAffects=returnOffensiveAffects(mob,target);

		if((success)&&(offensiveAffects.size()>0))
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			FullMsg msg=new FullMsg(mob,target,this,affectType,auto?"A visible glow surrounds <T-NAME>.":"<S-NAME> pray(s) for <T-NAMESELF> to speak.");
			if(mob.location().okAffect(msg))
			{
				mob.location().send(mob,msg);
				for(int a=offensiveAffects.size()-1;a>=0;a--)
					((Ability)offensiveAffects.elementAt(a)).unInvoke();
			}
		}
		else
			beneficialWordsFizzle(mob,target,auto?"":"<S-NAME> pray(s) for <T-NAMESELF>, but nothing happens.");

		// return whether it worked
		return success;
	}
}