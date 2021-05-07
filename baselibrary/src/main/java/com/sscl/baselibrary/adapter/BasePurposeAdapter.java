package com.sscl.baselibrary.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.sscl.baselibrary.image.ImageLoader;

import java.util.ArrayList;


/**
 * ListView万能适配器
 *
 * @author alm
 */

public abstract class BasePurposeAdapter<T> extends BaseAdapter {

    /*--------------------------------静态常量--------------------------------*/

    protected final String TAG = getClass().getSimpleName();

    /*--------------------------------成员变量--------------------------------*/

    /**
     * 上下文弱引用
     */
    @SuppressWarnings("WeakerAccess")
    protected Context mContext;

    /**
     * 适配器数据源
     */
    @SuppressWarnings("WeakerAccess")
    protected ArrayList<T> mData;

    /**
     * 适配器item的布局id
     */
    @LayoutRes
    private final int mItemLayoutId;

    /**
     * 自定义适配器总数(适配器会根据这个数与数据源的比例来显示)
     */
    private int mCountSum = -1;

    /*--------------------------------构造方法--------------------------------*/

    /**
     * 构造器
     *
     * @param dataList     适配器数据源
     * @param itemLayoutId 适配器item的布局id
     */
    public BasePurposeAdapter(@NonNull ArrayList<T> dataList, @LayoutRes int itemLayoutId) {
        mData = dataList;
        mItemLayoutId = itemLayoutId;
    }

