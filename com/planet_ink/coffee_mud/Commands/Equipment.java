package com.planet_ink.coffee_mud.Commands;
import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import com.planet_ink.coffee_mud.utils.*;
import java.util.*;

/* 
   Copyright 2000-2004 Bo Zimmerman

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
public class Equipment extends StdCommand
{
	public Equipment(){}

	private String[] access={"EQUIPMENT","EQ","EQUIP"};
	public String[] getAccessWords(){return access;}

	public static StringBuffer getEquipment(MOB seer, MOB mob, boolean allPlaces)
	{
		StringBuffer msg=new StringBuffer("");
		if(Sense.isSleeping(seer))
			return new StringBuffer("(nothing you can see right now)");

	    int numTattsDone=0;
	    long wornCode=0;
	    String header=null;
	    int found=0;
	    String wornName=null;
	    Item thisItem=null;
	    String tat=null;
	    int numWears=0;
		for(int l=0;l<Item.wornOrder.length;l++)
		{
		    found=0;
			wornCode=Item.wornOrder[l];
			wornName=Sense.wornLocation(wornCode);
			header="^N(^H"+wornName+"^?)";
			header+=Util.SPACES.substring(0,26-header.length())+": ^!";
			for(int i=0;i<mob.inventorySize();i++)
			{
				thisItem=mob.fetchInventory(i);
				if((thisItem.container()==null)&&(thisItem.amWearingAt(wornCode)))
				{
					found++;
					if(Sense.canBeSeenBy(thisItem,seer))
					{
						String name=thisItem.name();
						if(name.length()>53) name=name.substring(0,50)+"...";
						if(mob==seer)
							msg.append(header+"^<EItem^>"+name+"^</EItem^>"+Sense.colorCodes(thisItem,seer)+"^?\n\r");
						else
							msg.append(header+name+Sense.colorCodes(thisItem,seer)+"^?\n\r");
					}
					else
					if(seer==mob)
						msg.append(header+"(something you can`t see)"+Sense.colorCodes(thisItem,seer)+"^?\n\r");
				}
			}
			numWears=mob.getWearPositions(wornCode);
			if(found<numWears)
			{
			    numTattsDone=found;
			    wornName=wornName.toUpperCase();
				for(int i=0;i<mob.numTattoos();i++)
				{
				    tat=mob.fetchTattoo(i).toUpperCase();
				    if((tat.startsWith(wornName+":"))
				    &&((++numTattsDone)<=numWears))
				    {
				        tat=Util.capitalize(tat.substring(wornName.length()+1).toLowerCase());
						if(tat.length()>53) tat=tat.substring(0,50)+"...";
						msg.append(header+tat+"^?\n\r");
				    }
				}
			}
			if((allPlaces)&&(wornCode!=Item.FLOATING_NEARBY))
			{
				int total=mob.getWearPositions(wornCode)-found;
				for(int i=0;i<total;i++)
					msg.append(header+"^?\n\r");
			}
		}
		if(msg.length()==0)
			msg.append("^!(nothing)^?\n\r");
		return msg;
	}

	public boolean execute(MOB mob, Vector commands)
		throws java.io.IOException
	{
		if((commands.size()==1)&&(commands.firstElement() instanceof MOB))
		{
			commands.addElement(getEquipment((MOB)commands.firstElement(),mob,false));
			return true;
		}
		if(!mob.isMonster())
		{
			if((commands.size()>1)&&(Util.combine(commands,1).equalsIgnoreCase("long")))
				mob.session().wraplessPrintln("You are wearing:\n\r"+getEquipment(mob,mob,true));
			else
				mob.session().wraplessPrintln("You are wearing:\n\r"+getEquipment(mob,mob,false));
		}
		return false;
	}
	public int ticksToExecute(){return 0;}
	public boolean canBeOrdered(){return true;}

	public int compareTo(Object o){ return CMClass.classID(this).compareToIgnoreCase(CMClass.classID(o));}
}
