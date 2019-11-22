package yuelj.utils.imgdetect;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import yuelj.utils.logs.SystemLog;

/***
 * 对图片进行操作
 * 
 * @author lxr
 * 
 */
public class ImageHelper {

	private static ImageHelper imageHelper = null;

	public static ImageHelper getImageHelper() {
		if (imageHelper == null) {
			imageHelper = new ImageHelper();
		}
		return imageHelper;
	}

	/***
	 * 按指定的比例缩放图片
	 * 
	 * @param sourceImagePath
	 *            源地址
	 * @param destinationPath
	 *            改变大小后图片的地址
	 * @param scale
	 *            缩放比例，如1.2
	 */
	public static File scaleImage(File file, double scale, String format) {
		BufferedImage bufferedImage;
		try {
			bufferedImage = ImageIO.read(file);
			int width = bufferedImage.getWidth();
			int height = bufferedImage.getHeight();

			width = parseDoubleToInt(width * scale);
			height = parseDoubleToInt(height * scale);

			Image image = bufferedImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
			BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics graphics = outputImage.getGraphics();
			graphics.drawImage(image, 0, 0, null);
			graphics.dispose();

			ImageIO.write(outputImage, format, file);
		} catch (IOException e) {
			SystemLog.printlog("scaleImage方法压缩图片时出错了");
			e.printStackTrace();
		}
		return file;
	}

	public static File scaleImageByWidth(File file, int towidth, String format) {
		BufferedImage bufferedImage;
		try {
			bufferedImage = ImageIO.read(file);
			int width = bufferedImage.getWidth();
			int height = bufferedImage.getHeight();
			Double towidthD = new Double(towidth);
			Double widthd = new Double(width);
			height = parseDoubleToInt(towidthD / widthd * height);
			width = parseDoubleToInt(towidth);

			Image image = bufferedImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
			BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics graphics = outputImage.getGraphics();
			graphics.drawImage(image, 0, 0, null);
			graphics.dispose();
			ImageIO.write(outputImage, format, file);
		} catch (IOException e) {
			SystemLog.printlog("scaleImage方法压缩图片时出错了");
			e.printStackTrace();
		}
		return file;
	}

	/***
	 * 将图片缩放到指定的高度或者宽度
	 * 
	 * @param sourceImagePath
	 *            图片源地址
	 * @param destinationPath
	 *            压缩完图片的地址
	 * @param width
	 *            缩放后的宽度
	 * @param height
	 *            缩放后的高度
	 * @param auto
	 *            是否自动保持图片的原高宽比例
	 * @param format
	 *            图图片格式 例如 jpg
	 */
	public static File scaleImageWithParams(File file, int width, int height, boolean auto, String format) {

		try {
			BufferedImage bufferedImage = null;
			bufferedImage = ImageIO.read(file);
			if (auto) {
				ArrayList<Integer> paramsArrayList = getAutoWidthAndHeight(bufferedImage, width, height);
				width = paramsArrayList.get(0);
				height = paramsArrayList.get(1);
				SystemLog.printlog("自动调整比例，width=" + width + " height=" + height);
			}

			Image image = bufferedImage.getScaledInstance(width, height, Image.SCALE_DEFAULT);
			BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics graphics = outputImage.getGraphics();
			graphics.drawImage(image, 0, 0, null);
			graphics.dispose();
			ImageIO.write(outputImage, format, file);
		} catch (Exception e) {
			SystemLog.printlog("scaleImageWithParams方法压缩图片时出错了");
			e.printStackTrace();
		}
		return file;
	}

	/**
	 * 将double类型的数据转换为int，四舍五入原则
	 * 
	 * @param sourceDouble
	 * @return
	 */
	private static int parseDoubleToInt(double sourceDouble) {
		int result = 0;
		result = (int) sourceDouble;
		return result;
	}

