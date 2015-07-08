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

package com.sample.jsonstoreandroid;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.worklight.jsonstore.api.JSONStoreAddOptions;
import com.worklight.jsonstore.api.JSONStoreChangeOptions;
import com.worklight.jsonstore.api.JSONStoreCollection;
import com.worklight.jsonstore.api.JSONStoreFileInfo;
import com.worklight.jsonstore.api.JSONStoreFindOptions;
import com.worklight.jsonstore.api.JSONStoreInitOptions;
import com.worklight.jsonstore.api.JSONStoreQueryPart;
import com.worklight.jsonstore.api.JSONStoreQueryParts;
import com.worklight.jsonstore.api.JSONStoreRemoveOptions;
import com.worklight.jsonstore.api.JSONStoreReplaceOptions;
import com.worklight.jsonstore.api.WLJSONStore;
import com.worklight.jsonstore.database.SearchFieldType;
import com.worklight.wlclient.api.WLClient;
import com.worklight.wlclient.api.WLFailResponse;
import com.worklight.wlclient.api.WLProcedureInvocationData;
import com.worklight.wlclient.api.WLResponse;
import com.worklight.wlclient.api.WLResponseListener;

public class MainActivity extends Activity {

	private WLClient client;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initFieldsAndButtons();

		client = WLClient.createInstance(this);
		
		client.connect(new WLResponseListener(){

			@Override
			public void onFailure(WLFailResponse arg0) {
				@SuppressWarnings("unused")
				WLFailResponse r = arg0;
			}

			@Override
			public void onSuccess(WLResponse arg0) {
				@SuppressWarnings("unused")
				WLResponse r = arg0;
				
			}
			
		});

		final Context context = getApplicationContext();

		initButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String username = usernameField.getText().toString();
				String password = passwordField.getText().toString();

