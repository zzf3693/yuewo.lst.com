package com.lst.lstjx.view;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.lst.lstjx.bean.AdInfo;
import com.lst.lstjx.bean.ProDetails;
import com.lst.lstjx.utils.AsyncImageLoader;
import com.lst.yuewo.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
public class ProBannerView extends FrameLayout
{
	private DisplayImageOptions options;
	// 轮播图图片数量
	private final static int IMAGE_COUNT = 5;
	// 自动轮播的时间间隔
	private final static int TIME_INTERVAL = 5;
	// 自动轮播启用开关
	private final static boolean isAutoPlay = true;
	// 自定义轮播图的资源
	private List<AdInfo> adList;
	private ProDetails mProDetails;
	// 放轮播图片的ImageView 的list
	private List<ImageView> imageViewsList;
	// 放圆点的View的list
	private List<View> dotViewsList;
	private ViewPager viewPager;
	// 当前轮播页
	private int currentItem = 0;
	// 定时任务
	private ScheduledExecutorService scheduledExecutorService;
	private Context context;
	private TextView mAdDescTv;
	private AsyncImageLoader asyncImageLoader;
	// Handler
	private Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			viewPager.setCurrentItem(currentItem, true);
		}
	};
	public ProBannerView(Context context)
	{
		this(context, null);
		// TODO Auto-generated constructor stub
	}
	public ProBannerView(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
		// TODO Auto-generated constructor stub
	}
	public ProBannerView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		this.context = context;
	}

	private Handler mHandler;
	private int clickFlag = 0;
	public int getClickFlag()
	{
		return clickFlag;
	}
	public void setClickFlag(int clickFlag)
	{
		this.clickFlag = clickFlag;
	}
	public void init(Handler mHandler, ProDetails mProDetails)
	{
		this.mHandler = mHandler;
		this.mProDetails = mProDetails;
		initopt();
		initData();
//		if (viewPager!=null&&mProDetails.getImgs().size()!=0) {
//			viewPager.setCurrentItem(0);
//		}
		
//		if (isAutoPlay)
//		{
//			startPlay();
//		}
	}
	/**
	 * 开始轮播图切换
	 */
	private void startPlay()
	{
		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		scheduledExecutorService.scheduleAtFixedRate(new SlideShowTask(), 1, 4, TimeUnit.SECONDS);
	}
	/**
	 * 停止轮播图切换
	 */
	private void stopPlay()
	{
		scheduledExecutorService.shutdown();
	}
	/**
	 * 初始化相关Data
	 */
	private void initData()
	{
		asyncImageLoader = new AsyncImageLoader(context);
		imageViewsList = new ArrayList<ImageView>();
		dotViewsList = new ArrayList<View>();
		// 一步任务获取图片
		// new GetListTask().execute("");
		initUI(context);
	}
	/**
	 * 初始化Views等UI
	 */
	private void initUI(Context context)
	{
		if (mProDetails.getImgs().size() == 0|| mProDetails == null) ;
		View rootView = LayoutInflater.from(context).inflate(R.layout.layout_pro_top_image, this, true);	
		LinearLayout dotLayout = (LinearLayout) rootView.findViewById(R.id.dotLayout);
		dotLayout.removeAllViews();
		// 热点个数与图片特殊相等
		for (int i = 0; i < mProDetails.getImgs().size();i++)
		{
			LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			params1.gravity = Gravity.CENTER_HORIZONTAL;
			ImageView view = new ImageView(context);
			view.setLayoutParams(params1);
			view.setTag(mProDetails.getImgs().get(i).toString());
			if (i == 0) // 给一个默认图
			view.setBackgroundResource(R.drawable.aha);
			view.setScaleType(ScaleType.FIT_XY);
			imageViewsList.add(view);
			ImageView dotView = new ImageView(context);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			params.leftMargin = 4;
			params.rightMargin = 4;
		
			dotLayout.addView(dotView, params);
			dotViewsList.add(dotView);
		}
		
		viewPager = (ViewPager) findViewById(R.id.viewPager);
		viewPager.setFocusable(true);
		viewPager.setAdapter(new MyPagerAdapter());
		viewPager.setOnPageChangeListener(new MyPageChangeListener());
		if ( mProDetails.getImgs().size()>1) {
		for (int i = 0; i < dotViewsList.size(); i++)
		{
			if (i ==0) {
				
			
				((View) dotViewsList.get(0)).setBackgroundResource(R.drawable.icon_circle_focus_on);
			}
			else
			{
				((View) dotViewsList.get(i)).setBackgroundResource(R.drawable.icon_circle_focus_off);
			}
		}
		}
		//ViewPagerScroller用于viewPage切换的过度
		ViewPagerScroller mViewPagerScroller = new ViewPagerScroller(context);
		mViewPagerScroller.initViewPagerScroll(viewPager);
	}
	private void initopt() {
		 options = new DisplayImageOptions.Builder()  
	        .showImageOnLoading(R.drawable.aha)            //加载图片时的图片  
	        .showImageForEmptyUri(R.drawable.aha)         //没有图片资源时的默认图片  
	        .showImageOnFail(R.drawable.aha)              //加载失败时的图片  
	        .cacheInMemory(true)                               //启用内存缓存  
	        .cacheOnDisc(true)                                 //启用外存缓存  
	        .considerExifParams(true)                          //启用EXIF和JPEG图像格式  
	            .bitmapConfig(Bitmap.Config.RGB_565) 
	        .displayer(new RoundedBitmapDisplayer(20))         //设置显示风格这里是圆角矩形  
	        .build(); 
		}
	/**
	 * 填充ViewPager的页面适配器
	 * 
	 */
	private class MyPagerAdapter extends PagerAdapter
	{
		@Override
		public void destroyItem(View container, int position, Object object)
		{
			// TODO Auto-generated method stub
			// ((ViewPag.er)container).removeView((View)object);
			((ViewPager) container).removeView(imageViewsList.get(position));
		}
		@Override
		public Object instantiateItem(View container, int position)
		{
			
			final int p = position;
			ImageView imageView = imageViewsList.get(position);
			String picUrl = imageView.getTag().toString();
			final ImageView childImg = imageViewsList.get(position);
			childImg.setTag(picUrl);
			Bitmap cachedImage = asyncImageLoader.loadDrawable(picUrl, new ImageCallback()
			{
				public void imageLoaded(Bitmap imageDrawable, String imageUrl)
				{
					if (childImg != null && null != imageDrawable)
					{
						childImg.setImageBitmap(imageDrawable);
					}
				}
			});
			if (cachedImage == null)
			{
				childImg.setImageResource(R.drawable.aha);
			}
			else
			{
				childImg.setImageBitmap(cachedImage);
			}

			((ViewPager) container).addView(imageViewsList.get(position));

			imageView.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					Message msg = new Message();
					msg.what = clickFlag;
					msg.obj = p;
					mHandler.sendMessage(msg);
				}
			});

			return imageViewsList.get(position);
		}

		@Override
		public int getCount()
		{
			// TODO Auto-generated method stub
			return imageViewsList.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1)
		{
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public Parcelable saveState()
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void startUpdate(View arg0)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void finishUpdate(View arg0)
		{
			// TODO Auto-generated method stub

		}

	}

	/**
	 * ViewPager的监听器 当ViewPager中页面的状态发生改变时调用
	 * 
	 */
	private class MyPageChangeListener implements OnPageChangeListener
	{

		boolean isAutoPlay = false;

		@Override
		public void onPageScrollStateChanged(int arg0)
		{
			// TODO Auto-generated method stub
			switch (arg0)
			{
				case 1:// 手势滑动，空闲中
					isAutoPlay = false;
					break;
				case 2:// 界面切换中
					isAutoPlay = true;
					break;
				case 0:// 滑动结束，即切换完毕或者加载完毕
						// 当前为最后一张，此时从右向左滑，则切换到第一张
					if (viewPager.getCurrentItem() == viewPager.getAdapter().getCount() - 1 && !isAutoPlay)
					{
						viewPager.setCurrentItem(0);
					}
					// 当前为第一张，此时从左向右滑，则切换到最后一张
					else if (viewPager.getCurrentItem() == 0 && !isAutoPlay)
					{
						viewPager.setCurrentItem(viewPager.getAdapter().getCount() - 1);
					}
					break;
			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageSelected(int pos)
		{
			
			currentItem = pos;
			for (int i = 0; i < dotViewsList.size(); i++)
			{
				if (i == pos)
				{
					((View) dotViewsList.get(pos)).setBackgroundResource(R.drawable.icon_circle_focus_on);
				}
				else
				{
					((View) dotViewsList.get(i)).setBackgroundResource(R.drawable.icon_circle_focus_off);
				}
			}
		}

	}

	/**
	 * 执行轮播图切换任务
	 * 
	 */
	private class SlideShowTask implements Runnable
	{

		@Override
		public void run()
		{
			// TODO Auto-generated method stub
			synchronized (viewPager)
			{
				if (null != imageViewsList && imageViewsList.size() > 1)
				{
					currentItem = (currentItem + 1) % imageViewsList.size();
					handler.obtainMessage().sendToTarget();
				}
			}
		}

	}

	/**
	 * 销毁ImageView资源，回收内存
	 * 
	 */
	private void destoryBitmaps()
	{
		for (int i = 0; i < IMAGE_COUNT; i++)
		{
			ImageView imageView = imageViewsList.get(i);
			Drawable drawable = imageView.getDrawable();
			if (drawable != null)
			{
				// 解除drawable对view的引用
				drawable.setCallback(null);
			}
		}
	}

}
