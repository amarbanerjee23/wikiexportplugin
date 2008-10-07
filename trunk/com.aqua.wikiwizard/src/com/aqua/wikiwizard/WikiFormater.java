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

public class WikiFormater implements Formater {

	@Override
	public String formatText(String text) {
		if (text == null) {
			return "N/A";
		}
		return text;
	}

	@Override
	public String h1(String header) {
		return "= " + formatText(header) + " =\n" ;
	}

	@Override
	public String h2(String header) {
		return "== " + formatText(header) + " ==\n" ;
	}

	@Override
	public String h3(String header) {
		return "=== " + formatText(header) + " ===\n" ;
	}

	@Override
	public String h4(String header) {
		return "==== " + formatText(header) + " ====\n" ;
	}

	@Override
	public String lineBreak() {
		return "<br />\n";
	}

	@Override
	public String bold(String text) {
		return "'''" + formatText(text) + "'''";
	}

	@Override
	public String tab() {
		return ": ";
	}
	
	@Override
	public String bullet(){
		return "* ";
	}
	
	@Override
    public String link(String link, String title) {
        return " [[" + link + " | " + title  + "]]" ;
    }
}
