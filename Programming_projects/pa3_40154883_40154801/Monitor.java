import java.util.PriorityQueue;
/**
 * Class Monitor
 * To synchronize dining philosophers.
 *
 * @author Serguei A. Mokhov, mokhov@cs.concordia.ca
 */

public class Monitor
{
	/*
	 * ------------
	 * Data members
	 * ------------
	 */

	private enum STATUS{thinking,hungry,eating};
	private int NumPhilo;
	private STATUS[] state;

	
	private boolean talking;

	private PriorityQueue<Integer> hungryList;
	


	/**
	 * Constructor
	 */
	public Monitor(int piNumberOfPhilosophers)
	{
		NumPhilo = piNumberOfPhilosophers;
		state = new STATUS[NumPhilo];

		
		// initialize all philosophers to thinking
		for(int i = 0; i < NumPhilo; i++) {
			state[i] = STATUS.thinking;
		}

	
		  //Creating a new hungryList for the constructor
		 
		hungryList = new PriorityQueue<Integer>();

		// initially, no philosopher is talking
		talking = false;

		
	}

	/*
	 * -------------------------------
	 * User-defined monitor procedures
	 * -------------------------------
	 */
	public synchronized void test (int i)
	{
		try
		{
			while(true)
			{
				if(state[(i+1)% NumPhilo]!=STATUS.eating && state[(i+(NumPhilo-1))% NumPhilo]!=STATUS.eating && state[i] == STATUS.hungry)
				{
					state[i]=STATUS.eating;
					break;
				}
				else
				{
					wait();
				}
			}
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Grants request (returns) to eat when both chopsticks/forks are available.
	 * Else forces the philosopher to wait()
	 */
	public synchronized void pickUp(final int piTID)
	{
		// ...
		int i = piTID - 1;

		state[i] = STATUS.hungry;

		
		// remove the philosopher to hungry List. 
		hungryList.add(piTID);

		test(i);
		
		// remove the philosopher from the hungry list since they are already eating
		hungryList.remove();

	}

	/**
	 * When a given philosopher's done eating, they put the chopstiks/forks down
	 * and let others know they are available.
	 */
	public synchronized void putDown(final int piTID)
	{
		// ...
		int i = piTID - 1;

		state[i]= STATUS.thinking;

		notifyAll();
	}

	/**
	 * Only one philopher at a time is allowed to philosophy
	 * (while she is not eating).
	 */
	public synchronized void requestTalk()
	{
		// ...
		if(talking) 
		{
			try 
			{

				/**
				 * Wait until another philosopher is still talking, when he's done, you request to talk
				 */
				wait();
				requestTalk();
			} 

			catch(InterruptedException e) 
			{
				System.out.println("A philosopher is speaking . Please wait!!");
			}

			// the philosopher is talking
			talking = true;
		}
	}

	/**
	 * When one philosopher is done talking stuff, others
	 * can feel free to start talking.
	 */
	public synchronized void endTalk()
	{
		// ...
		// the philosopher is no longer talking
		talking = false;

		// Notify all the other threads that you are the one done talking, and now they have a chance to speak as well.
		notifyAll();
	}
}

// EOF
