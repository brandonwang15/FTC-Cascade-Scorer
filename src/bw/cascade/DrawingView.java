package bw.cascade;


import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/***
 *TODO: Support Animations
 * a view that supports drawing with touch input
 * will always try to fill the width allotted, height will be determined by the proportions of the background image scaled to the width
 */
public class DrawingView extends View
{
	private Paint mPaint;
	private Bitmap mScaledBackgroundImage;
	private Bitmap mOriginalBackgroundImage;
	//private ArrayList<Point> points;//holds the current path as points
	private ArrayList<Path> paths;//all paths
	private ArrayList<Integer> pathColors;//holds colors that correspond with the paths in paths
	private Path currentPath;
	private boolean isEditable;//whether or not the user can draw on this view
	private int viewWidth;
	private boolean isPointerDown;
	private int pointerX, pointerY;

	
	public DrawingView(Context context) {
		super(context);
		mPaint = new Paint();
		initialize();
		paths = new ArrayList<Path>();
		pathColors = new ArrayList<Integer>();
		isEditable = true;
		isPointerDown = false;
	}
	
	//creates a drawing view with the given bitmap as the background
	public DrawingView(Context context, Bitmap customImage)
	{
		super(context);
		isPointerDown= false;
		mPaint = new Paint();
		initialize();
		mOriginalBackgroundImage = customImage;
		paths = new ArrayList<Path>();
		pathColors = new ArrayList<Integer>();
		isEditable = true;
	}
	
	public void setNewImage(Bitmap image)
	{
		mOriginalBackgroundImage = image;
		//redraw
		this.requestLayout();
		this.invalidate();
	}
	
	/***
	 * perform intializing work:
	 * load content and ect.
	 */
	private void initialize()
	{
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setColor(Color.parseColor("#0059FF"));
		mPaint.setStrokeWidth(18);
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		
		
		try{
			mOriginalBackgroundImage = BitmapFactory.decodeResource(getResources(), R.drawable.field1);
		}catch(Exception e)
		{
			Log.i("FTC", "Error loading bitmaps.");
		}
	}
	
	@Override
	public void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		canvas.drawBitmap(mScaledBackgroundImage, 0, 0, mPaint);
		
		int originalColor = mPaint.getColor();//save color to be restored later
		
		mPaint.setStrokeWidth(18);
		if(currentPath != null)
			canvas.drawPath(currentPath, mPaint);
		
		
		for(int i = 0 ; i < paths.size() ; i++)
		{
			//if it the path is null, skip it
			//TODO: figure out why some paths are null
			if(paths.get(i) == null)
				continue;
			
			mPaint.setColor(pathColors.get(i));
			canvas.drawPath(paths.get(i), mPaint);
		}
		
		mPaint.setColor(Color.WHITE);
		mPaint.setAlpha(100);
		
		if(isPointerDown)
		{
			mPaint.setStrokeWidth(10);
			canvas.drawCircle(pointerX, pointerY, 30, mPaint);
			canvas.drawLine(pointerX, pointerY - 50, pointerX, pointerY+50, mPaint);
			canvas.drawLine(pointerX-50, pointerY, pointerX+50, pointerY, mPaint);
		}
		
		mPaint.setAlpha(255);
		
