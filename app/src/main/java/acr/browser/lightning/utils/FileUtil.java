package acr.browser.lightning.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import net.chinaedu.aedu.utils.AeduSDCardUtil;
import net.chinaedu.aedu.utils.LogUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by MartinKent on 2017/5/9.
 */

@SuppressWarnings({"JavaDoc", "ResultOfMethodCallIgnored"})
public class FileUtil {
    private static final int TYPE_EXTERNAL_SHARE_DIR = 0;
    private static final int TYPE_FILE_SHARE_DIR = 1;
    private static final int TYPE_CACHE_SHARE_DIR = 2;

    private static String mTemporaryDirPath;
    private static String mCacheDirPath;

    /**
     * 获取缓存目录
     *
     * @param context
     * @return
     */
    public static File getCacheDir(Context context) {
        if (null == mCacheDirPath) {
            File cacheDirFile = getSDCard();
            if (null == cacheDirFile) {
                cacheDirFile = context.getCacheDir();
            } else {
                cacheDirFile = new File(cacheDirFile, context.getPackageName());
            }
            cacheDirFile = new File(cacheDirFile, "/cache_files/");
            if (!cacheDirFile.exists()) {
                cacheDirFile.mkdirs();
            }
            if (cacheDirFile.exists()) {
                mCacheDirPath = cacheDirFile.getPath();
                return cacheDirFile;
            } else {
                LogUtils.e("CacheDir create failed");
                return new File(context.getCacheDir(), "/cache_files/");
            }
        } else {
            return new File(mCacheDirPath);
        }
    }

    /**
     * 获取存放临时文件的目录
     *
     * @param context
     * @return
     */
    public static File getTemporaryDir(Context context) {
        if (null == mTemporaryDirPath) {
            File tmpDirFile = getSDCard();
            if (null == tmpDirFile) {
                tmpDirFile = context.getFilesDir();
            } else {
                tmpDirFile = new File(tmpDirFile, context.getPackageName());
            }
            tmpDirFile = new File(tmpDirFile, "/temporary_files/");
            if (!tmpDirFile.exists()) {
                tmpDirFile.mkdirs();
            }
            if (tmpDirFile.exists()) {
                mTemporaryDirPath = tmpDirFile.getPath();
                return tmpDirFile;
            } else {
                LogUtils.e("TemporaryDir create failed");
                return new File(context.getCacheDir(), "/temporary_files/");
            }
        } else {
            return new File(mTemporaryDirPath);
        }
    }

    /**
     * 获取sd卡
     *
     * @return sd卡
     */
    public static File getSDCard() {
        return isSDCardAvailable() ? Environment.getExternalStorageDirectory() : null;
    }

    /**
     * 判断sd是否可用
     *
     * @return sd可用状态
     */
    public static boolean isSDCardAvailable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * 判断sd卡是否能分配指定大小的空间
     *
     * @param size 需要分配的空间大小，单位byte
     * @return true:有足够的空间; false:没有足够的空间
     */
    public static boolean isSDCardAvailable(long size) {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) && Long.valueOf(AeduSDCardUtil.getSDFreeSize()).compareTo(size) >= 0;
    }

    public static void saveBitmapToFile(File file, Bitmap bitmap) {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static File saveBitmapToTempFile(Context context, Bitmap bitmap) {
        File tmpDir = FileUtil.getTemporaryDir(context);
        File tmpPicFile = new File(tmpDir, "tmp_pic_" + System.currentTimeMillis() + Double.valueOf(Math.random() * 100).intValue() + ".jpg");
        if (tmpPicFile.exists()) {
            tmpPicFile.delete();
        }
        try {
            tmpPicFile.createNewFile();
            FileUtil.saveBitmapToFile(tmpPicFile, bitmap);
            return tmpPicFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 判断是否有足够的空间
     *
     * @param size
     * @return
     */
    public static boolean isSpaceEnough(String dir, long size) {
        StatFs statFs = new StatFs(dir);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return statFs.getFreeBytes() >= size;
        }
        try {
            return statFs.getAvailableBytes() >= size;
        } catch (Exception e) {
            //sd卡可用分区数
            int avCounts = statFs.getAvailableBlocks();
            //一个分区数的大小
            long blockSize = statFs.getBlockSize();
            //sd卡可用空间
            long spaceLeft = avCounts * blockSize;
            return spaceLeft >= size;
        }
    }

    public static File getShareDirectory(Context context, int type) {
        if (TYPE_EXTERNAL_SHARE_DIR == type) {
            File file = new File(Environment.getExternalStorageDirectory() + "/" + context.getPackageName() + "/share");
            if (!file.exists()) {
                file.mkdirs();
            }
            return file;
        } else if (TYPE_FILE_SHARE_DIR == type) {
            File file = new File(context.getFilesDir() + "/share");
            if (!file.exists()) {
                file.mkdirs();
            }
            return file;
        } else if (TYPE_CACHE_SHARE_DIR == type) {
            File file = new File(context.getCacheDir() + "/share");
            if (!file.exists()) {
                file.mkdirs();
            }
            return file;
        }
        return null;
    }

    public static File getShareDirectoryForSize(Context context, long size) {
        try {
            File dir = getShareDirectory(context, TYPE_EXTERNAL_SHARE_DIR);
            if (!isSpaceEnough(Environment.getExternalStorageDirectory().getAbsolutePath(), size)) {
                dir = null;
            }
            if (null == dir) {
                dir = getShareDirectory(context, TYPE_CACHE_SHARE_DIR);
            }
            return dir;
        } catch (Exception e) {
            return getShareDirectory(context, TYPE_CACHE_SHARE_DIR);
        }
    }
}
