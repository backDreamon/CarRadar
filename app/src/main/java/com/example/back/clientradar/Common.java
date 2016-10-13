package com.example.back.clientradar;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by back on 2016-10-12.
 */

public class Common {
    public static String resId;
    public static String resState;
    public static String resNo;
    public static String resDistance;

    public static void sendData(HashMap<String, String> postData) {

        final String loginAddress = "http://dev-dreamon.cloud.or.kr/android.php";
        HttpURLConnection connection;

        try {
            URL url = new URL(loginAddress);
            //url 커넥션 생성
            connection = (HttpURLConnection) url.openConnection();
            //요청 방식
            connection.setRequestMethod("POST");
            // 요청 응답 타임아웃
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);
            connection.setDoOutput(true);
            //Log.d("data", data);

            String postDataString = getPostString(postData);
            Log.d("POSTDATA", postDataString);

            OutputStreamWriter osw = new OutputStreamWriter(connection.getOutputStream());
            osw.write(postDataString);
            osw.flush();

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder sb = new StringBuilder();

            String json;

            // server로 부터 메시지 받아오는 곳

            while ((json = br.readLine()) != null) {
                sb.append(json);
                break;
            }
            //Log.d("response", sb.toString().trim());
            jsonParsing(sb.toString().trim());
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =key&value 형태로 Map을 변경해주는 메소드
    private static String getPostString(HashMap<String, String> map) {
        StringBuilder result = new StringBuilder();
        boolean first = true; // 첫 번째 매개변수 여부

        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (first)
                first = false;
            else // 첫 번째 매개변수가 아닌 경우엔 앞에 &를 붙임
                result.append("&");

            try { // UTF-8로 주소에 키와 값을 붙임
                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            } catch (UnsupportedEncodingException ue) {
                ue.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result.toString();
    }

    public static void jsonParsing(String json) {
        try {
            JSONArray jsonArray = new JSONArray(json);

            // JSON 오브젝트 한개만 받아옴
            JSONObject jsonObject = jsonArray.getJSONObject(0);


            if (jsonObject.has("id")) {
                resId = jsonObject.getString("id");
                Log.d("ResID", resId);
            }

            if (jsonObject.has("no")) {
                resNo = jsonObject.getString("no");
                Log.d("ResNO", resNo);
            }

            if (jsonObject.has("state")) {
                resState = jsonObject.getString("state");
                Log.d("ResSTATE", resState);
            }

            if (jsonObject.has("distance")) {
                resDistance = jsonObject.getString("distance");
                Log.d("ResDistance", resDistance);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