	/***
	 * 
	 * @param bufferedImage
	 *            要缩放的图片对象
	 * @param width_scale
	 *            要缩放到的宽度
	 * @param height_scale
	 *            要缩放到的高度
	 * @return 一个集合，第一个元素为宽度，第二个元素为高度
	 */
	private static ArrayList<Integer> getAutoWidthAndHeight(BufferedImage bufferedImage, int width_scale,
			int height_scale) {
		ArrayList<Integer> arrayList = new ArrayList<Integer>();
		int width = bufferedImage.getWidth();
		int height = bufferedImage.getHeight();
		double scale_w = getDot2Decimal(width_scale, width);

		SystemLog.printlog("getAutoWidthAndHeight width=" + width + "scale_w=" + scale_w);
		double scale_h = getDot2Decimal(height_scale, height);
		if (scale_w < scale_h) {
			arrayList.add(parseDoubleToInt(scale_w * width));
			arrayList.add(parseDoubleToInt(scale_w * height));
		} else {
			arrayList.add(parseDoubleToInt(scale_h * width));
			arrayList.add(parseDoubleToInt(scale_h * height));
		}
		return arrayList;

	}

	/***
	 * 返回两个数a/b的小数点后三位的表示
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static double getDot2Decimal(int a, int b) {

		BigDecimal bigDecimal_1 = new BigDecimal(a);
		BigDecimal bigDecimal_2 = new BigDecimal(b);
		BigDecimal bigDecimal_result = bigDecimal_1.divide(bigDecimal_2, new MathContext(4));
		Double double1 = new Double(bigDecimal_result.toString());
		SystemLog.printlog("相除后的double为：" + double1);
		return double1;
	}

	private static int OVERLAP_WIDTH = 30;

	/**
	 * 图片拼接
	 * 
	 * @param files
	 *            要拼接的图片列表
	 * @param type
	 *            1 横向拼接， 2 纵向拼接
	 * @return
	 */
	public static InputStream mergeImages(List<InputStream> inslist, int type) {
		int len = inslist.size();
		if (len < 1) {
			// log.info("图片数量小于1");
			return null;
		}

		BufferedImage[] images = new BufferedImage[len];
		int[][] ImageArrays = new int[len][];
		BufferedImage resultImg = null;
		for (int i = 0; i < len; i++) {
			try {
				images[i] = ImageIO.read(inslist.get(i));
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			int width = images[i].getWidth();
			int height = images[i].getHeight();
			ImageArrays[i] = new int[width * height];// 从图片中读取RGB
			ImageArrays[i] = getEllipseImg(images[i]);
		}

		int newHeight = 0;
		int newWidth = OVERLAP_WIDTH;
		for (int i = 0; i < images.length; i++) {
			// 横向
			if (type == 1) {
				newHeight = newHeight > images[i].getHeight() ? newHeight : images[i].getHeight();
				newWidth += images[i].getWidth() - OVERLAP_WIDTH;
			} else if (type == 2) {// 纵向
				newWidth = newWidth > images[i].getWidth() ? newWidth : images[i].getWidth();
				newHeight += images[i].getHeight();
			}
		}
		for (int i = 0; i < images.length; i++) {
			images[i] = getEllipseImgBuffered(images[i]);
		}
		resultImg = getEllipseImgAll(images[0], images[1]);
		for (int i = 2; i < images.length; i++) {
			resultImg = getEllipseImgAll(resultImg, images[i]);
		}
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ImageIO.write(resultImg, "png", out);// 图片写入到输出流中

			ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());

			return in;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 将老图片左侧拼上新图片，遮盖老图OVERLAP_WIDTH像素
	 * 
	 * @param ImageOld
	 *            老图
	 * @param ImageNew
	 *            新图
	 * @return 合成后的图片
	 */
	private static BufferedImage getEllipseImgAll(BufferedImage ImageOld, BufferedImage ImageNew) {
		int width = ImageNew.getWidth() + ImageOld.getWidth() - OVERLAP_WIDTH;
		BufferedImage image = new BufferedImage(width, ImageNew.getHeight(), BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2 = image.createGraphics();
		image = g2.getDeviceConfiguration().createCompatibleImage(width, ImageNew.getHeight(),
				Transparency.TRANSLUCENT);
		g2 = image.createGraphics();

		// 从右往左，先画old图
		Rectangle shape2 = new Rectangle(ImageNew.getWidth() - OVERLAP_WIDTH, 0, ImageOld.getWidth(),
				ImageOld.getHeight());
		g2.setClip(shape2);
		g2.drawImage(ImageOld, ImageNew.getWidth() - OVERLAP_WIDTH, 0, null);
		// 画遮罩
		Ellipse2D.Double shape3 = new Ellipse2D.Double(OVERLAP_WIDTH / 2, 0, ImageNew.getWidth(), ImageNew.getHeight());
		g2.setColor(Color.WHITE);
		g2.setBackground(Color.WHITE);
		g2.fill(shape3);
		g2.draw(shape3);
		// 再画新图
		Ellipse2D.Double shape = new Ellipse2D.Double(0, 0, ImageNew.getWidth(), ImageNew.getHeight());
		g2.setClip(shape);
		g2.drawImage(ImageNew, 0, 0, null);

		g2.dispose();
		return image;
	}

	/**
	 * 图片切割为圆形
	 * 
	 * @param ImageNew
	 * @return
	 */
	private static BufferedImage getEllipseImgBuffered(BufferedImage ImageNew) {
		BufferedImage image = new BufferedImage(ImageNew.getWidth(), ImageNew.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Ellipse2D.Double shape = new Ellipse2D.Double(0, 0, ImageNew.getWidth(), ImageNew.getHeight());

		Graphics2D g2 = image.createGraphics();
		image = g2.getDeviceConfiguration().createCompatibleImage(ImageNew.getWidth(), ImageNew.getHeight(),
				Transparency.TRANSLUCENT);
		g2 = image.createGraphics();
		g2.setClip(shape);
		// 使用 setRenderingHint 设置抗锯齿
		g2.drawImage(ImageNew, 0, 0, null);
		g2.dispose();
		int width = image.getWidth();
		int height = image.getHeight();
		int[] imgArray = new int[width * height];// 从图片中读取RGB
		imgArray = image.getRGB(0, 0, width, height, imgArray, 0, width);
		return image;
	}

	/**
	 * 获取圆形图片
	 * 
	 * @param ImageNew
	 * @return
	 */
	private static int[] getEllipseImg(BufferedImage ImageNew) {
		BufferedImage image = new BufferedImage(ImageNew.getWidth(), ImageNew.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Ellipse2D.Double shape = new Ellipse2D.Double(0, 0, ImageNew.getWidth(), ImageNew.getHeight());

		Graphics2D g2 = image.createGraphics();
		image = g2.getDeviceConfiguration().createCompatibleImage(ImageNew.getWidth(), ImageNew.getHeight(),
				Transparency.TRANSLUCENT);
		g2 = image.createGraphics();
		// g2.fill(new Rectangle(image.getWidth(), image.getHeight()));
		// 透明度
		// g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC,
		// 0f));
		g2.setClip(shape);
		// 使用 setRenderingHint 设置抗锯齿
		g2.drawImage(ImageNew, 0, 0, null);
		g2.dispose();
		int width = image.getWidth();
		int height = image.getHeight();
		int[] imgArray = new int[width * height];// 从图片中读取RGB
		imgArray = image.getRGB(0, 0, width, height, imgArray, 0, width);
		return imgArray;
	}

	/**
	 * 图像格式转换
	 * 
	 * @param source
	 * @param formatName
	 * @param result
	 */
	public static void convert(String source, String formatName, String result) {
		try {
			File f = new File(source);
			f.canRead();
			BufferedImage src = ImageIO.read(f);
			File nf = new File(result);
			ImageIO.write(src, formatName, nf);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
