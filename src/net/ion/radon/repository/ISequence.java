package net.ion.radon.repository;

public interface ISequence {

	public long currVal();

	public void reset();

	public long nextVal();

}
