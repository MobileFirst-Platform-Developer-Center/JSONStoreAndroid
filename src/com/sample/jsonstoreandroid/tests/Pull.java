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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.test.AndroidTestCase;

import com.worklight.jsonstore.api.JSONStoreChangeOptions;
import com.worklight.jsonstore.api.JSONStoreCollection;
import com.worklight.jsonstore.api.WLJSONStore;
import com.worklight.jsonstore.database.SearchFieldType;
import com.worklight.jsonstore.exceptions.JSONStoreException;

public class Pull extends AndroidTestCase {

	public Pull() {
		super();
	}

	public void testPull() throws Throwable {

		try {
			Context ctx = getContext();

			//Destroy first to start with no data and get predictable results in the test.
			WLJSONStore.getInstance(ctx).destroy();

			//List for all the collections we want to open.
			List<JSONStoreCollection> collections = new LinkedList<JSONStoreCollection>();

			//Create the collection object that will be initialized.
			JSONStoreCollection people = new JSONStoreCollection("people");
			people.setSearchField("id", SearchFieldType.INTEGER);
			people.setSearchField("ssn", SearchFieldType.STRING);
			people.setSearchField("name", SearchFieldType.STRING);
			collections.add(people);			

			//Open collections.
			WLJSONStore.getInstance(ctx).openCollections(collections);

			//TIP: Get data from somewhere (e.g. Worklight Adapter). For this example, it is hard coded.
			List<JSONObject> data = new ArrayList<JSONObject>();

			JSONObject doc1 = new JSONObject("{id: 1, name: 'carlos', ssn: '111-22-3333'}");
			JSONObject doc2 = new JSONObject("{id: 2, name: 'mike', ssn: '444-55-6666'}");

			data.add(doc1);
			data.add(doc2);

			//Options for the change operation.
			JSONStoreChangeOptions options = new JSONStoreChangeOptions();

			//Data that does not exist in the collection will be added, default false.
			options.setAddNew(true); 

			//Mark data as dirty (true = yes, false = no), default false.
			options.setMarkDirty(true);

			//Search fields that exist in the data passed and make it unique inside a collection.
			options.addSearchFieldToCriteria("id");
			options.addSearchFieldToCriteria("ssn");

			//Add data if it doesn't exist, otherwise use the replace criteria to update existing data.
			int changed = people.changeData(data, options);

			assertEquals(2, changed);
			assertEquals(2, people.countAllDocuments());
		} 
		catch (JSONStoreException ex) {
			throw ex;
		}
		catch (Throwable t) {
			throw t;
		}		
	}
}
