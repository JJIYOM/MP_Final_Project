package com.example.mp_final_project;

        import androidx.appcompat.app.AlertDialog;
        import androidx.appcompat.app.AppCompatActivity;
        import androidx.core.app.ActivityCompat;

        import android.Manifest;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.graphics.Canvas;
        import android.graphics.Color;
        import android.graphics.Paint;
        import android.graphics.Path;
        import android.graphics.PorterDuff;
        import android.graphics.Rect;
        import android.media.Image;
        import android.os.Bundle;
        import android.os.Environment;
        import android.util.Log;
        import android.view.MenuItem;
        import android.view.MotionEvent;
        import android.view.View;
        import android.widget.EditText;
        import android.widget.ImageButton;
        import android.widget.ImageView;
        import android.widget.LinearLayout;
        import android.widget.PopupMenu;
        import android.widget.SeekBar;
        import android.widget.TextView;
        import android.widget.Toast;

        import java.io.File;
        import java.io.FileNotFoundException;
        import java.io.FileOutputStream;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.OutputStream;
        import java.util.ArrayList;
        import java.util.List;

public class MainActivity extends AppCompatActivity {
    ImageButton paintButton, pictureButton, textButton, trashButton, colorButton, saveButton;
    myGraphicView graphicView;

    SeekBar redBar, greenBar, blueBar;
    TextView backgroundColor;
    int choiceColor;
    final static int LINE = 1, CIRCLE = 2, RECTANGLE = 3, BITMAP = 4, TEXT = 5, LOAD = 6, ROTATE = 7, SCALEUP = 8, SCALEDOWN = 9, TRANSLATE= 10, SKEW = 11;
    static int picX, picY;
    static Bitmap img;
    static String str02;
    static int curShape = LINE;
    static int curColor = Color.DKGRAY;
    static int twidth, theight;
    static List<MyShape> myshape = new ArrayList<MyShape>();
    static MyShape myshapes;

    private static class MyShape {
        int shapeType;
        int startX, startY, stopX, stopY;
        Path path;
        String str01;
        int color;

        public MyShape() {
            shapeType = LINE;
            color = Color.BLUE;
        }

        public MyShape(int cur, int co) {
            shapeType = cur;
            color = co;
        }

