package com.planet_ink.coffee_mud.Abilities.Druid;

import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import com.planet_ink.coffee_mud.utils.*;
import java.util.*;


public class Druid_PredictWeather extends Chant
{
	String lastPrediction="";
	public Druid_PredictWeather()
	{
		super();
		myID=this.getClass().getName().substring(this.getClass().getName().lastIndexOf('.')+1);
		name="Predict Weather";
		displayText="(Predict Weather)";
		miscText="";

		canBeUninvoked=true;
		isAutoinvoked=false;

		baseEnvStats().setLevel(7);

		baseEnvStats().setAbility(0);
		uses=Integer.MAX_VALUE;
		recoverEnvStats();
	}

	public Environmental newInstance()
	{
		return new Druid_PredictWeather();
	}
	
	public void unInvoke()
	{
		if((affected==null)||(!(affected instanceof MOB)))
			return;
		MOB mob=(MOB)affected;
		lastPrediction="";
		super.unInvoke();
		mob.tell(mob,null,"Your senses are no longer sensitive to the weather.");
	}
	public boolean tick(int tickID)
	{
		if(!super.tick(tickID))
			return false;
		if((tickID==Host.MOB_TICK)
		   &&(affected!=null)
		   &&(affected instanceof MOB)
		   &&(((MOB)affected).location()!=null)
		   &&((((MOB)affected).location().domainType()&Room.INDOORS)==0))
		{
		   String prediction=(((MOB)affected).location().getArea().nextWeatherDescription(((MOB)affected).location()));
		   if(!prediction.equals(lastPrediction))
		   {
			   lastPrediction=prediction;
			   ((MOB)affected).tell("Your weather senses gaze into the future, you see: \n\r"+prediction);
		   }
		}
		return true;
	}
	

	public boolean invoke(MOB mob, Vector commands, Environmental givenTarget, boolean auto)
	{
		if(!super.invoke(mob,commands,givenTarget,auto))
			return false;

		if(mob.fetchAffect(this.ID())!=null)
		{
			mob.tell("You are already detecting weather.");
			return false;
		}

		boolean success=profficiencyCheck(0,auto);

		if(success)
		{
			FullMsg msg=new FullMsg(mob,null,this,affectType,auto?"<S-NAME> gain(s) sensitivity to the weather!":"<S-NAME> chant(s) for weather sensitivity!");
			if(mob.location().okAffect(msg))
			{
				lastPrediction="";
				mob.location().send(mob,msg);
				beneficialAffect(mob,mob,0);
			}
		}
		else
			beneficialVisualFizzle(mob,null,"<S-NAME> chant(s) into the sky, but the magic fizzles.");

		return success;
	}
}