package com.abc.driver;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CargoWeightVolumeDialog extends Dialog {

	private static final String TAG = "CargoWeightVolumeDialog";
	private CargoWeightVolumeInputListener IListener;
	private Context context;
	private Button okBtn;
	private Button cancelBtn;
	private String weightNum = "0";
	private String volumeNum = "0";
	private EditText weightEt;
	private EditText volumeEt;

	public CargoWeightVolumeDialog(Context context) {
		super(context);
	}

	public CargoWeightVolumeDialog(Context context,
			CargoWeightVolumeInputListener inputListener) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
		IListener = inputListener;
	}

	public interface CargoWeightVolumeInputListener {
		void getText(String str, String str2);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_cargo_weight_volume);
	
		weightEt = (EditText) findViewById(R.id.cargo_weight_et);
		volumeEt = (EditText) findViewById(R.id.cargo_volume_et);
		weightEt.setText("0");
		volumeEt.setText("0");
		
		okBtn = (Button) findViewById(R.id.ok_btn);
		cancelBtn = (Button) findViewById(R.id.cancel_btn);
		okBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				weightNum = weightEt.getText().toString().trim();
				volumeNum = volumeEt.getText().toString().trim();
				Log.d(TAG, "weightNum : " + weightNum);
				Log.d(TAG, "volumeNum : " + volumeNum);
				IListener.getText(weightNum, volumeNum);
				dismiss();
			}
		});
		cancelBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
			}
		});

	}

}
