/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package report;

import java.util.HashMap;

import core.Coord;
import core.DTNHost;
import core.Message;
import core.MessageListener;

/**
 * Report for how far apart the nodes were when the message
 * was sent and how long time & how many hops it took to deliver it.
 * Only messages created after the warm up period are counted.
 * If message is not delivered, its delivery time & hop count are reported as -1
 */
public class DelayPerMessageReport extends Report implements MessageListener {
	/** Syntax of the report lines */
	private HashMap<String, InfoTuple> creationInfos;
	
	/**
	 * Constructor.
	 */
	public DelayPerMessageReport() {
		init();
	}
	
	@Override
	protected void init() {
		super.init();
		this.creationInfos = new HashMap<String, InfoTuple>();
		printHeader();
	}

	/**
	 * This is called when a message is transferred between nodes
	 */
	public void messageTransferred(Message m, DTNHost from, DTNHost to,
			boolean firstDelivery) {
		if (isWarmupID(m.getId()) || !firstDelivery) {
			return; // report is only interested of first deliveries  
		}
		
		InfoTuple info = this.creationInfos.remove(m.getId());
		if (info == null) {
			return; /* message was created before the warm up period */
		}
		
		report(m.getId(), getSimTime() - info.getTime(), getSimTime(), info.getTime());
	}

	/**
	 * This is called when a new message is created
	 */
	public void newMessage(Message m) {
		if (isWarmup()) {
			addWarmupID(m.getId());
			return;
		}
		
		this.creationInfos.put( m.getId(), new InfoTuple(getSimTime()) );
	}

	/**
	 * Writes an informative header in the beginning of the file
	 */
	private void printHeader() {
		write("Message\tDelay");
	}
	
	/**
	 * Writes a report line
	 * @param id Id of the message
	 * @param startDistance Distance of the nodes when the message was creted
	 * @param time Time it took for the message to be delivered
	 * @param hopCount The amount of hops it took to deliver the message
	 */
	private void report(String id, double time, double simTime, double time1) {
		write(id + "\t" + format(time) + "\t" + simTime + "\t" + time1);
	}
	
	/* nothing to implement for the rest */
	public void messageDeleted(Message m, DTNHost where, boolean dropped) {}
	public void messageTransferStarted(Message m, DTNHost from, DTNHost to) {}
	public void messageTransferAborted(Message m, DTNHost from, DTNHost to) {}

	public void done() {
		// report rest of the messages as 'not delivered' (time == -1)
//		for (String id : creationInfos.keySet()) {
//			InfoTuple info = creationInfos.get(id);
//			report(id, info.getLoc1().distance(info.getLoc2()), -1, -1);
//		}
		super.done();
	}
	
 	/**
	 * Private class that encapsulates time and location related information
	 */
	private class InfoTuple {
		private double time;
//		private Coord loc1;
//		private Coord loc2;

		public InfoTuple(double time) {
			this.time = time;
//			this.loc1 = loc1;
//			this.loc2 = loc2;
		}

//		public Coord getLoc1() {
//			return loc1;
//		}
//
//		public Coord getLoc2() {
//			return loc2;
//		}

		public double getTime() {
			return time;
		}
	}

}
