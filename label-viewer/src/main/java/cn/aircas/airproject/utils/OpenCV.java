package cn.aircas.airproject.utils;

import org.apache.commons.lang3.StringUtils;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconst;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public final class OpenCV {

	//加载动态链接库
    static {
        gdal.AllRegister();
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    /**
     * 归一化公式类型
     * <p>
     * 参考：https://blog.csdn.net/sss_369/article/details/88563016
     * </p>
     */
    public enum NormalizeType {

        MINMAX(Core.NORM_MINMAX),
        INF(Core.NORM_INF),
        L1(Core.NORM_L1),
        L2(Core.NORM_L2);

        private final int code;

        NormalizeType(int code) {
            this.code = code;
        }

        public Integer getCode() {
            return code;
        }

    }

    /**
     * 归一化处理图片，生成灰度图并覆盖源文件
     *
     * @param src  源文件路径
     * @param type 归一化公式类型
     */
    public static void normalize(String src, NormalizeType type) {
        normalize(src, src, type);
    }

    /**
     * 归一化处理图片，生成灰度图并输出
     *
     * @param src  源文件路径
     * @param dst  输出文件路径
     * @param type 归一化公式类型
     */
    public static void normalize(String src, String dst, NormalizeType type) {
        Dataset dataset = gdal.Open(src);
        String projection = dataset.GetProjection();
        double[] geoTransform = dataset.GetGeoTransform();

        Mat matSrc = Imgcodecs.imread(src);
        //转换成灰度图
        Mat matGray = new Mat(matSrc.size(), CvType.CV_8UC1);
        Imgproc.cvtColor(matSrc, matGray, Imgproc.COLOR_BGR2GRAY);
        //转换成浮点类型矩阵
        Mat matFloatGray = new Mat(matGray.size(), CvType.CV_32F);
        matGray.convertTo(matFloatGray, CvType.CV_32F);
        //归一化处理
        Mat result = Mat.zeros(matFloatGray.size(), CvType.CV_32FC1);
        Core.normalize(matFloatGray, result, 0, 1, type.getCode());

        switch (type) {
            //MINMAX 按 alpha 和 beta 进行缩放限制，得到的结果为 0 或 1，需要乘以 255 恢复为灰度图数据
            case MINMAX:
            //NORM_INF，无穷范数，每个值除以最大值来进行无穷范数归一化，这里限制了最大值为 1，同样需要乘以 255
            case INF:
                Core.multiply(result, new Scalar(255), result);
                break;
            //1 范数，每个值除以它们的和来进行归一化，生成的最大值 < 1。这里随意取了一个值，保证输出为灰度图数据即可。
            case L1:
                Core.multiply(result, new Scalar(20000000), result);
                break;
            //2 范数，每个值除以该向量的模长，归一化为单位向量。与 L1 类似，需要乘以一个值保证输出。
            case L2:
                Core.multiply(result, new Scalar(30000), result);
                break;
            default:
        }

        result.convertTo(result, CvType.CV_8UC1);
        Imgcodecs.imwrite(dst, result);

        if (StringUtils.isNotBlank(projection)){
            dataset = gdal.Open(dst, gdalconst.GA_Update);
            dataset.SetProjection(projection);
            dataset.SetGeoTransform(geoTransform);
        }

    }
}
