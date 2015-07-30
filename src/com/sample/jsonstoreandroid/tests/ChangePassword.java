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

public class ChangePassword extends AndroidTestCase {

	public ChangePassword() {
		super();
	}

	public void testChangePassword() throws Throwable {

		try {
			Context ctx = getContext();

			//Destroy first to start with no data and get predictable results in the test.
			WLJSONStore.getInstance(ctx).destroy();

			//The password should be user input. It is hard coded in the example for brevity.
			String oldPassword = "123";
			String newPassword = "456";
			String username = "carlos";

			//List for all the collections we want to open.
			List<JSONStoreCollection> collections = new LinkedList<JSONStoreCollection>();

			//Create the collections object that will be initialized.
			JSONStoreCollection people = new JSONStoreCollection("people");
			people.setSearchField("name", SearchFieldType.STRING);
			people.setSearchField("age", SearchFieldType.INTEGER);
			collections.add(people);

			//Optional options object.
			JSONStoreInitOptions options = new JSONStoreInitOptions();
			//Optional username, default 'jsonstore'.
			options.setUsername(username);
			//Optional password, default no password.
			options.setPassword(oldPassword);

			//Open collections.
			WLJSONStore.getInstance(ctx).openCollections(collections, options);
			
			//Change the current password (oldPassword) to a new password (newPassword).
			WLJSONStore.getInstance(ctx).changePassword(username, oldPassword, newPassword);
			
			options.setPassword(newPassword);
			
			WLJSONStore.getInstance(ctx).openCollections(collections, options);
			
			assertEquals(0, people.countAllDocuments());
			
			//Remove the passwords from memory.
		    oldPassword = null;
		    newPassword = null;
		} 
		catch (JSONStoreException ex) {
			// Handle failure for any of the previous JSONStore operations.
			throw ex;
		}
		catch (Throwable t) {
			throw t;
		}		
	}
}
