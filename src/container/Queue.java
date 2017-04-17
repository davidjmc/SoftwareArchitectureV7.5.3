package container;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import utils.Utils;

public class Queue {
	private BlockingQueue<Object> queue;

	public Queue() {
		this.queue = new ArrayBlockingQueue<Object>(Utils.BLOCKING_QUEUE_MAX_SIZE);
	}

	public BlockingQueue<Object> getQueue() {
		return queue;
	}

	public void setQueue(BlockingQueue<Object> queue) {
		this.queue = queue;
	}
}
