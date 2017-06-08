package net.chinaedu.screenrecorder.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PreferenceService {

    private Context mContext;
    private SharedPreferences preference;

    public static final String KEY_LOGIN_USER_ACCOUNT = "loginuseraccount";//登录帐号
    //    public static final String KEY_USER_NAME = "username";
    public static final String KEY_USER_PWD = "password";//密码
    public static final String IS_LOGIN = "islogin";//是否第一次登录
    public static final String IS_SHOW_VERSION = "isShowVersion";//版本更新显示

    private static final String MEGREZ_COOKIE = "wepasscookie";
    public static final String GET_VERFYCODE_NO_EVERYDAY = "isShowVersion";//手机号获取验证码次数

    public PreferenceService(Context context) {
        mContext = context;
        preference = mContext.getSharedPreferences(MEGREZ_COOKIE, Context.MODE_PRIVATE);
    }

    /**
     * 注册监听存储数据变化
     */
    public void preferenceChanged() {
        preference.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            }
        });
    }

    /**
     * 保存参数
     */
    public void save(String key, String value) {
        try {
            Editor editor = preference.edit();
            editor.putString(key, value);
            editor.commit();
        } catch (Exception e) {
        }
    }

    public void save(String key, int value) {
        try {
            Editor editor = preference.edit();
            editor.putInt(key, value);
            editor.commit();
        } catch (Exception e) {
        }
    }

    public void save(String key, float value) {
        try {
            Editor editor = preference.edit();
            editor.putFloat(key, value);
            editor.commit();
        } catch (Exception e) {
        }
    }

    public void save(String key, long value) {
        try {
            Editor editor = preference.edit();
            editor.putLong(key, value);
            editor.commit();
        } catch (Exception e) {
        }
    }

    public void save(String key, boolean value) {
        try {
            Editor editor = preference.edit();
            editor.putBoolean(key, value);
            editor.commit();
        } catch (Exception e) {
        }
    }

    /**
     * 保存多个参数
     */
    public void save(Map<String, String> map) {
        try {
            Editor editor = preference.edit();
            for (String key : map.keySet()) {
                editor.putString(key, map.get(key));
            }
            editor.commit();
        } catch (Exception e) {
        }

    }

    public void save(String keys[], int values[]) {
        try {
            Editor editor = preference.edit();
            int n = keys.length;
            for (int i = 0; i < n; i++) {
                editor.putInt(keys[i], values[i]);
            }
            editor.commit();
        } catch (Exception e) {
        }
    }

    public void save(String key, Set<String> set) {
        try {
            Editor editor = preference.edit();
            editor.putStringSet(key, set);
            editor.commit();
        } catch (Exception e) {
        }
    }

    public void save(String key, List<String> list) {
        try {
            Editor editor = preference.edit();
            editor.putString(key, sceneList2String(list));
            editor.commit();
        } catch (Exception e) {
        }
    }

    public String getString(String key, String defValue) {
        String value = defValue;
        try {
            value = preference.getString(key, defValue);
        } catch (Exception e) {
        }
        return value;
    }

    public int getInt(String key, int defValue) {
        int value = defValue;
        try {
            value = preference.getInt(key, defValue);
        } catch (Exception e) {
        }
        return value;
    }

    public float getFloat(String key, float defValue) {
        float value = defValue;
        try {
            value = preference.getFloat(key, defValue);
        } catch (Exception e) {
        }
        return value;
    }

    public long getLong(String key, int defValue) {
        long value = defValue;
        try {
            value = preference.getLong(key, defValue);
        } catch (Exception e) {
        }
        return value;
    }

    public Boolean getBoolean(String key, Boolean defValue) {
        Boolean value = defValue;
        try {
            value = preference.getBoolean(key, defValue);
        } catch (Exception e) {
        }
        return value;
    }

    public Set<String> getStringSet(String key, Set<String> defValues) {
        Set<String> values = defValues;
        try {
            values = preference.getStringSet(key, defValues);
        } catch (Exception e) {
        }
        return values;
    }

    public List<String> getStringList(String key, List<String> defValues) {
        List<String> values = defValues;
        try {
            values = string2SceneList(getString(key, ""));
        } catch (Exception e) {
        }
        return values;
    }


    public String SceneList2String(Map<String, String> hashmap) throws IOException {
        // 实例化一个ByteArrayOutputStream对象，用来装载压缩后的字节文件。
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // 然后将得到的字符数据装载到ObjectOutputStream
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                byteArrayOutputStream);
        // writeObject 方法负责写入特定类的对象的状态，以便相应的 readObject 方法可以还原它
        objectOutputStream.writeObject(hashmap);
        // 最后，用Base64.encode将字节文件转换成Base64编码保存在String中
        String SceneListString = new String(Base64.encode(
                byteArrayOutputStream.toByteArray(), Base64.DEFAULT));
        // 关闭objectOutputStream
        objectOutputStream.close();
        return SceneListString;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, String> String2SceneList(String SceneListString) throws
            IOException, ClassNotFoundException {
        byte[] mobileBytes = Base64.decode(SceneListString.getBytes(),
                Base64.DEFAULT);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                mobileBytes);
        ObjectInputStream objectInputStream = new ObjectInputStream(
                byteArrayInputStream);
        HashMap<String, String> SceneList = (HashMap<String, String>) objectInputStream
                .readObject();
        objectInputStream.close();
        return SceneList;
    }

    public boolean putHashMap(String key, Map<String, String> hashmap) {

        Editor editor = preference.edit();
        try {
            String liststr = SceneList2String(hashmap);
            editor.putString(key, liststr);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return editor.commit();
    }

    public Map<String, String> getHashMap(String key) {
        String liststr = preference.getString(key, "");
        try {
            return String2SceneList(liststr);
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String sceneList2String(List SceneList)
            throws IOException {
        // 实例化一个ByteArrayOutputStream对象，用来装载压缩后的字节文件。
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // 然后将得到的字符数据装载到ObjectOutputStream
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                byteArrayOutputStream);
        // writeObject 方法负责写入特定类的对象的状态，以便相应的 readObject 方法可以还原它
        objectOutputStream.writeObject(SceneList);
        // 最后，用Base64.encode将字节文件转换成Base64编码保存在String中
        String SceneListString = new String(Base64.encode(
                byteArrayOutputStream.toByteArray(), Base64.DEFAULT));
        // 关闭objectOutputStream
        objectOutputStream.close();
        return SceneListString;

    }

    public static List string2SceneList(String SceneListString)
            throws StreamCorruptedException, IOException,
            ClassNotFoundException {
        byte[] mobileBytes = Base64.decode(SceneListString.getBytes(),
                Base64.DEFAULT);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                mobileBytes);
        ObjectInputStream objectInputStream = new ObjectInputStream(
                byteArrayInputStream);
        List SceneList = (List) objectInputStream
                .readObject();
        objectInputStream.close();
        return SceneList;
    }

    /**
     * 针对复杂类型存储<对象>
     *
     * @param key
     * @param object
     */
    public void save(String key, Object object) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(baos);
            out.writeObject(object);
            out.writeObject(null);
            String objectVal = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
            Editor editor = preference.edit();
            editor.putString(key, objectVal);
            editor.commit();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getObject(String key, Class<T> clazz) {
        if (preference.contains(key)) {
            String objectVal = preference.getString(key, null);
            byte[] buffer = Base64.decode(objectVal, Base64.DEFAULT);
            ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
            ObjectInputStream ois = null;
            try {
                ois = new ObjectInputStream(bais);
                T t = (T) ois.readObject();
                return t;
            } catch (StreamCorruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bais != null) {
                        bais.close();
                    }
                    if (ois != null) {
                        ois.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}