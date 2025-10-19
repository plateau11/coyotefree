package com.example.coyotefree;
import android.content.Context;

import java.io.File;
import java.util.ArrayList;
/***
 * used to filter file types
 */
public class Filter {
    History obj;
    ArrayList<String> fileList = new ArrayList<>();
    ArrayList<Integer> relativeindexList2;
    public ArrayList<String> myarraylist;
    /**
     *
     * @param courseModelArrayList is the new filtered arraylist
     * @param oldExtensionList is the old unfiltered arraylist
     * @param type
     * @param context
     * @return
     */
    public ArrayList<CourseModel> imageFilter(ArrayList<CourseModel>courseModelArrayList
            ,ArrayList<String> oldExtensionList, String type,Context context){
        relativeindexList2 = new ArrayList<>();
        obj = new History();
        myarraylist = oldExtensionList; //filling the array list
        int size = myarraylist.size();
        courseModelArrayList.clear();
        for (int i = 0; i < size; i++) {
            String filename = myarraylist.get(i);
            // Create a File object for each filename to extract the file name
            File file = new File(filename);
            // Get the name of the file (including the extension)
            String fileName = file.getName();
            // Find the index of the last dot (.) in the filename to split the extension
            int dotIndex = fileName.lastIndexOf(".");
            // Variable to hold the filename without the extension
            String filenameOnly = "";
            if (dotIndex > 0) {
                // Extract the part before the dot as the filename without the extension
                filenameOnly = fileName.substring(0, dotIndex);
            }
            if(type.equalsIgnoreCase("images")
                    &&(fileName.endsWith("jpeg")||
                    fileName.endsWith("jpg"))||
                    fileName.endsWith("png")||
                    fileName.endsWith("webp")
            )
            {
                relativeindexList2.add(i);
                courseModelArrayList.add(new CourseModel(filenameOnly, filename));
            }

           /*
            if (fileName.endsWith(extension)) {
                // If it ends with .pdf, add the filename to the courseModelArrayList
                courseModelArrayList.add(new CourseModel(filenameOnly, filename));
            }
            */
        }

        History.relativeindexList = relativeindexList2;

        return courseModelArrayList;
        //CourseGVAdapter adapter = new CourseGVAdapter(this, courseModelArrayList);

        //adapter.deactivate();

        //coursesGV.setAdapter(adapter);
    }

    public ArrayList<String> getFileList(){
        return fileList;
    }

    public ArrayList<CourseModel> docFilter(ArrayList<CourseModel>courseModelArrayList
            ,ArrayList<String> oldExtensionList, String type,Context context)
    {
        relativeindexList2 = new ArrayList<>();
        //dbHandler = new DBHandler(context);
        //Toast.makeText(context, "test", Toast.LENGTH_SHORT).show();
        myarraylist = oldExtensionList; //filling the array list
        // Loop through each filename in the original list
        //Toast.makeText(context, "test3", Toast.LENGTH_SHORT).show();
        int size = myarraylist.size();
        //Toast.makeText(context, "test3", Toast.LENGTH_SHORT).show();
        courseModelArrayList.clear();
        //Toast.makeText(context, "test3", Toast.LENGTH_SHORT).show();
        for (int i = 0; i < size; i++) {
            //Toast.makeText(context, "test3", Toast.LENGTH_SHORT).show();
            String filename = myarraylist.get(i);
            //Toast.makeText(context, "test3", Toast.LENGTH_SHORT).show();
            // Create a File object for each filename to extract the file name
            File file = new File(filename);

            // Get the name of the file (including the extension)
            String fileName = file.getName();

            // Find the index of the last dot (.) in the filename to split the extension
            int dotIndex = fileName.lastIndexOf(".");

            // Variable to hold the filename without the extension
            String filenameOnly = "";

            if (dotIndex > 0) {
                // Extract the part before the dot as the filename without the extension
                filenameOnly = fileName.substring(0, dotIndex);
            }

            if (type.equalsIgnoreCase("doc")
                    && (fileName.endsWith("docx")) ||
                    fileName.endsWith("doc")
            ) {
                relativeindexList2.add(i);
                //Toast.makeText(context, "test2", Toast.LENGTH_SHORT).show();
                courseModelArrayList.add(new CourseModel(filenameOnly, filename));
            }

        }
        History.relativeindexList = relativeindexList2;
        return courseModelArrayList;
    }
    public ArrayList<CourseModel> pdfFilter(ArrayList<CourseModel>courseModelArrayList
            ,ArrayList<String> oldExtensionList, String type,Context context){
        relativeindexList2 = new ArrayList<>();
        //dbHandler = new DBHandler(context);
        //Toast.makeText(context, "test", Toast.LENGTH_SHORT).show();
        myarraylist = oldExtensionList; //filling the array list
        // Loop through each filename in the original list
        //Toast.makeText(context, "test3", Toast.LENGTH_SHORT).show();
        int size = myarraylist.size();
        //Toast.makeText(context, "test3", Toast.LENGTH_SHORT).show();
        courseModelArrayList.clear();
        //Toast.makeText(context, "test3", Toast.LENGTH_SHORT).show();
        for (int i = 0; i < size; i++) {
            //Toast.makeText(context, "test3", Toast.LENGTH_SHORT).show();
            String filename = myarraylist.get(i);
            //Toast.makeText(context, "test3", Toast.LENGTH_SHORT).show();
            // Create a File object for each filename to extract the file name
            File file = new File(filename);

            // Get the name of the file (including the extension)
            String fileName = file.getName();

            // Find the index of the last dot (.) in the filename to split the extension
            int dotIndex = fileName.lastIndexOf(".");

            // Variable to hold the filename without the extension
            String filenameOnly = "";

            if (dotIndex > 0) {
                // Extract the part before the dot as the filename without the extension
                filenameOnly = fileName.substring(0, dotIndex);
            }

            if (type.equalsIgnoreCase("pdf")
                    && fileName.endsWith("pdf"))
            {
                fileList.add(filename);
                relativeindexList2.add(i);
                //Toast.makeText(context, "test2", Toast.LENGTH_SHORT).show();
                courseModelArrayList.add(new CourseModel(filenameOnly, filename));
            }

        }
        History.relativeindexList = relativeindexList2;
        return courseModelArrayList;
    }

