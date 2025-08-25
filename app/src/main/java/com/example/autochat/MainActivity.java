package com.example.autochat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Instrumentation;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Runnable{

    RecyclerView recyclerView;
    TextView welcomeTextView;
    EditText messageEditText;
    ImageButton sendButton;
    ImageButton sendAudioButton;
    List<Message> messageList;
    MessageAdapter messageAdapter;
    private static final String ACTION_USB_PERMISSION = "com.examples.accessory.controller.action.USB_PERMISSION";
    private final String TAG = "_ITE";
    private UsbManager mUsbManager;
    private PendingIntent mPermissionIntent;
    private boolean mPermissionRequestPending;
    UsbAccessory mAccessory;
    ParcelFileDescriptor mFileDescriptor;
    FileInputStream mInputStream;
    FileOutputStream mOutputStream;
    int counter =0;
    boolean accessoryStatus = false;

    private static  final int PERMISSION_REQUEST_STORAGE = 1000;
    private static  final int READ_REQUEST_CODE = 40;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //system Run check
        System.out.println("App Started !");

        // initialize Usb related activities
        mUsbManager = (UsbManager)getSystemService(Context.USB_SERVICE);
        //mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED);
        registerReceiver(mUsbReceiver, filter);

        // message model object for the class
        messageList = new ArrayList<>();

        //Recycle view and it's items
        recyclerView = findViewById(R.id.recycler_view);
        welcomeTextView = findViewById(R.id.welcome_text);
        messageEditText = findViewById(R.id.message_edit_text);
        sendButton = findViewById(R.id.send_btn);
        sendAudioButton = findViewById(R.id.send_pcmbtn);

        //set up recycler view
        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(messageAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);

        // Send Audio file button action
        sendAudioButton.setOnClickListener((v) -> {
            welcomeTextView.setVisibility(View.GONE);
            //send audio code
            performFileSearch();

            messageEditText.setText("");
        });

        // send button button action
        sendButton.setOnClickListener((v)->{
            String question = messageEditText.getText().toString().trim();
            //Toast.makeText(this,question,Toast.LENGTH_LONG).show();
            /*if(counter%2==0)
                addToChat(question,Message.SENT_BY_ME);
            else if(counter%2==1)
                addToChat(question,Message.SENT_BY_IVI);
            counter=(counter+1)%2;*/
            byte [] questionByte = question.getBytes();
            try {
                if(accessoryStatus == true)
                    mOutputStream.write(questionByte);
                addToChat(question,Message.SENT_BY_ME);
                if(accessoryStatus == false)
                    addToChat("Message not sent as IVI device is not connected !!",Message.SENT_BY_IVI);
            }catch (IOException e){
                Log.d(TAG,"IOException during write message");
                addToChat("Device not connected, IOException occurred",Message.SENT_BY_IVI);
            }
            messageEditText.setText("");
            welcomeTextView.setVisibility(View.GONE);
        });
        // end of send button
    }

    // Add to chat Function to add message to chat window
    void addToChat(String message,String sentBy){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageList.add(new Message(message,sentBy));
                messageAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
            }
        });
    }

    // ------ On Resume begin -----
    @Override
    public void onResume()
    {
        super.onResume();

        Intent intent = getIntent();
        if (mInputStream != null && mOutputStream != null) {
            return;
        }

        UsbAccessory[] accessories = mUsbManager.getAccessoryList();
        UsbAccessory accessory = (accessories == null ? null : accessories[0]);
        if (accessory != null)
        {
            if (mUsbManager.hasPermission(accessory))
            {
                openAccessory(accessory);
            }
            else
            {
                synchronized (mUsbReceiver)
                {
                    if (!mPermissionRequestPending)
                    {
                        mUsbManager.requestPermission(accessory,mPermissionIntent);
                        mPermissionRequestPending = true;
                    }
                }
            }
            accessoryStatus= true;
        }
        else
        {
            Log.d(TAG, "mAccessory is null");
            accessoryStatus = false;
        }
    }
    // --- end of on resume ----

    // --- begin on pause -----
    @Override
    public void onPause() {
        super.onPause();
        closeAccessory();
    }
    // --- end of pause ---

    // -- begin of on destroy ---
    @Override
    public void onDestroy() {
        unregisterReceiver(mUsbReceiver);
        super.onDestroy();
    }
    // -- end of on destroy --

    // -- start of open accessory --
    private void openAccessory(UsbAccessory accessory)
    {
        mFileDescriptor = mUsbManager.openAccessory(accessory);
        if (mFileDescriptor != null) {
            mAccessory = accessory;
            FileDescriptor fd = mFileDescriptor.getFileDescriptor();
            mInputStream = new FileInputStream(fd);
            mOutputStream = new FileOutputStream(fd);
            Thread thread = new Thread(null, this, "AccessoryController");
            thread.start();
            Log.d(TAG, "accessory opened");
            //enableControls(true);
        } else {
            Log.d(TAG, "accessory open fail");
        }
    }
    // end of open accessory

    // -- start of close accessory --
    private void closeAccessory() {
        //enableControls(false);

        try {
            if (mFileDescriptor != null) {
                mFileDescriptor.close();
            }
        } catch (IOException e) {
        } finally {
            mFileDescriptor = null;
            mAccessory = null;
        }
    }
    //-- end of close Accessory ---

    //--- start of broadcast receiver and on receive ---
    /*
     * This receiver monitors for the event of a user granting permission to use
     * the attached accessory.  If the user has checked to always allow, this will
     * be generated following attachment without further user interaction.
     */
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbAccessory accessory = (UsbAccessory) intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if(accessory != null){
                            openAccessory(accessory);
                        }
                        else
                            Log.d(TAG,"No device attached !!");
                    } else {
                        Log.d(TAG, "permission denied for accessory "+ accessory);
                    }
                    mPermissionRequestPending = false;
                }
            } else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {
                UsbAccessory accessory = (UsbAccessory) intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
                if (accessory != null && accessory.equals(mAccessory)) {
                    closeAccessory();
                }
            }
        }
    };

    // --- end of broadcast receiver and on receive ----

    // Runnable for Usb manager

    @Override
    public void run() {
        int ret =0;
        byte[]  buffer = new byte[Integer.MAX_VALUE];
        Log.d(TAG,"Runnable for usb triggered");

        while(ret >=0)
        {
            try{
                ret = mInputStream.read(buffer);
                String IVIMsg = new String(buffer);
                addToChat(IVIMsg,Message.SENT_BY_IVI);

            }catch (IOException e){
                Log.d(TAG,"IOException occurred in Run method");
                break;
            }
        }

    }

    //end of run
    /*************** code to perform audio transfer ***********/

    //perform file search
    private void performFileSearch(){

        /*if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            requestPermissions( new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_STORAGE);
        }*/
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, READ_REQUEST_CODE);
        //Instrumentation.ActivityResult(intent, READ_REQUEST_CODE);
    }

    // on activity result

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                Log.d(TAG, uri.getScheme());
                //String path = uri.getPath();
                //path = path.substring(path.indexOf(":")+1);
                DocumentFile d = DocumentFile.fromSingleUri(this, uri);
                String path = "", file_name = "";
                if (d != null) {
                    path = d.getUri().getPath();
                    file_name = d.getName();
                }
                Log.d(TAG, "file Name: " + file_name + " File Path: " + path + " using util: " + uri);
                //String[] split = path.split(":");
                //path = getPathFromExtSD(split);
                Toast.makeText(this, "" + path, Toast.LENGTH_SHORT).show();
                addToChat("Attached File:"+file_name,Message.SENT_BY_ME);
                // content of attached file
                if(file_name.split("\\.")[1].equals("txt"))
                    addToChat(readText(getApplicationContext(), uri),Message.SENT_BY_ME);
                else
                    readText(getApplicationContext(), uri);

            }
        }
    }

    //read text from file

    String readText(Context context, Uri input){
        Log.d(TAG, input.toString());
        InputStream istream = null;

        try{
            istream = context.getContentResolver().openInputStream(input);

            Log.d(TAG,"available print of byte array : " + istream.available());
            byte[] Buffer = new byte[istream.available()];

            istream.read(Buffer);
            // send audio file if accessory is connected
            if(accessoryStatus== true)
                mOutputStream.write(Buffer);
            else
                addToChat("Message not sent as IVI device is not connected !!",Message.SENT_BY_IVI);
            if(istream.available()<=50)
                return new String(Buffer);
            addToChat("content Big to display ...",Message.SENT_BY_ME);
            return null;

        }catch (IOException e){
            e.printStackTrace();
        }
        return "File read unsuccessful !!";
    }



}