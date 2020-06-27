package com.example.mp_final_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

import java.util.logging.Level;

public class MainActivity extends AppCompatActivity {
    ImageButton paintButton;
    final static int LINE = 1, CIRCLE = 2;
    static int curShape = LINE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new myGraphicView(this));
        LinearLayout linearLayout = (LinearLayout)View.inflate(this, R.layout.activity_main,null);
        addContentView(linearLayout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        paintButton = (ImageButton)findViewById(R.id.paintButton);
        registerForContextMenu(paintButton);
}
    public void onPoPupButtonClick(View ImageButton)
    {
        PopupMenu popup = new PopupMenu(this, ImageButton);
        popup.getMenuInflater().inflate(R.menu.menu01,popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.drawLine:
                        curShape = LINE;
                        break;
                    case R.id.drawCircle:
                        curShape = CIRCLE;
                        break;
                }
                return MainActivity.super.onOptionsItemSelected(menuItem);
            }
        });
        popup.show();
    }

    public static class myGraphicView extends View
    {
        int startX = -1, startY = -1, stopX = -1, stopY = -1;
        public myGraphicView(Context context){super(context);}

        @Override
        public boolean onTouchEvent (MotionEvent event){
            super.onTouchEvent(event);
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    startX = (int)event.getX();
                    startY = (int)event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                case MotionEvent.ACTION_UP:
                    stopX = (int)event.getX();
                    stopY = (int)event.getY();
                    this.invalidate();
                    break;
                case MotionEvent.ACTION_CANCEL:
                    break;
                default:
                    break;
            }
            return true;
        }
        protected  void onDraw(Canvas canvas){
            super.onDraw(canvas);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(Color.RED);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(5);
            switch (curShape)
            {
                case LINE:
                    canvas.drawLine(startX,startY,stopX,stopY,paint);
                    break;
                case CIRCLE:
                    int radius = (int) Math.sqrt(Math.pow(stopX-startX,2)+Math.pow((stopY-startY),2));
                    canvas.drawCircle(startX,startY,radius ,paint);
                    break;
            }
        }

    }
}

