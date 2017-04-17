package framework.basic;

import container.Queue;

public interface IElement {

	public void i_PreInvP(Queue local);

	public void invP(Queue local, Queue remote);

	public void i_PosInvP(Queue local);

	public void i_PreTerP(Queue local);

	public void terP(Queue local, Queue remote);

	public void i_PosTerP(Queue local);

	public void i_PreInvR(Queue local);

	public void invR(Queue local, Queue remote);

	public void i_PosInvR(Queue local);

	public void i_PreTerR(Queue local);

	public void terR(Queue local, Queue remote);

	public void i_PosTerR(Queue local);
}