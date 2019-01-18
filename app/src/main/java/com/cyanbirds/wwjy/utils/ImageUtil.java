package com.cyanbirds.wwjy.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.util.Log;


import com.cyanbirds.wwjy.entity.Picture;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 
 * @ClassName:ImageUtil.java
 * @Description:图片处理工具类
 * @author Administrator
 * @Date:2015年5月21日下午2:14:59
 *
 */
public class ImageUtil {


	/**
	 * 对外接口，压缩图片，并将其保存到指定位置
	 * @param filePath 压缩图片的路径
	 * @param savePath   将压缩后的图片保存的路径
	 */
	public static String compressImage(String filePath, String savePath) {
		Bitmap bm = getSmallBitmap(filePath);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 70, baos);
		byte[] b = baos.toByteArray();
		Log.d("d", "压缩后的大小=" + b.length);//1.5M的压缩后在100Kb以内，测试得值,压缩后的大小=94486,压缩后的大小=74473
		Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
		return savePhotoToSDCard(bitmap, savePath, FileUtils.getFileName(filePath));
	}

	/**
	 * 保存图像到sd card
	 *
	 * @param photoBitmap
	 * @param photoName
	 * @param path
	 */
	public static String savePhotoToSDCard(Bitmap photoBitmap, String path,
			String photoName) {
		if (checkSDCardAvailable()) {
			File dir = new File(path);
			if (!dir.exists()) {
				dir.mkdirs();
			}

			File photoFile = new File(path, photoName);

			FileOutputStream fileOutputStream = null;
			try {
				fileOutputStream = new FileOutputStream(photoFile);
				if (photoBitmap != null) {
					if (photoBitmap.compress(Bitmap.CompressFormat.JPEG, 80,
							fileOutputStream)) {
						fileOutputStream.flush();
					}
				}
				return photoFile.getAbsolutePath();
			} catch (FileNotFoundException e) {
				photoFile.delete();
				e.printStackTrace();
			} catch (IOException e) {
				photoFile.delete();
				e.printStackTrace();
			} finally {
				try {
					fileOutputStream.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	// 根据路径获得图片并压缩，返回bitmap用于显示
	public static Bitmap getSmallBitmap(String filePath) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, 480, 800);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;

		return BitmapFactory.decodeFile(filePath, options);
	}

	//计算图片的缩放值
	public static int calculateInSampleSize(BitmapFactory.Options options,int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		return inSampleSize;
	}

	/**
	 * 验证sd card
	 *
	 * @return
	 */
	public static boolean checkSDCardAvailable() {
		return android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
	}


	public static Picture getPicInfoForPath(String url){
		Picture picture = new Picture();
		picture.size = FileUtils.decodeFileLength(url);
		Options options = ImageUtil
				.getBitmapOptions(new File(url).getAbsolutePath());
		picture.width = options.outWidth;
		picture.height = options.outHeight;
		picture.form = options.outMimeType;
		picture.path = url;
		return picture;
	}

	/**
	 * 得到指定路径图片的options
	 * @param srcPath
	 * @return Options {@link Options}
	 */
	public final static Options getBitmapOptions(String srcPath) {
		Options options = new Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(srcPath, options);
		return options;
	}
}
