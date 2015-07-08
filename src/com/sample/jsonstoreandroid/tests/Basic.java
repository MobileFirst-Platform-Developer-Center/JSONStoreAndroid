/*
 *
    COPYRIGHT LICENSE: This information contains sample code provided in source code form. You may copy, modify, and distribute
    these sample programs in any form without payment to IBM® for the purposes of developing, using, marketing or distributing
    application programs conforming to the application programming interface for the operating platform for which the sample code is written.
    Notwithstanding anything to the contrary, IBM PROVIDES THE SAMPLE SOURCE CODE ON AN "AS IS" BASIS AND IBM DISCLAIMS ALL WARRANTIES,
    EXPRESS OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, ANY IMPLIED WARRANTIES OR CONDITIONS OF MERCHANTABILITY, SATISFACTORY QUALITY,
    FITNESS FOR A PARTICULAR PURPOSE, TITLE, AND ANY WARRANTY OR CONDITION OF NON-INFRINGEMENT. IBM SHALL NOT BE LIABLE FOR ANY DIRECT,
    INDIRECT, INCIDENTAL, SPECIAL OR CONSEQUENTIAL DAMAGES ARISING OUT OF THE USE OR OPERATION OF THE SAMPLE SOURCE CODE.
    IBM HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS OR MODIFICATIONS TO THE SAMPLE SOURCE CODE.

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
