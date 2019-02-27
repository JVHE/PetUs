package com.example.jvhe.petus.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.jvhe.petus.R;

import java.util.ArrayList;

public class CafeImagePagerAdapter extends PagerAdapter {

    private static final String TAG = "CafeImagePagerAdapter";
    Context context;
    //    private SparseArray<Bitmap> sparseArray_image;
    private ArrayList<Bitmap> arrayList_image;
    private LayoutInflater inflater;
    public int size = -1;

    public CafeImagePagerAdapter(Context context, ArrayList<Bitmap> arrayList_image) {
        this.context = context;
//        this.sparseArray_image = new SparseArray<>();
        this.arrayList_image = arrayList_image;
//        for (int i = 0; i < arrayList_image.size(); i++) {
//            if (!arrayList_url.get(0).equals("")) {
//                String url = "https://maps.googleapis.com/maps/api/place/photo?maxheight=200&photoreference=" + arrayList_url.get(i) + "&key=AIzaSyAWheKGum5zVeWuuW9efB9glFYHdO5Yfy8";
//                final int finalI = i;
//                Glide.with(context).asBitmap().load(url).into(new SimpleTarget<Bitmap>() {
//                    @Override
//                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
//                        sparseArray_image.append(finalI, resource);
//                    }
//                });
//            }
//        }
    }

    TextView tv;

    public void chk(int size) {
        this.size = size;
        if (tv != null) {
            tv.setText("1 / " + size);
        }
        Log.e(TAG,"size: "+size);
    }

    @Override
    public int getCount() {
        return arrayList_image.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {

        return view == ((View) object);
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull final ViewGroup container, final int position) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View view = inflater.inflate(R.layout.viewpager_cafe, null);
        final TextView page = view.findViewById(R.id.tv_page);

        final ImageView imageView = view.findViewById(R.id.iv_cafe_image);

        // 첫 번째 이미지에 표시되는 페이지 뷰 가져오기
        if (position == 0) {
            tv = page;
        }

        if (arrayList_image.get(position) != null) {
            imageView.setImageBitmap(arrayList_image.get(position));
            if (size == -1) {
                page.setText("");
//                new AsyncTask() {
//                    @Override
//                    protected Object doInBackground(Object[] objects) {
//                        for (int i=0;i<20;i++) {
//                            if (size == -1) {
//                                try {
//                                    Thread.sleep(100);
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
//                            } else {
//                                new Handler().post(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        page.setText((position+1) + " / " + size);
//                                    }
//                                });
//                                break;
//                            }
//                        }
//                        return null;
//                    }
//                }.execute();
            } else {
                page.setText((position + 1) + " / " + size);
            }
            container.addView(view);
            return view;
        } else//(arrayList_url.get(0).equals("")) {
        {
//            imageView.setImageResource(R.drawable.dog_cafe_6);
            page.setText("");
//            page.setText((position+1) + " / " + size);
            container.addView(view);
            return view;
        }
    }

    //화면에 보이지 않은 View는파괴를 해서 메모리를 관리함.
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // TODO Auto-generated method stub

        //ViewPager에서 보이지 않는 View는 제거
        //세번째 파라미터가 View 객체 이지만 데이터 타입이 Object여서 형변환 실시
        container.removeView((View) object);

    }
}
