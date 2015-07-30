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

import android.content.Context;
import android.test.AndroidTestCase;

import com.worklight.jsonstore.api.JSONStoreCollection;
import com.worklight.jsonstore.api.JSONStoreInitOptions;
import com.worklight.jsonstore.api.WLJSONStore;
import com.worklight.jsonstore.database.SearchFieldType;
import com.worklight.jsonstore.exceptions.JSONStoreException;

public class OpenCollections extends AndroidTestCase {

	public OpenCollections() {
		super();
	}

	public void testOpenCollections() throws Throwable {

		try {
			Context ctx = getContext();
			
			//Destroy first to start with no data and get predictable results in the test.
			WLJSONStore.getInstance(ctx).destroy();
			
			//List for all the collections we want to open.
			List<JSONStoreCollection> collections = new LinkedList<JSONStoreCollection>();
			
			//Create the collection objects that will be initialized.
			JSONStoreCollection people = new JSONStoreCollection("people");
			people.setSearchField("name", SearchFieldType.STRING);
			people.setSearchField("age", SearchFieldType.INTEGER);
			collections.add(people);

			JSONStoreCollection orders = new JSONStoreCollection("orders");
			people.setSearchField("item", SearchFieldType.STRING);
			collections.add(orders);			
			
			//Optional options object.
			JSONStoreInitOptions options = new JSONStoreInitOptions();
			//Optional username, default 'jsonstore'.
			options.setUsername("carlos");
			//Optional password, default no password.
			options.setPassword("123");
			
			//Open collections.
			WLJSONStore.getInstance(ctx).openCollections(collections, options);
			
			assertEquals("people", people.getName());
			assertEquals("orders", orders.getName());
		} 
		catch (JSONStoreException ex) {
			throw ex;
		}
		catch (Throwable t) {
			throw t;
		}		
	}
}
