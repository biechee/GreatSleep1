package com.example.greatsleep;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.login.LoginManager;
import com.golife.customizeclass.ScanBluetoothDevice;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.goyourlife.gofitsdk.GoFITSdk;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public  class  SettingActivity extends AppCompatActivity {
    public static GoFITSdk _goFITSdk;
    FirebaseAuth firebaseAuth;
    Button google_logout;
    private FirebaseAuth.AuthStateListener authStateListener;
    private AccessTokenTracker accessTokenTracker;
    public Button bt_ini;
    public Button bt_scan;
    public Button bt_pair;
    public Button bt_connect;
    public Button bt_disconnect;

    public TextView state;
    public TextView pair_state;
    public TextView battery;
    public ScanBluetoothDevice mSelectDevice = null;
    public String productName = null;
    public String mMacAddress = null;
    public String mPairingCode = null;
    public String mPairingTime = null;
    public String mProductID = null;
    public String sdk_certificate = null;
    public String success = "Success !\n";
    public String initial_msg = null;
    public String license = null;

    public  String connect_state;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        editor = preferences.edit();

        setContentView(R.layout.activity_setting);
        state = findViewById(R.id.certificate);
        pair_state = findViewById(R.id.textView_pair);

        sdk_certificate = null;
        try {
            InputStream inputstream = this.getAssets().open("client_cert.crt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputstream));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
            sdk_certificate = sb.toString();
        } catch (Exception e) {
            state.setText("Exception : " + e.toString());
        }
        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();
        google_logout = (Button) findViewById(R.id.logout);
        google_logout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                checkUser();
                accessTokenTracker = new AccessTokenTracker() {
                    @Override
                    protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                        if (currentAccessToken == null) {
                            firebaseAuth.signOut();
                        }
                    }
                };
                LoginManager.getInstance().logOut();
                updateUI();
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();
        init();
        connect();
        pair_state.setText(productName + connect_state);

        bt_ini = findViewById(R.id.button6);
        bt_ini.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                init();
            }
        });

        bt_scan = findViewById(R.id.button3);
        bt_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doScanDevice();
            }
        });

        bt_pair = findViewById(R.id.button5);
        bt_pair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pairing();
            }
        });

        bt_connect = findViewById(R.id.button4);
        bt_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connect();
            }
        });
        bt_disconnect = findViewById(R.id.bt_disconnect);
        bt_disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disc();
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("setting", "onRestart: ");
        checkUser();
    }

    private void checkUser() {
        FirebaseUser user=firebaseAuth.getCurrentUser();
        if(user==null){
            //user not login
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this,LoginActivity.class));
            finish();
        }
        else{
            //user login
            String email=user.getEmail();
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user=firebaseAuth.getCurrentUser();
        if(user==null){
            updateUI();
        }
    }

    private void updateUI() {
        startActivity(new Intent(SettingActivity.this,LoginActivity.class));
        finish();
    }
    @Override
    protected void onResume() {
        super.onResume();
        checkUser();
        connect();
        get_battery_info();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        connect();
    }

    public void init() {
        if (_goFITSdk == null) {
            license = read("sdk_license");
            Log.i("sp", "init: " + read("sdk_license"));

            initial_msg = "initial " + success;
            _goFITSdk = GoFITSdk.getInstance(this, sdk_certificate, license,  // In case this is the very first time the SDK is activated
                    new GoFITSdk.ReceivedLicenseCallback() {
                        @Override
                        public void onSuccess(String receivedLicense) {
                            Log.i("I", receivedLicense);
                            license = receivedLicense;
                            initial_msg = "initial " + success;
                            state.setText(initial_msg);
                            write("sdk_license", license);
                            Log.i("ini", "onSuccess: " + read("sdk_license"));
                        }

                        @Override
                        public void onFailure(int errorCode, String errorMsg) {
                            initial_msg = errorCode + errorMsg + "failure";
                            Log.i("ini", " onFailure: " + read("sdk_license") + initial_msg);
                        }
                    });
            _goFITSdk.reInitInstance();
        } else {
            _goFITSdk.reInitInstance();
        }
        state.setText(initial_msg);
    }

    public void doScanDevice() {
        if (_goFITSdk != null) {
            Log.i("scan", "demo_function_scan");
            // Demo - doScanDevice API
            _goFITSdk.doScanDevice(new GoFITSdk.DeviceScanCallback() {

                @Override
                public void onSuccess(ScanBluetoothDevice device) {
                    Log.i("", "doScanDevice() : onSuccess() : device = " + device.getDevice().getName() + ", " + device.getDevice().getAddress() + ", " + device.getRSSI() + ", " + device.getProductID());
                    state.setText("device has founded !");
                    productName = device.getDevice().getName();
                    //productName寫入firebase
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    String userdocument = preferences.getString("userdocument","");

                    Map<String, Object> data = new HashMap<>();
                    data.put("productName",productName);

                    DocumentReference docRef = db.collection("User").document(userdocument).collection("productinfo").document("productdata");
                    docRef.set(data, SetOptions.merge())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("ss", "Document successfully written! "+data);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("ss", "Error writing document", e);
                                }
                            });
                }

                @Override
                public void onCompletion(ArrayList<ScanBluetoothDevice> devices) {

                    for (ScanBluetoothDevice device : devices) {
                        Log.i("", "doScanDevice() : onCompletion() : device = " + productName + ", " + device.getDevice().getAddress() + ", " + device.getRSSI() + ", " + device.getProductID());
                        String summary = "scan completion\n" + "device = " + productName + ", " + device.getDevice().getAddress() + ", " + device.getRSSI() + ", " + device.getProductID();
                        state.setText(summary);
                    }
                    if (devices.size() > 0) {
                        mSelectDevice = devices.get(0);
                        write("device", mSelectDevice.getDevice().getName());
                        String summary = "Recommended Device : \n" + productName;
                        state.setText(summary);
                    } else {
                        state.setText("No more devices be founded");
                    }
                }

                @Override
                public void onFailure(int errorCode, String errorMsg) {
                    String summary = "Device Not Found\n errorCode = " + errorCode + ", " + "errorMsg = " + errorMsg;
                    Log.e("", "doScanDevice() : onFailure() : errorCode = " + errorCode + ", " + "errorMsg = " + errorMsg);
                    state.setText(summary);
                }
            });
        } else {
            state.setText("sdk_license needs to be initial !");
        }
    }

    public void pairing() {
        if (_goFITSdk != null) {

            if (mSelectDevice != null) {
                mMacAddress = mSelectDevice.getDevice().getAddress();
                //productName寫入firebase
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                String userdocument = preferences.getString("userdocument","");

                Map<String, Object> data = new HashMap<>();
                data.put("mMacAddress",mMacAddress);

                DocumentReference docRef = db.collection("User").document(userdocument).collection("productinfo").document("productdata");
                docRef.set(data, SetOptions.merge())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("ss", "Document successfully written! "+data);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("ss", "Error writing document", e);
                            }
                        });
            } else {
                pair_state.setText("No Device Selected, `Scan Device` First!");
                return;
            }

            // Demo - doNewPairing API
            _goFITSdk.doNewPairing(mSelectDevice, new GoFITSdk.NewPairingCallback() {
                @Override
                public void onSuccess(String pairingCode, String pairingTime) {
                    String summary = "doNewPairing() : onSuccess() : Got pairingCode = " + pairingCode + "  Confirming...";
                    state.setText(summary);

                    mPairingCode = pairingCode;
                    mPairingTime = pairingTime;
                    //productName寫入firebase
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    String userdocument = preferences.getString("userdocument","");

                    Map<String, Object> data = new HashMap<>();
                    data.put("mPairingCode",mPairingCode);
                    data.put("mPairingTime", mPairingTime);

                    DocumentReference docRef = db.collection("User").document(userdocument).collection("productinfo").document("productdata");
                    docRef.set(data, SetOptions.merge())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("ss", "Document successfully written! "+data);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("ss", "Error writing document", e);
                                }
                            });

                    mConfirmPairingCodeHandler.postDelayed(mConfirmPairingCodeRunnable, 5000);
                    Log.i("pref", "doNewPairing  pref: code: " + mPairingCode + " time: " + mPairingTime);

                }

                @Override
                public void onFailure(int errorCode, String errorMsg) {
                    String summary = "onFailure() : errorCode = " + errorCode + ", " + "errorMsg = " + errorMsg;
                    state.setText(summary);
                }
            });
        } else {
            pair_state.setText("SDK Instance invalid, needs `SDK init`");
        }
    }

    public void connect() {
        if (_goFITSdk != null) {
            // Demo - get connect information from local storage
            if (mMacAddress == null || mPairingCode == null || mPairingTime == null) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                String userdocument = "s0958952199@gmail.com";
                DocumentReference docRef = db.collection("User").document(userdocument).collection("productinfo").document("productdata");
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d("ss", "DocumentSnapshot data: " + document.getData());
                                Map<String, Object> data = new HashMap<>();
                                data = document.getData();
                                productName=data.get("productName").toString();
                                mMacAddress=data.get("mMacAddress").toString();
                                mPairingCode=data.get("mPairingCode").toString();
                                mPairingTime=data.get("mPairingTime").toString();
                                mProductID=data.get("mProductID").toString();
                                String summary = "productName: " + productName +
                                        "\n" + "mMacAddress: " + mMacAddress +
                                        "\n\n" + "mPairingCode: " + mPairingCode +
                                        "\n" + "mPairingTime: " + mPairingTime +
                                        "\n" + "mProductI: " + mProductID;

                                Log.i("手環資料", "productinfo "+summary);
                            }
                            else {
                                Log.d("ss", "No such document");
                            }
                            // Demo - doConnectDevice API
                            _goFITSdk.doConnectDevice(mMacAddress, mPairingCode, mPairingTime, mProductID, new GoFITSdk.GenericCallback() {
                                @Override
                                public void onSuccess() {
                                    boolean isConnect = _goFITSdk.isBLEConnect();
                                    connect_state = isConnect ? " Connected " : " Disconnected " ;
                                    Log.i("", "connect onSuccess: "+productName+connect_state);
                                    pair_state.setText(productName + connect_state);
                                }
                                @Override
                                public void onFailure(int errorCode, String errorMsg) {
                                    Log.i("", "connect onFailure: "+errorMsg);
                                    connect_state=errorMsg;
                                    pair_state.setText(productName + connect_state);
                                }
                            });
                        }
                        else {
                            Log.d("ss", "get failed with ", task.getException());
                        }
                    }
                });
            }
        }
    }

    public Handler mConfirmPairingCodeHandler = new Handler();
    public Runnable mConfirmPairingCodeRunnable = new Runnable() {
        public void run() {
            mConfirmPairingCodeHandler.removeCallbacks(mConfirmPairingCodeRunnable);

            // Demo - confirmPairingCode API
            if (_goFITSdk != null) {
                mProductID = mSelectDevice.getProductID();
                _goFITSdk.doConfirmPairingCode(mPairingCode, mPairingTime, mProductID, new GoFITSdk.GenericCallback() {
                    @Override
                    public void onSuccess() {

                        pair_state.setText("doConfirmPairingCode onSuccess Pairing Complete!");

                        //mProductID寫入firebase
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        String userdocument = preferences.getString("userdocument","");

                        Map<String, Object> data = new HashMap<>();
                        data.put("mProductID", mProductID);

                        DocumentReference docRef = db.collection("User").document(userdocument).collection("productinfo").document("productdata");
                        docRef.set(data, SetOptions.merge())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("ss", "Document successfully written! "+data);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("ss", "Error writing document", e);
                                    }
                                });

                        Log.i(" ", "confirm handler: " + " ID: " + mProductID + " code " + mPairingCode);

                        boolean isConnect = _goFITSdk.isBLEConnect();
                        String summary = isConnect ? "Connected" : "Disconnected";
                        pair_state.setText("Confirm Paring Code : " + mPairingCode + " " + mPairingTime + " 連接狀態:" + summary);

                    }

                    @Override
                    public void onFailure(int errorCode, String errorMsg) {
                        Log.e("text2", "doConfirmPairingCode() : onFailure() : errorCode = " + errorCode + ", " + "errorMsg = " + errorMsg);
                        pair_state.setText("doConfirmPairingCode() : onFailure() : errorCode = " + errorCode + ", " + "errorMsg = " + errorMsg);
                    }
                });
            } else {
                pair_state.setText("SDK Instance invalid, needs `SDK init`");
            }
        }
    };
    public String read(String info) {
        SharedPreferences sharedPreferences = getSharedPreferences(info, Context.MODE_PRIVATE);
        return sharedPreferences.getString(info, null);
    }

    public void clear_sp(String info) {
        SharedPreferences sharedPreferences = getSharedPreferences(info, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

    }

    public boolean write(String s1, String s2) {
        if (s1.length() == 0) return false;
        SharedPreferences sharedPreferences = getSharedPreferences(s1, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(s1, s2);
        return editor.commit();
    }
    public  void  disc(){
        if(_goFITSdk!=null){
            _goFITSdk.doDisconnectDevice();
            pair_state.setText("disconnect");
        }
    }

    public void vibrate(int times) {
        if (_goFITSdk != null) {
            Log.i("", "demo_function vibrate");
            _goFITSdk.doFindMyCare(times);
        } else {
            state.setText("need ini first　!");
        }

    }
    public void get_battery_info(){
        if (_goFITSdk != null) {
            Log.i("_tag", "demo_battery");

            // Demo - getDeviceBatteryValue API
            _goFITSdk.getDeviceBatteryValue(new GoFITSdk.GetDeviceInfoCallback() {
                @Override
                public void onSuccess(String info) {
                    Log.i("_tag", "getDeviceBatteryValue() : onSuccess() : info = " + info);
                }

                @Override
                public void onFailure(int errorCode, String errorMsg) {
                    Log.i("_tag", "getDeviceBatteryValue() : onFailure() : errorCode = " + errorCode + ", " + "errorMsg = " + errorMsg);

                }
            });
        }
    }
    public void backto(View view){
        finish();
    }
}