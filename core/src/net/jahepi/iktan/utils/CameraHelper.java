package net.jahepi.iktan.utils;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

import net.jahepi.iktan.models.ModelEntity;

public class CameraHelper {

	private PerspectiveCamera camera;
	private float theta;
	private float phi = -90;
	private Vector3 dir = new Vector3();

	public CameraHelper(PerspectiveCamera camera) {
		this.camera = camera;
	}

	public void addTheta(float radians) {
		theta += radians;
		if (theta * MathUtils.radiansToDegrees >= 89) {
			theta = 89 * MathUtils.degreesToRadians;
		}
		if (theta * MathUtils.radiansToDegrees <= -89) {
			theta = -89 * MathUtils.degreesToRadians;
		}
	}

	public void addPhi(float radians) {
		phi += radians;
	}

	public Vector3 getDirVector() {
		dir.y = MathUtils.sin(theta);
		dir.x = MathUtils.cos(theta) * MathUtils.cos(phi);
		dir.z = MathUtils.cos(theta) * MathUtils.sin(phi);
		return dir.nor();
	}

	public void moveCameraPosToTarget(ModelEntity model, float deltatime) {
		Vector3 position = model.getPosition();
		Vector3 modelDir = model.getDirection();
		float distanceFromCamera = 4;
		Vector3 scaledDir = new Vector3(modelDir.x * distanceFromCamera, modelDir.y * distanceFromCamera, modelDir.z * distanceFromCamera);
		position.add(scaledDir);

		float speed = 0.6f;
		float x = (position.x - camera.position.x) * deltatime * speed;
		float y = (position.y - camera.position.y) * deltatime * speed;
		float z = (position.z - camera.position.z) * deltatime * speed;

		this.camera.position.add(x, y, z);
	}

	public void moveCameraDirToTarget(ModelEntity model, float deltatime) {
		Vector3 modelDir = model.getDirection();
		Vector3 up = model.getUpVector();

		modelDir.x *= -1;
		modelDir.y *= -1;
		modelDir.z *= -1;

		float speed = 0.6f;
		float x = (modelDir.x - camera.direction.x) * deltatime * speed;
		float y = (modelDir.y - camera.direction.y) * deltatime * speed;
		float z = (modelDir.z - camera.direction.z) * deltatime * speed;

		Vector3 newDir = new Vector3(camera.direction.x + x, camera.direction.y + y, camera.direction.z + z);
		newDir.nor();

		camera.direction.x = newDir.x;
		camera.direction.y = newDir.y;
		camera.direction.z = newDir.z;

		x = (up.x - camera.up.x) * deltatime * speed;
		y = (up.y - camera.up.y) * deltatime * speed;
		z = (up.z - camera.up.z) * deltatime * speed;

		Vector3 newUpDir = new Vector3(camera.up.x + x, camera.up.y + y, camera.up.z + z);
		newUpDir.nor();

		camera.up.x = newUpDir.x;
		camera.up.y = newUpDir.y;
		camera.up.z = newUpDir.z;

	}

	public Vector3 getWalkVector() {
		Vector3 v = dir.cpy();
		v.y = 0;
		return v.nor();
	}
}
