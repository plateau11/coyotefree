package com.example.coyotefree;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
public class History extends AppCompatActivity {
    public ArrayList<String> unlockfilelist;
    DBHandler2 db2;
    ArrayList<String> dateTimeList;
    String showfilename = "";
    String CHANNEL_ID = "mychannel";
    String textTitle = "";
    String textContent = "File saved";
    GridView gv;
    LinearLayout ll;
    public static String appSpaceFilepath = "";  //keeps track of the filepath of
    //newly created file for doc & ppt viewer
    public static int cardwidth;
    public static int cardheight;
    public static int imgHeight;
    public static int imgWidth;
    public static boolean itemlongclick = false;
    Toolbar toolbar;
    public static boolean cancelFlag = false;
    CourseGVAdapter adapter;
    String filename2;
    ArrayList<String> filteredFiles = new ArrayList<>();
    public static ArrayList<Boolean> checklistToggle;
    public static ArrayList<Integer> relativeindexList;
    public DialogInterface currentDialog;
    String pdfExtension="";
    String docExtension="";
    String videosExtension="";
    String imageExtension="";
    String pptExtension="";
    String excelExtension="";
    String audioExtension="";
    String allfiles="";
    private String[] fruits = {pdfExtension, docExtension, videosExtension, imageExtension, pptExtension,
            excelExtension, audioExtension, allfiles};
    public static Boolean dialogOptionSelected = false;
    Boolean okclicked = false;
    private String selectedFruits;
    private int selectedFruitsIndex = 0;
    GridView coursesGV;
    private DBHandler dbHandler;
    private static int rowcount;
    private static int firstid;
    public ArrayList<String> myarraylist;
    public static HashMap<Integer, Integer> checkitems_hash_map;
    public static ArrayList<Integer> checkitems;
    public static ArrayList<CourseModel> courseModelArrayList;
    boolean random = false;  //controls visibility of option 3 and 4
    public static boolean selectall = false;
    Menu global;
    boolean isDarkModeEnabled;
    public static int gridvalue = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db2 = new DBHandler2(History.this);
        unlockfilelist = new ArrayList<>();
        dateTimeList = new ArrayList<>();
        SharedPreferences sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        isDarkModeEnabled = sharedPreferences.getBoolean("dark_mode", false);
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(Color.parseColor("#D8F2F5"));
        if (!isDarkModeEnabled)
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_history);
        System.gc();

        toolbar = findViewById(R.id.toolbar2);

        ViewCompat.setOnApplyWindowInsetsListener(toolbar, (view, insets) -> {
            int topInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top;
            view.setPadding(0, 70, 0, 0);
            return insets;
        });


        if (isDarkModeEnabled) {
            Toolbar bar = findViewById(R.id.toolbar2);
            bar.setBackgroundColor(Color.parseColor("#0C0F10"));
            window.setStatusBarColor(Color.parseColor("#0C0F10"));
            AppBarLayout abl = findViewById(R.id.barbackground2);
            abl.setBackgroundColor(Color.parseColor("#0C0F10"));
            LinearLayout backgroundLayout = findViewById(R.id.historybackgroundimage2);
            backgroundLayout.setBackgroundColor(Color.parseColor("#0B0A0A"));
            //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_LAYOUT_FLAGS);
        }

        pdfExtension = getString(R.string.pdf);
        docExtension = getString(R.string.doc);
        videosExtension = getString(R.string.videos);
        imageExtension = getString(R.string.images);
        pptExtension = getString(R.string.ppt);
        excelExtension = getString(R.string.excel);
        audioExtension =  getString(R.string.audio);
        allfiles = getString(R.string.Allfiles);

        String[] fruits1 = {pdfExtension, docExtension, videosExtension, imageExtension, pptExtension,
                excelExtension, audioExtension, allfiles};

        int size_temp = fruits1.length;
        for(int i=0;i<size_temp;i++){
            fruits[i] = fruits1[i];
        }


        SharedPreferences gridpreference = getSharedPreferences("gridpreference", MODE_PRIVATE);
        String preference = gridpreference.getString("gpref", "2x2");
        if (preference.equalsIgnoreCase("3x3")) {
            if (MainActivity.isTabletConfirmed) {
                GridView myView = findViewById(R.id.idGVcourses);
                //ViewGrou params = (ViewGroup.MarginLayoutParams) myView.getLayoutParams();
                myView.setNumColumns(3);
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) myView.getLayoutParams();
                params.setMargins(dptopxconverter(82),
                        dptopxconverter(20),
                        dptopxconverter(20),
                        dptopxconverter(0)); // Left, Top, Right, Bottom margins (in pixels)
                myView.setLayoutParams(params);
                gridvalue = 3;
                //ease of calculation for CourseGVAdapter class
                cardwidth = dptopxconverter(170);
                cardheight = dptopxconverter(175);
                imgHeight = dptopxconverter(155);
            } else {
                GridView myView = findViewById(R.id.idGVcourses);
                //ViewGrou params = (ViewGroup.MarginLayoutParams) myView.getLayoutParams();
                myView.setNumColumns(3);
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) myView.getLayoutParams();
                params.setMargins(dptopxconverter(29),
                        dptopxconverter(20),
                        dptopxconverter(0),
                        dptopxconverter(0)); // Left, Top, Right, Bottom margins (in pixels)
                myView.setLayoutParams(params);
                gridvalue = 3;
                //ease of calculation for CourseGVAdapter class
                cardwidth = dptopxconverter(105);
                cardheight = dptopxconverter(110);
                imgHeight = dptopxconverter(90);
            }
        }
        if (preference.equalsIgnoreCase("2x2")) {
            if (MainActivity.isTabletConfirmed) {
                GridView myView = findViewById(R.id.idGVcourses);
                myView.setNumColumns(2);
                myView.setVerticalSpacing(dptopxconverter(45));
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) myView.getLayoutParams();
                params.setMargins(dptopxconverter(148),
                        dptopxconverter(42),
                        dptopxconverter(25),
                        dptopxconverter(0)); // Left, Top, Right, Bottom margins (in pixels)
                myView.setLayoutParams(params);
                gridvalue = 2;
                cardwidth = dptopxconverter(195);
                cardheight = dptopxconverter(200);
                imgHeight = dptopxconverter(175);
                //imgWidth = dptopxconverter();
            } else {
                GridView myView = findViewById(R.id.idGVcourses);
                myView.setNumColumns(2);
                gridvalue = 2;
                cardwidth = dptopxconverter(140);
                cardheight = dptopxconverter(145);
                imgHeight = dptopxconverter(125);
            }
        }
        selectall = false;
        CourseGVAdapter.selectall = false;
        toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        // Enable the back arrow
        if (getSupportActionBar() != null) {
            String title = getString(R.string.LockedFiles);
            getSupportActionBar().setTitle(title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                random = false;
                invalidateOptionsMenu();
                adapter.deactivate();
                this.setEnabled(false);
                getOnBackPressedDispatcher().onBackPressed();
            }
        };
        getOnBackPressedDispatcher().addCallback(callback);
        dbHandler = new DBHandler(History.this);
        coursesGV = findViewById(R.id.idGVcourses);  //grid view
        courseModelArrayList = new ArrayList<CourseModel>();
        myarraylist = new ArrayList<>();
        rowcount = dbHandler.getRowCount();
        firstid = dbHandler.getFirstId();
        myarraylist = dbHandler.readCourses(); //filling the array list
        int size = myarraylist.size();
        for (int i = 0; i < size; i++) {
            String filename = myarraylist.get(i);
            // Create a File object using the file path
            File file = new File(filename);
            // Get the name of the file (including the extension)
            String fileName = file.getName();
            // Find the index of the last dot (.) in the filename
            int dotIndex = fileName.lastIndexOf(".");
            String filenameonly = "";
            // If a dot exists and is not the first character, extract the filename without the extension
            if (dotIndex > 0) {
                filenameonly = fileName.substring(0, dotIndex);
            } else {
                // If there is no extension, just return the full filename
            }
            courseModelArrayList.add(new CourseModel(filenameonly, filename)); //filling the arraylist
        }
        adapter = new CourseGVAdapter(this, courseModelArrayList);
        adapter.setSize(courseModelArrayList.size());
        coursesGV.setAdapter(adapter);
        ArrayList<String> finalMyarraylist = myarraylist;
        //single press option
        coursesGV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //for filtered list item selection
                String name = "";
                String fn = "";
                if (okclicked) {
                    CourseModel cm = courseModelArrayList.get(position);
                    fn = cm.filename;
                    name = fn;
                } else {
                    name = finalMyarraylist.get(position);
                }
                String extension = "";
                int dotIndex = name.lastIndexOf(".");
                if (dotIndex > 0 && dotIndex < name.length() - 1)
                    extension = name.substring(dotIndex + 1);
                //image type handling
                if (extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg") ||
                        extension.equalsIgnoreCase("png") ||
                        extension.equalsIgnoreCase("webp")) {
                    Intent intent2 = new Intent(History.this, displayItem.class);
                    if (okclicked) {
                        filename2 = fn;
                    } else
                        filename2 = finalMyarraylist.get(position);
                    intent2.putExtra("filename", filename2);
                    startActivity(intent2);
                }
                //video type handling
                if (extension.equalsIgnoreCase("mkv") || extension.equalsIgnoreCase("mp4")
                        || extension.equalsIgnoreCase("mpeg") || extension.equalsIgnoreCase("opus")
                        || extension.equalsIgnoreCase("mp3")
                        || extension.equalsIgnoreCase("wav") || extension.equalsIgnoreCase("aac")) {
                    Intent intent2 = new Intent(History.this, displayItem2.class);
                    if (okclicked) {
                        filename2 = fn;
                    } else
                        filename2 = finalMyarraylist.get(position);
                    intent2.putExtra("filename", filename2);
                    startActivity(intent2);

                }
                //pdf type handling
                if (extension.equalsIgnoreCase(pdfExtension)
                ) {
                    Intent intent2 = new Intent(History.this, displayItem3.class);
                    //Toast.makeText(History.this, "another check", Toast.LENGTH_SHORT).show();
                    if (okclicked) {
                        filename2 = fn;
                    } else
                        filename2 = finalMyarraylist.get(position);
                    intent2.putExtra("filename", filename2);
                    startActivity(intent2);
                }
                //docx type handling
                if (extension.equalsIgnoreCase("docx") || extension.equalsIgnoreCase(docExtension)
                ) {
                    //first destroying the previously temporarily created doc file for viewer
                    //to save app space memory
                    if (!(appSpaceFilepath.equals(""))) {
                        fileDestroyer(appSpaceFilepath);
                    }
                    Intent intent2 = new Intent(History.this, displayItem4.class);
                    if (okclicked) {
                        filename2 = fn;
                    } else
                        filename2 = finalMyarraylist.get(position);
                    intent2.putExtra("filename", filename2);
                    startActivity(intent2);
                }
                //ppt type handling
                if (extension.equalsIgnoreCase(pptExtension) || extension.equalsIgnoreCase("pptx")
                ) {
                    //first destroying the previously temporarily created ppt file for viewer
                    //to save app space memory
                    if (!(appSpaceFilepath.equals(""))) {
                        fileDestroyer(appSpaceFilepath);
                    }
                    Intent intent2 = new Intent(History.this, displayItem5.class);
                    if (okclicked) {
                        filename2 = fn;
                    } else
                        filename2 = finalMyarraylist.get(position);
                    intent2.putExtra("filename", filename2);
                    startActivity(intent2);
                }
                //csv type handling
                if (extension.equalsIgnoreCase("csv")
                ) {
                    //first destroying the previously temporarily created ppt file for viewer
                    //to save app space memory
                    if (!(appSpaceFilepath.equals(""))) {
                        fileDestroyer(appSpaceFilepath);
                    }
                    Intent intent2 = new Intent(History.this, displayItem6.class);
                    if (okclicked) {
                        filename2 = fn;
                    } else
                        filename2 = finalMyarraylist.get(position);
                    intent2.putExtra("filename", filename2);
                    startActivity(intent2);
                }
                //excel type handling
                if (extension.equalsIgnoreCase("xls") || extension.equalsIgnoreCase("xlsx")
                ) {
                    //first destroying the previously temporarily created ppt file for viewer
                    //to save app space memory
                    if (!(appSpaceFilepath.equals(""))) {
                        fileDestroyer(appSpaceFilepath);
                    }
                    Intent intent2 = new Intent(History.this, displayItem7.class);
                    if (okclicked) {
                        filename2 = fn;
                    } else
                        filename2 = finalMyarraylist.get(position);
                    intent2.putExtra("filename", filename2);
                    startActivity(intent2);
                }
                //gif type handling
                if (extension.equalsIgnoreCase("gif")) {
                    //first destroying the previously temporarily created ppt file for viewer
                    //to save app space memory
                    if (!(appSpaceFilepath.equals(""))) {
                        fileDestroyer(appSpaceFilepath);
                    }
                    Intent intent2 = new Intent(History.this, displayItem8.class);
                    if (okclicked) {
                        filename2 = fn;
                    } else
                        filename2 = finalMyarraylist.get(position);
                    intent2.putExtra("filename", filename2);
                    startActivity(intent2);
                }
            }
        });

        //Long press option
        coursesGV.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                itemlongclick = true;
                toolbar.setTitle("");
                checkitems_hash_map = new HashMap<Integer, Integer>();
                //Toast.makeText(History.this, "position: "+position, Toast.LENGTH_SHORT).show();
                random = true;
                invalidateOptionsMenu();
                //Trigger Logic(Check items)
                adapter = new CourseGVAdapter(History.this, courseModelArrayList);
                adapter.setSize(courseModelArrayList.size());
                adapter.activate();
                coursesGV.setAdapter(adapter);
                // Create the PopupMenu and use the correct context (History.this)
                PopupMenu popupMenu = new PopupMenu(History.this, view);
                // Inflate the menu items from a menu XML
                popupMenu.inflate(R.menu.popup_menu); // Ensure you have popup_menu.xml
                Menu menu = popupMenu.getMenu();
                //checking whether to show restore option based on com.coyote word
                ArrayList<String> optionCheck = dbHandler.readOriginalFileLocation();
                String name2 = optionCheck.get(position);
                //Toast.makeText(History.this, name2, Toast.LENGTH_SHORT).show();
                boolean exists = processPathAndCheck(name2, "com.example.coyote");
                if (!exists) {
                    //invalidateOptionsMenu();
                    MenuItem restoreItem = menu.findItem(R.id.restore);
                    // Check your condition and set visibility
                    restoreItem.setVisible(true);
                }
                // Set the menu item click listener
                /*popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int itemId = item.getItemId();
                        switch (itemId) {
                            case R.id.delete:
                                dbHandler = new DBHandler(History.this);
                                ArrayList<String> my = dbHandler.readCourses();
                                ArrayList<Integer> ids = dbHandler.readId();
                                String name = my.get(position);
                                File file = new File(name);
                                //position is the long pressed item's position
                                int deleteItemPosition = ids.get(position);
                                try {
                                    if (dbHandler.deleteRowById(deleteItemPosition)) {
                                    } else {
                                    }
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                                return true;
                            case R.id.restore:
                                //restoring image code
                                String AESSalt = "AES128bit";
                                String text_key = getString(R.string.text_key);
                                String cypherInstance = "AES/CBC/PKCS5Padding";
                                String initializationVector = "2195081919109305";
                                SecretKeySpec sks = new SecretKeySpec(getRaw(text_key, AESSalt), "AES");
                                Cipher cipher = null;
                                try {
                                    cipher = Cipher.getInstance(cypherInstance);
                                } catch (NoSuchAlgorithmException e) {
                                    throw new RuntimeException(e);
                                } catch (NoSuchPaddingException e) {
                                    throw new RuntimeException(e);
                                }
                                try {
                                    cipher.init(Cipher.DECRYPT_MODE, sks, new IvParameterSpec(initializationVector.getBytes()));
                                } catch (InvalidAlgorithmParameterException e) {
                                    throw new RuntimeException(e);
                                } catch (InvalidKeyException e) {
                                    throw new RuntimeException(e);
                                }
                                dbHandler = new DBHandler(History.this);
                                ArrayList<String> my2 = dbHandler.readCourses();
                                ArrayList<String> my3 = dbHandler.readOrgFilename();
                                String lockedFilename = my2.get(position);
                                String restoreFilename = my3.get(position);
                                FileInputStream fis = null;
                                FileOutputStream fos = null;
                                try {
                                    fis = new FileInputStream(lockedFilename);
                                } catch (FileNotFoundException e) {
                                }
                                try {
                                    fos = new FileOutputStream(restoreFilename);
                                } catch (FileNotFoundException e) {
                                    throw new RuntimeException(e);
                                }
                                CipherInputStream cis = new CipherInputStream(fis, cipher);
                                byte[] b = new byte[1024];
                                int i = 0;
                                try {
                                    i = cis.read(b);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                while (i != -1) {
                                    try {
                                        fos.write(b, 0, i);
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                    try {
                                        i = cis.read(b);
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                                try {
                                    fos.close();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                try {
                                    fis.close();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                try {
                                    cis.close();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            default:
                                return false;
                        }
                    }
                });

                 */
                // Show the popup menu
                return true; // Indicate the long-click event has been consumed
            }
        });

    }

    public static boolean processPathAndCheck(String path, String target) {
        // Create an ArrayList to store the components of the path
        List<String> pathComponents = new ArrayList<>();
        // Split the path by "/" and add the parts to the ArrayList
        String[] parts = path.split("/");
        // Loop through the parts and add to ArrayList (ignoring empty strings)
        for (String part : parts) {
            if (!part.isEmpty()) {
                pathComponents.add(part);
            }
        }
        // Now check if the target exists in the ArrayList
        return pathComponents.contains(target);
    }

    private static byte[] getRaw(String plainText, String salt) {
        String secretKeyInstance = "PBKDF2WithHmacSHA1";
        int pswdIterations = 100;
        int keySize = 128;
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(secretKeyInstance);
            KeySpec spec = new PBEKeySpec(plainText.toCharArray(), salt.getBytes(), pswdIterations, keySize);
            return factory.generateSecret(spec).getEncoded();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    /**
     * Navbar item selection function
     *
     * @param item The menu item that was selected.
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.option1:  //filter option
                dialog();
                return true;
            case R.id.option3:   //remove items option
                if (selectall) {
                    dbHandler = new DBHandler(History.this);
                    ArrayList<Integer> ids = dbHandler.readId();
                    int dPosition;
                    Iterator it = checkitems_hash_map.entrySet().iterator();
                    //hash loop
                    while (it.hasNext()) {
                        //Toast.makeText(this, "check random", Toast.LENGTH_SHORT).show();
                        Map.Entry pair = (Map.Entry) it.next();
                        Integer deletePosition = (Integer) pair.getValue();
                        if (dialogOptionSelected && okclicked)
                            dPosition = ids.get(relativeindexList.get(deletePosition));
                        else
                            dPosition = ids.get(deletePosition);
                        try {
                            if (dbHandler.deleteRowById(dPosition)) {
                                //Toast.makeText(this, "filename deleted: "+ DBHandler.firstValue, Toast.LENGTH_SHORT).show();
                            } else {
                                //Toast.makeText(this, "not deleted", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                    adapter.deactivate();
                    finish();
                    startActivity(getIntent());
                    overridePendingTransition(0, 0);
                }
                if (!selectall) {
                    dbHandler = new DBHandler(History.this);
                    ArrayList<Integer> ids = dbHandler.readId();
                    int dPosition;
                    Iterator it = checkitems_hash_map.entrySet().iterator();
                    //hash loop
                    while (it.hasNext()) {
                        //Toast.makeText(this, "check random", Toast.LENGTH_SHORT).show();
                        Map.Entry pair = (Map.Entry) it.next();
                        Integer deletePosition = (Integer) pair.getValue();
                        if (dialogOptionSelected && okclicked)
                            dPosition = ids.get(relativeindexList.get(deletePosition));
                        else
                            dPosition = ids.get(deletePosition);
                        try {
                            //DBHandler.context = History.this;
                            if (dbHandler.deleteRowById(dPosition)) {
                                //Toast.makeText(this, "filename deleted: "+ DBHandler.firstValue, Toast.LENGTH_SHORT).show();
                            } else {
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                    //for loop
                    adapter.deactivate();
                    finish();
                    overridePendingTransition(0, 0);
                    startActivity(getIntent());
                    overridePendingTransition(0, 0);
                }
                return true;
            case R.id.option4:  //select all option
                int temp_size = courseModelArrayList.size();
                checkitems_hash_map.clear();
                //select all should first overwrite
                //the previous entry in checkitems
                //to prevent duplication
                for (int i = 0; i < temp_size; i++) {
                    checkitems_hash_map.put(i, i);
                }
                selectall = true;
                CourseGVAdapter.selectall = true;
                adapter = new CourseGVAdapter(History.this, courseModelArrayList);
                coursesGV.setAdapter(adapter);
                return true;
            case R.id.option5: //cancel option
                itemlongclick = false;
                toolbar.setTitle("Locked Files");
                cancelFlag = true;
                adapter = new CourseGVAdapter(History.this, courseModelArrayList);
                adapter.setSize(courseModelArrayList.size());
                CourseGVAdapter.selectall = false;
                selectall = false;
                adapter.deactivate();
                coursesGV.setAdapter(adapter);
                invalidateOptionsMenu();
                return true;
            case R.id.option6:  //Restore files option
                if (checkitems_hash_map.size() > 0) {
                    runBeforeRestore();
                    gv = findViewById(R.id.idGVcourses);
                    gv.setVisibility(View.GONE);
                    ll = findViewById(R.id.filesrestoreloading);
                    ll.setVisibility(View.VISIBLE);
                    ExecutorService executorService = Executors.newSingleThreadExecutor();
                    Handler mainHandler = new Handler(Looper.getMainLooper());
                    executorService.execute(() -> {
                        // Simulate background work
                        try {
                            restorefunction();

                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        // Notify the main thread when work is done
                        mainHandler.post(() -> {
                            //System.out.println("Running post-thread function on the main thread.");
                            runafterRestore();

                            Toast.makeText(this, "File Restored Successfully", Toast.LENGTH_SHORT).show();
                        });
                    });
                    executorService.shutdown();
                } else
                    Toast.makeText(this, "item not selected", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void runBeforeRestore() {
        toolbar.setTitle("Locked Files");
        cancelFlag = true;
        invalidateOptionsMenu();
    }

    public static String decrypt(SecretKey key, String base64Ciphertext) throws Exception {
        byte[] combined = android.util.Base64.decode(base64Ciphertext, android.util.Base64.DEFAULT);

        // Extract IV (first 12 bytes)
        byte[] iv = new byte[12];
        System.arraycopy(combined, 0, iv, 0, iv.length);

        // Extract ciphertext
        byte[] ciphertext = new byte[combined.length - iv.length];
        System.arraycopy(combined, iv.length, ciphertext, 0, ciphertext.length);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.DECRYPT_MODE, key, spec);

        byte[] plaintext = cipher.doFinal(ciphertext);
        return new String(plaintext, "UTF-8");
    }

    public String keyfinal() throws Exception {
        final String PREFS_NAME = "my_prefs";
        final String KEY_ENCRYPTED_STRING = "encrypted_string";

        // Step 1: Create KeyUpdate instance
        KeyUpdate keyUpdate = new KeyUpdate();
        // Step 2: Get or create key with alias "MyAppKey"
        SecretKey secretKey = keyUpdate.getOrCreateKey("MyAppKey");

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        // Check if encrypted string already exists
        String encryptedString = prefs.getString(KEY_ENCRYPTED_STRING, null);

        // Decrypt whenever needed
        String decryptedString;
        try {
            decryptedString = decrypt(secretKey, encryptedString);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return decryptedString;
    }

    public void runafterRestore() {
        //using cancel block
        itemlongclick = false;
        toolbar.setTitle("Locked Files");
        cancelFlag = true;
        adapter = new CourseGVAdapter(History.this, courseModelArrayList);
        adapter.setSize(courseModelArrayList.size());
        adapter.deactivate();
        coursesGV.setAdapter(adapter);
        invalidateOptionsMenu();
        //resetting the visibility of grid view and progress bar
        gv.setVisibility(View.VISIBLE);
        ll.setVisibility(View.GONE);
        if (!selectall&&!CourseGVAdapter.selectall) {
            textTitle="File Restored At";
            createNotificationChannel(showfilename);
        }
        if (selectall&&CourseGVAdapter.selectall){
            textTitle="Files Restored Successfully";
            CourseGVAdapter.selectall = false;
            selectall = false;
            createNotificationChannel(textContent);
        }

        Unlockdelete(); //for removing locker files after unlock
    }

    public void restorefunction() {
        if (selectall&&CourseGVAdapter.selectall) { //is able to handle both filtered and non filtered list successfully
            String AESSalt = "AES128bit";
            String text_key = "";
            /** new security code **/
            SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            boolean hasRunBefore = prefs.getBoolean("hasRunBefore", false);

            if(!(hasRunBefore))
                text_key = getString(R.string.text_key);
            else{
                //String text_key;
                String second_test = null;
                try {
                    second_test = keyfinal();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                text_key = second_test;
            }
            //Toast.makeText(this, "Key final: "+ text_key, Toast.LENGTH_SHORT).show();
            /** new security code **/

            String cypherInstance = "AES/CBC/PKCS5Padding";
            String initializationVector = "2195081919109305";
            SecretKeySpec sks = new SecretKeySpec(getRaw(text_key, AESSalt), "AES");
            Cipher cipher = null;
            try {
                cipher = Cipher.getInstance(cypherInstance);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            } catch (NoSuchPaddingException e) {
                throw new RuntimeException(e);
            }
            try {
                cipher.init(Cipher.DECRYPT_MODE, sks, new IvParameterSpec(initializationVector.getBytes()));
            } catch (InvalidAlgorithmParameterException e) {
                throw new RuntimeException(e);
            } catch (InvalidKeyException e) {
                throw new RuntimeException(e);
            }
            dbHandler = new DBHandler(History.this);
            ArrayList<String> my2 = dbHandler.readCourses();  //files currently in the locker
            ArrayList<String> my3 = dbHandler.readOrgFilename();  //target location for file restoration
            int listsize = my2.size();
            for (int j = 0; j < listsize; j++) {
                String lockedFilename = my2.get(j);
                String restoreFilename = my3.get(j);
                FileInputStream fis = null;
                FileOutputStream fos = null;
                try {
                    fis = new FileInputStream(lockedFilename);
                } catch (FileNotFoundException e) {
                }
                try {
                    fos = new FileOutputStream(restoreFilename);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
                CipherInputStream cis = new CipherInputStream(fis, cipher);
                byte[] b = new byte[1024];
                int i = 0;
                try {
                    i = cis.read(b);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                while (i != -1) {
                    try {
                        fos.write(b, 0, i);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        i = cis.read(b);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                try {
                    fos.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    fis.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    cis.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            //bulk media scanner code goes here to notify gallery app for update on creation
            int temp_size = my3.size();
            String[] filePaths = new String[temp_size];
            String[] mimeTypes = new String[temp_size];
            String[] temporary = new String[1];
            String[] temporary2 = new String[1];

            for (int i = 0; i < temp_size; i++) {
                filePaths[i] = my3.get(i);

                mimeTypes[i] = "image/"+getFileExtension(my3.get(i));  // Change this based on file type

                temporary[0] = my3.get(i);
                temporary2[0] = "image/"+getFileExtension(my3.get(i));

                /*MediaScannerConnection.scanFile(History.this,
                        temporary,
                        temporary2,
                        (path, uri) -> Log.d("MediaScanner", "Scanned " + path + ": " + uri));
                */

                File file = new File(filePaths[i]);

                new SingleMediaScanner(this, file);
            }

           /* MediaScannerConnection.scanFile(History.this, filePaths, mimeTypes,
                    (path, uri) -> Log.d("MediaScanner", "Scanned: " + path));
*/

        }

        if (!selectall&!CourseGVAdapter.selectall) {
            //statements
            String AESSalt = "AES128bit";
            String text_key = "";
            /** new security code **/
            SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            boolean hasRunBefore = prefs.getBoolean("hasRunBefore", false);

            if(!(hasRunBefore))
                text_key = getString(R.string.text_key);
            else{

                //String text_key;
                String second_test = null;
                try {
                    second_test = keyfinal();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                text_key = second_test;
            }
            //Toast.makeText(this, "Key final: "+ text_key, Toast.LENGTH_SHORT).show();
            /** new security code **/
            //Toast.makeText(this, "short key: "+ text_key, Toast.LENGTH_SHORT).show();
            String cypherInstance = "AES/CBC/PKCS5Padding";
            String initializationVector = "2195081919109305";
            SecretKeySpec sks = new SecretKeySpec(getRaw(text_key, AESSalt), "AES");
            Cipher cipher = null;
            try {
                cipher = Cipher.getInstance(cypherInstance);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            } catch (NoSuchPaddingException e) {
                throw new RuntimeException(e);
            }
            try {
                cipher.init(Cipher.DECRYPT_MODE, sks, new IvParameterSpec(initializationVector.getBytes()));
            } catch (InvalidAlgorithmParameterException e) {
                throw new RuntimeException(e);
            } catch (InvalidKeyException e) {
                throw new RuntimeException(e);
            }
            dbHandler = new DBHandler(History.this);
            ArrayList<String> my2 = dbHandler.readCourses();
            ArrayList<String> my3 = dbHandler.readOrgFilename();
            //second level
            Iterator it = checkitems_hash_map.entrySet().iterator();
            //hash loop
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                Integer deletePosition = (Integer) pair.getValue();
                if (dialogOptionSelected && okclicked)
                    deletePosition = relativeindexList.get(deletePosition);
                try {
                    //Toast.makeText(this, "delete position: "+ deletePosition, Toast.LENGTH_SHORT).show();
                    String lockedFilename = my2.get(deletePosition);
                    String restoreFilename = my3.get(deletePosition);
                    showfilename = restoreFilename;  //show file name to user in notification
                    FileInputStream fis = null;
                    FileOutputStream fos = null;
                    try {
                        fis = new FileInputStream(lockedFilename);
                    } catch (FileNotFoundException e) {
                    }
                    try {
                        fos = new FileOutputStream(restoreFilename);
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    CipherInputStream cis = new CipherInputStream(fis, cipher);
                    byte[] b = new byte[1024];
                    int i = 0;
                    try {
                        i = cis.read(b);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    while (i != -1) {
                        try {
                            fos.write(b, 0, i);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        try {
                            i = cis.read(b);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    try {
                        fos.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        fis.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        cis.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            //media scanner code for single restore

            int temp_size = my3.size();
            String[] filePaths = new String[temp_size];
            String[] mimeTypes = new String[temp_size];

            for (int i = 0; i < temp_size; i++) {
                filePaths[i] = my3.get(i);
                mimeTypes[i] = "image/"+getFileExtension(my3.get(i));  // Change this based on file type

                File file = new File(filePaths[i]);

                new SingleMediaScanner(this, file);
            }

            /*MediaScannerConnection.scanFile(History.this, filePaths, mimeTypes,
                    (path, uri) -> Log.d("MediaScanner", "Scanned: " + path));
            */
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu
        getMenuInflater().inflate(R.menu.menu2, menu);
        if (isDarkModeEnabled) {
            menu.findItem(R.id.option1).setIcon(R.drawable.filter20);
            //menu.findItem(R.id.option4).setIcon(R.drawable.selectall_white);
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Below items are displayed outside the menu
        if (!cancelFlag) {
            global = menu;
            menu.findItem(R.id.option1).setVisible(true);
            if (random == true) {
                menu.findItem(R.id.option1).setVisible(false);
                menu.findItem(R.id.option3).setVisible(true);
                menu.findItem(R.id.option4).setVisible(true);
                menu.findItem(R.id.option5).setVisible(true);

                //menu.findItem(R.id.option5).setIcon(R.mipmap.cross2_foreground);
                menu.findItem(R.id.option6).setVisible(true);

                if (isDarkModeEnabled) {
                    menu.findItem(R.id.option5).setIcon(R.mipmap.cross2_foreground);
                    menu.findItem(R.id.option4).setIcon(R.drawable.selectall_white);
                    menu.findItem(R.id.option3).setIcon(R.drawable.delete7new);
                }
            }
        } else {
            global = menu;
            menu.findItem(R.id.option1).setVisible(true);
            menu.findItem(R.id.option3).setVisible(false);
            menu.findItem(R.id.option4).setVisible(false);
            menu.findItem(R.id.option5).setVisible(false);
            menu.findItem(R.id.option6).setVisible(false);
            cancelFlag = false;
            if (isDarkModeEnabled) {
                menu.findItem(R.id.option1).setIcon(R.drawable.filter20);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    public void dialog() {
        selectedFruits = fruits[selectedFruitsIndex];
        new MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.Show))
                .setSingleChoiceItems(fruits, selectedFruitsIndex, (dialog, which) -> {
                    selectedFruitsIndex = which;
                })
                .setPositiveButton("Ok", (dialog, which) -> {
                    selectedFruits = fruits[selectedFruitsIndex];
                    dialogOptionSelected = true;
                    okclicked = true;
                    currentDialog = dialog;
                    runAfterOk();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    public void runAfterOk() {
        if (dialogOptionSelected && okclicked) {
            if (selectedFruits.equalsIgnoreCase(pdfExtension)) {
                Filter obj = new Filter();
                myarraylist = dbHandler.readCourses();
                courseModelArrayList = obj.pdfFilter(courseModelArrayList, myarraylist, pdfExtension, this);
                CourseGVAdapter adapter = new CourseGVAdapter(History.this, courseModelArrayList);
                adapter.deactivate();
                coursesGV.setAdapter(adapter);
            }
            if (selectedFruits.equalsIgnoreCase(imageExtension)) {
                Filter obj = new Filter();
                myarraylist = dbHandler.readCourses();
                courseModelArrayList = obj.imageFilter(courseModelArrayList, myarraylist, imageExtension, this);
                CourseGVAdapter adapter = new CourseGVAdapter(History.this, courseModelArrayList);
                adapter.deactivate();
                coursesGV.setAdapter(adapter);
            }
            if (selectedFruits.equalsIgnoreCase(docExtension)) {
                Filter obj = new Filter();
                myarraylist = dbHandler.readCourses();
                courseModelArrayList = obj.docFilter(courseModelArrayList, myarraylist, docExtension, this);
                CourseGVAdapter adapter = new CourseGVAdapter(History.this, courseModelArrayList);
                adapter.deactivate();
                coursesGV.setAdapter(adapter);
            }
            if (selectedFruits.equalsIgnoreCase(allfiles)) {
                Filter obj = new Filter();
                myarraylist = dbHandler.readCourses();
                courseModelArrayList = obj.AllFilesShow(courseModelArrayList, myarraylist, "allfiles", this);
                CourseGVAdapter adapter = new CourseGVAdapter(History.this, courseModelArrayList);
                adapter.deactivate();
                coursesGV.setAdapter(adapter);
            }
            if (selectedFruits.equalsIgnoreCase(videosExtension)) {
                Filter obj = new Filter();
                ;
                myarraylist = dbHandler.readCourses();
                courseModelArrayList = obj.videoFilter(courseModelArrayList, myarraylist, videosExtension, this);
                CourseGVAdapter adapter = new CourseGVAdapter(History.this, courseModelArrayList);
                adapter.deactivate();
                coursesGV.setAdapter(adapter);
            }
            if (selectedFruits.equalsIgnoreCase(pptExtension)) {
                Filter obj = new Filter();
                myarraylist = dbHandler.readCourses();
                courseModelArrayList = obj.pptFilter(courseModelArrayList, myarraylist, pptExtension, this);
                CourseGVAdapter adapter = new CourseGVAdapter(History.this, courseModelArrayList);
                adapter.deactivate();
                coursesGV.setAdapter(adapter);
            }
            if (selectedFruits.equalsIgnoreCase(excelExtension)) {
                Filter obj = new Filter();
                myarraylist = dbHandler.readCourses();
                courseModelArrayList = obj.excelFilter(courseModelArrayList, myarraylist, excelExtension, this);
                CourseGVAdapter adapter = new CourseGVAdapter(History.this, courseModelArrayList);
                adapter.deactivate();
                coursesGV.setAdapter(adapter);
            }
            if (selectedFruits.equalsIgnoreCase(audioExtension)) {
                Filter obj = new Filter();
                myarraylist = dbHandler.readCourses();
                courseModelArrayList = obj.audioFilter(courseModelArrayList, myarraylist, audioExtension, this);
                CourseGVAdapter adapter = new CourseGVAdapter(History.this, courseModelArrayList);
                adapter.deactivate();
                coursesGV.setAdapter(adapter);
            }
        }
    }

    public void fileDestroyer(String path) {
        File file = new File(path);
        try {
            if (file.delete()) {

            }
        } catch (Exception e) {

        }
    }

    public int dptopxconverter(int marginInDp) {
        float scale = getResources().getDisplayMetrics().density;
        return ((int) (marginInDp * scale + 0.5f));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    private void createNotificationChannel(String msg) {
        // Create a NotificationCompat.Builder with the channel ID
        NotificationCompat.Builder builder = new NotificationCompat.Builder(History.this, CHANNEL_ID)
                .setSmallIcon(R.drawable.animal10)
                .setContentTitle(textTitle)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        // Use BigTextStyle to handle long messages
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.bigText(msg); // Set the long message here
        builder.setStyle(bigTextStyle); // Apply BigTextStyle to the notification

        // Create an Intent to open the History activity when clicked
        Intent intent = new Intent(History.this, History.class);
        PendingIntent pendingIntent = TaskStackBuilder.create(this)
                .addNextIntentWithParentStack(intent)
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        builder.setContentIntent(pendingIntent);

        // Get the NotificationManager to handle notifications
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // For Android O and above, create a notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("mychannel", name, importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }

        // Use the current time in milliseconds as the notification ID
        int notificationId = (int) System.currentTimeMillis();
        notificationManager.notify(notificationId, builder.build());
    }

    public static String getFileExtension(String filePath) {
        int lastDotIndex = filePath.lastIndexOf(".");
        if (lastDotIndex == -1 || lastDotIndex == filePath.length() - 1) {
            return "";  // No extension found
        }
        return filePath.substring(lastDotIndex + 1);
    }

    public void Unlockdelete(){  //for removing files from locker after unlock
        if(dateTimeList.size()>0&&unlockfilelist.size()>0){
            dateTimeList.clear();
            unlockfilelist.clear();
        }
        if (selectall) {
            dbHandler = new DBHandler(History.this);
            ArrayList<Integer> ids = dbHandler.readId();
            int dPosition;
            Iterator it = checkitems_hash_map.entrySet().iterator();
            //hash loop
            while (it.hasNext()) {
                //Toast.makeText(this, "check random", Toast.LENGTH_SHORT).show();
                Map.Entry pair = (Map.Entry) it.next();
                Integer deletePosition = (Integer) pair.getValue();
                if (dialogOptionSelected && okclicked)
                    dPosition = ids.get(relativeindexList.get(deletePosition));
                else
                    dPosition = ids.get(deletePosition);

                //collect the unlock file list before deletion for restore history feature

                unlockfilelist.add(dbHandler.readOrgFilename2(dPosition));

                //generate date time

                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy hh:mm a");
                String currentDateTime = sdf.format(new Date());

                //add date time
                dateTimeList.add(currentDateTime);  // Example date-time


                try {
                    if (dbHandler.deleteRowById(dPosition)) {
                        //Toast.makeText(this, "filename deleted: "+ DBHandler.firstValue, Toast.LENGTH_SHORT).show();
                    } else {
                        //Toast.makeText(this, "not deleted", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            db2.insertMultipleRecords(unlockfilelist,dateTimeList);

            adapter.deactivate();
            finish();
            startActivity(getIntent());
            overridePendingTransition(0, 0);
        }
        if (!selectall) {
            dbHandler = new DBHandler(History.this);
            ArrayList<Integer> ids = dbHandler.readId();
            int dPosition;
            Iterator it = checkitems_hash_map.entrySet().iterator();
            //hash loop
            while (it.hasNext()) {
                //Toast.makeText(this, "check random", Toast.LENGTH_SHORT).show();
                Map.Entry pair = (Map.Entry) it.next();
                Integer deletePosition = (Integer) pair.getValue();
                if (dialogOptionSelected && okclicked)
                    dPosition = ids.get(relativeindexList.get(deletePosition));
                else
                    dPosition = ids.get(deletePosition);


                unlockfilelist.add(dbHandler.readOrgFilename2(dPosition));

                //generate date time

                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy hh:mm a");
                String currentDateTime = sdf.format(new Date());

                //add date time
                dateTimeList.add(currentDateTime);  // Example date-time

                try {
                    //DBHandler.context = History.this;
                    if (dbHandler.deleteRowById(dPosition)) {
                        //Toast.makeText(this, "filename deleted: "+ DBHandler.firstValue, Toast.LENGTH_SHORT).show();
                    } else {
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            db2.insertMultipleRecords(unlockfilelist,dateTimeList);

            //for loop
            adapter.deactivate();
            finish();
            overridePendingTransition(0, 0);
            startActivity(getIntent());
            overridePendingTransition(0, 0);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        /*SharedPreferences prefs = newBase.getSharedPreferences("langpreference", MODE_PRIVATE);
        String langPref = prefs.getString("lpref", "en");
        //String langCode = langPref.equalsIgnoreCase("es") ? "es" : "en";
        //String langCode = getLocaleCodeFromPreference(langPref);
        Context context = LocaleHelper.wrap(newBase, langPref);
        super.attachBaseContext(context);*/
        super.attachBaseContext(LocaleHelper.wrap(newBase));
    }
}