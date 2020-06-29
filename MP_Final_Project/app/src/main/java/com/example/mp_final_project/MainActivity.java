package com.example.mp_final_project;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.provider.ContactsContract;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    ImageButton paintButton, pictureButton, textButton;

    final static int LINE = 1, CIRCLE = 2, RECTANGLE = 3, BITMAP = 4, TEXT = 5;
    static int picX, picY;
    static String str01;
    static Bitmap img;
    static int curShape = LINE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new myGraphicView(this));

        //위(메뉴 있는 레이아웃) 추가
        LinearLayout linearLayout = (LinearLayout)View.inflate(this, R.layout.activity_main,null);
        addContentView(linearLayout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        //메뉴 생성
        paintButton = (ImageButton)findViewById(R.id.paintButton);
        registerForContextMenu(paintButton);

        pictureButton = (ImageButton)findViewById(R.id.pictureButton);
        registerForContextMenu(pictureButton);

        textButton = (ImageButton)findViewById(R.id.textButton);
        registerForContextMenu(textButton);
}

    public void onPopupButtonClick(View ImageButton) //버튼 클릭 시 메뉴 목록
    {
        PopupMenu popup = new PopupMenu(this, ImageButton);

        if( ImageButton == paintButton)
            popup.getMenuInflater().inflate(R.menu.menu01,popup.getMenu());
        else if( ImageButton == pictureButton)
            popup.getMenuInflater().inflate(R.menu.menu02,popup.getMenu());
        else if( ImageButton == textButton)
            popup.getMenuInflater().inflate(R.menu.menu03,popup.getMenu());

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
                    case R.id.drawRectangle:
                        curShape = RECTANGLE;
                        break;
                    case R.id.printPicture:
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(intent,1);
                        curShape = BITMAP;
                        break;
                    case R.id.printText:
                        final View dlg_view = (View)View.inflate(MainActivity.this,R.layout.dialog01,null);
                        AlertDialog.Builder mydlg = new AlertDialog.Builder(MainActivity.this);
                        mydlg.setTitle("텍스트 입력");
                        mydlg.setView(dlg_view);

                        mydlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                               EditText text_contents = (EditText)dlg_view.findViewById(R.id.text_contents);
                               str01 = text_contents.getText().toString();
                               curShape = TEXT;
                            }
                        });
                        mydlg.setNegativeButton("취소",null);
                        mydlg.show();
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
            paint.setColor(Color.BLUE);
            paint.setTextSize(100);
            paint.setStyle(Paint.Style.FILL);
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
                case RECTANGLE:
                    Rect rect1 = new Rect(startX,startY,stopX,stopY);
                    canvas.drawRect(rect1, paint);
                    break;
                case BITMAP:
                    int picA = (this.getWidth() - picX)/2;
                    int picB = (this.getWidth() - picY)/2;
                    canvas.drawBitmap(img,picA,picB,null);
                    break;
                case TEXT:
                    canvas.drawText(str01,(float)startX,(float)startY,paint);
                    break;
            }
        }
    }

    //비트맵 가져오기
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == 1)
        {
            if(resultCode == RESULT_OK)
            {
                try{
                    InputStream in = getContentResolver().openInputStream(data.getData());
                    img = BitmapFactory.decodeStream(in);
                    in.close();
                    picX = img.getWidth();
                    picY = img.getHeight();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

}

