
import java.util.Arrays;       //remove this after test complete
import java.util.*;
import java.io.*;
import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;


public class readfile {
	//variables to track slot timings
	   public static int[] earliestSlot = {540,780,1980,2220};    // start time of each session
	   	public static int[] endTimeOfSlot = {720,1020,2160,2460};  //end time of each session
	    public static int[] compulsoryEventTimes = {720 , 1020, 2160, 2460};  //start times of compulsory events (Lunch & networking session)
	    public static String[] compulsoryEvents = {"Lunch 60mins", "Networking Session 60mins", "Lunch 60mins", "Networking Session 60mins"}; //names of events	
	    public static String[] submittedTalks;
	    
	public static void main(String[] args){
		
		
	try{
		BufferedReader br = new BufferedReader (new FileReader("input.txt"));

	try {
	StringBuilder input = new StringBuilder();
	String line = br.readLine();
	
	
	 while (line != null) {
         input.append(line);
         input.append("\n");
         line = br.readLine();
     }
	 submittedTalks = input.toString().split("\\\n");
	}
    
  finally {
	 
     br.close();
 }

} 
	catch (IOException e) {
// TODO Auto-generated catch block
e.printStackTrace();
}
	
	  //initialize string to check for
    String word = "min";   
        
   //concatenate compulsory events with submission
    String[] submissions = concat(submittedTalks,compulsoryEvents);
    
    //returns size of the array (to find number of entries)
    int numEntries = submissions.length;
      
    //create two arrays to handle name & duration
    String[] nameOfTalk = new String[numEntries];
   int[] Duration = new int[numEntries];                
  
    //parse submissions into the two arrays
    for (int x = 0;x < numEntries; x++){
 	   
 	   if (submissions[x].lastIndexOf(word) > -1 ){    //checks if the duration is declared explicitly in minutes or via "lightning"		      	   
 	   nameOfTalk[x] = submissions[x].substring(0,submissions[x].lastIndexOf(word)-3);
 	   Duration[x] = Integer.parseInt(submissions[x].substring(submissions[x].lastIndexOf(word)-2,submissions[x].lastIndexOf(word)-0));
 	   }
 	   
 	   else{  																		// instead, parse the string for "lightning" and set Duration = 5
 		   nameOfTalk[x] = submissions[x].substring(0,submissions[x].lastIndexOf("lightning")-1);
     	   Duration[x]= 5;		   
 	   }	  
 	   }   //end "for" loop
      int[] talkStartTime = scheduleEachEvent(Duration, earliestSlot,endTimeOfSlot,submissions, submittedTalks.length,compulsoryEventTimes);  //this stores the start times of the various slots
     printSchedule(talkStartTime,submissions,endTimeOfSlot);	
	
}
	
	

private static int[] scheduleEachEvent (int[] Duration, int[] earliestSlot, int[] endTimeOfSlot,String[] submissions, int lenSubmittedTalks, int[] compulsoryEventTimes){
	        
	int numTalks = Duration.length;   // int to record total number of talks to allocate

	//int numSlots = earliestSlot.length; //int to record total number of available sessions
	int[] talkStartTime = new int[numTalks];  //new array to record start time of each Talk
	System.arraycopy(compulsoryEventTimes, 0, talkStartTime, lenSubmittedTalks, compulsoryEventTimes.length);  //copies Start time of compulsory events into the array
	for(int talkNum = 0; talkNum < lenSubmittedTalks; talkNum++){
		int slotNum = 0; //counter to cycle through sessions
		do {			       //loops until the talk is assigned a start time	
				int currentSlotDuration = endTimeOfSlot[slotNum] - earliestSlot[slotNum];     //temp variable to record available duration of current slot
		if (Duration[talkNum] <= currentSlotDuration){     //checks if time in session is longer than duration of talk, and if slot is not already allocated
			talkStartTime[talkNum] = earliestSlot[slotNum]; //slot will start at the earliest possible time in the session
			earliestSlot[slotNum] = Duration[talkNum] + earliestSlot[slotNum]; //earliest possible start time in the session is updated.
		}
		else {
			//System.out.println("talk exceeds remaining available duration of session");
		    slotNum++;  
		}
		} while(talkStartTime[talkNum] <= 0);
	}
	return talkStartTime;
}

public static String[] concat(String[] submittedTalks, String[] compulsoryEvents){
	int slen = submittedTalks.length; 
	int clen = compulsoryEvents.length;
	String[] submissions = new String[slen+clen];	//creates a new array with size = sum of both input arrays
	System.arraycopy(submittedTalks, 0, submissions, 0, slen);
	System.arraycopy(compulsoryEvents, 0, submissions, slen, clen);
	return submissions;
}

public static void printSchedule(int[] talkStartTime, String[] submissions,int[] endTimeOfSlot){
	//first things first: to print string "Track 1"
	System.out.println("Track 1");
	
	//this section looks for earliest slot:
	int numTalks = talkStartTime.length;  // tool to record total number of Events (compulsory + talks)
	int nextPossibleTime = 0;  //container for earliest time to consider (earlier times which are printed will not be considered)
	int nextStartTime = endTimeOfSlot[endTimeOfSlot.length-1] ;
	int nextStartTimeIndex=0;
	
	for (int count = 0; count< numTalks;count++){			//iterate this method for an equal number of times to the # entries in the array
		for(int talkNum=0; talkNum < numTalks ; talkNum++){
		if (talkStartTime[talkNum] > nextPossibleTime){			//if the talk has not been printed(i.e. start time after last printed time)
			if(talkStartTime[talkNum]<nextStartTime){
				nextStartTime = talkStartTime[talkNum];
				nextStartTimeIndex = talkNum;
			}else{
				//do nothing
			}	
		}
		else if(count == numTalks - 1){
			nextStartTimeIndex = talkStartTime.length-1;
		}else{
			//do nothing
		}
		
	}	
		//variables to hold start time in hrs and minutes
		int startMin = talkStartTime[nextStartTimeIndex] % 60;
		int startHr;
	 String amPM;
 
	 //checks for session number
	   if(talkStartTime[nextStartTimeIndex] < 720){				//morning on Day 1	
		   startHr = talkStartTime[nextStartTimeIndex]/60;	
		   amPM = "AM";
	   }
	   else if(talkStartTime[nextStartTimeIndex] == 720){		 //12pm on Day 1	   		
		   startHr = talkStartTime[nextStartTimeIndex]/60;
		   amPM = "PM";
	   }
	   
	   else if(talkStartTime[nextStartTimeIndex] < 1440){		 //Afternoon on Day 1	   		
		   startHr = talkStartTime[nextStartTimeIndex]/60 - 12;
		   amPM = "PM";
	   } 
	   	   
	   else if(talkStartTime[nextStartTimeIndex] < 2160){  		//Morning on Day 2
	   startHr = talkStartTime[nextStartTimeIndex]/60 - 24;
	   amPM = "AM";
   }
	   else if(talkStartTime[nextStartTimeIndex] == 2160){  		//12pm on Day 2
		   startHr = talkStartTime[nextStartTimeIndex]/60 - 24;
		   amPM = "PM";
	   }
	 
   else{  						//only option left is Afternoon on Day 2
	   startHr = talkStartTime[nextStartTimeIndex]/60 - 36;
	   amPM = "PM";
   }
	   
	   if(talkStartTime[nextStartTimeIndex] > 1440 && nextPossibleTime < 1440){    //prints new line + string "Track 2" if its a new day. otherwise continues
		   System.out.println("\nTrack 2");
	   }

	 //this section appends additional 0 to the front of integer if either hour or min is of a singl digit
	   int startHrLen = String.valueOf(startHr).length();
	   int startMinLen = String.valueOf(startMin).length();
	   
	 if (startHrLen < 2){
		 if(startMinLen < 2){		//both hr and mins are single digits
			 System.out.println("0"+ startHr + ":" + "0" + startMin + amPM + " " + submissions[nextStartTimeIndex] );
		 }
		 else{
			 System.out.println("0"+ startHr + ":" + startMin + amPM + " " + submissions[nextStartTimeIndex] );
		 }
	 }
	 else if(startMinLen <2 ){
		 System.out.println(startHr + ":" + "0" + startMin + amPM + " " + submissions[nextStartTimeIndex]);
	 }
	 else{
		 System.out.println(startHr + ":" + startMin + amPM + " " + submissions[nextStartTimeIndex]);
	 }
	nextPossibleTime = talkStartTime[nextStartTimeIndex]; //pushes start time of printed talk to min start time criteria
	nextStartTime = endTimeOfSlot[endTimeOfSlot.length-1] ;
	}
}

}