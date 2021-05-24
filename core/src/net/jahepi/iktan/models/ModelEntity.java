package net.jahepi.iktan.models;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;

public class ModelEntity {

	Model model;
	public ModelInstance modelInstance;
	BoundingBox boundingBox = new BoundingBox();
	Vector3 center = new Vector3(), dimensions = new Vector3(), position = new Vector3();
	float halfLen;

	public ModelEntity(Model model) {
		this.model = model;
		modelInstance = new ModelInstance(model);
		this.model.calculateBoundingBox(boundingBox);
		boundingBox.getCenter(center);
		boundingBox.getDimensions(dimensions);
		halfLen = dimensions.len() * 0.5f;
		position = new Vector3(MathUtils.random(-5, 5), MathUtils.random(-5, 5),
				MathUtils.random(-5, 5));
		modelInstance.transform.translate(position);

		modelInstance.transform.rotate(Vector3.Z, MathUtils.random(-89, 89));
		modelInstance.transform.rotate(Vector3.Y, MathUtils.random(0, 90));
		modelInstance.transform.rotate(Vector3.X, MathUtils.random(-89, 89));
	}
	
	public void reset() {
		Vector3 origin = position.cpy().scl(-1);
		modelInstance.transform.translate(origin);
		modelInstance.transform.translate(MathUtils.random(-5, 5), MathUtils.random(-5, 5),
				MathUtils.random(-5, 5));

		modelInstance.transform.rotate(Vector3.Z, MathUtils.random(-89, 89));
		modelInstance.transform.rotate(Vector3.Y, MathUtils.random(0, 90));
		modelInstance.transform.rotate(Vector3.X, MathUtils.random(-89, 89));
	}

	public Vector3 getDirection() {
		Vector3 vector = new Vector3(0, 0, 1).rot(modelInstance.transform);
		return vector;
	}

	public Vector3 getUpVector() {
		Vector3 vector = new Vector3(0, 1, 0).rot(modelInstance.transform);
		return vector;
	}

	public Vector3 getPosition() {
		Vector3 position = new Vector3();
		modelInstance.transform.getTranslation(position);
		position.add(center);
		return position;
	}

	public boolean collide(Ray ray) {

		Vector3 position = getPosition();

		Vector3 rayToModelVec = new Vector3();
		rayToModelVec.x = position.x - ray.origin.x;
		rayToModelVec.y = position.y - ray.origin.y;
		rayToModelVec.z = position.z - ray.origin.z;

		float projectionRatio = rayToModelVec.dot(ray.direction) / ray.direction.dot(ray.direction);
		Vector3 scaledTrans = ray.direction.scl(projectionRatio).add(ray.origin);

		Vector3 diff = scaledTrans.sub(position);
		//System.out.println(diff.len() + "," + halfLen);
		return diff.len2() <= halfLen * halfLen;
	}

	public void dispose() {
		model.dispose();
	}
}
