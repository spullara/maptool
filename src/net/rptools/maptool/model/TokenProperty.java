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
package net.rptools.maptool.model;

import java.io.Serializable;

public class TokenProperty implements Serializable {
	private String name;
	private String shortName;
	private boolean highPriority; // showOnStatSheet; so that 1.3b28 files load in 1.3b29
	private boolean ownerOnly;
	private boolean gmOnly;
	private String defaultValue;

	public TokenProperty() {
		// For serialization
	}

	public TokenProperty(String name) {
		this(name, null, false, false, false);
	}

	public TokenProperty(String name, String shortName) {
		this(name, shortName, false, false, false);
	}

	public TokenProperty(String name, boolean highPriority, boolean isOwnerOnly, boolean isGMOnly) {
		this(name, null, highPriority, isOwnerOnly, isGMOnly);
	}
	public TokenProperty(String name, String shortName, boolean highPriority, boolean isOwnerOnly, boolean isGMOnly) {
		this.name = name;
		this.shortName = shortName;
		this.highPriority = highPriority;
		this.ownerOnly = isOwnerOnly;
		this.gmOnly = isGMOnly;
	}
	public TokenProperty(String name, String shortName, boolean highPriority, boolean isOwnerOnly, boolean isGMOnly, String defaultValue) {
		this.name = name;
		this.shortName = shortName;
		this.highPriority = highPriority;
		this.ownerOnly = isOwnerOnly;
		this.gmOnly = isGMOnly;
		this.defaultValue= defaultValue;
	}

	public boolean isOwnerOnly() {
		return ownerOnly;
	}

	public void setOwnerOnly(boolean ownerOnly) {
		this.ownerOnly = ownerOnly;
	}

	public boolean isShowOnStatSheet() {
		return highPriority;
	}
	public void setShowOnStatSheet(boolean showOnStatSheet) {
		this.highPriority = showOnStatSheet;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public boolean isGMOnly() {
		return gmOnly;
	}

	public void setGMOnly(boolean gmOnly) {
		this.gmOnly = gmOnly;
	}

	public String getDefaultValue()
	{
		return this.defaultValue;
	}

	public void setDefaultValue(String defaultValue)
	{
		this.defaultValue = defaultValue;
	}
}
