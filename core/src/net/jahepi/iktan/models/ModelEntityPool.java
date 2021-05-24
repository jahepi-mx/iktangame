package net.jahepi.iktan.models;

import com.badlogic.gdx.utils.Queue;

public class ModelEntityPool {
	
	public Queue<ModelEntity> queue = new Queue<>();
	int size;
	
	public ModelEntityPool(int size) {
		this.size = size;
	}
	
	public boolean isFull() {
		return queue.size >= size;
	}
	
	public void add(ModelEntity entity) {
		if (!isFull()) {
			queue.addFirst(entity);
		}
	}

	public ModelEntity get() {
		ModelEntity entity = null;
		if (queue.size > 0) {
			entity = queue.first();
			entity.reset();
		}
		if (queue.size >= size) {
			queue.addFirst(queue.removeLast());
		}
		return entity;
	}
}
