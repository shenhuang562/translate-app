package com.example.trans;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;

import java.io.IOException;
import java.text.SimpleDateFormat;
import android.widget.LinearLayout;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.content.Intent;
import android.speech.RecognizerIntent;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.Manifest;
import android.provider.MediaStore;
import okhttp3.OkHttpClient;
import org.json.JSONObject;
import org.json.JSONException;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Callback;
import okhttp3.Call;
import okhttp3.Response;
import okhttp3.ResponseBody;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;


import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.Map;


public class Trans extends Fragment {
    private static final int SPEECH_REQUEST_CODE = 123;
    private Spinner from;
    private String fromvalue;
    private Spinner to;
    private String tovalue;
    private EditText fromtext;
    private String fromtextvalue;
    private Button trans;
    private Button keybord;
    private Button sound;
    private Button picture;

    private  String totextvalue;
    private Button take;

    private Button clearall;
    private SQLiteDatabase db;

    private TextView totext;

    private  String result;

    public interface TranslationCallback {
        void onTranslationResult(String result);
    }

    private ActivityResultLauncher<Intent> speechRecognitionLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null) {
                                ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                                if (matches != null && !matches.isEmpty()) {
                                    String spokenText = matches.get(0);
                                    fromtext.setText(matches.get(0));
                                }
                            }
                        }
                    });

    private ActivityResultLauncher<String> requestPermissionLauncher;
    private ActivityResultLauncher<Intent> takePictureLauncher;
    private ActivityResultLauncher<String> getContentLauncher;

    private void performOperation(String fromvalue, String tovalue, String fromtextvalue, TranslationCallback callback) {
        OkHttpClient client = new OkHttpClient();
        String url = "https://fanyi-api.baidu.com/api/trans/vip/translate";
        String apiKey = "oC4yw4ZNmhoCEPWT5qtm"; // 替换为你自己的百度翻译 API 密钥
        String appid = "20240422002032026";

        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        urlBuilder.addQueryParameter("q", fromtextvalue); // 待翻译的文本
        urlBuilder.addQueryParameter("from", fromvalue); // 源语言
        urlBuilder.addQueryParameter("to", tovalue); // 目标语言
        urlBuilder.addQueryParameter("appid", "20240422002032026"); // 替换为你自己的百度翻译 APP ID
        urlBuilder.addQueryParameter("salt", String.valueOf(System.currentTimeMillis()));
        urlBuilder.addQueryParameter("sign", generateSign("hello", "en", "zh")); // 根据API要求生成签名

        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                // 记录请求失败的日志
                System.out.println("Request failed: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) {
                        if (response.code() == 404) {
                            // 处理 404 错误
                            // 记录 404 错误日志
                            System.out.println("404 Error: Resource not found");
                        } else if (response.code() == 500) {
                            // 处理 500 错误
                            // 记录 500 错误日志
                            System.out.println("500 Error: Internal server error");
                        }
                        throw new IOException("Unexpected code " + response);
                    }
                    TranslateHelper translateHelper = new TranslateHelper();
                    String responseData = "";
                    try {
                        responseData = TranslateHelper.get(fromtextvalue, tovalue, fromvalue);
                        System.out.println("Translated text: " + responseData);
                    } catch (Exception e) {
                        System.out.println("An error occurred: " + e.getMessage());
                    }
                    //String responseData = responseBody.string();
                    //System.out.println(responseData);

                    try {
                        JsonObject jsonObject = JsonParser.parseString(responseData).getAsJsonObject();

                        if (jsonObject.has("trans_result")) {
                            JsonArray translationArray = jsonObject.getAsJsonArray("trans_result");

                            for (JsonElement translationElement : translationArray) {
                                JsonObject translationObject = translationElement.getAsJsonObject();

                                if (translationObject.has("dst")) {
                                    String translatedText = translationObject.get("dst").getAsString();
                                    callback.onTranslationResult(translatedText);
                                } else {
                                    // 记录缺少 "dst" 键的日志
                                    System.out.println("Missing 'dst' key in translation response");
                                }
                            }
                        } else {
                            // 记录缺少 "trans_result" 键的日志
                            System.out.println("Missing 'trans_result' key in translation response");
                        }
                    } catch (JsonParseException e) {
                        // 记录 JSON 解析异常日志
                        e.printStackTrace();
                        System.out.println("JSON parsing error: " + e.getMessage());
                    }

                } catch (IOException e) {
                    // 记录其他 IO 异常日志
                    e.printStackTrace();
                    System.out.println("IO error: " + e.getMessage());
                }
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.frag_trans, container, false);

        // Find spinners and edittext by their IDs
        from = view.findViewById(R.id.from);
        to = view.findViewById(R.id.to);
        fromtext = view.findViewById(R.id.fromtext);
        trans = view.findViewById(R.id.clear);
        totext = view.findViewById(R.id.totext);
        keybord = view.findViewById(R.id.keyboard);
        sound = view.findViewById(R.id.sound);
        picture = view.findViewById(R.id.picture);
        take = view.findViewById(R.id.take);
        clearall = view.findViewById(R.id.clearall);

        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                // 用户授予了权限，执行打开相机或相册的操作
                startCameraOrGallery();
            } else {
                // 用户拒绝了权限，可以显示一个提示或者采取其他适当的措施
            }
        });

        takePictureLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // 从相机或相册返回的数据处理
                        Intent data = result.getData();
                        if (data != null) {
                            fromtext.setText("你输入了一张图片");
                        }
                    }
                });
        getContentLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                fromtext.setText("你输入了一张图片");
            }
        });

        // Populate spinners with data
        setupSpinners();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainActivity mainActivity = (MainActivity) getActivity();

        // 获取数据库实例
        if (mainActivity != null) {
            db = mainActivity.getDatabaseInstance();
        } else {
            // 处理无法获取数据库实例的情况
        }

        clearall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromtext.setText("");
            }
        });


        trans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromtext.clearFocus();
                String fromvaluedata = language.getLanguageCode(fromvalue);
                String tovaluedata = language.getLanguageCode(tovalue);
                performOperation(fromvaluedata, tovaluedata, fromtextvalue, new TranslationCallback() {
                    @Override
                    public void onTranslationResult(String translatedText) {
                        // 在回调函数中获取翻译结果
                        totextvalue = translatedText;
                        // 启动后台线程执行数据库操作
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // 执行数据库操作
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String currentTime = sdf.format(new Date());
                                ContentValues values = new ContentValues();
                                values.put("from_column", fromvalue);
                                values.put("to_column", tovalue);
                                values.put("fromtext", fromtextvalue);
                                values.put("totext", totextvalue);
                                values.put("time_column", currentTime);
                                long newRowId = db.insert("history", null, values);

                                // 查询数据库
                                Cursor cursor = db.query("history", null, null, null, null, null, "_id DESC", "50");
                                // 切换回主线程更新 UI
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        totext.setText(totextvalue);
                                        History historyFragment = (History) mainActivity.getHistoryFragment();
                                        if (historyFragment != null) {
                                            LayoutInflater inflater = LayoutInflater.from(getActivity());
                                            LinearLayout linearLayout = historyFragment.view.findViewById(R.id.historycontainer);
                                            historyFragment.displayHistoryData(cursor, inflater, linearLayout);
                                        }
                                    }
                                });
                            }
                        }).start();
                    }
                });
            }

        });


        keybord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 唤起输入法
                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(fromtext, InputMethodManager.SHOW_IMPLICIT);
                fromtext.requestFocus();
            }
        });

        sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                speechRecognitionLauncher.launch(intent);
            }
        });

        take.setOnClickListener(v -> {
            // 首先检查权限，如果已经获得了权限，直接启动相机/相册，否则请求相应的权限
            if (getContext().checkSelfPermission(Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                // 已经有相机权限，直接启动相机/相册
                startCameraOrGallery();
            } else {
                // 请求相机权限
                requestPermissionLauncher.launch(Manifest.permission.CAMERA);
            }
        });

        picture.setOnClickListener(v -> {
            getContentLauncher.launch("image/*");
        });



        // Set listener for spinner item selection
        from.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get selected item from spinner1
                fromvalue = parent.getItemAtPosition(position).toString();
                // Do something with the selected item
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do something when nothing is selected
            }
        });

        // Set listener for spinner item selection
        to.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get selected item from spinner2
                tovalue = parent.getItemAtPosition(position).toString();
                // Do something with the selected item
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do something when nothing is selected
            }
        });

        // Set listener for EditText focus change
        fromtext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    // Get text from EditText when focus is lost
                    fromtextvalue = fromtext.getText().toString();
                    // Do something with the entered text
                }
            }
        });
    }

    private void setupSpinners() {
        // Dummy data for spinners
        List<String> spinnerData = new ArrayList<>();
        language language = new language();
        Map<String, String> languageMap = language.getLanguageMap();
        for (String languageName : languageMap.keySet()) {
            spinnerData.add(languageName);
        }

        // Create ArrayAdapter for spinners
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, spinnerData);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set ArrayAdapter to spinners
        from.setAdapter(adapter);
        to.setAdapter(adapter);
    }

    private void startCameraOrGallery() {
        // 创建一个启动相机或相册的 Intent，并根据用户选择启动相应的操作
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 或者
        // Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // 然后将 Intent 传递给 takePictureLauncher
        takePictureLauncher.launch(takePictureIntent);
    }

    public String generateSign(String query, String from, String to) {
        String appId = "20240422002032026"; // 替换为你自己的百度翻译 APP ID
        String key = "oC4yw4ZNmhoCEPWT5qtm"; // 替换为你自己的百度翻译密钥

        String salt = "1234567";
        String rawSign = appId + query + salt + key;
        MD5 md5 = new MD5();
        String sign = md5.md5(rawSign); // 使用MD5算法生成签名，MD5方法需要自行实现
        System.out.println(sign);
        System.out.println(rawSign);
        return sign;
    }
}


