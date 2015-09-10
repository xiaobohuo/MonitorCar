package com.example.monitorcar.ui;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.microedition.khronos.opengles.GL10;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapDrawFrameCallback;
import com.baidu.mapapi.map.BaiduMap.OnMapLoadedCallback;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Projection;
import com.baidu.mapapi.model.LatLng;
import com.example.monitorcar.R;
import com.example.monitorcar.TCP.CommService;
import com.example.monitorcar.TCP.DataTransfer;
import com.example.monitorcar.TCP.Event;
import com.example.monitorcar.TCP.IMessage;
import com.example.monitorcar.TCP.Pdu;
import com.example.monitorcar.bin.GPSInfo;
import com.example.monitorcar.listeners.MyOnTouchListener;
import com.example.monitorcar.utils.DateUtil;
import com.example.monitorcar.utils.DrawableUtil.Cusor;
import com.example.monitorcar.utils.TCPUtil;
import com.example.monitorcar.utils.ThreadPoolManager;
import com.example.monitorcar.widgets.SelectTimePopupWindow;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.fourmob.datetimepicker.date.DatePickerDialog.OnDateSetListener;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;

public class MapTraceActivity extends BaseActivity implements
        OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    public static final int NOWTRACE = 0;
    public static final int HISTORYTRACE = 1;

    private static final int MAP_LOCATING = 0x02;

    public static final String CARPAINUM = "carpainum";
    public static final String INSIDEID = "insideid";
    public static final String WHICHTRACE = "tracekind";

    public static final String DATEPICKER_TAG = "datepicker";
    public static final String TIMEPICKER_TAG = "timepicker";

    private LinearLayout ll_mapcontroller;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private SelectTimePopupWindow menuWindow;
    private Button btn_search, btn_next, btn_pause, btn_previous;
    private BitmapDescriptor bitmap;
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private Projection project;
    private MyOnTouchListener myOnTouchListener = new MyOnTouchListener();
    private Cusor cusor = Cusor.UNSET;

    private static ReadWriteLock lock = new ReentrantReadWriteLock();

    private List<GPSInfo> latLngPolygon = new ArrayList<GPSInfo>();
    private List<GPSInfo> pastGPSList = new ArrayList<GPSInfo>();
    private FloatBuffer vertexBuffer;
    private float[] vertexs;

    private long carInsideId = -1;
    private long inId = -1;
    private int traceKind = 0;
    private int count = 4;

    private boolean isMapLoaded = false;
    private boolean IsExitActivity = false;
    private boolean need2Stop = false;

    private long beginTime, endTime;
    private static double PI = 3.1415926535897932384626;
    private int INTERVAL = 30 * 60;
    private int sleepTime = 3000;
    private float lastAngle = 0;

    private int myYear_begin, myMonth_begin, myDay_begin, myHour_begin,
            myMinute_begin;
    private int myYear_end, myMonth_end, myDay_end, myHour_end, myMinute_end;

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MAP_LOCATING:
                    mBaiduMap.clear();
                    if (pastGPSList.size() > 0) {
                        LatLng cur_position = new LatLng(pastGPSList.get(
                                pastGPSList.size() - 1).getLatitude(), pastGPSList
                                .get(pastGPSList.size() - 1).getLongtitude());

                        if (pastGPSList.size() > 1) {
                            LatLng last_position = new LatLng(pastGPSList.get(
                                    pastGPSList.size() - 2).getLatitude(),
                                    pastGPSList.get(pastGPSList.size() - 2)
                                            .getLongtitude());
                            lastAngle = calAngle(last_position, cur_position);
                        }
                        OverlayOptions option = new MarkerOptions()
                                .position(cur_position).rotate(lastAngle)
                                .icon(bitmap);

                        mBaiduMap.addOverlay(option);
                        MapStatus mMapStatus = new MapStatus.Builder()
                                .target(cur_position).zoom(18).build();
                        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory
                                .newMapStatus(mMapStatus);
                        mBaiduMap.setMapStatus(mMapStatusUpdate);

                    }
                    break;

                default:
                    break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapactivity);

        init();

    }

    @Override
    protected void onPause() {
        need2Stop = true;
        super.onPause();
    }

    @Override
    protected void onResume() {
        need2Stop = false;
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        need2Stop = true;
        IsExitActivity = true;
        super.onDestroy();
    }

    private void init() {

//        startActivityForResult(new Intent(Intent.ACTION_VIEW, new Uri("content://contacts")), 0);

        Bundle bundle = getIntent().getExtras();
        carInsideId = bundle.getLong(CARPAINUM);
        inId = bundle.getLong(INSIDEID);
        traceKind = bundle.getInt(WHICHTRACE);

        final Calendar calendar = Calendar.getInstance();

        datePickerDialog = DatePickerDialog.newInstance(this,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), false);
        timePickerDialog = TimePickerDialog.newInstance(this,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE), false, false);

        bitmap = BitmapDescriptorFactory.fromResource(R.drawable.car);

        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();

        mBaiduMap.setOnMapDrawFrameCallback(callback);
        mBaiduMap.setOnMapLoadedCallback(new OnMapLoadedCallback() {

            @Override
            public void onMapLoaded() {
                if (!isMapLoaded) {
                    project = mBaiduMap.getProjection();
                    isMapLoaded = true;
                }
            }
        });

        switch (traceKind) {
            case NOWTRACE:
                ThreadPoolManager.getInstance().addTask(fetchNowTraceRunnable);
                ThreadPoolManager.getInstance().addTask(drawNowTraceRunnable);
                break;

            case HISTORYTRACE:
                btn_search = (Button) findViewById(R.id.map_selec_time_btn);
                btn_next = (Button) findViewById(R.id.trace_accelerate);
                btn_pause = (Button) findViewById(R.id.trace_pause);
                btn_previous = (Button) findViewById(R.id.trace_slowdown);
                ll_mapcontroller = (LinearLayout) findViewById(R.id.ll_mapcontroller);

                btn_search.setOnClickListener(contrlMenuOnClick);
                btn_next.setOnClickListener(contrlMenuOnClick);
                btn_next.setOnTouchListener(myOnTouchListener);
                btn_pause.setOnClickListener(contrlMenuOnClick);
                btn_pause.setOnTouchListener(myOnTouchListener);
                btn_previous.setOnClickListener(contrlMenuOnClick);
                btn_previous.setOnTouchListener(myOnTouchListener);

                ll_mapcontroller.setVisibility(View.VISIBLE);
                break;
        }
    }

    private OnClickListener itemsOnClick = new OnClickListener() {

        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.edtStartDay:
                    cusor = Cusor.STARTDAY;
                    datePickerDialog.setVibrate(false);
                    datePickerDialog.setYearRange(1985, 2028);
                    datePickerDialog.show(getSupportFragmentManager(),
                            DATEPICKER_TAG);
                    break;
                case R.id.edtStartHour:
                    cusor = Cusor.STARTHOUR;
                    timePickerDialog.setVibrate(false);
                    timePickerDialog.show(getSupportFragmentManager(),
                            TIMEPICKER_TAG);
                    break;
                case R.id.edtEndDay:
                    cusor = Cusor.ENDDAY;
                    datePickerDialog.setVibrate(false);
                    datePickerDialog.setYearRange(1985, 2028);
                    datePickerDialog.show(getSupportFragmentManager(),
                            DATEPICKER_TAG);
                    break;
                case R.id.edtEndHour:
                    cusor = Cusor.ENDHOUR;
                    timePickerDialog.setVibrate(false);
                    timePickerDialog.show(getSupportFragmentManager(),
                            TIMEPICKER_TAG);
                    break;
                case R.id.button_pop_search:
                    beginTime = DateUtil.DateStringToLong(myYear_begin,
                            myMonth_begin, myDay_begin, myHour_begin,
                            myMinute_begin);
                    endTime = DateUtil.DateStringToLong(myYear_end, myMonth_end,
                            myDay_end, myHour_end, myMinute_end);
                    int interval = (int) (endTime - beginTime);
                    if (beginTime > endTime) {
                        showShortToast("开始时间应小于结束时间");
                    } else {
                        menuWindow.dismiss();
                        need2Stop = false;
                        latLngPolygon.clear();
                        pastGPSList.clear();

                        ThreadPoolManager.getInstance().addTask(
                                fetchPeriodTraceRunnable);
                        ThreadPoolManager.getInstance().addTask(
                                drawPeriodTraceRunnable);
                    }
                    break;
                default:
                    break;
            }

        }

    };

    private OnClickListener contrlMenuOnClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.map_selec_time_btn:
                    menuWindow = new SelectTimePopupWindow(MapTraceActivity.this,
                            itemsOnClick);
                    menuWindow.showAtLocation(
                            MapTraceActivity.this.findViewById(R.id.map_slideback),
                            Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                    break;
                case R.id.trace_accelerate:
                    if (count-- > 0) {
                        sleepTime = (int) (0.8 * sleepTime);
                    } else {
                        showShortToast("已达顶速");
                    }
                    break;
                case R.id.trace_pause:
                    need2Stop = !need2Stop;
                    if (need2Stop == true) {
                        btn_pause.setBackgroundResource(R.drawable.action_pause);
                    } else {
                        btn_pause.setBackgroundResource(R.drawable.action_play);
                    }
                    break;
                case R.id.trace_slowdown:
                    sleepTime = (int) (1.25 * sleepTime);
                    count++;
                    break;
                default:
                    break;
            }
        }
    };

    private Runnable drawNowTraceRunnable = new Runnable() {

        @Override
        public void run() {
            while (!IsExitActivity) {
                if (need2Stop) {
                    break;
                }
                if (latLngPolygon.size() > 0) {
                    try {
                        lock.writeLock().lock();
                        GPSInfo tmp = latLngPolygon.remove(0);
                        pastGPSList.add(tmp);
                    } finally {
                        lock.writeLock().unlock();
                    }
                    handler.obtainMessage(MAP_LOCATING).sendToTarget();
                    sleepSomeTime(sleepTime);
                }

            }
        }
    };

    private Runnable drawPeriodTraceRunnable = new Runnable() {

        @Override
        public void run() {
            while (!IsExitActivity) {
                while (!IsExitActivity) {
                    if (need2Stop) {
                        break;
                    }
                    if (latLngPolygon.size() > 0) {
                        try {
                            lock.writeLock().lock();
                            GPSInfo tmp = latLngPolygon.remove(0);
                            pastGPSList.add(tmp);
                        } finally {
                            lock.writeLock().unlock();
                        }
                        handler.obtainMessage(MAP_LOCATING).sendToTarget();
                        sleepSomeTime(sleepTime);
                    }

                }

            }
        }
    };

    private Runnable fetchNowTraceRunnable = new Runnable() {

        @Override
        public void run() {
            while (!IsExitActivity) {
                if (need2Stop) {
                    break;
                }
                queryGPSInfoImmediately(carInsideId);
                sleepSomeTime(3000);
            }
        }
    };

    private Runnable fetchPeriodTraceRunnable = new Runnable() {

        @Override
        public void run() {
            while (!IsExitActivity) {
                if (need2Stop) {
                    break;
                }
                queryGPSInfoPeriodicity(0, beginTime, INTERVAL);
                if (beginTime + INTERVAL > endTime) {
                    break;
                }
                beginTime += INTERVAL;
                sleepSomeTime(30 * 1000);
            }
        }
    };

    private OnMapDrawFrameCallback callback = new OnMapDrawFrameCallback() {
        @Override
        public void onMapDrawFrame(GL10 gl, MapStatus drawingMapStatus) {
            if (mBaiduMap.getProjection() != null) {
                calPolylinePoint(drawingMapStatus);
                drawPolyline(gl, Color.BLACK, vertexBuffer, 15,
                        pastGPSList.size(), drawingMapStatus);
            }
        }
    };

    private void drawPolyline(GL10 gl, int color, FloatBuffer lineVertexBuffer,
                              float lineWidth, int pointSize, MapStatus drawingMapStatus) {

        gl.glEnable(GL10.GL_BLEND);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

        float colorA = Color.alpha(color) / 255f;
        float colorR = Color.red(color) / 255f;
        float colorG = Color.green(color) / 255f;
        float colorB = Color.blue(color) / 255f;

        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, lineVertexBuffer);
        gl.glColor4f(colorR, colorG, colorB, colorA);
        gl.glLineWidth(lineWidth);
        gl.glDrawArrays(GL10.GL_LINE_STRIP, 0, pointSize);

        gl.glDisable(GL10.GL_BLEND);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }

    public void calPolylinePoint(MapStatus mspStatus) {
        PointF[] polyPoints = new PointF[pastGPSList.size()];
        vertexs = new float[3 * pastGPSList.size()];
        int i = 0;
        try {
            lock.readLock().lock();
            for (GPSInfo xy : pastGPSList) {

                LatLng tmp = new LatLng(xy.getLatitude(), xy.getLongtitude());
                polyPoints[i] = mBaiduMap.getProjection().toOpenGLLocation(tmp,
                        mspStatus);
                vertexs[i * 3] = polyPoints[i].x;
                vertexs[i * 3 + 1] = polyPoints[i].y;
                vertexs[i * 3 + 2] = 0.0f;
                i++;
            }
        } finally {
            lock.readLock().unlock();
        }
        vertexBuffer = makeFloatBuffer(vertexs);

    }

    private FloatBuffer makeFloatBuffer(float[] fs) {
        ByteBuffer bb = ByteBuffer.allocateDirect(fs.length * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = bb.asFloatBuffer();
        fb.put(fs);
        fb.position(0);

        return fb;

    }

    public void queryGPSInfoImmediately(long carInsideId) {
        Pdu pdu = new Pdu();
        pdu.msgId = Event.MONITOR_CAR_NEWEST_POSITION_REQ;
        pdu.sender = Event.CLIENT_SERVER;
        pdu.receiver = TCPUtil.getInstance(this).getUserMgnSvrId();
        pdu.sendProcess = Event.CLIENT_USER_PROCESS;
        pdu.recvRrocess = Event.MONITOR_AGENT_PROCESS;
        pdu.prototype = Event.PROTO_TCP;
        pdu.routerType = Event.ROUTE_APPOINT;
        pdu.sericeType = Event.COMMON_SERVICE;

        pdu.addDataUnit(Event.SRC_INSIDE_USER_ID_KEY, inId);
        // carInsideId = 72057594037927965L;
        pdu.addDataUnit(Event.MONITOR_TARGET_INSIDEID_KEY, carInsideId);
        try {
            IMessage msg = CommService.getInstance().sendMessage(pdu,
                    Event.MONITOR_CAR_NEWEST_POSITION_RSP);
            int result = DataTransfer.ByteArrayToInt(msg
                    .getValue(Event.RSP_KEY));

            if (result == Event.RSP_OK) {
                byte[] gpsInfo = msg.getValue(Event.CAR_PATH_INFO_KEY);
                GPSInfo tmp = GPSInfo.splitGPSByteArray(gpsInfo);
                latLngPolygon.add(tmp);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void queryGPSInfoPeriodicity(long carInsideId, long beginTime,
                                        int interval) {
        Pdu pdu = new Pdu();
        pdu.msgId = Event.MONITOR_CAR_PATH_REQ;
        pdu.sender = Event.CLIENT_SERVER;
        pdu.receiver = TCPUtil.getInstance(this).getUserMgnSvrId();
        pdu.sendProcess = Event.CLIENT_USER_PROCESS;
        pdu.recvRrocess = Event.MONITOR_AGENT_PROCESS;

        pdu.prototype = Event.PROTO_TCP;
        pdu.routerType = Event.ROUTE_APPOINT;
        pdu.sericeType = Event.COMMON_SERVICE;

        pdu.addDataUnit(Event.SRC_INSIDE_USER_ID_KEY, inId);
        // carInsideId = 72057594037927965L;
        pdu.addDataUnit(Event.MONITOR_TARGET_INSIDEID_KEY, carInsideId);
        pdu.addDataUnit(Event.CAR_PATH_QUERY_BEGIN_TIME_KEY, beginTime);
        pdu.addDataUnit(Event.CAR_PATH_QUERY_INTERVAL_KEY, interval);

        try {
            IMessage msg = CommService.getInstance().sendMessage(pdu,
                    Event.MONITOR_CAR_PATH_RSP);
            int result = DataTransfer.ByteArrayToInt(msg
                    .getValue(Event.RSP_KEY));
            if (result == Event.RSP_NO_DATA) {
                System.out.println("!nodata container!");
            }
            if (result == Event.RSP_OK) {
                byte[] op = msg.getValue(Event.CAR_PATH_INFO_KEY);
                List<GPSInfo> pastGPSList = GPSInfo.byteArray2GPSInfoList(op);
                latLngPolygon.addAll(pastGPSList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public float calAngle(LatLng nowLatLng, LatLng nextLatLng) {

        Point nowPoint = project.toScreenLocation(nowLatLng);
        Point nextPoint = project.toScreenLocation(nextLatLng);
        if ((nextPoint.y - nowPoint.y) == 0 && (nextPoint.x - nowPoint.x) == 0) {
            return lastAngle;
        } else if ((nextPoint.y - nowPoint.y) == 0
                && (nextPoint.x - nowPoint.x) > 0) {
            lastAngle = 0;
            return lastAngle;
        } else if ((nextPoint.y - nowPoint.y) == 0
                && (nextPoint.x - nowPoint.x) < 0) {
            lastAngle = 180;
            return lastAngle;
        } else if ((nextPoint.x - nowPoint.x) == 0
                && (nextPoint.y - nowPoint.y) < 0) {
            lastAngle = 90;
            return lastAngle;
        } else if ((nextPoint.x - nowPoint.x) == 0
                && (nextPoint.y - nowPoint.y) > 0) {
            lastAngle = -90;
            return lastAngle;
        } else {
            lastAngle = 180 - (float) (Math.atan((nextPoint.y - nowPoint.y)
                    / (nextPoint.x - nowPoint.x))
                    / PI * 180);
            return lastAngle;
        }

    }

    @Override
    public void onBackPressed() {
        // notExiting = false;
        super.onBackPressed();
    }

    public void back_call(View view) {
        onBackPressed();
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        if (cusor == Cusor.STARTHOUR) {
            myHour_begin = hourOfDay;
            myMinute_begin = minute;
            menuWindow.startHour.setText(hourOfDay + "时" + minute + "分");
        } else if (cusor == Cusor.ENDHOUR) {
            myHour_end = hourOfDay;
            myMinute_end = minute;
            menuWindow.endHour.setText(hourOfDay + "时" + minute + "分");
        }
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year,
                          int month, int day) {
        if (cusor == Cusor.STARTDAY) {
            myYear_begin = year;
            myMonth_begin = month + 1;
            myDay_begin = day;
            menuWindow.startDay.setText(year + "年" + (month + 1) + "月" + day
                    + "日");
        } else if (cusor == Cusor.ENDDAY) {
            myYear_end = year;
            myMonth_end = month + 1;
            myDay_end = day;
            menuWindow.endDay.setText(year + "年" + (month + 1) + "月" + day
                    + "日");
        }
    }

}
