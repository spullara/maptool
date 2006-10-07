package net.rptools.maptool.client.ui;


public class StaticMessageDialog extends MessageDialog {

	private String status;
	
	public StaticMessageDialog(String status) {
		this.status = status;
	}

	@Override
	protected String getStatus() {
		return status;
	}
	
}
