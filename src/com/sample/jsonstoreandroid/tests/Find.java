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
import com.worklight.jsonstore.api.JSONStoreFindOptions;
import com.worklight.jsonstore.api.JSONStoreQueryPart;
import com.worklight.jsonstore.api.JSONStoreQueryParts;
import com.worklight.jsonstore.api.WLJSONStore;
import com.worklight.jsonstore.database.SearchFieldType;
import com.worklight.jsonstore.exceptions.JSONStoreException;

public class Find extends AndroidTestCase {

	public Find() {
		super();
	}

	public void testFind() throws Throwable {

		try {
			Context ctx = getContext();

			//Destroy first to start with no data and get predictable results in the test.
			WLJSONStore.getInstance(ctx).destroy();

			//List for all the collections we want to open.
			List<JSONStoreCollection> collections = new LinkedList<JSONStoreCollection>();

			//Create the collection object that will be initialized.
			JSONStoreCollection people = new JSONStoreCollection("people");
			people.setSearchField("name", SearchFieldType.STRING);
			people.setSearchField("age", SearchFieldType.INTEGER);
			collections.add(people);			

			//Open collections.
			WLJSONStore.getInstance(ctx).openCollections(collections);

			//Add data.
			people.addData(new JSONObject("{age: 20, name: 'carlos'}"));
			people.addData(new JSONObject("{age: 30, name: 'mike'}"));

			//Build the query parts
			JSONStoreQueryParts quertParts = new JSONStoreQueryParts();
			JSONStoreQueryPart part = new JSONStoreQueryPart();
			part.addEqual("name", "carlos");
			part.addLessThanOrEqual("age", 20);
			quertParts.addQueryPart(part);

			//Add additional find options (optional).
			JSONStoreFindOptions findOptions = new JSONStoreFindOptions();

			//Returns a maximum of 10 documents, default no limit.
			findOptions.setLimit(10);
			
			//Skip 0 documents, default no offset.
			findOptions.setOffset(0);

			//Search fields to return, default: '_id' and 'json'.
			findOptions.addSearchFilter("_id");
			findOptions.addSearchFilter("json");

			//How to sort the returned values, default no sort.
			findOptions.sortBySearchFieldAscending("name");
			findOptions.sortBySearchFieldDescending("age");

			//Find documents that match the query.
			List<JSONObject> results = people.findDocuments(quertParts, findOptions);

			assertEquals("carlos", results.get(0).getJSONObject("json").getString("name"));
		} 
		catch (JSONStoreException ex) {
			throw ex;
		}
		catch (Throwable t) {
			throw t;
		}		
	}
}
