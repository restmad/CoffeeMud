package com.planet_ink.coffee_mud.Abilities.Misc;

import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import com.planet_ink.coffee_mud.utils.*;
import com.planet_ink.coffee_mud.Abilities.StdAbility;
import java.util.*;

public class Dragonbreath extends StdAbility
{
	private String puffPhrase="<S-NAME> puff(s) smoke from <S-HIS-HER> mouth.";
	private String autoPhrase="A blast of flames erupts!";
	private String stuffWord="flames";
	private String castPhrase="<S-NAME> blast(s) flames from <S-HIS-HER> mouth!";
	private int WeaponType=Weapon.TYPE_BURNING;
	private int strikeType=Affect.TYP_FIRE;
	private final static String[][] DragonColors={
		{"WHITE","c"},
		{"BLACK","a"},
		{"BLUE","l"},
		{"GREEN","g"},
		{"RED","f"},
		{"BRASS","f"},
		{"COPPER","a"},
		{"BRONZE","l"},
		{"SILVER","c"},
		{"GOLD","g"},
	};

	public String ID() { return "Dragonbreath"; }
	public String name(){ return "Dragonbreath";}
	public int quality(){return Ability.MALICIOUS;}
	public int maxRange(){return 10;}
	protected int canAffectCode(){return 0;}
	protected int canTargetCode(){return Ability.CAN_MOBS;}
	public boolean putInCommandlist(){return false;}
	private static final String[] triggerStrings = {"DRAGONBREATH"};
	public String[] triggerStrings(){return triggerStrings;}
	public Environmental newInstance(){	return new Dragonbreath();}
	public int classificationCode(){return Ability.SKILL;}

	public Dragonbreath()
	{
		super();
		setMiscText("");
	}
	public void setMiscText(String newType)
	{
		super.setMiscText(newType);
		if(newType.trim().length()==0)
		{
			int x=Dice.roll(1,5,-1);
			newType=("rlcag").substring(x,x+1);
		}
		char c=newType.trim().toLowerCase().charAt(0);
		switch(c)
		{
		case 'f':
				puffPhrase="<S-NAME> puff(s) smoke from <S-HIS-HER> mouth.";
				autoPhrase="A blast of flames erupts!";
				stuffWord="flames";
				castPhrase="<S-NAME> blast(s) flames from <S-HIS-HER> mouth!"+CommonStrings.msp("fireball.wav",40);
				WeaponType=Weapon.TYPE_BURNING;
				break;
		case 'l':
				puffPhrase="<S-NAME> spark(s) a little from <S-HIS-HER> mouth.";
				autoPhrase="A blast of lightning bursts erupt!";
				stuffWord="bolt";
				castPhrase="<S-NAME> shoot(s) numerous bursts of lightning from <S-HIS-HER> mouth!"+CommonStrings.msp("lightning.wav",40);
				WeaponType=Weapon.TYPE_STRIKING;
				break;
		case 'c':
				puffPhrase="<S-NAME> puff(s) cold air from <S-HIS-HER> mouth.";
				autoPhrase="A blast of frozen air erupts!";
				stuffWord="cold";
				castPhrase="<S-NAME> blast(s) a frozen cone of frost from <S-HIS-HER> mouth!"+CommonStrings.msp("spelldam1.wav",40);
				WeaponType=Weapon.TYPE_FROSTING;
				break;
		case 'a':
				puffPhrase="<S-NAME> dribble(s) acid harmlessly from <S-HIS-HER> mouth.";
				autoPhrase="A spray of acid erupts!";
				stuffWord="acid";
				castPhrase="<S-NAME> spray(s) acid from <S-HIS-HER> mouth!"+CommonStrings.msp("water.wav",40);
				WeaponType=Weapon.TYPE_MELTING;
				break;
		case 'g':
				puffPhrase="<S-NAME> puff(s) gas harmlessly from <S-HIS-HER> mouth.";
				autoPhrase="A cloud of deadly gas descends!";
				stuffWord="gas";
				castPhrase="<S-NAME> blow(s) deadly gas from <S-HIS-HER> mouth!";
				WeaponType=Weapon.TYPE_GASSING;
				break;

		}
	}

	public boolean invoke(MOB mob, Vector commands, Environmental givenTarget, boolean auto)
	{
		Hashtable h=properTargets(mob,givenTarget,auto);
		if(h==null)
		{
			mob.tell("There doesn't appear to be anyone here worth breathing on.");
			return false;
		}
		if((text().length()==0)
		&&(mob.charStats().getMyRace().racialCategory().equals("Dragon")))
		{
			int color=-1;
			for(int i=0;i<DragonColors.length;i++)
				if(CoffeeUtensils.containsString(mob.Name(),DragonColors[i][0]))
				{ color=i; break;}
			if(color<0)
			for(int i=0;i<DragonColors.length;i++)
				if(CoffeeUtensils.containsString(mob.displayText(),DragonColors[i][0]))
				{ color=i; break;}
			if(color<0)	
				setMiscText("fire");
			else 
				setMiscText(DragonColors[color][1]);
		}

		// the invoke method for spells receives as
		// parameters the invoker, and the REMAINING
		// command line parameters, divided into words,
		// and added as String objects to a vector.
		if(!super.invoke(mob,commands,givenTarget,auto))
			return false;

		boolean success=profficiencyCheck(0,auto);

		if(success)
		{

			if(text().length()==0)
				setMiscText("");
			if(mob.location().show(mob,null,this,Affect.MSG_NOISYMOVEMENT,auto?autoPhrase:castPhrase))
			for(Enumeration f=h.elements();f.hasMoreElements();)
			{
				MOB target=(MOB)f.nextElement();

				// it worked, so build a copy of this ability,
				// and add it to the affects list of the
				// affected MOB.  Then tell everyone else
				// what happened.
				FullMsg msg=new FullMsg(mob,target,this,Affect.MSK_MALICIOUS_MOVE|strikeType|(auto?Affect.MASK_GENERAL:0),null);
				if(mob.location().okAffect(mob,msg))
				{
					mob.location().send(mob,msg);
					invoker=mob;

					int damage = 0;
					int maxDie =  mob.envStats().level();
					if (maxDie > 10)
						maxDie = 10;
					damage += Dice.roll(maxDie,6,1);
					if(msg.wasModified())
						damage = (int)Math.round(Util.div(damage,2.0));
					ExternalPlay.postDamage(mob,target,this,damage,Affect.MASK_GENERAL|Affect.MASK_SOUND|strikeType,WeaponType,"^FThe "+stuffWord+" <DAMAGE> <T-NAME>!^?");
				}
			}
		}
		else
			return maliciousFizzle(mob,null,puffPhrase);


		// return whether it worked
		return success;
	}
}