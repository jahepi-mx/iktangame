package net.jahepi.iktan;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffectLoader;
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem;
import com.badlogic.gdx.graphics.g3d.particles.batches.BillboardParticleBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool;

public class ParticleWrapper {

	AssetManager assets;
	ParticleSystem particleSystem;
	PFXPool pool;
	
	private static class PFXPool extends Pool<ParticleEffect> {
		private ParticleEffect sourceEffect;

		public PFXPool(ParticleEffect sourceEffect) {
			this.sourceEffect = sourceEffect;
		}

		@Override
		public void free(ParticleEffect pfx) {
			pfx.reset();
			super.free(pfx);
		}

		@Override
		protected ParticleEffect newObject() {
			return sourceEffect.copy();
		}
	}
	
	public ParticleWrapper(PerspectiveCamera camera) {
		assets = new AssetManager();
		particleSystem = new ParticleSystem();
		// Since our particle effects are PointSprites, we create a PointSpriteParticleBatch
		BillboardParticleBatch pointSpriteBatch = new BillboardParticleBatch();
		pointSpriteBatch.setCamera(camera);
		particleSystem.add(pointSpriteBatch);
		
		ParticleEffectLoader.ParticleEffectLoadParameter loadParam = new ParticleEffectLoader.ParticleEffectLoadParameter(particleSystem.getBatches());
		assets.load("effect.pfx", ParticleEffect.class, loadParam);
		assets.finishLoading();
		
		pool = new PFXPool((ParticleEffect) assets.get("effect.pfx"));
	}
	
	public void addEffect(Vector3 position) {
		ParticleEffect originalEffect = pool.obtain();
		// we cannot use the originalEffect, we must make a copy each time we create new particle effect
		ParticleEffect effect = originalEffect.copy();
		effect.init();
		effect.start();  // optional: particle will begin playing immediately
		effect.translate(position);
		particleSystem.add(effect);
	}
	
	public void render(ModelBatch modelBatch) {
		particleSystem.update(); // technically not necessary for rendering
		particleSystem.begin();
		particleSystem.draw();
		particleSystem.end();
		modelBatch.render(particleSystem);
	}
}
