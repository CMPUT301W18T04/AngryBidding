package ca.ualberta.angrybidding;


import android.content.Context;
import android.util.Log;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import ca.ualberta.angrybidding.elasticsearch.UpdateResponseListener;

public class TaskCache {
    public static final String FILE_NAME = "CacheTask";

    public static void saveToFile(Context context, ArrayList<ElasticSearchTask> tasks) {
        try {
            File newFile = new File(context.getFilesDir(), FILE_NAME);

            if (!newFile.exists()) {
                newFile.createNewFile();
            }
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(newFile));
            JSONArray jsonArray = new JSONArray();
            for (ElasticSearchTask task : tasks) {
                String taskToString = new Gson().toJson(task);
                JSONObject jsonObject = new JSONObject(taskToString);
                jsonObject.put("id", task.getID());
                jsonArray.put(jsonObject);
            }

            writer.write(jsonArray.toString());

            writer.close();
        } catch (IOException io) {
            Log.e("MainActivity", "Exception occurred", io);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<ElasticSearchTask> readFromFile(Context context) {
        try {
            File newFile = new File(context.getFilesDir(), FILE_NAME);
            if (!newFile.exists()) {
                return null;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(newFile)));
            StringBuilder stringBuilder = new StringBuilder();
            String line = reader.readLine();
            while (line != null) {
                stringBuilder.append('\n' + line);
                line = reader.readLine();
            }
            reader.close();

            JSONArray jsonArray = new JSONArray(stringBuilder.toString());
            ArrayList<ElasticSearchTask> tasks = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("id");
                ElasticSearchTask task = new Gson().fromJson(jsonObject.toString(), ElasticSearchTask.class);
                task.setID(id);
                tasks.add(task);
            }
            return tasks;

        } catch (IOException io) {
            Log.e("MainActivity", "Exception occurred", io);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void synchronizeWithElasticSearch(Context context, ArrayList<ElasticSearchTask> remoteTasks, ElasticSearchTask.ListTaskListener listener) {
        ArrayList<ElasticSearchTask> fileTasks = readFromFile(context);
        ArrayList<ElasticSearchTask> mergedTasks = new ArrayList<>();
        for (ElasticSearchTask remoteTask : remoteTasks) {
            boolean found = false;
            for (ElasticSearchTask fileTask : fileTasks) {
                if (remoteTask.getID().equals(fileTask.getID())) {
                    mergedTasks.add(fileTask);
                    found = true;
                    break;
                }
            }
            if (!found) {
                mergedTasks.add(remoteTask);
            }
        }

        for (ElasticSearchTask fileTask : fileTasks) {
            if (fileTask.getID() == null) {
                mergedTasks.add(fileTask);
            }
        }
        uploadTasks(context, 0, mergedTasks, listener);
    }

    private static void uploadTasks(final Context context, final int index, final ArrayList<ElasticSearchTask> tasks, final ElasticSearchTask.ListTaskListener listener) {

        if (index == tasks.size()) {
            listener.onResult(tasks);
        }
        ElasticSearchTask.updateTask(context, tasks.get(index), new UpdateResponseListener() {
            @Override
            public void onCreated(String id) {
                tasks.get(index).setID(id);
                uploadTasks(context, index + 1, tasks, listener);
            }

            @Override
            public void onUpdated(int version) {
                uploadTasks(context, index + 1, tasks, listener);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError(error);
            }
        });
    }
}
