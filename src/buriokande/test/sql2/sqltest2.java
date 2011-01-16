package buriokande.test.sql2;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.app.Dialog;
import java.util.Calendar;
import android.app.DatePickerDialog;
import android.widget.DatePicker;
import buriokande.test.sql2.R;
import android.util.Log;
import android.database.Cursor; //db操作用　跡で分離
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.content.Context;

public class sqltest2 extends Activity {
	final int DLG_ID_DATE = 0;
	final int DLG_ID_BLOOD = 1;
	final int DLG_ID_RESULT = 2;
	
	private int mYear;
	private int mMonth;
	private int mDay;
	private String Es;//kibdb
	
	private int mBlood = 0;
    private DatabaseHelper helper;//kibdb
	private DatePickerDialog.OnDateSetListener mDateSetListener = 
	new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view,int year ,int monthOfYear,int dayOfMonth){}
	
	};
	
    /** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //レイアウトの設定
        setContentView(R.layout.simple_button_view);
        Log.w("sqltest2","hoge");
        //日付の初期化
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR) -20;
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        
        //開始ボタンの処理を設定
        Button btn1 = (Button)findViewById(R.id.simple_button);
        btn1.setText("Start");
        
        btn1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDialog(DLG_ID_DATE);
				
			}
		}


        );
        //kibdb

        helper = new DatabaseHelper(this);
		SQLiteDatabase db = helper.getReadableDatabase();
		//kibdb
    	String sql = "select prefecture from capitals;";
    	Cursor c2 = db.rawQuery(sql, null);
    	c2.moveToFirst();
    	Es= c2.getString(0);

    	c2.close();
    }
    //ダイアログ作成
    protected Dialog onCreateDialog(int id){
    	if(id == DLG_ID_DATE){
    	//日付ダイアログ
    	return new DatePickerDialog(
    		this ,
    		mDateSetListener,
    		mYear,mMonth,mDay){
    		@Override
    		public void onClick(DialogInterface dialog,int which){
    			if(which == DialogInterface.BUTTON1){
    				//血液型のダイアログを開く
    				showDialog(DLG_ID_BLOOD);
    				Log.w("sqltest2","ketueki");
    			}
    		}
    		@Override
    		public void onDateChanged(DatePicker view,int year,int month,int day){
    			//日付の設定
    			mYear = year;
    			mMonth = month;
    			mDay = day;
    		}
    	};
    	}else 
    	if (id == DLG_ID_BLOOD ){
    		//血液型ダイアログ
    		return new AlertDialog.Builder(this).setTitle(R.string.dlg_ttl_uranai_blood).
    		setSingleChoiceItems(
    					R.array.dlg_uranai_blood,
    					mBlood,
    					new DialogInterface.OnClickListener() {
							
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								//選択項目をクリックした場合
	    						mBlood = which;
								
							}
						}
    		)
    		.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int whichButton) {
						// TODO Auto-generated method stub
						//結果ダイアログを開く
						showDialog(DLG_ID_RESULT);
					}
				}
    		)
    		.create();
    	}else
    		if (id == DLG_ID_RESULT ){
        		//結果ダイアログ
        		return new AlertDialog.Builder(this)
        		.setTitle(R.string.dlg_ttl_uranai_result)
        		.setMessage("")
        		.setNegativeButton(android.R.string.ok,
    				new DialogInterface.OnClickListener(){
    					public void onClick(DialogInterface dialog, int which) {
    						// TODO Auto-generated method stub
    						//処理なし
    					}
    				}
        		)
        		.create();
    		}
    	return null;
   	}
    //@Override
    @Override
	protected void onPrepareDialog(int id,Dialog dialog){
    	if (id == DLG_ID_RESULT){
    		((AlertDialog)dialog).setMessage(getUranaiRes());
    	}
    }
    //占い結果の取得
    private String getUranaiRes(){
    	//変数の初期化
    	String resStr;
    	CharSequence[] resArray;
    	int resType;
    	//占い結果配列の初期化
    	resArray = this.getResources().getTextArray(R.array.dlg_uranai_result);
    	//占い結果の計算
    	resType = Math.max(mMonth + mDay + mYear + mBlood , 0) % resArray.length ;
    	//結果文字列の作成
    	resStr = String.format(this.getResources().getString(R.string.dlg_msg_uranai_result), 
    			mMonth,
    			mDay,
    			mYear,
    			Es,
    			this.getResources().getTextArray(R.array.dlg_uranai_blood)[mBlood],
    			resArray[resType]
    	);
    	//返り値を返して終了
    	return resStr;
    	
    	
    }
	class DatabaseHelper extends SQLiteOpenHelper {
		
		public DatabaseHelper(Context context) {
			super(context, "KIBDB", null, 1);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.w("sqltest2","Mkdbstart");
			db.beginTransaction();
			try {
				SQLiteStatement stmt;
				db.execSQL("create table capitals (prefecture text primary key, capital text not null);");
				stmt = db.compileStatement("insert into capitals values (?, ?);");
				

				stmt.bindString(1, "熊本県");
				stmt.bindString(2, "熊本市");
				stmt.executeInsert();
				db.setTransactionSuccessful();
				Log.w("sqltest2","Mkdbsuccess");
			} finally {
				db.endTransaction();
				Log.w("sqltest2","Mkdberr");
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
		
	}
}