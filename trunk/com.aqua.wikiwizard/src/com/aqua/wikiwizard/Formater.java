/**
 * Copyright 2008, AQUA Software
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aqua.wikiwizard;

/**
 * Formater interface.
 * This interface defines all methods required for formatting the object.
 * @author Guy Arieli, Michael Oziransky
 */
public interface Formater {
	public String h1(String header);
	public String h2(String header);
	public String h3(String header);
	public String h4(String header);
	public String bold(String text);
	public String tab();
	public String lineBreak();
	public String formatText(String text);
	public String link(String link, String title);
	public String bullet();
	
}
