package com.sscl.baselibrary.utils;

import android.content.Context;
import android.graphics.Bitmap;
import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.StringRes;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.sscl.baselibrary.image.ImageLoader;


/**
 * ListView万能适配器专用的ViewHolder
 *
 * @author alm
 */

@SuppressWarnings({"WeakerAccess", "unused"})
public class ViewHolder {

    /*--------------------------------成员变量--------------------------------*/

    /**
     * 当前位置
     */
    private int mPosition;

    /**
     * 根据id将不同的View保存起来
     */
    private SparseArray<View> viewSparseArray;

    /**
     * 当前item的View
     */
    private View convertView;

    /**
     * 图片加载工具
     */
    private ImageLoader imageLoader;

    /*--------------------------------构造函数--------------------------------*/

    /**
     * 构造函数
     *
     * @param context      上下文
     * @param parent       ViewGroup
     * @param itemLayoutId 布局文件资源id
     * @param position     当前位置
     */
    private ViewHolder(Context context, ViewGroup parent, @LayoutRes int itemLayoutId, int position) {
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
    public static ViewHolder get(Context context, View convertView, ViewGroup parent, @LayoutRes int itemLayoutId, int position) {
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
    public ViewHolder setText(@IdRes int viewId, CharSequence text) {
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
    public ViewHolder setImageBitmap(@IdRes int viewId, Bitmap bitmap) {
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
    public ViewHolder setImageByUrl(@IdRes int viewId, String url, boolean isCircle) {
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
    public ViewHolder setBackgroundColor(@IdRes int viewId, @ColorInt int color) {
        View view = getView(viewId);
        view.setBackgroundColor(color);
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
