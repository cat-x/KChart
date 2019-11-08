package xyz.a1api.kchart.base

import android.database.DataSetObserver


/**
 * 数据适配器
 * @param <T> 实体类泛型
 * Created by tifezh on 2016/6/14.
 * For KChart
 * Cat-x All Rights Reserved
</T> */

interface IAdapter<T> {

    /**
     * 获取点的数目
     */
    fun getCount(): Int

    /**
     * 通过序号获取item
     *
     * @param position 对应的序号
     * @return 数据实体
     */
    fun getItem(position: Int): T

    /**
     * 通过序号获取时间
     *
     * @param position 索引
     */
    fun getDate(position: Int): String

    /**
     * 注册一个数据观察者
     *
     * @param observer 数据观察者
     */
    fun registerDataSetObserver(observer: DataSetObserver)

    /**
     * 移除一个数据观察者
     *
     * @param observer 数据观察者
     */
    fun unregisterDataSetObserver(observer: DataSetObserver)

    /**
     * 当数据发生变化时调用
     */
    fun notifyDataSetChanged()

    /**
     * 获取数据集合的实体
     */
    fun getData(): List<T>

    /**
     * 清空历史数据并添加新的数据集合
     * @param newData 新数据集合
     */
    fun setNewData(newData: Collection<T>?)

    /**
     * 在尾部追加数据
     * @param item 实体类
     */
    fun addPrevData(item: T)

    /**
     * 在尾部追加数据
     */
    fun addPrevData(newData: Collection<T>)

    /**
     * 在头部追加数据
     * @param item 实体类
     */
    fun addData(item: T)

    /**
     * 在头部追加数据
     */
    fun addData(newData: Collection<T>)

    /**
     * 根据索引改变实体值
     * @param index 索引
     * @param item 实体
     */
    fun changeItem(index: Int, item: T)

}
