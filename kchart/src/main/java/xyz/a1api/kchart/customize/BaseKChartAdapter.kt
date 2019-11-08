package xyz.a1api.kchart.customize

import android.database.DataSetObservable
import android.database.DataSetObserver
import xyz.a1api.kchart.base.IAdapter
import java.util.*
import kotlin.properties.Delegates

/**
 * k线图的数据适配器
 */
abstract class BaseKChartAdapter<T> : IAdapter<T> {

    protected val mData = ArrayList<T>()

    private val mDataSetObservable = DataSetObservable()

    /**
     * 是否支持实时数据刷新(默认支持)
     */
    open var isSupportRealTime = true

    /**
     * 实时数据，自动监听数据变化，通知数据刷新
     */
    open var realTimeData: T? by Delegates.observable((null as T?)) { _, oldValue, newValue ->
        if (!isSameData(oldValue, newValue)) {
            notifyDataSetChanged()
        }
    }

    /**
     * 获取点的数目 （存在实时数据时，数目会增加1）
     */
    override fun getCount(): Int {
        return if (isSupportRealTime && realTimeData != null) {
            mData.size + 1
        } else {
            mData.size
        }
    }

    /**
     * 获取数据集合的实体 （存在实时数据时，会包含实时数据）
     */
    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "UNCHECKED_CAST")
    override fun getData(): List<T> {
        return if (isSupportRealTime && realTimeData != null) {
            listOf(*(mData.toArray()), realTimeData) as List<T>
        } else {
            mData
        }
    }

    /**
     * 获取原始的数据集合的实体 （不会包含实时数据）
     * @return ArrayList<T>
     */
    open fun getOriginalData(): ArrayList<T> {
        return mData
    }

    override fun getItem(position: Int): T {
        if (isSupportRealTime && position == mData.size) {
            return realTimeData ?: mData.last()
        }
        return mData[position]
    }

    override fun changeItem(index: Int, item: T) {
        mData[index] = item
        notifyDataSetChanged()
    }

    override fun setNewData(newData: Collection<T>?) {
        mData.clear()
        if (newData != null) {
            mData.addAll(newData)
        }
        notifyDataSetChanged()
    }

    override fun addPrevData(item: T) {
        mData.add(0, item)
        notifyDataSetChanged()
    }

    override fun addPrevData(newData: Collection<T>) {
        mData.addAll(0, newData)
        notifyDataSetChanged()
    }

    /**
     * 刷新指标数据
     * @param isBackground 是否在UI不可见的地方操作
     */
    open fun refreshData(isBackground: Boolean = true) {
        notifyDataSetChanged()
    }

    override fun addData(item: T) {
        mData.add(item)
        notifyDataSetChanged()
    }

    override fun addData(newData: Collection<T>) {
        mData.addAll(newData)
        notifyDataSetChanged()
    }

    override fun registerDataSetObserver(observer: DataSetObserver) {
        mDataSetObservable.registerObserver(observer)
    }

    override fun unregisterDataSetObserver(observer: DataSetObserver) {
        mDataSetObservable.unregisterObserver(observer)
    }

    override fun notifyDataSetChanged() {
        if (getCount() > 0) {
            mDataSetObservable.notifyChanged()
        } else {
            mDataSetObservable.notifyInvalidated()
        }
    }

    /**
     * 是否是相同的数据
     */
    abstract fun isSameData(oldData: T?, newData: T?): Boolean
}
