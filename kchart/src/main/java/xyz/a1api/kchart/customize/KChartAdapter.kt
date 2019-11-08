package xyz.a1api.kchart.customize

import xyz.a1api.kchart.view.KChartView


/**
 * KLineEntity数据适配器
 */
open class KChartAdapter<T : BaseKLineEntity>(open var mKChartView: KChartView? = null) :
    BaseKChartAdapter<T>() {

    /**
     * 添加实时数据，用于最后一列数据跳动变化显示
     * @param data 数据
     * @param isNeedAddToData 是否永久添加到数据集合列表
     */
    @Suppress("unused")
    open fun addRealTimeData(data: T, isNeedAddToData: Boolean = false) {
        requireNotNull(mKChartView) { "mKChartView is null，mKChartView must be set" }
        if (isNeedAddToData) {
            mKChartView?.showLoading()
        }
        val list = arrayListOf<T>()
        val originalData = getOriginalData()
        if (originalData.isNotEmpty()) {
            list.addAll(originalData)
            list.add(data)
            //重新计算指标
            KChartDataHelper.calculate(list)
            //设置原有数据（指标数据会刷新）
            mData.clear()
            if (isNeedAddToData) {//如果加入集合，则不用修改list
                mData.addAll(list)
            } else {
                mData.addAll(list.dropLast(1))
            }
            //设置实时数据
            realTimeData = list.last()
        } else {
            KChartDataHelper.calculate(listOf(data))
            realTimeData = data;
        }

        if (isNeedAddToData) {
//            notifyDataSetChanged()
            mKChartView?.refreshComplete()
        }
    }

    /**
     * 刷新指标数据
     * @param isBackground 是否在UI不可见的地方操作
     */
    override fun refreshData(isBackground: Boolean) {
        if (isBackground) {
            KChartDataHelper.calculate(mData) //重新计算指标
            notifyDataSetChanged()
        } else {
            requireNotNull(mKChartView) { "mKChartView is null，mKChartView must be set" }
            mKChartView?.showLoading()
            KChartDataHelper.calculate(mData) //重新计算指标
            notifyDataSetChanged()
            mKChartView?.refreshComplete()
        }

    }

    /**
     * 向数据集合添加一个数据
     */
    override fun addData(item: T) {
        requireNotNull(mKChartView) { "mKChartView is null，mKChartView must be set" }
        mKChartView?.showLoading()
        mData.add(item)
        KChartDataHelper.calculate(mData) //重新计算指标
        notifyDataSetChanged()
        mKChartView?.refreshComplete()
    }

    /**
     * 向数据集合添加一个集合数据
     */
    override fun addData(newData: Collection<T>) {
        requireNotNull(mKChartView) { "mKChartView is null，mKChartView must be set" }
        if (newData.isNotEmpty()) {
            mKChartView?.showLoading()
            mData.addAll(newData)
            KChartDataHelper.calculate(mData) //重新计算指标
            notifyDataSetChanged()
            mKChartView?.refreshComplete()
        }
    }


    /**
     * 替换指定的数据
     */
    override fun changeItem(index: Int, item: T) {
        requireNotNull(mKChartView) { "mKChartView is null，mKChartView must be set" }
        mKChartView?.showLoading()
        mData[index] = item
        KChartDataHelper.calculate(mData) //重新计算指标
        notifyDataSetChanged()
        mKChartView?.refreshComplete()
    }


    /**
     * 向数据集合头部添加数据
     */
    override fun addPrevData(item: T) {
        requireNotNull(mKChartView) { "mKChartView is null，mKChartView must be set" }
        mKChartView?.showLoading()
        mData.add(0, item)
        KChartDataHelper.calculate(mData) //重新计算指标
        notifyDataSetChanged()
        mKChartView?.refreshComplete()
    }

    /**
     * 向数据集合头部添加集合数据
     */
    override fun addPrevData(newData: Collection<T>) {
        requireNotNull(mKChartView) { "mKChartView is null，mKChartView must be set" }
        if (newData.isNotEmpty()) {
            mKChartView?.showLoading()
            mData.addAll(0, newData)
            KChartDataHelper.calculate(mData) //重新计算指标
            notifyDataSetChanged()
            mKChartView?.refreshComplete()
        }
    }

    /**
     * 获取显示时间
     */
    @Suppress("DEPRECATION")
    override fun getDate(position: Int): String {
        return getItem(position).getShowDateTime()
//        try {
//            val s = getItem(position).getShowDateTime()
//            val split = s.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
//            val date = Date()
//            date.year = Integer.parseInt(split[0]) - 1900
//            date.month = Integer.parseInt(split[1]) - 1
//            date.date = Integer.parseInt(split[2])
//            return date
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//        error("getDate null")
    }

    /**
     * 是否是相同的数据
     */
    override fun isSameData(oldData: T?, newData: T?): Boolean {
        return (oldData?.getClosePrice() != newData?.getClosePrice() ||
                oldData?.getOpenPrice() != newData?.getOpenPrice() ||
                oldData?.getLowPrice() != newData?.getLowPrice() ||
                oldData?.getHighPrice() != newData?.getHighPrice() ||
                oldData?.getVolume() != newData?.getVolume() ||
                oldData?.getAvgPrice() != newData?.getAvgPrice() ||
                oldData?.getPrice() != newData?.getPrice())
    }

}
