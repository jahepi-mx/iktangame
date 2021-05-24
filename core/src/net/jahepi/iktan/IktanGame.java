package net.jahepi.iktan;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;

import net.jahepi.iktan.models.ModelEntity;
import net.jahepi.iktan.models.ModelEntityPool;
import net.jahepi.iktan.utils.CameraHelper;

public class IktanGame extends InputAdapter implements ApplicationListener {

	public PerspectiveCamera cam;
	public ModelBatch modelBatch;
	public AssetManager assets;
	public Environment environment;
	public boolean loading = true;
	private CameraHelper camera;
	private ModelEntity model;
	private ParticleWrapper particleWrapper;
	private Sound sound;
	private ModelEntityPool pool = new ModelEntityPool(6);

	@Override
	public void create() {
		modelBatch = new ModelBatch();
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

		cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(0, 0, 0);
		cam.lookAt(0, 0, 0);
		cam.near = 1f;
		cam.far = 300f;
		// cam.position.z = 5;
		cam.update();
		camera = new CameraHelper(cam);

		assets = new AssetManager();
		assets.load("ship.obj", Model.class);
		particleWrapper = new ParticleWrapper(cam);
		Gdx.input.setInputProcessor(this);
		sound = Gdx.audio.newSound(Gdx.files.internal("magic.ogg"));
	}

	private void doneLoading() {
		//model = new ModelEntity(assets.get("ship.obj", Model.class));
		//models.add(model);
		if (!pool.isFull()) {
			pool.add(new ModelEntity(assets.get("ship.obj", Model.class)));
		}
		model = pool.get();
		loading = false;
	}

	@Override
	public void render() {

		float dt = Gdx.graphics.getDeltaTime();

		if (loading && assets.update()) {
			doneLoading();
		}

		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		if (model != null) {
			camera.moveCameraPosToTarget(model, dt);
			camera.moveCameraDirToTarget(model, dt);
		}

		cam.update();

		modelBatch.begin(cam);
		for (ModelEntity model : pool.queue) {
			modelBatch.render(model.modelInstance, environment);
		}
		particleWrapper.render(modelBatch);
		modelBatch.end();
	}

	@Override
	public void dispose() {
		modelBatch.dispose();
		assets.dispose();
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		Ray ray = cam.getPickRay(screenX, screenY);
		if (model.collide(ray)) {
			particleWrapper.addEffect(model.getPosition());
			if (!pool.isFull()) {
				pool.add(new ModelEntity(assets.get("ship.obj", Model.class)));
			}
			model = pool.get();
			Gdx.input.vibrate(1000 * 1);
			sound.play();
		}
		return true;
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}
}
