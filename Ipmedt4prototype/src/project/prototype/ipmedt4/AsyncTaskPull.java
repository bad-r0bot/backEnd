package project.prototype.ipmedt4;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
//import android.os.SystemClock;

/**
 * 
 * @author RobotKoala
 *
 */
public class AsyncTaskPull extends Activity {

	private static final String TAG_SUCCESS = "success";
	private static final String TAG_PRODUCTS = "products";
	private static final String TAG_PID = "pid";
	private static final String TAG_NAME = "name";
	
	TextView txt;
	Button btn_start;
	ProgressBar progressBar;
	TextView txt_percentage;
	
	// Creating JSON Parser object
	JSONParser jParser = new JSONParser();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_async_task);
		txt = (TextView) findViewById(R.id.textView1);  

		btn_start = (Button) findViewById(R.id.button2);

		btn_start.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				btn_start.setEnabled(false);
				new ShowDialogAsyncTask().execute();
			}
		});
	}


	private class ShowDialogAsyncTask extends AsyncTask<Void, Void, String>
	{

		@Override
		protected void onPreExecute() 
		{
			// update the UI immediately after the task is executed
			super.onPreExecute();

			Toast.makeText(AsyncTask.this,
					"Invoke onPreExecute()", Toast.LENGTH_SHORT).show();

			// Set the text and call the connect function.  
			txt.setText("Connecting...");

		}

		@Override
		protected String doInBackground(Void... params) {

			InputStream is = null;

			String result = "";
			//Welke data wordt doorgestuurd.
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("naam","test"));
			
			// In test.php goes the SQL query.
			String URL = "http://10.0.2.2:8080/android_connect/test.php";


			//http post
			try{
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(URL);
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity entity = response.getEntity();
				is = entity.getContent();


			}catch(Exception e){
				Log.e("log_tag", "Error in http connection "+e.toString());
			}

			try{
				BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				is.close();
				result=sb.toString();
			}catch(Exception e){
				Log.e("log_tag", "Error converting result "+e.toString());
			}
			//parse json data
			String returnString = null;
			/*
			try{
				
				JSONArray jArray = new JSONArray(result);
				for(int i=0;i<jArray.length();i++){
					JSONObject json_data = jArray.getJSONObject(i);
					Log.i("log_tag"," Ville: "+json_data.getString("Ville")  );
					//Get an output to the screen
					
					String jsonStr = json_data.getString("");
					//String jsonStr = "{\"Ville_ID\":\"1\",\"Ville\":null},{\"Ville_ID\":\"2\",\"Ville\":\"Port de Plaisance\"},{\"Ville_ID\":\"3\",\"Ville\":null},{\"Ville_ID\":\"4\",\"Ville\":\"Station de ski\"},{\"Ville_ID\":\"5\",\"Ville\":\"Ville Fleurie\"},{\"Ville_ID\":\"6\",\"Ville\":\"Station verte\"}";
							
					JSONObject myJsonObj = new JSONObject(jsonStr);

					String villeID = myJsonObj.getString("Ville");
					String ville = myJsonObj.getString("Ville");
					
					returnString += "" + villeID + "\n" + ville; 
					//jArray.getJSONObject(i).getString("Ville_ID");
					}
			}*/
			try{
				JSONArray jArray = new JSONArray(result);
				// Loop om alles in de collumns te vinden.
				for(int i=0;i<jArray.length();i++)
				{
					JSONObject json_data = jArray.getJSONObject(i);
					Log.i("log_tag"," Ville_ID: "+json_data.getString("Ville_ID")  );
					//Get an output to the screen
					returnString += "\n\t" + jArray.getJSONObject(i); 
				}
			}
			catch(JSONException e)
			{
				Log.e("log_tag", "Error parsing data "+e.toString());
			}
			return returnString; 

		}  
	//
	//	@Override
	//	protected void onProgressUpdate(Integer... values) {
	//		super.onProgressUpdate(values);
	//
	//		progressBar.setProgress(values[0]);
	//		txt_percentage.setText("downloading " +values[0]+"%");
	//
	//	}
	
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
	
			Toast.makeText(AsyncTask.this,
					"Invoke onPostExecute()", Toast.LENGTH_SHORT).show();
	
			txt.setText(result); 

			btn_start.setEnabled(true);
		}
	}
}