    public ArrayList<CourseModel> AllFilesShow(ArrayList<CourseModel>courseModelArrayList
            ,ArrayList<String> oldExtensionList, String type,Context context){
        relativeindexList2 = new ArrayList<>();
        //dbHandler = new DBHandler(context);
        //Toast.makeText(context, "test", Toast.LENGTH_SHORT).show();
        myarraylist = oldExtensionList; //filling the array list
        // Loop through each filename in the original list
        //Toast.makeText(context, "test3", Toast.LENGTH_SHORT).show();
        int size = myarraylist.size();
        //Toast.makeText(context, "test3", Toast.LENGTH_SHORT).show();
        courseModelArrayList.clear();
        //Toast.makeText(context, "test3", Toast.LENGTH_SHORT).show();
        for (int i = 0; i < size; i++) {
            //Toast.makeText(context, "test3", Toast.LENGTH_SHORT).show();
            String filename = myarraylist.get(i);
            //Toast.makeText(context, "test3", Toast.LENGTH_SHORT).show();
            // Create a File object for each filename to extract the file name
            File file = new File(filename);

            // Get the name of the file (including the extension)
            String fileName = file.getName();

            // Find the index of the last dot (.) in the filename to split the extension
            int dotIndex = fileName.lastIndexOf(".");

            // Variable to hold the filename without the extension
            String filenameOnly = "";

            if (dotIndex > 0) {
                // Extract the part before the dot as the filename without the extension
                filenameOnly = fileName.substring(0, dotIndex);
            }
            relativeindexList2.add(i);
                //Toast.makeText(context, "test2", Toast.LENGTH_SHORT).show();
            courseModelArrayList.add(new CourseModel(filenameOnly, filename));


        }
        History.relativeindexList = relativeindexList2;
        return courseModelArrayList;
    }

    public ArrayList<CourseModel> videoFilter(ArrayList<CourseModel>courseModelArrayList
            ,ArrayList<String> oldExtensionList, String type,Context context){
        relativeindexList2 = new ArrayList<>();
        myarraylist = oldExtensionList; //filling the array list
        int size = myarraylist.size();
        courseModelArrayList.clear();
        for (int i = 0; i < size; i++) {
            String filename = myarraylist.get(i);
            File file = new File(filename);
            String fileName = file.getName();
            int dotIndex = fileName.lastIndexOf(".");
            String filenameOnly = "";
            if (dotIndex > 0) {
                filenameOnly = fileName.substring(0, dotIndex);
            }
            if (type.equalsIgnoreCase("videos")
                    && (fileName.endsWith("mkv") ||
                    fileName.endsWith("mp4")) ||
                    fileName.endsWith("flv") ||
                    fileName.endsWith("gif")||
                    fileName.endsWith("mpeg")
            ){
                relativeindexList2.add(i);
                courseModelArrayList.add(new CourseModel(filenameOnly, filename));
            }
        }
        History.relativeindexList = relativeindexList2;
        return courseModelArrayList;
    }

