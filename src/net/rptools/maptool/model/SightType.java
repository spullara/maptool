package net.rptools.maptool.model;

public class SightType {

	private String name;
	private double multiplier;
	private LightSource personalLightSource;
	
	public SightType() {
		// For serialization
	}
	
	public SightType(String name, double multiplier, LightSource personalLightSource) {
		this.name = name;
		this.multiplier = multiplier;
		this.personalLightSource = personalLightSource;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getMultiplier() {
		return multiplier;
	}
	public void setMultiplier(double multiplier) {
		this.multiplier = multiplier;
	}
	public LightSource getPersonalLightSource() {
		return personalLightSource;
	}
	public void setPersonalLightSource(LightSource personalLightSource) {
		this.personalLightSource = personalLightSource;
	}
	
	
}
