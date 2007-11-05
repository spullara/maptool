package net.rptools.maptool.model;

public class AttachedLightSource {

	private GUID lightSourceId;
	private String direction;

	public AttachedLightSource() {
		// for serialization
	}
	
	public AttachedLightSource(LightSource source, Direction direction) {
		lightSourceId = source.getId();
		this.direction = direction.name();
	}
	
	public Direction getDirection() {
		return direction != null ? Direction.valueOf(direction) : Direction.CENTER;
	}

	public GUID getLightSource() {
		return lightSourceId;
	}
}
