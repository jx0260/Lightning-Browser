package acr.browser.lightning.utils;

import android.text.TextUtils;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 排序工具
 */
public class SHA1Utils {

    public static final String KEY = "27921d87-8017-4922-83d3-9f1e415c148c";//开发环境
//    public static final String KEY = "074ae994-4f13-4884-a1da-8d22427e6da1";//正式环境

    /**
     * 字典排序
     */
    public static String[] decimal2Binary(String[] strArray) {
        String t = null;
        Log.e("==decimal2Binary==", "排序前");
        for (String s : strArray)
            System.out.print(s + "\t");
        int i, j, k;
        for (i = 0; i < strArray.length - 1; i++) {
            k = i;
            for (j = i + 1; j < strArray.length; j++) {
                // Character c1 = Character.valueOf(strArray[j].charAt(0));
                // Character c2 = Character.valueOf(strArray[k].charAt(0));
                if (strArray[j].compareTo(strArray[k]) < 0)
                    k = j;
            }
            if (i != k) {
                t = strArray[i];
                strArray[i] = strArray[k];
                strArray[k] = t;
            }
        }
        Log.e("==decimal2Binary==", "排序后");

        for (String s : strArray)
            Log.e("==decimal2Binary==", "排序后" + s);

        return strArray;
    }

    public static String makeSignature(Map<String, Object> params, String signature) {
        if (params == null || params.size() == 0) {
            return null;
        }
        List<Pair> pairs = new ArrayList<Pair>();
        Iterator<String> it = params.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            Pair pair = new Pair();
            pair.setKey(key);
            pair.setValue(String.valueOf(params.get(key)));
            pairs.add(pair);
        }
        Collections.sort(pairs, new Comparator<Pair>() {
            public int compare(Pair pair1, Pair pair2) {
                int i = pair1.key.compareTo(pair2.key);
                if ((i == 0) && (!(TextUtils.isEmpty(pair1.value)))
                        && (!(TextUtils.isEmpty(pair2.value)))) {
                    i = pair1.value.compareTo(pair2.value);
                }
                return i;
            }
        });
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pairs.size(); i++) {
            if (i != 0) {
                sb.append("&");
            }

            sb.append(urlEncode(pairs.get(i).key) + "="
                    + urlEncode(pairs.get(i).value));

        }
        sb.append(signature);
        return SHA1(sb.toString());
    }

/*    public static String makeSignature(List<KeyValue> pairs, String signatureGenKey) {
        Collections.sort(pairs, new Comparator<KeyValue>() {
            public int compare(KeyValue pair1, KeyValue pair2) {
                return pair1.key.compareTo(pair2.key);
            }
        });
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pairs.size(); i++) {
            if (i != 0) {
                sb.append("&");
            }

            sb.append(urlEncode(pairs.get(i).key) + "="
                    + urlEncode(pairs.get(i).value.toString()));
        }
        sb.append(signatureGenKey);
        return SHA1(sb.toString());
    }*/

    private static class Pair {
        private String key;
        private String value;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

    }

    public static String SHA1(String decript) {

        try {
            MessageDigest digest = MessageDigest
                    .getInstance("SHA-1");
            digest.update(decript.getBytes());
            byte messageDigest[] = digest.digest();
            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            // 字节数组转换为 十六进制 数
            for (int i = 0; i < messageDigest.length; i++) {
                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString().toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String urlEncode(String v) {
        try {
            return URLEncoder.encode(v, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return v;
    }
}

