package it.unibo.pps2017.core.gui;

public class Command {

	/* Dovrà essere busso, striscio o volo */
	String command;
	
	public Command(final String command) {
		this.command = command;
	}
	
	public String getCommand() {
		return this.command;
	}
}
