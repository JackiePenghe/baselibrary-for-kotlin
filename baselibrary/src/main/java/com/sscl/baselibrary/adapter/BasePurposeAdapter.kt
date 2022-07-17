package com.sscl.baselibrary.adapter

import android.content.*
import android.view.*
import android.widget.*
import java.util.ArrayList

import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import com.sscl.baselibrary.image.ImageLoader
import androidx.annotation.StringRes
import androidx.annotation.ColorInt
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes

/**
 * ListView万能适配器
 *
 * @author alm
 */
abstract class BasePurposeAdapter<T> constructor(
    /**
     * 适配器数据源
     */
    protected var mData: ArrayList<T>,
    /**
     * 适配器item的布局id
     */
    @field:LayoutRes @param:LayoutRes private val mItemLayoutId: Int
) : BaseAdapter() {

    /*--------------------------------静态常量--------------------------------*/

    @Suppress("PropertyName")
    protected val TAG: String = javaClass.simpleName

    /*--------------------------------成员变量--------------------------------*/
    /**
     * 上下文弱引用
     */
    private var mContext: Context? = null

    /**
     * 自定义适配器总数(适配器会根据这个数与数据源的比例来显示)
     */
    private var mCountSum: Int = -1
    /*--------------------------------实现父类方法--------------------------------*/
    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    override fun getCount(): Int {
        return if (mCountSum == -1) {
            mData.size
        } else {
            mCountSum
        }
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     * data set.
     * @return The data at the specified position.
     */
    override fun getItem(position: Int): T {
        return if (mCountSum == -1) {
            mData[position]
        } else {
            mData[mCountSum % mData.size]
        }
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    /**
     * Get a View that displays the data at the specified position in the data set. You can either
     * create a View manually or inflate it from an XML layout file. When the View is inflated, the
     * parent View (GridView, ListView...) will apply default layout parameters unless you use
     * [LayoutInflater.inflate]
     * to specify a root view and to prevent attachment to the root.
     *
     * @param position    The position of the item within the adapter's data set of the item whose view
     * we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     * is non-null and of an appropriate type before using. If it is not possible to convert
     * this view to display the correct data, this method can create a new view.
     * Heterogeneous lists can specify their number of view types, so that this View is
     * always of the right type (see [.getViewTypeCount] and
     * [.getItemViewType]).
     * @param parent      The parent that this view will eventually be attached to
     * @return a View corresponding to the data at the specified position.
     */
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        if (mContext == null) {
            mContext = parent.context
        }
        val viewHolder: ViewHolder = getViewHolder(position, convertView, parent)
        convert(viewHolder, position, getItem(position))
        return viewHolder.convertView
    }

    /*--------------------------------公开方法--------------------------------*/

    /**
     * 更新数据源并刷新适配器进行显示
     *
     * @param dataList 新的数据源
     */
    @Suppress("unused")
    fun refresh(dataList: ArrayList<T>) {
        mData = dataList
        notifyDataSetChanged()
    }

    /**
     * 删除数据源中指定位置的数据
     *
     * @param position 指定删除位置
     */
    fun deleteListByPosition(position: Int) {
        if (position >= mData.size) {
            return
        }
        mData.removeAt(position)
        notifyDataSetChanged()
    }

    /**
     * 设置自定义数据总量
     *
     * @param countSum 自定义数据总量
     * @return AllPurposeAdapter本类对象
     */
    fun setCount(countSum: Int): BasePurposeAdapter<T> {
        mCountSum = countSum
        return this
    }
    /*--------------------------------私有方法--------------------------------*/
    /**
     * 获取ViewHolder
     *
     * @param position    当前位置
     * @param convertView 复用的View
     * @param parent      父布局
     * @return ViewHolder
     */
    private fun getViewHolder(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): ViewHolder {
        val context: Context = if (convertView != null) {
            convertView.context
        } else {
            mContext ?: parent.context
        }
        return ViewHolder[context, convertView, parent, mItemLayoutId, position]
    }
    /*--------------------------------抽象方法--------------------------------*/
    /**
     * 每一个单独的选项的内容设置
     *
     * @param viewHolder ViewHolder
     * @param position   position
     * @param item       item
     */
    protected abstract fun convert(
        viewHolder: ViewHolder,
        position: Int,
        item: T
    )

    /**
     * ListView万能适配器专用的ViewHolder
     *
     * @author alm
     */
    class ViewHolder private constructor(
        context: Context, parent: ViewGroup, @LayoutRes itemLayoutId: Int,
        /**
         * 当前位置
         */
        val position: Int
    ) {
        /*--------------------------------成员变量--------------------------------*/
        /**
         * 获取当前位置
         *
         * @return 当前位置
         */
        /**
         * 获取item的整个布局控件
         *
         * @return item的整个布局控件
         */
        /**
         * 当前item的View
         */
        val convertView: View

        /**
         * 图片加载工具
         */
        private val imageLoader: ImageLoader?
        /*--------------------------------公开函数--------------------------------*/
        /**
         * 根据控件的Id获取控件(先从缓存中获取，如果没有则findViewById,然后保存到缓存)
         *
         * @param viewId 控件的Id
         * @return 对应的控件
         */
        fun <T : View?> getView(@IdRes viewId: Int): T? {
            return convertView.findViewById(viewId)
        }

        /**
         * 为TextView设置文本内容
         *
         * @param viewId 控件的Id
         * @param text   要设置的文本内容
         * @return ViewHolder本类
         */
        fun setText(@IdRes viewId: Int, text: CharSequence): ViewHolder {
            val view: View = (getView<View>(viewId)) ?: return this
            if (view is TextView) {
                view.text = text
            }
            return this
        }

        /**
         * 为CompoundButton设置选中状态
         *
         * @param viewId  控件的Id
         * @param checked 选中状态
         * @return ViewHolder本类
         */
        @Suppress("unused")
        fun setChecked(@IdRes viewId: Int, checked: Boolean): ViewHolder {
            val view: View = (getView<View>(viewId)) ?: return this
            if (view is CompoundButton) {
                view.isChecked = checked
            }
            return this
        }

        /**
         * 为TextView设置文本内容
         *
         * @param viewId  控件的Id
         * @param textRes 要设置的文本内容资源ID
         * @return ViewHolder本类
         */
        fun setText(@IdRes viewId: Int, @StringRes textRes: Int): ViewHolder {
            val view: View = (getView<View>(viewId)) ?: return this
            if (view is TextView) {
                view.setText(textRes)
            }
            return this
        }

        /**
         * 获取editText的文本
         *
         * @param viewId 控件的Id
         * @return editText的文本
         */
        @Suppress("unused")
        fun getEditText(@IdRes viewId: Int): CharSequence? {
            val view: View = (getView<View>(viewId)) ?: return null
            if (view is EditText) {
                return view.text
            }
            return null
        }

        /**
         * 为ImageView设置图片
         *
         * @param viewId        控件的Id
         * @param drawableResId 图片的资源Id
         * @return ViewHolder本类
         */
        fun setImageResource(
            @IdRes viewId: Int,
            @DrawableRes drawableResId: Int
        ): ViewHolder {
            val view: View = (getView<View>(viewId)) ?: return this
            if (view is ImageView) {
                view.setImageResource(drawableResId)
            }
            return this
        }

        /**
         * 为ImageView设置图片
         *
         * @param viewId 控件的Id
         * @param bitmap 位图图片
         * @return ViewHolder本类
         */
        @Suppress("unused")
        fun setImageBitmap(@IdRes viewId: Int, bitmap: Bitmap): ViewHolder {
            val view: View = (getView<View>(viewId)) ?: return this
            if (view is ImageView) {
                view.setImageBitmap(bitmap)
            }
            return this
        }

        /**
         * 为ImageView设置图片
         *
         * @param viewId   控件的Id
         * @param url      图片的网络地址
         * @param isCircle 是否将图片显示为圆形
         * @return ViewHolder本类
         */
        @Suppress("unused")
        fun setImageByUrl(
            @IdRes viewId: Int,
            url: String,
            isCircle: Boolean
        ): ViewHolder {
            val view: View = (getView<View>(viewId)) ?: return this
            if (view is ImageView) {
                imageLoader?.displayImage(url, view, isCircle)
            }
            return this
        }

        /**
         * 给view设置背景色
         *
         * @param viewId 控件的Id
         * @param color  背景色
         * @return ViewHolder本类
         */
        @Suppress("unused")
        fun setBackgroundColor(
            @IdRes viewId: Int,
            @ColorInt color: Int
        ): ViewHolder {
            getView<View>(viewId)?.setBackgroundColor(color)
            return this
        }

        companion object {

            /*--------------------------------静态函数--------------------------------*/

            /**
             * 获取一个ViewHolder本类实例
             *
             * @param context      上下文
             * @param convertView  当前item的View
             * @param parent       ViewGroup
             * @param itemLayoutId 布局文件资源id
             * @param position     当前位置
             * @return ViewHolder本类实例
             */
            operator fun get(
                context: Context,
                convertView: View?,
                parent: ViewGroup,
                @LayoutRes itemLayoutId: Int,
                position: Int
            ): ViewHolder {
                if (convertView == null) {
                    return ViewHolder(context, parent, itemLayoutId, position)
                }
                return convertView.tag as ViewHolder
            }
        }
        /*--------------------------------构造函数--------------------------------*/

        init {
            convertView = LayoutInflater.from(context).inflate(itemLayoutId, parent, false)
            convertView.tag = this
            imageLoader = ImageLoader.getInstance(context)
        }
    }
    /*--------------------------------构造方法--------------------------------*/
}