				try {

					List<JSONStoreCollection> collections = new LinkedList<JSONStoreCollection>();
					people = new JSONStoreCollection(PEOPLE_COLLECTION_NAME);
					people.setSearchField("name", SearchFieldType.STRING);
					people.setSearchField("age", SearchFieldType.INTEGER);
					collections.add(people);

					JSONStoreInitOptions initOptions = new JSONStoreInitOptions();

					if (username.length() > 0) {
						initOptions.setUsername(username);
					} else {
						initOptions.setUsername(DEFAULT_USER);
					}

					if (password.length() > 0) {
						initOptions.setPassword(password);
					}

					WLJSONStore.getInstance(context).openCollections(collections, initOptions);

					logMessage(getString(R.string.init_message));

					usernameField.setText(null);
					passwordField.setText(null);

				} catch (final Exception e) {
					logError(e.getMessage());
				}
			}

		});

		closeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					WLJSONStore.getInstance(context).closeAll();

					logMessage(getString(R.string.close_message));
				} catch (Exception e) {
					logError(e.getMessage());
				}
			}
		});

		destroyButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					WLJSONStore.getInstance(context).destroy();

					logMessage(getString(R.string.destroy_message));

				} catch (Exception e) {
					logError(e.getMessage());
				}
			}

		});

		removeCollectionButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					WLJSONStore.getInstance(context).getCollectionByName(PEOPLE_COLLECTION_NAME).removeCollection();

					logMessage(getString(R.string.remove_collection_message));

				} catch (Exception e) {
					logError(e.toString());
				}
			}

		});

		addDataButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				enterNameField.setError(null);
				enterAgeField.setError(null);

				String name = enterNameField.getText().toString();
				String ageString = enterAgeField.getText().toString();

				if (name.length() == 0) {
					enterNameField.setError(getString(R.string.required));
					return;
				}

				if (!isInteger(ageString)) {
					enterAgeField.setError(getString(R.string.invalid_format));
					return;
				}

				int age = parseInt(ageString);

				JSONStoreAddOptions options = new JSONStoreAddOptions();
				options.setMarkDirty(true);

				try {

					people.addData(new JSONObject("{age:" + age + ", name: '" + name + "'}"), options);

					logMessage(getString(R.string.add_message));

					enterNameField.setText(null);
					enterAgeField.setText(null);

				} catch (Exception e) {
					logError(e.toString());
				}

			}

		});

		findByNameButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				searchField.setError(null);
				limitField.setError(null);
				offsetField.setError(null);

				String name = searchField.getText().toString();
				String limitString = limitField.getText().toString();
				String offsetString = offsetField.getText().toString();

				if (limitString.length() > 0 && !isInteger(limitString)) {
					limitField.setError(getString(R.string.invalid_format));
					return;
				}

				if (offsetString.length() > 0 && !isInteger(offsetString)) {
					offsetField.setError(getString(R.string.invalid_format));
					return;
				}

				int limit = parseInt(limitString);
				int offset = parseInt(offsetString);

				if (name.length() == 0) {
					searchField.setError(getString(R.string.required));
					return;
				}

				try {
					JSONStoreQueryPart part = new JSONStoreQueryPart();
					part.addLike("name", name);

					JSONStoreQueryParts quertParts = new JSONStoreQueryParts();
					quertParts.addQueryPart(part);

					JSONStoreFindOptions findOptions = new JSONStoreFindOptions();

					if (limit > 0) {
						findOptions.setLimit(limit);
					}

					if (offset > 0) {
						findOptions.setOffset(offset);
					}

					findOptions.addSearchFilter("_id");
					findOptions.addSearchFilter("json");

					findOptions.sortBySearchFieldAscending("name");
					findOptions.sortBySearchFieldDescending("age");


					List<JSONObject> results = people.findDocuments(quertParts, findOptions);

					logList(String.format(getString(R.string.docs_found_message), results.size()), results);

				} catch (Exception e) {
					logError(e.toString());
				}
			}

		});

		findByAgeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				searchField.setError(null);
				limitField.setError(null);
				offsetField.setError(null);

				String ageString = searchField.getText().toString();
				String limitString = limitField.getText().toString();
				String offsetString = offsetField.getText().toString();

				if (!isInteger(ageString)) {
					searchField.setError(getString(R.string.invalid_format));
					return;
				}

				if (limitString.length() > 0 && !isInteger(limitString)) {
					limitField.setError(getString(R.string.invalid_format));
					return;
				}

				if (offsetString.length() > 0 && !isInteger(offsetString)) {
					offsetField.setError(getString(R.string.invalid_format));
					return;
				}

				int age = parseInt(ageString);
				int limit = parseInt(limitString);
				int offset = parseInt(offsetString);

				try {
					JSONStoreQueryPart part = new JSONStoreQueryPart();
					part.addEqual("age", age);

					JSONStoreQueryParts quertParts = new JSONStoreQueryParts();					
					quertParts.addQueryPart(part);

					JSONStoreFindOptions findOptions = new JSONStoreFindOptions();

					if (limit > 0) {
						findOptions.setLimit(limit);
					}

					if (offset > 0) {
						findOptions.setOffset(offset);
					}

					findOptions.addSearchFilter("_id");
					findOptions.addSearchFilter("json");

					findOptions.sortBySearchFieldAscending("name");
					findOptions.sortBySearchFieldDescending("age");

					List<JSONObject> results = people.findDocuments(quertParts, findOptions);

					logList(String.format(getString(R.string.docs_found_message), results.size()), results);

				} catch (Exception e) {
					logError(e.toString());
				}

			}

		});

		findAllButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				searchField.setError(null);
				limitField.setError(null);
				offsetField.setError(null);

				String limitString = limitField.getText().toString();
				String offsetString = offsetField.getText().toString();

				if (limitString.length() > 0 && !isInteger(limitString)) {
					limitField.setError(getString(R.string.invalid_format));
					return;
				}

				if (offsetString.length() > 0 && !isInteger(offsetString)) {
					offsetField.setError(getString(R.string.invalid_format));
					return;
				}

				int limit = parseInt(limitString);
				int offset = parseInt(offsetString);

				try {

					JSONStoreFindOptions findOptions = new JSONStoreFindOptions();

					if (limit > 0) {
						findOptions.setLimit(limit);
					}

					if (offset > 0) {
						findOptions.setOffset(offset);
					}

					findOptions.addSearchFilter("_id");
					findOptions.addSearchFilter("json");

					findOptions.sortBySearchFieldAscending("name");
					findOptions.sortBySearchFieldDescending("age");

					List<JSONObject> results = people.findAllDocuments(findOptions);

					logList(String.format(getString(R.string.docs_found_message), results.size()), results);

				} catch (Exception e) {
					logError(e.toString());
				}
			}

		});

		findByIdButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				findByIdField.setError(null);

				String idString = findByIdField.getText().toString();

				if (!isInteger(idString)) {
					findByIdField.setError(getString(R.string.invalid_format));
					return;
				}

				int id = parseInt(idString);

				try {

					JSONObject record = people.findDocumentById(id);

					if (record != null) {
						logMessage(record.toString());
					} else {
						logError(String.format(getString(R.string.not_found_message), id));
					}

					findByIdField.setText(null);
				} catch (Exception e) {
					logError(e.toString());
				}

			}

		});

		replaceByIdButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				replaceNameField.setError(null);
				replaceAgeField.setError(null);
				replaceIdField.setError(null);

				String name = replaceNameField.getText().toString();
				String ageString = replaceAgeField.getText().toString();
				String idString = replaceIdField.getText().toString();

				if (name.length() == 0) {
					replaceNameField.setError(getString(R.string.required));
					return;
				}
				if (!isInteger(ageString)) {
					replaceAgeField.setError(getString(R.string.required));
					return;
				}
				if (!isInteger(idString)) {
					replaceIdField.setError(getString(R.string.required));
					return;
				}

				int id = parseInt(idString);
				int age = parseInt(ageString);

				try {

					JSONStoreReplaceOptions options = new JSONStoreReplaceOptions();
					options.setMarkDirty(true);

					JSONObject data = new JSONObject("{_id: " + id + ", json: {age: " + age + ", name: '" + name+ "'}}");
					
					people.replaceDocument(data, options);

					logMessage(String.format(getString(R.string.replace_message), id));

					replaceNameField.setText(null);
					replaceAgeField.setText(null);
					replaceIdField.setText(null);

				} catch (Exception e) {
					logError(e.toString());
				}

			}

		});

		removeByIdButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				removeByIdField.setError(null);

				String idString = removeByIdField.getText().toString();

				if (!isInteger(idString)) {
					removeByIdField
							.setError(getString(R.string.invalid_format));
					return;
				}

				int id = parseInt(idString);

				try {

					JSONStoreRemoveOptions options = new JSONStoreRemoveOptions();
					options.setMarkDirty(true);

					people.removeDocumentById(id, options);

					logMessage(String.format(getString(R.string.remove_message), id));

					removeByIdField.setText(null);

				} catch (Exception e) {
					logError(e.toString());
				}

			}

		});

		loadDataFromAdapterButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				WLResponseListener responseListener = new WLResponseListener() {

					@Override
					public void onFailure(final WLFailResponse response) {

						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								logError(response.getErrorMsg());
							}
						});
					}

					@Override
					public void onSuccess(WLResponse response) {
						JSONObject responseObject = response.getResponseJSON();

						final List<JSONObject> pulledData = new ArrayList<JSONObject>();

						try {
							JSONArray list = responseObject.getJSONArray("peopleList");

							for (int i = 0; i < list.length(); i++) {
								pulledData.add(list.getJSONObject(i));
							}

							JSONStoreChangeOptions options = new JSONStoreChangeOptions();

							options.setAddNew(true);

							options.setMarkDirty(false);

							final int changed = people.changeData(pulledData, options);

							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									logList(String.format(getString(R.string.add_docs_message), changed), pulledData);
								}
							});

						} catch (final Exception e) {
							runOnUiThread(new Runnable() {

								@Override
								public void run() {
									logError(e.toString());
								}
							});
						}

					}
				};
				
				WLProcedureInvocationData invocationData = new WLProcedureInvocationData("People", "getPeople");
				client.invokeProcedure(invocationData, responseListener);

			}

		});

		getDirtyDocumentsButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				try {

					List<JSONObject> dirtyDocs = people.findAllDirtyDocuments();

					logList(String.format(getString(R.string.dirty_docs_message), dirtyDocs.size()), dirtyDocs);

				} catch (Exception e) {
					logError(e.toString());
				}

			}

		});

		pushChangesToAdapterButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				try {

					final List<JSONObject> dirtyDocuments = people.findAllDirtyDocuments();
					
					WLResponseListener responseListener = new WLResponseListener() {

						@Override
						public void onFailure(final WLFailResponse response) {

							runOnUiThread(new Runnable() {

								@Override
								public void run() {

									logError(response.getErrorMsg());
								}
							});
						}

						@Override
						public void onSuccess(WLResponse response) {
							runOnUiThread(new Runnable() {

								@Override
								public void run() {
									logMessage(String.format(getString(R.string.docs_pushed_message), dirtyDocuments.size()));
								}
							});
							
							try {
								people.markDocumentsClean(dirtyDocuments);
							} catch (Exception e) {}

						}
					};
					
					WLProcedureInvocationData invocationData = new WLProcedureInvocationData("People", "pushPeople");
					
					invocationData.setParameters(new Object[]{dirtyDocuments});
					client.invokeProcedure(invocationData, responseListener);


				} catch (Exception e) {
					logError(e.toString());
				}
				
				
			}

		});

		countAllButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				try {

					int allCount = people.countAllDocuments();

					logMessage(String.format(getString(R.string.count_all_message), allCount));

				} catch (Exception e) {
					logError(e.toString());
				}

			}

		});

		countByNameButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				countNameField.setError(null);

				String name = countNameField.getText().toString();

				if (name.length() == 0) {
					countNameField.setError(getString(R.string.required));
					return;
				}

				try {
					JSONStoreQueryPart part = new JSONStoreQueryPart();
					part.addEqual("name", name);

					JSONStoreQueryParts quertParts = new JSONStoreQueryParts();
					quertParts.addQueryPart(part);

					int count = people.countDocuments(quertParts);

					String message = String.format(getString(R.string.count_name_message), name, count);

					logMessage(message);
				} catch (Exception e) {
					logError(e.toString());
				}

			}

		});

		changePasswordButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String oldPassword = changePasswordOldField.getText().toString();
				String newPassword = changePasswordNewField.getText().toString();
				String username = changePasswordUserField.getText().toString();

				if (username.length() == 0) {
					username = DEFAULT_USER;
				}

				try {

					WLJSONStore.getInstance(context).changePassword(username, oldPassword, newPassword);

					logMessage(getString(R.string.password_change_message));
				} catch (Exception e) {
					logError(e.toString());
				}

			}

		});

		getFileInfoButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				List<JSONStoreFileInfo> results = WLJSONStore.getInstance(context).getFileInfo();

				StringBuilder fileInfo = new StringBuilder();

				for (JSONStoreFileInfo info : results) {
					fileInfo.append("FileInfo:\n");
					fileInfo.append(String.format("size=%d bytes\n", info.getFileSizeBytes()));
					fileInfo.append(String.format("username=%s\n", info.getUsername()));
					fileInfo.append(String.format("encrypted=%s\n", info.isEncrypted()));
				}

				logMessage(fileInfo.toString());
			}

		});

	}

	private void logError(String message) {
		consoleTextView.setTextColor(Color.RED);
		consoleTextView.setText("ERROR:\n" + message);

	}

	private void logList(String heading, List<JSONObject> list) {
		consoleTextView.setTextColor(Color.WHITE);

		StringBuilder consoleText = new StringBuilder();

		consoleText.append(heading + "\n");

		for (JSONObject object : list) {
			consoleText.append(object.toString() + "\n");

		}
		consoleTextView.setText(consoleText.toString());
	}

	private void logMessage(String messageId) {
		consoleTextView.setTextColor(Color.WHITE);
		consoleTextView.setText(messageId);
	}

	private void initFieldsAndButtons() {
		usernameField = (EditText) findViewById(R.id.userField);
		passwordField = (EditText) findViewById(R.id.passwordField);

		enterNameField = (EditText) findViewById(R.id.nameField);
		enterAgeField = (EditText) findViewById(R.id.ageField);

		searchField = (EditText) findViewById(R.id.searchField);
		limitField = (EditText) findViewById(R.id.limitField);
		offsetField = (EditText) findViewById(R.id.offsetField);

		findByIdField = (EditText) findViewById(R.id.findByIdField);

		replaceNameField = (EditText) findViewById(R.id.replaceNameField);
		replaceAgeField = (EditText) findViewById(R.id.replaceAgeField);
		replaceIdField = (EditText) findViewById(R.id.replaceIdField);

		removeByIdField = (EditText) findViewById(R.id.removeIdField);

		countNameField = (EditText) findViewById(R.id.countNameField);

		changePasswordOldField = (EditText) findViewById(R.id.changePasswordOldField);
		changePasswordNewField = (EditText) findViewById(R.id.changePasswordNewField);
		changePasswordUserField = (EditText) findViewById(R.id.changePasswordUserField);

		consoleTextView = (TextView) findViewById(R.id.consoleTextField);

		initButton = (Button) findViewById(R.id.initializeButton);
		closeButton = (Button) findViewById(R.id.closeButton);
		destroyButton = (Button) findViewById(R.id.destroyButton);
		removeCollectionButton = (Button) findViewById(R.id.removeButton);
		addDataButton = (Button) findViewById(R.id.addDataButton);
		findByNameButton = (Button) findViewById(R.id.findByNameButton);
		findByAgeButton = (Button) findViewById(R.id.findByAgeButton);
		findAllButton = (Button) findViewById(R.id.findAllButton);
		findByIdButton = (Button) findViewById(R.id.findByIdButton);
		replaceByIdButton = (Button) findViewById(R.id.replaceByIdButton);
		removeByIdButton = (Button) findViewById(R.id.removeByIdButton);
		loadDataFromAdapterButton = (Button) findViewById(R.id.loadDataFromAdapterButton);
		getDirtyDocumentsButton = (Button) findViewById(R.id.getPushRequiredButton);
		pushChangesToAdapterButton = (Button) findViewById(R.id.pushChangesToAdapterButton);
		countAllButton = (Button) findViewById(R.id.countAllButton);
		countByNameButton = (Button) findViewById(R.id.countByNameButton);
		changePasswordButton = (Button) findViewById(R.id.changePasswordButton);

		getFileInfoButton = (Button) findViewById(R.id.getFileInfoButton);
	}

	public static int parseInt(String number) {
		try {
			return Integer.parseInt(number);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	public static boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return false;
		}

		return true;
	}

	private static final String PEOPLE_COLLECTION_NAME = "people";
	private static final String DEFAULT_USER = "jsonstore";

	private EditText usernameField;
	private EditText passwordField;

	private EditText enterNameField;
	private EditText enterAgeField;

	private EditText searchField;
	private EditText limitField;
	private EditText offsetField;

	private EditText findByIdField;

	private EditText replaceNameField;
	private EditText replaceAgeField;
	private EditText replaceIdField;

	private EditText removeByIdField;

	private EditText countNameField;

	private EditText changePasswordOldField;
	private EditText changePasswordNewField;
	private EditText changePasswordUserField;

	private TextView consoleTextView;

	private Button initButton;
	private Button closeButton;
	private Button destroyButton;
	private Button removeCollectionButton;
	private Button addDataButton;
	private Button findByNameButton;
	private Button findByAgeButton;
	private Button findAllButton;
	private Button findByIdButton;
	private Button replaceByIdButton;
	private Button removeByIdButton;
	private Button loadDataFromAdapterButton;
	private Button getDirtyDocumentsButton;
	private Button pushChangesToAdapterButton;
	private Button countAllButton;
	private Button countByNameButton;
	private Button changePasswordButton;
	private Button getFileInfoButton;

	private JSONStoreCollection people;
}
