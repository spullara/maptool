package net.rptools.maptool.client.swing;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import javax.swing.AbstractButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import yasb.Binder;
import yasb.YLogger;
import yasb.core.AdapterException;
import yasb.core.BindingInfo;
import yasb.core.Property;
import yasb.core.UpdateTime;
import yasb.swing.AbstractComponentAdapter;
import yasb.swing.BindingResolver;

import com.jeta.forms.components.panel.FormPanel;

public class AbeillePanel <T> extends JPanel {

	private FormPanel panel;

	private T model;
	
	static {
		Binder.setDefaultAdapter(JRadioButton.class, RadioButtonAdapter.class);
		Binder.setBindingResolver(new BindingResolver() {
			public BindingInfo getBindingInfo(Component view) {
				String name = view.getName();
				if (name == null || !name.startsWith("@")) {
					return null;
				}
				
//				System.out.println("Name:" + name);
				name = name.substring(1); // cut the "@"
				int point = name.indexOf(".");
				if (point >= 0) {
					name = name.substring(0, point);
				}
				
				return new BindingInfo(name);
			}			
			public void storeBindingInfo(Component view, BindingInfo info) {
			}
		});
	}
	
	public AbeillePanel(String panelForm) {
		setLayout(new GridLayout());

		panel = new FormPanel(panelForm);

		add(panel);
		
	}

	public T getModel() {
		return model;
	}
	
	/** 
	 * Call any method on the class that matches "init*" that has zero arguments
	 */
	protected void panelInit() {

		for (Method method : getClass().getMethods()) {

			if (method.getName().startsWith("init")) {
				try {
					method.invoke(this, new Object[]{});
				} catch (IllegalArgumentException e) {
					System.err.println("Coule not init method '" + method.getName() + "': " + e);
				} catch (IllegalAccessException e) {
					System.err.println("Coule not init method '" + method.getName() + "': " + e);
				} catch (InvocationTargetException e) {
					System.err.println("Coule not init method '" + method.getName() + "': " + e);
					e.getCause().printStackTrace();
				}
			}
		}
	}

	protected void replaceComponent(String panelName, String name, Component component) {
		panel.getFormAccessor(panelName).replaceBean(name, component);
		panel.reset();
	}
	
	protected Component getComponent(String name) {
		return panel.getComponentByName(name);
	}

	public void bind(T model) {
		if (this.model != null) {
			throw new IllegalStateException ("Already bound exception");
		}

		this.model = model;
		
		Binder.bindContainer(model.getClass(), panel, UpdateTime.NEVER);
		Binder.modelToView(model, panel);
	}
	
	public boolean commit() {
		if (model != null) {
			
			try {
				Binder.viewToModel(model, panel);
			} catch (AdapterException e) {
				e.printStackTrace();
				return false;
			}
		}
		
		return true;
	}
	
	public void unbind() {
		
		model = null;
	}
	
	public static class RadioButtonAdapter extends AbstractComponentAdapter implements ItemListener {

		private JRadioButton button;
		private Enum selected;

		////
		// COMPONENT ADAPTER
		@Override
		protected Object getActualContent() {
			try {
				return getValue();
			} catch (Exception e) {
//				YLogger.logException(e);
				return null;
			}
		}
		
		@Override
		protected Object getValue() throws Exception {
			return button.isSelected() ? selected : null;
		}
		
		@Override
		protected void setupListener() {
			button.addItemListener(this);
		}
		
		@Override
		protected void showValue(Object value) {
			
			if (value == selected) {
				button.setSelected(true);
			}
		}
		
		@Override
		public void viewToModel(Object dataSource) throws AdapterException {
			if (!button.isSelected()) {
				return;
			}
			super.viewToModel(dataSource);
		}
		
		public void bind(Property property, Component view, UpdateTime updateTime) {
//			System.out.println("bind:" + view.getName() + " - " + view);
			if (view instanceof JRadioButton) {
				button = (JRadioButton) view;
				super.bind(property, view, updateTime);
				
				String bindVal = button.getName();
				bindVal = bindVal.substring(bindVal.indexOf(".")+1);
				
				selected = Enum.valueOf(property.getType(), bindVal);
			}

		}

		////
		// ITEM LISTENER
		public void itemStateChanged(ItemEvent e) {
			fireViewChanged();
			fireViewEditValidated();
		}
	}
}
