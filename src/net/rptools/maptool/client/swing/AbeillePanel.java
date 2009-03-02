/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package net.rptools.maptool.client.swing;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.JPanel;
import javax.swing.JRadioButton;

import yasb.Binder;
import yasb.core.AdapterException;
import yasb.core.BindingInfo;
import yasb.core.Property;
import yasb.core.UpdateTime;
import yasb.swing.AbstractComponentAdapter;
import yasb.swing.BindingResolver;

import com.jeta.forms.components.panel.FormPanel;

/**
 * <p>
 * This class acts as a "field binding front-end" for the {@link FormPanel} class.
 * </p>
 * <p>
 * After instantiating an object and passing it the name of the Abeille form, call
 * the {@link #bind(Object)} method and pass the data model instance as a parameter.
 * This class will will then copy the data from the model to the view using the
 * field names specified when the Abeille form was created.  View field names must start
 * with "@" to be automatically associated with a corresponding model field.
 * </p>
 * <p>
 * As change occur to the view (the user has edited the text fields or changed the value
 * of a radiobutton), those changes will NOT propagate back to the model -- the
 * application programmer must call {@link #commit()} for those changes to be recorded
 * in the model.
 * </p>
 * <p>
 * In all cases, the {@link Binder} class is the one that uses Reflection and standard JavaBean
 * characteristics to link view fields with model fields.
 * </p>
 * @author crash
 *
 * @param <T>
 */
@SuppressWarnings("serial")
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
					System.err.println("Could not init method '" + method.getName() + "': " + e);
				} catch (IllegalAccessException e) {
					System.err.println("Could not init method '" + method.getName() + "': " + e);
				} catch (InvocationTargetException e) {
					System.err.println("Could not init method '" + method.getName() + "': " + e);
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

	/**
	 * Creates the link between the model and the view by calling
	 * {@link Binder#bindContainer(Class, java.awt.Container, UpdateTime)} and passing
	 * it the class of the model, the view component, and when to make the updates.
	 * <p>
	 * This code assumes that the updates will never occur automatically.
	 * </p>
	 * <p>
	 * Also, this code calls the protected method {@link #preModelBind()} to allow
	 * subclasses to modify the binding characteristics before copying the model
	 * fields to the view.
	 * </p>
	 * @param model
	 */
	public void bind(T model) {
		if (this.model != null) {
			throw new IllegalStateException ("Already bound exception");
		}

		this.model = model;
		
		Binder.bindContainer(model.getClass(), panel, UpdateTime.NEVER);
		preModelBind();
		Binder.modelToView(model, panel);
	}
	
	protected void preModelBind() {
		// Do nothing
	}

	/**
	 * This method is invoked by the application code whenever it wants to copy data from
	 * the view to the model.
	 * @return <code>true</code> if successful, <code>false</code> otherwise
	 */
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

	/**
	 * Breaks the binding between the model and the view.
	 */
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