        public void display(Canvas canvas) {
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStrokeWidth(5);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(color);
            paint.setTextSize(100);

            switch (shapeType) {
                case LINE:
                    canvas.drawLine(startX, startY, stopX, stopY, paint);
                    break;
                case CIRCLE:
                    int radius = (int) Math.sqrt(Math.pow(stopX - startX, 2) + Math.pow((stopY - startY), 2));
                    canvas.drawCircle(startX, startY, radius, paint);
                    break;
                case RECTANGLE:
                    Rect rect1 = new Rect(startX, startY, stopX, stopY);
                    canvas.drawRect(rect1, paint);
                    break;
                case BITMAP:
                    int picA = (twidth - picX) / 2;
                    int picB = (twidth - picY) / 2;
                    canvas.drawBitmap(img, picA, picB, null);
                    break;
                case TEXT:
                    myshapes.str01 = str02;
                    canvas.drawText(str01, (float) startX, (float) startY, paint);
                    break;
                case LOAD:
                    canvas.drawBitmap(img,0,0,null);
                    break;
                case ROTATE:
                    canvas.rotate(45,twidth/2, theight/2);
                    canvas.drawBitmap(img,(twidth-picX)/2, (theight-picY)/2, null);
                    break;
                case SCALEUP:
                    canvas.scale(2,2,twidth/2,theight/2);
                    canvas.drawBitmap(img,(twidth-picX)/2, (theight-picY)/2, null);
                    break;
                case SCALEDOWN:
                    canvas.scale(0.5f,0.5f,twidth/2,theight/2);
                    canvas.drawBitmap(img,(twidth-picX)/2, (theight-picY)/2, null);
                    break;
                case TRANSLATE:
                    canvas.translate(-150,200);
                    canvas.drawBitmap(img,(twidth-picX)/2, (theight-picY)/2, null);
                    break;
                case SKEW:
                    canvas.skew(0.3f,0.3f);
                    canvas.drawBitmap(img,(twidth-picX)/2, (theight-picY)/2, null);
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        setContentView(new myGraphicView(this));

        LinearLayout pictureLayout = (LinearLayout) findViewById(R.id.pictureLayout);
        graphicView = (myGraphicView) new myGraphicView(this);

        pictureLayout.addView(graphicView);

        //메뉴 생성
        paintButton = (ImageButton) findViewById(R.id.paintButton);
        registerForContextMenu(paintButton);

        pictureButton = (ImageButton) findViewById(R.id.pictureButton);
        registerForContextMenu(pictureButton);

        textButton = (ImageButton) findViewById(R.id.textButton);
        registerForContextMenu(textButton);

        trashButton = (ImageButton) findViewById(R.id.trashButton);

        saveButton = (ImageButton) findViewById(R.id.saveButton);
        registerForContextMenu(saveButton);

        colorButton = (ImageButton) findViewById(R.id.colorButton);
    }

    public void onPopupButtonClick(View ImageButton) //버튼 클릭 시 메뉴 목록
    {
        PopupMenu popup = new PopupMenu(this, ImageButton);

        if (ImageButton == paintButton)
            popup.getMenuInflater().inflate(R.menu.menu01, popup.getMenu());
        else if (ImageButton == pictureButton)
            popup.getMenuInflater().inflate(R.menu.menu02, popup.getMenu());
        else if (ImageButton == textButton)
            popup.getMenuInflater().inflate(R.menu.menu03, popup.getMenu());
        else if (ImageButton == trashButton){
            myshape.clear();
            graphicView.invalidate();}
        else if (ImageButton == saveButton)
            popup.getMenuInflater().inflate(R.menu.menu05, popup.getMenu());
        else if (ImageButton == colorButton) {
            final View color_view = (View) View.inflate(MainActivity.this, R.layout.dialog02, null);
            AlertDialog.Builder cldlg = new AlertDialog.Builder(MainActivity.this);
            cldlg.setView(color_view);

            redBar = (SeekBar) color_view.findViewById(R.id.redBar);
            greenBar = (SeekBar) color_view.findViewById(R.id.greenBar);
            blueBar = (SeekBar) color_view.findViewById(R.id.blueBar);
            backgroundColor = (TextView) color_view.findViewById(R.id.backgroundColor);

            redBar.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
            redBar.getThumb().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
            greenBar.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
            greenBar.getThumb().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
            blueBar.getProgressDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);
            blueBar.getThumb().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);

            ChangeColor();

            redBar.setOnSeekBarChangeListener(MySeek);
            greenBar.setOnSeekBarChangeListener(MySeek);
            blueBar.setOnSeekBarChangeListener(MySeek);

            cldlg.setPositiveButton("선택", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    curColor = choiceColor;
                }
            });
            cldlg.show();
        }

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
                        startActivityForResult(intent, 1);
                        curShape = BITMAP;
                        break;
                    case R.id.printText:
                        final View dlg_view = (View) View.inflate(MainActivity.this, R.layout.dialog01, null);
                        AlertDialog.Builder mydlg = new AlertDialog.Builder(MainActivity.this);
                        mydlg.setTitle("텍스트 입력");
                        mydlg.setView(dlg_view);

                        mydlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                EditText text_contents = (EditText) dlg_view.findViewById(R.id.text_contents);
                                str02 = text_contents.getText().toString();
                                curShape = TEXT;
                            }
                        });
                        mydlg.setNegativeButton("취소", null);
                        mydlg.show();
                        break;
                    case R.id.readFile:
                        loadPicture();
                        break;
                    case R.id.saveFile:
                        savePicture();
                        break;
                    case R.id.changePicture:
                        if(img == null)
                        {
                            error();
                        }
                        break;
                    case R.id.rotate:
                        if(img == null) {
                            error();
                            break;      }
                        curShape = ROTATE;
                        break;
                    case R.id.scaleUp:
                        if(img == null) {
                            error();
                            break;      }
                        curShape = SCALEUP;
                        break;
                    case R.id.scaleDown:
                        if(img == null) {
                            error();
                            break;      }
                        curShape = SCALEDOWN;
                        break;
                    case R.id.translate:
                        if(img == null) {
                            error();
                            break;      }
                        curShape = TRANSLATE;
                        break;
                    case R.id.skew:
                        if(img == null) {
                            error();
                            break;      }
                        curShape = SKEW;
                        break;
                }
                return MainActivity.super.onOptionsItemSelected(menuItem);
            }
        });
        popup.show();
    }

    public static class myGraphicView extends View {
        int startX = -1, startY = -1, stopX = -1, stopY = -1;

        public myGraphicView(Context context) {
            super(context);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            super.onTouchEvent(event);
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    myshapes = new MyShape(curShape, curColor);
                    startX = (int) event.getX();
                    startY = (int) event.getY();
                    myshapes.startX = startX;
                    myshapes.startY = startY;
                    break;
                case MotionEvent.ACTION_MOVE:
                case MotionEvent.ACTION_UP:
                    stopX = (int) event.getX();
                    stopY = (int) event.getY();
                    myshapes.stopX = stopX;
                    myshapes.stopY = stopY;
                    myshape.add(myshapes);
                    this.invalidate();
                    break;
                case MotionEvent.ACTION_CANCEL:
                    break;
                default:
                    break;
            }
            return true;
        }

        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            twidth = this.getWidth();
            theight = this.getHeight();
            for (MyShape myshapes : myshape) {
                myshapes.display(canvas);
            }
        }
    }

    //비트맵 가져오기
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                try {
                    InputStream in = getContentResolver().openInputStream(data.getData());
                    img = BitmapFactory.decodeStream(in);
                    in.close();
                    picX = img.getWidth();
                    picY = img.getHeight();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    SeekBar.OnSeekBarChangeListener MySeek = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            ChangeColor();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    };

    public void ChangeColor() {
        int red = redBar.getProgress();
        int green = greenBar.getProgress();
        int blue = blueBar.getProgress();

        choiceColor = (Color.argb(255, red, green, blue));
        backgroundColor.setBackgroundColor(Color.argb(255, red, green, blue));
    }

    private void loadPicture() {
        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},MODE_PRIVATE);

        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        Bitmap bm = BitmapFactory.decodeFile(dir+"/my.png");

        img = bm;
        curShape = LOAD;
    }


    private void savePicture() {
        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},MODE_PRIVATE);
        graphicView.setDrawingCacheEnabled(true);
        Bitmap screenshot = Bitmap.createBitmap(graphicView.getDrawingCache());
        graphicView.setDrawingCacheEnabled(false);

        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            dir.mkdirs();
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(new File(dir,"my.png"));
            screenshot.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        }
        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, "실패",Toast.LENGTH_SHORT).show();
        }
    }
    private void error()
    {
        Toast.makeText(this, "사진을 먼저 골라주세요!",Toast.LENGTH_SHORT).show();
    }
}