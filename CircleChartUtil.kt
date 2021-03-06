package com.seuic.mobiledevicemanager.utils


/**
 * <pre>
 *     author: Bruce_Yang
 *     email : yangjianan@seuic.com
 *     time  : 2021/03/05
 *     desc  :
 * </pre>
 */
object CircleChartUtil {
    data class CircleData(val x:Double,val y:Double,val radius:Double)

    @JvmStatic
    fun main(args: Array<String>) {
        //todo 定义屏幕宽高，时间是前端计算出来
        var screenWidth = 1920
        var screenHeight = 1080
        //todo 高度有一个padding，防止圆占满了整个高度，具体值实际调整
        var heightPadding = 30

        //C1圆半径
        var r1 = getC1Radius()
        //C2圆半径
        var r2 = getC2Radius()
        //C3圆半径
        var r3 = getC3Radius()

        //定义中心圆坐标，圆称作C1
        var xC1 = screenWidth / 2.0
        var yC1 = screenHeight / 2.0
        println("中心圆坐标$xC1,$yC1")

        //中心圆C1与他的五等分（以360°分割）圆C2之间的连线长度（圆弧连线不包括半径）定义为line1
        //圆C2与他的五等分（以180°分割）圆C3之间的连线长度（圆弧连线不包括半径）定义为line2
        //这里把约定 line1=2*line2
        var lineMultiple = 2 //倍数
        //目标是求出line1与line2的长度
        var line2 = (screenHeight - heightPadding - 3 * r2 - 4 * r3 - r1 * Math.cos(Math.toRadians(36.0)) - r2 * Math.cos(Math.toRadians(36.0))) / (1 + lineMultiple + lineMultiple * Math.cos(Math.toRadians(36.0)))
        var line1 = lineMultiple * line2
        println("line1 = $line1  line2 = $line2")

        //求出C1五等分圆坐标列表
        val c1_c2List = getC1_C2Points(r1 + line1 + r2, xC1, yC1, r2, 5)
        //println(c1_c2List)
        //存储所有c2_c3List
        val c2_c3List = ArrayList<ArrayList<CircleData>>()
        //遍历c2，然后算出每个c2的（180°）五等分圆c3列表
        c1_c2List.forEach {
            val x = it.x
            val y = it.y
            c2_c3List.add(getC2_C3Points(r2 + line2 + r3, x, y, 5, y >= yC1))
        }

        println("五组等分圆:\n$c2_c3List")

    }

    //todo 前端计算中心圆C1半径
    fun getC1Radius(): Double {
        return 50.0
    }

    //todo 前端计算圆C2半径(C2是C1的以360°分割等分卫星圆)
    fun getC2Radius(): Double {
        return 30.0
    }

    //todo 前端计算圆C3半径(C3是C2的以180°分割等分卫星圆)
    fun getC3Radius(): Double {
        return 20.0
    }

    /**
     * 以某点为圆心，生成圆周上等分点的坐标
     * @param R 圆心半径
     * @param cx 圆心x坐标
     * @param xy 圆心y坐标
     * @param count 等分点数量
     * @param targetR 等分圆的半径
     */
    fun getC1_C2Points(R: Double, cx: Double, xy: Double, targetR: Double, count: Int): ArrayList<CircleData> {
        var points = ArrayList<CircleData>()
        var radians = Math.PI / 180 * Math.round((360 / count).toDouble())
        for (i in 0 until count) {
            var x = cx + R * Math.sin(radians * i)
            var y = xy + R * Math.cos(radians * i)
            points.add(CircleData(x,y,targetR))
            //println("$x, y")
        }
        return points
    }

    /**
     * 以某点为圆心，生成圆周上等分点的坐标
     * @param r 半径
     * @param pos 圆心坐标
     * @param count 等分点数量
     * @param up 是否是上半圆
     */
    fun getC2_C3Points(r: Double, cx: Double, xy: Double, count: Int, up: Boolean): ArrayList<CircleData> {
        var points = ArrayList<CircleData>()
        var realCount = 2 * count
        var radians = Math.PI / 180 * Math.round((360 / realCount).toDouble())

        //先把当前圆中心坐标加进来，让他与等分圆作为一组数据,这里把对应的圆半径也加上
        points.add(CircleData(cx, xy, getC2Radius()))

        for (i in 0 until realCount) {
            var x = cx + r * Math.sin(radians * i)
            var y = xy + r * Math.cos(radians * i)
            if (up) {
                //上半圆判断y坐标要大于等于圆心y坐标
                if (y < xy) {
                    continue
                }
            } else {
                //下半圆判断y坐标要小于于等于圆心y坐标
                if (y > xy) {
                    continue
                }
            }
            points.add(CircleData(x, y, getC3Radius()))
            //println("$x, $y")
        }
        return points
    }


}