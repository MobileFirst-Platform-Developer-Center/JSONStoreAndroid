/**
* Copyright 2015 IBM Corp.
*
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

package com.sample.jsonstoreandroid.tests;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.test.AndroidTestCase;

import com.worklight.jsonstore.api.JSONStoreCollection;
import com.worklight.jsonstore.api.WLJSONStore;
import com.worklight.jsonstore.database.SearchFieldType;
import com.worklight.jsonstore.exceptions.JSONStoreException;

public class Basic extends AndroidTestCase {

	public Basic() {
		super();
	}

	public void testBasic() throws Throwable {

		try {
			Context ctx = getContext();
			
			WLJSONStore.getInstance(ctx).destroy();
			
			List<JSONStoreCollection> collections = 
					new LinkedList<JSONStoreCollection>();
			JSONStoreCollection people = new JSONStoreCollection("people");
			people.setSearchField("name", SearchFieldType.STRING);
			people.setSearchField("age", SearchFieldType.INTEGER);
			collections.add(people);

			WLJSONStore.getInstance(ctx).openCollections(collections);
			
			JSONObject data1 = new JSONObject("{age: 20, name: 'carlos'}");
			JSONObject data2 = new JSONObject("{age: 30, name: 'mike'}");

			people.addData(data1);
			people.addData(data2);
			
			List<JSONObject> results = people.findAllDocuments();
						
			assertEquals(
				new JSONObject("{_id: 1, "
						+ "json: {'name' : 'carlos', 'age' : 20}}")
					.toString(),
				results.get(0)
					.toString());
			
			assertEquals(
				new JSONObject("{_id: 2, "
						+ "json: {name: 'mike', age: 30}}")
					.toString(),
				results.get(1)
					.toString());
		} 
		catch (JSONStoreException ex) {
			throw ex;
		}
		catch (Throwable t) {
			throw t;
		}		
	}
}