    public ArrayList<CourseModel> pptFilter(ArrayList<CourseModel>courseModelArrayList
            ,ArrayList<String> oldExtensionList, String type,Context context){
        relativeindexList2 = new ArrayList<>();
        //dbHandler = new DBHandler(context);
        //Toast.makeText(context, "test", Toast.LENGTH_SHORT).show();
        myarraylist = oldExtensionList; //filling the array list
        // Loop through each filename in the original list
        //Toast.makeText(context, "test3", Toast.LENGTH_SHORT).show();
        int size = myarraylist.size();
        //Toast.makeText(context, "test3", Toast.LENGTH_SHORT).show();
        courseModelArrayList.clear();
        //Toast.makeText(context, "test3", Toast.LENGTH_SHORT).show();
        for (int i = 0; i < size; i++) {
            //Toast.makeText(context, "test3", Toast.LENGTH_SHORT).show();
            String filename = myarraylist.get(i);
            //Toast.makeText(context, "test3", Toast.LENGTH_SHORT).show();
            // Create a File object for each filename to extract the file name
            File file = new File(filename);

            // Get the name of the file (including the extension)
            String fileName = file.getName();

            // Find the index of the last dot (.) in the filename to split the extension
            int dotIndex = fileName.lastIndexOf(".");

            // Variable to hold the filename without the extension
            String filenameOnly = "";

            if (dotIndex > 0) {
                // Extract the part before the dot as the filename without the extension
                filenameOnly = fileName.substring(0, dotIndex);
            }

            if (type.equalsIgnoreCase("ppt")
                    && (fileName.endsWith("ppt") ||
                    fileName.endsWith("pptx"))
            ){
                relativeindexList2.add(i);
                //Toast.makeText(context, "test2", Toast.LENGTH_SHORT).show();
                courseModelArrayList.add(new CourseModel(filenameOnly, filename));
            }

        }
        History.relativeindexList = relativeindexList2;
        return courseModelArrayList;
    }

    public ArrayList<CourseModel> excelFilter(ArrayList<CourseModel>courseModelArrayList
            ,ArrayList<String> oldExtensionList, String type,Context context){
        relativeindexList2 = new ArrayList<>();
        //dbHandler = new DBHandler(context);
        //Toast.makeText(context, "test", Toast.LENGTH_SHORT).show();
        myarraylist = oldExtensionList; //filling the array list
        // Loop through each filename in the original list
        //Toast.makeText(context, "test3", Toast.LENGTH_SHORT).show();
        int size = myarraylist.size();
        //Toast.makeText(context, "test3", Toast.LENGTH_SHORT).show();
        courseModelArrayList.clear();
        //Toast.makeText(context, "test3", Toast.LENGTH_SHORT).show();
        for (int i = 0; i < size; i++) {
            //Toast.makeText(context, "test3", Toast.LENGTH_SHORT).show();
            String filename = myarraylist.get(i);
            //Toast.makeText(context, "test3", Toast.LENGTH_SHORT).show();
            // Create a File object for each filename to extract the file name
            File file = new File(filename);

            // Get the name of the file (including the extension)
            String fileName = file.getName();

            // Find the index of the last dot (.) in the filename to split the extension
            int dotIndex = fileName.lastIndexOf(".");

            // Variable to hold the filename without the extension
            String filenameOnly = "";

            if (dotIndex > 0) {
                // Extract the part before the dot as the filename without the extension
                filenameOnly = fileName.substring(0, dotIndex);
            }

            if (type.equalsIgnoreCase("excel")
                    && ((fileName.endsWith("csv")||(fileName.endsWith("xlsx")||
                    fileName.endsWith("xls"))))
            ){
                relativeindexList2.add(i);
                //Toast.makeText(context, "test2", Toast.LENGTH_SHORT).show();
                courseModelArrayList.add(new CourseModel(filenameOnly, filename));
            }

        }
        History.relativeindexList = relativeindexList2;
        return courseModelArrayList;
    }

    public ArrayList<CourseModel> audioFilter(ArrayList<CourseModel>courseModelArrayList
            ,ArrayList<String> oldExtensionList, String type,Context context){
        relativeindexList2 = new ArrayList<>();
        //dbHandler = new DBHandler(context);
        //Toast.makeText(context, "test", Toast.LENGTH_SHORT).show();
        myarraylist = oldExtensionList; //filling the array list
        // Loop through each filename in the original list
        //Toast.makeText(context, "test3", Toast.LENGTH_SHORT).show();
        int size = myarraylist.size();
        //Toast.makeText(context, "test3", Toast.LENGTH_SHORT).show();
        courseModelArrayList.clear();
        //Toast.makeText(context, "test3", Toast.LENGTH_SHORT).show();
        for (int i = 0; i < size; i++) {
            //Toast.makeText(context, "test3", Toast.LENGTH_SHORT).show();
            String filename = myarraylist.get(i);
            //Toast.makeText(context, "test3", Toast.LENGTH_SHORT).show();
            // Create a File object for each filename to extract the file name
            File file = new File(filename);

            // Get the name of the file (including the extension)
            String fileName = file.getName();

            // Find the index of the last dot (.) in the filename to split the extension
            int dotIndex = fileName.lastIndexOf(".");

            // Variable to hold the filename without the extension
            String filenameOnly = "";

            if (dotIndex > 0) {
                // Extract the part before the dot as the filename without the extension
                filenameOnly = fileName.substring(0, dotIndex);
            }

            if (type.equalsIgnoreCase("audio")
                    && (fileName.endsWith("wav") ||
                    fileName.endsWith("mp3")) ||
                    fileName.endsWith("opus") ||
                    fileName.endsWith("aac")
            ){
                relativeindexList2.add(i);
                //Toast.makeText(context, "test2", Toast.LENGTH_SHORT).show();
                courseModelArrayList.add(new CourseModel(filenameOnly, filename));
            }

        }
        History.relativeindexList = relativeindexList2;
        return courseModelArrayList;
    }
}