    /*--------------------------------实现父类方法--------------------------------*/

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        if (mCountSum == -1) {
            return mData.size();
        } else {
            return mCountSum;
        }
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @NonNull
    @Override
    public T getItem(int position) {
        if (mCountSum == -1) {
            return mData.get(position);
        } else {
            return mData.get(mCountSum % mData.size());
        }
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Get a View that displays the data at the specified position in the data set. You can either
     * create a View manually or inflate it from an XML layout file. When the View is inflated, the
     * parent View (GridView, ListView...) will apply default layout parameters unless you use
     * {@link LayoutInflater#inflate(int, ViewGroup, boolean)}
     * to specify a root view and to prevent attachment to the root.
     *
     * @param position    The position of the item within the adapter's data set of the item whose view
     *                    we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *                    is non-null and of an appropriate type before using. If it is not possible to convert
     *                    this view to display the correct data, this method can create a new view.
     *                    Heterogeneous lists can specify their number of view types, so that this View is
     *                    always of the right type (see {@link #getViewTypeCount()} and
     *                    {@link #getItemViewType(int)}).
     * @param parent      The parent that this view will eventually be attached to
     * @return a View corresponding to the data at the specified position.
     */
    @Nullable
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        ViewHolder viewHolder = getViewHolder(position, convertView, parent);
        convert(viewHolder, position, getItem(position));
        return viewHolder.getConvertView();
    }

    /*--------------------------------公开方法--------------------------------*/

    /**
     * 更新数据源并刷新适配器进行显示
     *
     * @param dataList 新的数据源
     */
    @SuppressWarnings("unused")
    public void refresh(ArrayList<T> dataList) {
        mData = dataList;
        notifyDataSetChanged();
    }

    /**
     * 删除数据源中指定位置的数据
     *
     * @param position 指定删除位置
     */
    @SuppressWarnings("unused")
    public void deleteListByPosition(int position) {
        if (position >= mData.size()) {
            return;
        }

        mData.remove(position);
        notifyDataSetChanged();
    }

    /**
     * 设置自定义数据总量
     *
     * @param countSum 自定义数据总量
     * @return AllPurposeAdapter本类对象
     */
    @SuppressWarnings("unused")
    public BasePurposeAdapter<T> setCount(int countSum) {
        mCountSum = countSum;
        return this;
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
    @NonNull
    private ViewHolder getViewHolder(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Context context;
        if (convertView != null) {
            context = convertView.getContext();
        } else {
            context = mContext;
        }

        return ViewHolder.get(context, convertView, parent, mItemLayoutId, position);
    }

    /*--------------------------------抽象方法--------------------------------*/

    /**
     * 每一个单独的选项的内容设置
     *
     * @param viewHolder ViewHolder
     * @param position   position
     * @param item       item
     */
    protected abstract void convert(@NonNull ViewHolder viewHolder, int position, T item);

    /**
     * ListView万能适配器专用的ViewHolder
     *
     * @author alm
     */

    public static class ViewHolder {

        /*--------------------------------成员变量--------------------------------*/

        /**
         * 当前位置
         */
        private final int mPosition;

        /**
         * 根据id将不同的View保存起来
         */
        private final SparseArray<View> viewSparseArray;

        /**
         * 当前item的View
         */
        private final View convertView;

        /**
         * 图片加载工具
         */
        private final ImageLoader imageLoader;

        /*--------------------------------构造函数--------------------------------*/

        /**
         * 构造函数
         *
         * @param context      上下文
         * @param parent       ViewGroup
         * @param itemLayoutId 布局文件资源id
         * @param position     当前位置
         */
        private ViewHolder(@NonNull Context context, @NonNull ViewGroup parent, @LayoutRes int itemLayoutId, int position) {
            mPosition = position;
            viewSparseArray = new SparseArray<>();
            convertView = LayoutInflater.from(context).inflate(itemLayoutId, parent, false);
            convertView.setTag(this);
            imageLoader = ImageLoader.getInstance(context);
        }

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
        public static ViewHolder get(@NonNull Context context, @Nullable View convertView, @NonNull ViewGroup parent, @LayoutRes int itemLayoutId, int position) {
            if (convertView == null) {
                return new ViewHolder(context, parent, itemLayoutId, position);
            }
            return (ViewHolder) convertView.getTag();
        }

        /*--------------------------------公开函数--------------------------------*/

        /**
         * 获取item的整个布局控件
         *
         * @return item的整个布局控件
         */
        public View getConvertView() {
            return convertView;
        }

        /**
         * 根据控件的Id获取控件(先从缓存中获取，如果没有则findViewById,然后保存到缓存)
         *
         * @param viewId 控件的Id
         * @return 对应的控件
         */
        @Nullable
        public View getView(@IdRes int viewId) {
            View view = viewSparseArray.get(viewId);
            if (view == null) {
                view = convertView.findViewById(viewId);
                viewSparseArray.put(viewId, view);
            }
            return view;
        }

        /**
         * 为TextView设置文本内容
         *
         * @param viewId 控件的Id
         * @param text   要设置的文本内容
         * @return ViewHolder本类
         */
        @NonNull
        public ViewHolder setText(@IdRes int viewId, @NonNull CharSequence text) {
            View view = getView(viewId);
            if (view instanceof TextView) {
                TextView textView = (TextView) view;
                textView.setText(text);
            }
            return this;
        }

        /**
         * 为CompoundButton设置选中状态
         *
         * @param viewId  控件的Id
         * @param checked 选中状态
         * @return ViewHolder本类
         */
        @NonNull
        public ViewHolder setChecked(@IdRes int viewId, boolean checked) {
            View view = getView(viewId);
            if (view instanceof CompoundButton) {
                CompoundButton compoundButton = (CompoundButton) view;
                compoundButton.setChecked(checked);
            }
            return this;
        }

        /**
         * 为TextView设置文本内容
         *
         * @param viewId  控件的Id
         * @param textRes 要设置的文本内容资源ID
         * @return ViewHolder本类
         */
        @NonNull
        public ViewHolder setText(@IdRes int viewId, @StringRes int textRes) {
            View view = getView(viewId);
            if (view instanceof TextView) {
                TextView textView = (TextView) view;
                textView.setText(textRes);
            }
            return this;
        }

        /**
         * 获取editText的文本
         *
         * @param viewId 控件的Id
         * @return editText的文本
         */
        @Nullable
        public CharSequence getEditText(@IdRes int viewId) {
            View view = getView(viewId);
            if (view instanceof EditText) {
                EditText editText = (EditText) view;
                return editText.getText();
            }
            return null;
        }

        /**
         * 为ImageView设置图片
         *
         * @param viewId        控件的Id
         * @param drawableResId 图片的资源Id
         * @return ViewHolder本类
         */
        @NonNull
        public ViewHolder setImageResource(@IdRes int viewId, @DrawableRes int drawableResId) {
            View view = getView(viewId);
            if (view instanceof ImageView) {
                ImageView imageView = (ImageView) view;
                imageView.setImageResource(drawableResId);
            }
            return this;
        }

        /**
         * 为ImageView设置图片
         *
         * @param viewId 控件的Id
         * @param bitmap 位图图片
         * @return ViewHolder本类
         */
        @NonNull
        public ViewHolder setImageBitmap(@IdRes int viewId, @NonNull Bitmap bitmap) {
            View view = getView(viewId);
            if (view instanceof ImageView) {
                ImageView imageView = (ImageView) view;
                imageView.setImageBitmap(bitmap);
            }
            return this;
        }

        /**
         * 为ImageView设置图片
         *
         * @param viewId   控件的Id
         * @param url      图片的网络地址
         * @param isCircle 是否将图片显示为圆形
         * @return ViewHolder本类
         */
        @NonNull
        public ViewHolder setImageByUrl(@IdRes int viewId, @NonNull String url, boolean isCircle) {
            View view = getView(viewId);
            if (view instanceof ImageView) {
                ImageView imageView = (ImageView) view;
                imageLoader.displayImage(url, imageView, isCircle);
            }
            return this;
        }

        /**
         * 给view设置背景色
         *
         * @param viewId 控件的Id
         * @param color  背景色
         * @return ViewHolder本类
         */
        @NonNull
        public ViewHolder setBackgroundColor(@IdRes int viewId, @ColorInt int color) {
            View view = getView(viewId);
            if (view != null) {
                view.setBackgroundColor(color);
            }
            return this;
        }

        /**
         * 获取当前位置
         *
         * @return 当前位置
         */
        public int getPosition() {
            return mPosition;
        }
    }

}