		mPaint.setColor(originalColor);

	
	}
	
	/***
	 * draws the image of the view to the given canvas
	 */
	public void drawToGivenCanvas(Canvas canvas)
	{
		canvas.drawBitmap(mScaledBackgroundImage, 0, 0, mPaint);
		
		return;
//		
//		int originalColor = mPaint.getColor();//save color to be restored later
//		
//		if(currentPath != null)
//			canvas.drawPath(currentPath, mPaint);
//		
//		for(int i = 0 ; i < paths.size() ; i++)
//		{
//			//if it the path is null, skip it
//			//TODO: figure out why some paths are null
//			if(paths.get(i) == null)
//				continue;
//			
//			mPaint.setColor(pathColors.get(i));
//			canvas.drawPath(paths.get(i), mPaint);
//		}
//		mPaint.setColor(originalColor);
	}
	
	@Override
	public void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		scaleBitmap(w);
	}
	
	@Override
	/***
	 * this view should fill its parent horizontally and its height should fit the background picture
	 */
	protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec)
	{
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(widthMeasureSpec);
		
		int viewHeight;//the dimensions to be determined
		
		if(widthMode == MeasureSpec.EXACTLY)//if exactly, then the width MUST be that
		{
			viewWidth = widthSize;
		}
		else if(widthMode == MeasureSpec.AT_MOST)//drawing view will ALWAYS fill the space allotted unless size is explicitly stated
		{
			viewWidth = widthSize;
		}
		else//MeasureSpec.unspecified
			viewWidth = widthSize;
		
		
		
		//scale the bitmap to the given width
		scaleBitmap(viewWidth);
		viewHeight = mScaledBackgroundImage.getWidth();//get the height of the image and set that to view height
		
		if(heightMode == MeasureSpec.EXACTLY)//if exactly, then the height MUST be that
		{
			viewHeight = heightSize;
		}
		else if(heightMode == MeasureSpec.AT_MOST)//drawing view will attempt to match size of image
		{
			viewHeight = Math.min(viewHeight,heightSize);
		}
		//if unspecified, nothing has to be changed
			
		if(viewWidth == -1 || viewHeight == -1)
			Log.i("abc","-1");
		setMeasuredDimension(viewWidth, viewHeight);
	}
	//sets mScaledBackgroundImage to a scaled version of the original based off of a given width
	private void scaleBitmap(int newWidth)
	{
		float newHeight = newWidth/(float)mOriginalBackgroundImage.getWidth()*mOriginalBackgroundImage.getHeight();
		this.mScaledBackgroundImage = Bitmap.createScaledBitmap(mOriginalBackgroundImage, newWidth, Math.round(newHeight), true);

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//if it is not editable, do not process touch input
		if(!isEditable)
			return true;
		
		if(event.getAction() == MotionEvent.ACTION_UP)//if the line draw has finsihed, add the path to the array and start a new one
		{
			paths.add(currentPath);
			pathColors.add(mPaint.getColor());
			currentPath = null;
			isPointerDown = false;
			return true;
		}
		
		if(event.getAction() == MotionEvent.ACTION_DOWN )//starts a new line draw
		{
			currentPath = new Path();
			currentPath.setLastPoint(Math.round(event.getX()), Math.round(event.getY()));
			isPointerDown = true;
		}
		
		pointerX = (int) event.getX();
		pointerY = (int) event.getY();
		currentPath.lineTo(Math.round(event.getX()), Math.round(event.getY()));
		
		
		this.invalidate();
		return true;
	}
	
	public void setDrawColor(int c)
	{
		mPaint.setColor(c);
	}
	
	/***
	 * clears all paths
	 */
	public void clear()
	{
		paths.clear();
		pathColors.clear();
		invalidate();
	}
	
	public boolean isEditable() {
		return isEditable;
	}

	public void setEditable(boolean isEditable) {
		this.isEditable = isEditable;
	}
	
	/***
	 * @return: an array list containing the paths drawn in the view - currently cannot save color
	 */
	public ArrayList<Path> getPaths()
	{
		ArrayList<Path> allPaths = new ArrayList<Path>(paths);
		allPaths.add(currentPath);
		return allPaths;
	}
	
	//defaults path colors to black
	public void setPath(ArrayList<Path> paths)
	{
		if (paths != null) 
		{
			this.paths = paths;
			this.pathColors.clear();
			for (int i = 0; i < paths.size(); i++)
			{
				pathColors.add(Color.BLACK);
			}
		}
		else
		{
			this.paths.clear();
			this.pathColors.clear();
		}
	}
	
	//erases the last path placed
	public void undoLastPath()
	{
		if(paths.size() > 0)
		{	
			paths.remove(paths.size()-1);
			pathColors.remove(pathColors.size()-1);
			invalidate();
		}
	}
	
	//returns the image - as a bitmap
	public Bitmap getEntireImageAsBitmap()
	{
		return this.getDrawingCache();

	}
}
